package net.sourceforge.squirrel_sql.client.session.mcp.server.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional metadata for an MCP tool, placed on a {@code SquirrelMcpTools} method.
 * Consumed by {@code ToolsSpecProvider} when building the {@code tools/list} payload.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface McpTool
{
   /** Human-readable tool description; surfaced in {@code tools/list}. */
   String description() default "";

   /** Optional tool-name override; defaults to the method name when blank. */
   String name() default "";
}
