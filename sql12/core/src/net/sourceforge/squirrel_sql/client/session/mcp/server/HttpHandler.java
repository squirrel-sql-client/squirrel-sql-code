package net.sourceforge.squirrel_sql.client.session.mcp.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler.JsonRpcError;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler.JsonRpcRequest;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler.JsonRpcResponse;

public class HttpHandler
{
   public static JsonRpcRequest readRequestAndPropagateErrs(HttpExchange httpExchange) throws IOException
   {
      if( !"POST".equalsIgnoreCase(httpExchange.getRequestMethod()) )
      {
         httpExchange.getResponseHeaders().set("Allow", "POST");
         sendText(httpExchange, 405, "Method Not Allowed: use POST");
         return null;
      }

      JsonRpcRequest request;
      try
      {
         request = JsonMapperFactory.createJsonMapper().readValue(readBody(httpExchange), JsonRpcRequest.class);
      }
      catch( IOException parseError )
      {
         sendJson(httpExchange, JsonRpcResponse.fail(null, new JsonRpcError(JsonRpcCodes.PARSE_ERROR, "Parse error")));
         return null;
      }
      return request;
   }

   public static void sendResponse(HttpExchange httpExchange, JsonRpcResponse dispatch, JsonNode id) throws IOException
   {
      try
      {
         sendJson(httpExchange, dispatch);
      }
      catch( RuntimeException internal )
      {
         sendJson(httpExchange, JsonRpcResponse.fail(id, new JsonRpcError(JsonRpcCodes.INTERNAL_ERROR, "Internal error: " + internal.getMessage())));
      }
   }


   private static String readBody(HttpExchange ex) throws IOException
   {
      try( InputStream in = ex.getRequestBody() )
      {
         return new String(in.readAllBytes(), StandardCharsets.UTF_8);
      }
   }

   private static void sendJson(HttpExchange ex, JsonRpcResponse response) throws IOException
   {
      byte[] bytes = JsonMapperFactory.createJsonMapper().writeValueAsBytes(response);
      ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
      ex.sendResponseHeaders(200, bytes.length);
      try( OutputStream out = ex.getResponseBody() )
      {
         out.write(bytes);
      }
   }

   private static void sendText(HttpExchange ex, int status, String text) throws IOException
   {
      byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
      ex.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
      ex.sendResponseHeaders(status, bytes.length);
      try( OutputStream out = ex.getResponseBody() )
      {
         out.write(bytes);
      }
   }
}
