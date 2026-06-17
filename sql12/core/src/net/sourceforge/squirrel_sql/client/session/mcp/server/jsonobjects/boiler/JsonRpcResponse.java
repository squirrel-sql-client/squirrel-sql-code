package net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JsonRpcResponse(String jsonrpc, JsonNode id, Object result, JsonRpcError error)
{
   public static JsonRpcResponse ok(JsonNode id, Object result)
   {
      return new JsonRpcResponse("2.0", id, result, null);
   }

   public static JsonRpcResponse fail(JsonNode id, JsonRpcError error)
   {
      return new JsonRpcResponse("2.0", id, null, error);
   }
}
