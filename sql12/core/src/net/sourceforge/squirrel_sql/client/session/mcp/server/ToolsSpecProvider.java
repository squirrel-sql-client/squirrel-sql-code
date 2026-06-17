package net.sourceforge.squirrel_sql.client.session.mcp.server;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import net.sourceforge.squirrel_sql.client.session.mcp.server.annotations.McpProp;
import net.sourceforge.squirrel_sql.client.session.mcp.server.annotations.McpTool;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler.ToolSpec;

/**
 * Builds the {@code tools/list} payload by reflecting over a tools interface
 * (e.g. {@link SquirrelMcpTools}) and resolves a tool name back to its
 * {@link Method} for {@code tools/call} dispatch. Adding a method, parameter or
 * return type to the interface therefore needs no changes to either endpoint.
 * <p>
 * Stateless utility: every call re-derives the tool set from the interface, so
 * there is nothing to cache or keep in sync.
 * <p>
 * Conventions enforced on each call (fail fast):
 * <ul>
 *   <li>one MCP tool per abstract interface method; tool name = method name,
 *       overridable via {@link McpTool#name()};</li>
 *   <li>each tool method takes exactly one parameter, and that parameter is a
 *       {@code record} — its components become the input-schema properties.
 *       Record component names are always available via reflection, so no
 *       {@code -parameters} compile flag is required;</li>
 *   <li>no overloaded tool names.</li>
 * </ul>
 * Parameter and return types are assumed to be simple, acyclic, JSON-serializable
 * structures (records, collections, arrays, enums, primitives, strings). A depth
 * guard is the only safeguard against accidental cycles.
 */
public final class ToolsSpecProvider
{
   private static final int MAX_DEPTH = 50;

   private ToolsSpecProvider()
   {
      // utility class — not instantiable
   }

   /** The tool descriptors for the {@code tools/list} endpoint, ordered by tool name. */
   public static List<ToolSpec> listTools(Class<?> toolsInterface)
   {
      List<ToolSpec> specs = new ArrayList<>();
      for( Map.Entry<String, Method> entry : toolMethods(toolsInterface).entrySet() )
      {
         Method method = entry.getValue();
         McpTool annotation = method.getAnnotation(McpTool.class);
         String description = annotation != null ? annotation.description() : "";
         Map<String, Object> inputSchema = recordSchema(method.getParameterTypes()[0], 0);
         specs.add(new ToolSpec(entry.getKey(), description, inputSchema));
      }
      return specs;
   }

   /** Resolves a tool name to its method, for {@code tools/call} dispatch. */
   public static Optional<Method> findTool(Class<?> toolsInterface, String name)
   {
      return Optional.ofNullable(toolMethods(toolsInterface).get(name));
   }

   private static Map<String, Method> toolMethods(Class<?> toolsInterface)
   {
      Map<String, Method> map = new TreeMap<>();
      for( Method method : toolsInterface.getDeclaredMethods() )
      {
         if( method.isSynthetic() || !Modifier.isAbstract(method.getModifiers()) )
         {
            continue; // skip static / default / compiler-generated members
         }
         if( method.getParameterCount() != 1 )
         {
            throw new IllegalStateException(
                  "Tool method '" + method.getName() + "' must take exactly one parameter (a record).");
         }
         Class<?> paramType = method.getParameterTypes()[0];
         if( !paramType.isRecord() )
         {
            throw new IllegalStateException(
                  "Tool method '" + method.getName() + "' parameter must be a record, but was " + paramType.getName() + ".");
         }
         String name = toolName(method);
         if( map.containsKey(name) )
         {
            throw new IllegalStateException("Overloaded / duplicate tool name is not allowed: '" + name + "'.");
         }
         map.put(name, method);
      }
      return map;
   }

   private static String toolName(Method method)
   {
      McpTool annotation = method.getAnnotation(McpTool.class);
      if( annotation != null && !annotation.name().isBlank() )
      {
         return annotation.name();
      }
      return method.getName();
   }

