package net.sourceforge.squirrel_sql.client.session.mcp.server.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional metadata for a single input property, placed on a record component
 * (or parameter / field) of a tool's argument record. Consumed by
 * {@code ToolsSpecProvider} when building the input schema for {@code tools/list}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.RECORD_COMPONENT, ElementType.PARAMETER, ElementType.FIELD})
public @interface McpProp
{
   /** Human-readable property description; surfaced in the input schema. */
   String description() default "";

   /**
    * Marks the property as required. Primitive-typed components are always
    * required (they cannot be {@code null}) regardless of this flag.
    */
   boolean required() default false;
}
