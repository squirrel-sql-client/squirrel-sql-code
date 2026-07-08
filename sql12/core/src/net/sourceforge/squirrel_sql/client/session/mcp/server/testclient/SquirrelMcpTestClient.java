package net.sourceforge.squirrel_sql.client.session.mcp.server.testclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sourceforge.squirrel_sql.client.session.mcp.server.SquirrelMcpConstants;
import net.sourceforge.squirrel_sql.client.session.mcp.server.SquirrelMcpHttpServer;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetColumnsArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetExportedKeysArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetImportedKeysArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetIndexInfoArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetPrimaryKeysArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetTablesArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpNoArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpSimpleString;

/**
 * A tiny command-line client for {@link SquirrelMcpHttpServer}.
 * <p>
 * It exists so MCP calls can be made from Java instead of fighting shell
 * quoting: the JSON body is built with Jackson and sent as UTF-8 bytes over
 * {@link HttpClient}, so blanks, quotes and non-ASCII characters in arguments
 * are never exposed to a shell parser.
 * <p>
 * The endpoint is taken from {@link SquirrelMcpConstants} (host / port / root
 * path). {@code main} runs the administrative handshake (initialize, ping,
 * tools/list) and then a representative {@code getTables} call.
 */
public final class SquirrelMcpTestClient
{
   public static final int PORT = 23368;
   private final ObjectMapper mapper = new ObjectMapper();
   private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
   private final URI endpoint;

   private int nextId = 1;

   public SquirrelMcpTestClient(URI endpoint)
   {
      this.endpoint = endpoint;
   }

   public static void main(String[] args) throws IOException, InterruptedException
   {
      SquirrelMcpTestClient client = new SquirrelMcpTestClient(defaultEndpoint());
      //callMcpAdministrationMethods(client);

      // Representative getTables call: no catalog filter, schema PUBLIC, every
      // table name, restricted to base tables and views.
      //GetTablesArgs getTablesArgs = new GetTablesArgs(null, "PUBLIC", "%", new String[]{"TABLE", "VIEW"});
      //client.print("tools/call getTables", client.getTables(getTablesArgs));
      //client.print("tools/call getDriverClassName", client.getDriverClassName());
      //client.print("tools/call getSessionName", client.getSessionName());
      //client.print("tools/call getDriverName", client.getDriverName());
      //client.print("tools/call getDriverVersion", client.getDriverVersion());
      //client.print("tools/call getDatabaseProductName", client.getDatabaseProductName());
      //client.print("tools/call getDatabaseProductVersion", client.getDatabaseProductVersion());

      //GetTablesArgs getTablesArgs = new GetTablesArgs(null, "public", "%", new String[]{"TABLE"});
      //client.print("tools/call getTables", client.getTables(getTablesArgs));
      //McpSimpleString sql = new McpSimpleString("select * from suppliers order by id");
      //McpSimpleString sql = new McpSimpleString("select * from articles ORDER BY id");
      //McpSimpleString sql = new McpSimpleString("SELECT * FROM SaraErsteTabelle");
      //McpSimpleString sql = new McpSimpleString("INSERT INTO MCPTestTable(mcpid, mcpname) VALUES(2, 'Mcp2')");
      //client.print("tools/call executeQuery", client.executeQuery(sql));

      //McpGetPrimaryKeysArgs primaryKeysArgs = new McpGetPrimaryKeysArgs(null, "public", "suppliers");
      //client.print("tools/call getTables", client.getPrimaryKeys(primaryKeysArgs));

      //McpGetIndexInfoArgs indexInfoArgs = new McpGetIndexInfoArgs(null, "public", "suppliers", false, false);
      //client.print("tools/call getTables", client.getIndexInfo(indexInfoArgs));

      //McpGetImportedKeysArgs importedKeysArgs = new McpGetImportedKeysArgs(null, "public", "articles");
      //client.print("tools/call getImportedKeys", client.getImportedKeys(importedKeysArgs));

      //McpGetExportedKeysArgs exportedKeysArgs = new McpGetExportedKeysArgs(null, "public", "articles");
      //client.print("tools/call getExportedKeys", client.getExportedKeys(exportedKeysArgs));

      McpGetColumnsArgs columnsArgs = new McpGetColumnsArgs(null, "SQLUser", "WArt");
      client.print("tools/call getColumns", client.getColumns(columnsArgs));
   }

   private static void callMcpAdministrationMethods(SquirrelMcpTestClient client) throws IOException, InterruptedException
   {
      client.print("initialize", client.initialize());
      client.print("ping", client.ping());
      client.print("tools/list", client.toolsList());
   }

   public JsonNode initialize() throws IOException, InterruptedException
   {
      Map<String, Object> params = Map.of(
            "protocolVersion", "2025-06-18",
            "capabilities", Map.of(),
            "clientInfo", Map.of("name", "squirrel-mcp-test-client", "version", "1.0.0"));
      return rpc("initialize", params);
   }