   private static Map<String, Object> recordSchema(Class<?> recordClass, int depth)
   {
      guardDepth(depth);
      Map<String, Object> schema = objectSchema();
      Map<String, Object> properties = new LinkedHashMap<>();
      List<String> required = new ArrayList<>();
      for( RecordComponent component : recordClass.getRecordComponents() )
      {
         Map<String, Object> propertySchema = schemaForType(component.getGenericType(), depth + 1);
         McpProp annotation = component.getAnnotation(McpProp.class);
         if( annotation != null && !annotation.description().isBlank() )
         {
            propertySchema.put("description", annotation.description());
         }
         properties.put(component.getName(), propertySchema);

         if( component.getType().isPrimitive() || (annotation != null && annotation.required()) )
         {
            required.add(component.getName());
         }
      }
      schema.put("properties", properties);
      schema.put("required", required);
      return schema;
   }

   private static Map<String, Object> schemaForType(Type type, int depth)
   {
      guardDepth(depth);
      if( type instanceof Class<?> clazz )
      {
         return schemaForClass(clazz, depth);
      }
      if( type instanceof ParameterizedType parameterizedType )
      {
         Class<?> raw = (Class<?>) parameterizedType.getRawType();
         if( Collection.class.isAssignableFrom(raw) )
         {
            return arraySchema(schemaForType(parameterizedType.getActualTypeArguments()[0], depth + 1));
         }
         if( Map.class.isAssignableFrom(raw) )
         {
            Map<String, Object> schema = objectSchema();
            schema.put("additionalProperties", schemaForType(parameterizedType.getActualTypeArguments()[1], depth + 1));
            return schema;
         }
         return schemaForClass(raw, depth);
      }
      if( type instanceof GenericArrayType genericArrayType )
      {
         return arraySchema(schemaForType(genericArrayType.getGenericComponentType(), depth + 1));
      }
      return objectSchema();
   }

   private static Map<String, Object> schemaForClass(Class<?> clazz, int depth)
   {
      guardDepth(depth);
      if( clazz == String.class || clazz == char.class || clazz == Character.class || CharSequence.class.isAssignableFrom(clazz) )
      {
         return typeSchema("string");
      }
      if( clazz == boolean.class || clazz == Boolean.class )
      {
         return typeSchema("boolean");
      }
      if( clazz == int.class || clazz == Integer.class || clazz == long.class || clazz == Long.class
          || clazz == short.class || clazz == Short.class || clazz == byte.class || clazz == Byte.class
          || clazz == BigInteger.class )
      {
         return typeSchema("integer");
      }
      if( clazz == double.class || clazz == Double.class || clazz == float.class || clazz == Float.class
          || clazz == BigDecimal.class )
      {
         return typeSchema("number");
      }
      if( clazz.isEnum() )
      {
         Map<String, Object> schema = typeSchema("string");
         List<String> values = new ArrayList<>();
         for( Object constant : clazz.getEnumConstants() )
         {
            values.add(((Enum<?>) constant).name());
         }
         schema.put("enum", values);
         return schema;
      }
      if( clazz.isArray() )
      {
         return arraySchema(schemaForClass(clazz.getComponentType(), depth + 1));
      }
      if( clazz.isRecord() )
      {
         return recordSchema(clazz, depth + 1);
      }
      if( Collection.class.isAssignableFrom(clazz) )
      {
         return arraySchema(objectSchema()); // raw collection: element type unknown
      }
      return objectSchema();
   }

   private static void guardDepth(int depth)
   {
      if( depth > MAX_DEPTH )
      {
         throw new IllegalStateException("Schema nesting exceeded " + MAX_DEPTH + " levels — cyclic type?");
      }
   }

   private static Map<String, Object> typeSchema(String jsonType)
   {
      Map<String, Object> schema = new LinkedHashMap<>();
      schema.put("type", jsonType);
      return schema;
   }

   private static Map<String, Object> objectSchema()
   {
      return typeSchema("object");
   }

   private static Map<String, Object> arraySchema(Map<String, Object> itemsSchema)
   {
      Map<String, Object> schema = typeSchema("array");
      schema.put("items", itemsSchema);
      return schema;
   }
}
