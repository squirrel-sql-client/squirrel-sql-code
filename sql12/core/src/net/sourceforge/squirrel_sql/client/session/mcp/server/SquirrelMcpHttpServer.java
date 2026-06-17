package net.sourceforge.squirrel_sql.client.session.mcp.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler.CallToolResult;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler.Content;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler.InitializeResult;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler.JsonRpcError;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler.JsonRpcRequest;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler.JsonRpcResponse;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler.ServerCapabilities;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler.ServerInfo;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler.ToolsListResult;

/**
 * Minimal MCP server over HTTP, using Jackson + records.
 * <p>
 * Transport: Streamable HTTP in minimal (non-streaming) mode — every JSON-RPC
 * request is answered with a single {@code application/json} response. One POST
 * endpoint, {@code /mcp}.
 * <p>
 * <b>Single-client by design.</b> The server runs on a single-threaded
 * executor, so requests are handled strictly one at a time: a call blocks the
 * next until it has returned its result. Because no per-client state is kept
 * and nothing runs concurrently, there is no session to correlate — hence no
 * {@code Mcp-Session-Id} handling.
 * <p>
 * The tools are defined by {@link SquirrelMcpTools}; both {@code tools/list} and
 * {@code tools/call} are driven entirely by {@link ToolsSpecProvider} via
 * reflection, so adding a method there needs no change to this class.
 */
public final class SquirrelMcpHttpServer
{
   /**
    * MCP protocol revision this server advertises in the {@code initialize}
    * result (MCP revisions are date-based, e.g. 2024-11-05 / 2025-03-26 /
    * 2025-06-18). Returned as-is regardless of the version the client requests:
    * this is a simplification, not full version negotiation. To support more
    * than one revision, echo the client's requested version when supported and
    * fall back to this otherwise.
    */
   private static final String PROTOCOL_VERSION = "2025-06-18";

   public static void main(String[] args) throws IOException
   {
      new SquirrelMcpHttpServer().start(SquirrelMcpConstants.PORT);
   }

   public void start(int port) throws IOException
   {
      // Bind to the wildcard address (0.0.0.0): reachable from other hosts on
      // the network, not just localhost. SECURITY: there is no authentication,
      // so anyone who can reach this port can invoke the tools — restrict via
      // firewall / network segmentation if the tools are not meant to be public.
      HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext(SquirrelMcpConstants.ROOT_PATH, httpExchange -> handle(httpExchange));
      // Single worker => requests are serialized; one call blocks the next.
      server.setExecutor(Executors.newSingleThreadExecutor());
      server.start();
      System.out.println("hello-java-http MCP server listening on http://0.0.0.0:" + port + SquirrelMcpConstants.ROOT_PATH + " (all interfaces)");
   }

   private void handle(HttpExchange httpExchange) throws IOException
   {
      JsonRpcRequest request = HttpHandler.readRequestAndPropagateErrs(httpExchange);
      if( request == null )
      {
         // When request is null an error occurred which was already propagated to the client
         return;
      }

      // A message without an id is a notification (e.g. notifications/initialized):
      // acknowledge with 202 and send no JSON-RPC reply.
      if( request.id() == null || request.id().isNull() )
      {
         httpExchange.sendResponseHeaders(JsonRpcCodes.ACCEPTED, -1);
         httpExchange.close();
         return;
      }

      JsonNode id = request.id();
      JsonRpcResponse dispatch = dispatch(id, request);
      HttpHandler.sendResponse(httpExchange, dispatch, id);
   }

   private JsonRpcResponse dispatch(JsonNode id, JsonRpcRequest request)
   {
      return switch( request.method() == null ? "" : request.method() )
      {
         case "initialize" -> JsonRpcResponse.ok(id, new InitializeResult(
               PROTOCOL_VERSION,
               new ServerCapabilities(Map.of()),
               new ServerInfo(SquirrelMcpConstants.MCP_NAME, "1.0.0")));

         case "ping" -> JsonRpcResponse.ok(id, Map.of());

         case "tools/list" -> JsonRpcResponse.ok(id, new ToolsListResult(ToolsSpecProvider.listTools(SquirrelMcpTools.class)));

         case "tools/call" -> JsonRpcResponse.ok(id, callTool(request.params()));

         default -> JsonRpcResponse.fail(id, new JsonRpcError(JsonRpcCodes.METHOD_NOT_FOUND, "Method not found: " + request.method()));
      };
   }

   /**
    * Invokes a tool generically: resolves the method by name, binds the
    * {@code arguments} object to its single record parameter, calls it, and
    * returns the result as {@code structuredContent} plus a JSON text rendering.
    */
   private CallToolResult callTool(JsonNode params)
   {
      String toolName = params != null && params.hasNonNull("name") ? params.get("name").asText() : "";
      Optional<Method> resolved = ToolsSpecProvider.findTool(SquirrelMcpTools.class, toolName);
      if( resolved.isEmpty() )
      {
         return new CallToolResult(List.of(new Content("text", "Unknown tool: " + toolName)), null, true);
      }

      Method method = resolved.get();
      Class<?> paramType = method.getParameterTypes()[0];
      ObjectMapper mapper = JsonMapperFactory.createJsonMapper();

      JsonNode arguments = params.get("arguments");
      Object argObject = mapper.convertValue(
            arguments == null || arguments.isNull() ? mapper.createObjectNode() : arguments,
            paramType);

      Object result;
      try
      {
         result = method.invoke(new SquirrelMcpToolsImpl(), argObject);
      }
      catch( IllegalAccessException | InvocationTargetException e )
      {
         Throwable cause = e instanceof InvocationTargetException ite && ite.getCause() != null ? ite.getCause() : e;
         return new CallToolResult(List.of(new Content("text", "Tool '" + toolName + "' failed: " + cause.getMessage())), null, true);
      }

      return new CallToolResult(List.of(new Content("text", toJson(mapper, result))), result, false);
   }

   private static String toJson(ObjectMapper mapper, Object value)
   {
      try
      {
         return mapper.writeValueAsString(value);
      }
      catch( JsonProcessingException e )
      {
         return String.valueOf(value);
      }
   }
}
