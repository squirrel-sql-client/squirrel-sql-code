package net.sourceforge.squirrel_sql.client.session.mcp.server;

/**
 * The standard JSON-RPC 2.0 error codes, as defined by the JSON-RPC 2.0
 * specification, section 5.1 ("Error object").
 * See <a href="https://www.jsonrpc.org/specification#error_object">jsonrpc.org/specification</a>.
 * <p>
 * These constants are <em>not</em> invented here — they are part of the
 * protocol. We declare them locally only because this project has no JSON-RPC
 * library on the classpath (only Jackson, which is a JSON serializer, not a
 * JSON-RPC framework). If such a dependency is added later, prefer its
 * constants over these. Known external definitions of the same values:
 * <ul>
 *   <li>Eclipse LSP4J — {@code org.eclipse.lsp4j.jsonrpc.messages.ResponseErrorCode}
 *       (enum), artifact {@code org.eclipse.lsp4j:org.eclipse.lsp4j.jsonrpc}.</li>
 *   <li>jsonrpc4j — {@code com.googlecode.jsonrpc4j.ErrorResolver.JsonError}
 *       (int constants), artifact {@code com.github.briandilley.jsonrpc4j:jsonrpc4j}.</li>
 *   <li>MCP Java SDK — {@code io.modelcontextprotocol.spec.McpSchema.ErrorCodes}
 *       (int constants), artifact {@code io.modelcontextprotocol.sdk:mcp}.</li>
 * </ul>
 * <p>
 * Note: a constant-holder interface is the form explicitly requested here;
 * Effective Java (Item 22) would otherwise favour an {@code enum} (as LSP4J
 * uses) or a {@code final} class with a private constructor. There is nothing
 * to implement — refer to the constants via {@code JsonRpcErrorCodes.X}.
 */
public interface JsonRpcCodes
{
   /** Invalid JSON was received by the server (malformed / not parseable). */
   int PARSE_ERROR = -32700;

   /** The JSON sent is not a valid Request object. */
   int INVALID_REQUEST = -32600;

   /** The requested method does not exist or is not available. */
   int METHOD_NOT_FOUND = -32601;

   /** Invalid method parameter(s). */
   int INVALID_PARAMS = -32602;

   /** Internal JSON-RPC error. */
   int INTERNAL_ERROR = -32603;

   /**
    * Inclusive lower bound of the range reserved for implementation-defined
    * server errors ({@code -32099} … {@code -32000}). Codes outside the
    * reserved {@code -32768 … -32000} block are available for application use.
    */
   int SERVER_ERROR_RANGE_START = -32099;

   /** Inclusive upper bound of the reserved implementation-defined server error range. */
   int SERVER_ERROR_RANGE_END = -32000;


   int ACCEPTED = 202;
}