   public JsonNode ping() throws IOException, InterruptedException
   {
      return rpc("ping", null);
   }

   public JsonNode toolsList() throws IOException, InterruptedException
   {
      return rpc("tools/list", null);
   }

   public JsonNode getTables(McpGetTablesArgs args) throws IOException, InterruptedException
   {
      Map<String, Object> params = Map.of(
            "name", "getTables",
            "arguments", args);
      return rpc("tools/call", params);
   }

   public JsonNode getPrimaryKeys(McpGetPrimaryKeysArgs args) throws IOException, InterruptedException
   {
      Map<String, Object> params = Map.of(
            "name", "getPrimaryKeys",
            "arguments", args);
      return rpc("tools/call", params);
   }

   public JsonNode getIndexInfo(McpGetIndexInfoArgs args) throws IOException, InterruptedException
   {
      Map<String, Object> params = Map.of(
            "name", "getIndexInfo",
            "arguments", args);
      return rpc("tools/call", params);
   }

   public JsonNode getImportedKeys(McpGetImportedKeysArgs args) throws IOException, InterruptedException
   {
      Map<String, Object> params = Map.of(
            "name", "getImportedKeys",
            "arguments", args);
      return rpc("tools/call", params);
   }

   public JsonNode getExportedKeys(McpGetExportedKeysArgs args) throws IOException, InterruptedException
   {
      Map<String, Object> params = Map.of(
            "name", "getExportedKeys",
            "arguments", args);
      return rpc("tools/call", params);
   }

   public JsonNode getColumns(McpGetColumnsArgs args) throws IOException, InterruptedException
   {
      Map<String, Object> params = Map.of(
            "name", "getColumns",
            "arguments", args);
      return rpc("tools/call", params);
   }

   private JsonNode executeQuery(McpSimpleString args) throws IOException, InterruptedException
   {
      Map<String, Object> params = Map.of(
            "name", "executeQuery",
            "arguments", args);
      return rpc("tools/call", params);
   }


   private JsonNode getSessionName()  throws IOException, InterruptedException
   {
      Map<String, Object> params = Map.of(
            "name", "getSessionName",
            "arguments", new McpNoArgs());

      return rpc("tools/call", params);
   }

   public JsonNode getDriverClassName() throws IOException, InterruptedException
   {
      Map<String, Object> params = Map.of(
            "name", "getDriverClassName",
            "arguments", new McpNoArgs());

      return rpc("tools/call", params);
   }

   public JsonNode getDriverName() throws IOException, InterruptedException
   {
      Map<String, Object> params = Map.of(
            "name", "getDriverName",
            "arguments", new McpNoArgs());

      return rpc("tools/call", params);
   }

   public JsonNode getDriverVersion() throws IOException, InterruptedException
   {
      Map<String, Object> params = Map.of(
            "name", "getDriverVersion",
            "arguments", new McpNoArgs());

      return rpc("tools/call", params);
   }

   public JsonNode getDatabaseProductName() throws IOException, InterruptedException
   {
      Map<String, Object> params = Map.of(
            "name", "getDatabaseProductName",
            "arguments", new McpNoArgs());

      return rpc("tools/call", params);
   }

   public JsonNode getDatabaseProductVersion() throws IOException, InterruptedException
   {
      Map<String, Object> params = Map.of(
            "name", "getDatabaseProductVersion",
            "arguments", new McpNoArgs());

      return rpc("tools/call", params);
   }



   /**
    * Sends one JSON-RPC request and returns the parsed response, or
    * {@code null} for an empty body (e.g. a 202 to a notification).
    */
   private JsonNode rpc(String method, Object params) throws IOException, InterruptedException
   {
      Map<String, Object> request = new LinkedHashMap<>();
      request.put("jsonrpc", "2.0");
      request.put("id", nextId++);
      request.put("method", method);
      if( params != null )
      {
         request.put("params", params);
      }

      byte[] body = mapper.writeValueAsBytes(request);
      HttpRequest httpRequest = HttpRequest.newBuilder(endpoint)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofByteArray(body))
            .build();

      HttpResponse<String> response =
            http.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

      String responseBody = response.body();
      if( responseBody == null || responseBody.isBlank() )
      {
         return null;
      }
      return mapper.readTree(responseBody);
   }

   private void print(String label, JsonNode response) throws IOException
   {
      System.out.println("== " + label + " ==");
      if( response == null )
      {
         System.out.println("(no body)");
      }
      else
      {
         System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
      }
      System.out.println();
   }

   private static URI defaultEndpoint()
   {
      return URI.create("http://" + SquirrelMcpConstants.HOST + ":" + PORT + SquirrelMcpConstants.ROOT_PATH);
   }
}
