package net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler;

import java.util.List;

/**
 * MCP {@code tools/call} result.
 *
 * @param content           required human-/legacy-readable content blocks
 * @param structuredContent optional typed result payload (MCP 2025-06-18);
 *                          omitted from JSON when {@code null}
 * @param isError           whether the tool call failed (tool-level error,
 *                          reported in the result rather than as a JSON-RPC error)
 */
public record CallToolResult(List<Content> content, Object structuredContent, boolean isError)
{
}
