package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectionFieldValueList implements Serializable
{
   private final String _projectionClassName;
   private List<ProjectionFieldValue> _list = new ArrayList<>();

   public ProjectionFieldValueList(String projectionClassName)
   {
      _projectionClassName = projectionClassName;
   }

   public void add(Object value, String fieldName, Class<?> fieldType)
   {
      _list.add(new ProjectionFieldValue(prepareTransportToUI(value), isNull(value), fieldName, asQualifiedTypeName(fieldType), isJsonValueWithQuotes(fieldType)));
   }

   public void addUntyped(Object value)
   {
      _list.add(new ProjectionFieldValue(prepareTransportToUI(value), isNull(value), tryGetTypeName(value), ProjectionFieldValue.UNKNOWN_FIELD_NAME, true));
   }

   private String prepareTransportToUI(Object value)
   {
      if( isNull(value) )
      {
         return "<null>";
      }

      try
      {
         return "" + value;
      }
      catch(Exception e)
      {
         return ProjectionFieldValue.UNKNOWN_FIELD_VALUE;
      }
   }

   private String asQualifiedTypeName(Class<?> fieldType)
   {
      if(null == fieldType)
      {
         return ProjectionFieldValue.UNKNOWN_TYPE_NAME;
      }

      try
      {
         return fieldType.getName();
      }
      catch(Exception e)
      {
         return ProjectionFieldValue.UNKNOWN_TYPE_NAME;
      }
   }

   private boolean isNull(Object value)
   {
      return null == value;
   }

   private String tryGetTypeName(Object value)
   {
      try
      {
         if(isNull(value))
         {
            return ProjectionFieldValue.UNKNOWN_TYPE_NAME;
         }
         return value.getClass().getName();
      }
      catch(Exception e)
      {
         return ProjectionFieldValue.UNKNOWN_TYPE_NAME;
      }
   }

   private boolean isJsonValueWithQuotes(Class<?> fieldType)
   {
      return false ==
             (
                      fieldType.isPrimitive()
                   || Boolean.class == fieldType
                   || Character.class == fieldType
                   || Byte.class == fieldType
                   || Short.class == fieldType
                   || Integer.class == fieldType
                   || Long.class == fieldType
                   || Float.class == fieldType
                   || Double.class == fieldType
             );
   }

   public String toUiRepresentationString()
   {
      if(_list.isEmpty())
      {
         return "<noFields>";
      }

      switch( _list.get(0).getProjectionDisplayMode() )
      {
         case DEFAULT_MODE:
            return _list.stream().map(pv -> pv.toUiRepresentationString()).collect(Collectors.joining("|"));
         case JSON_MODE:
            return "{\n" + _list.stream().map(pv -> pv.toUiRepresentationString()).collect(Collectors.joining(",\n")) + "\n}";
         case JSON_MODE_INC_TYPES:
            return "{\"projectionClass\" : \"" + _projectionClassName + "\",\n\"fields\" : [\n" + _list.stream().map(pv -> pv.toUiRepresentationString()).collect(Collectors.joining(",\n")) + "]\n}";
         case XML_MODE:
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<fields>\n" +
                   _list.stream().map(pv -> pv.toUiRepresentationString()).collect(Collectors.joining("\n")) + "\n</fields>";
         case XML_MODE_INC_TYPES:
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                   + "<projection>\n"
                   + "<projectionClass>" + _projectionClassName + "</projectionClass>\n"
                   + "<fields>\n"
                   + _list.stream().map(pv -> pv.toUiRepresentationString()).collect(Collectors.joining("\n")) + "\n</fields>\n</projection>";
         default:
            throw new IllegalStateException("Unknown ProjectionDisplayMode: " + _list.get(0).getProjectionDisplayMode());

      }

   }

   /**
    * @return true if visiting is to continue
    */
   public boolean visitValues(ProjectionFieldValueVisitor visitor)
   {
      for( ProjectionFieldValue projectionFieldValue : _list )
      {
         if(false == visitor.visit(projectionFieldValue))
         {
            return false;
         }
      }

      return true;
   }
}
