# SquirreL SQL MCP Server (Java, HTTP)

An MCP server in plain Java (JDK + Jackson, no framework) that exposes a SQuirreL
SQL **Session**'s database access as MCP tools over HTTP/JSON-RPC. Entry point:
[pack/SquirrelMcpHttpServer.java](pack/SquirrelMcpHttpServer.java); the tool
contract is [pack/SquirrelMcpTools.java](pack/SquirrelMcpTools.java).

- **Transport:** Streamable HTTP, minimal (non-streaming) — each JSON-RPC request
  gets a single `application/json` response. One `POST` endpoint; `GET` → `405`.
  Stateless (no `Mcp-Session-Id`).
- **Protocol version:** `2025-06-18`.
- **Path:** `/squirrel-sql-mcp`.
- **Tool:** `getTables(catalog?, schemaPattern?, tableNamePattern?, types?[])` →
  a typed `ResultSet` (in MCP `structuredContent`, plus a text summary).

## How a Claude instance connects (this is the intended workflow)

A SQuirreL Session (one open JDBC connection) can start its **own** MCP server on
its **own** port; the Session UI shows that port. The user gives you (Claude) the
port. **Talk to it directly over HTTP** with the Bash tool + `curl` — do **not**
register it as a native MCP server: the port is per-session and dynamic, so
ad-hoc HTTP calls are the right approach.

- **Endpoint:** `http://<host>:<PORT>/squirrel-sql-mcp`
  - `<host>` = `127.0.0.1` when SQuirreL runs on the same machine as you; otherwise the SQuirreL machine's name/IP.
  - `<PORT>` = the number the user gave you (different per session).
- The server is **stateless**, so you may call `tools/list` / `tools/call`
  directly; `initialize` is optional (only needed to read `serverInfo` / the
  protocol version).
- Every request needs an `id`. A request without one is a notification → `202`, no body.
- Discover before you call: run `tools/list` to see the available tools and their
  input schemas, then `tools/call`. Don't assume the tool set — it's generated
  from the server's interface and may grow.

## Calls (bash — replace `<PORT>`)

Discover tools:
```bash
curl -sS -X POST http://127.0.0.1:<PORT>/squirrel-sql-mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":1,"method":"tools/list"}'
```

Call `getTables`:
```bash
curl -sS -X POST http://127.0.0.1:<PORT>/squirrel-sql-mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":2,"method":"tools/call","params":{"name":"getTables","arguments":{"schemaPattern":"PUBLIC","tableNamePattern":"%","types":["TABLE","VIEW"]}}}'
```

Optional handshake / liveness:
```bash
curl -sS -X POST http://127.0.0.1:<PORT>/squirrel-sql-mcp -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":3,"method":"initialize","params":{"protocolVersion":"2025-06-18","capabilities":{},"clientInfo":{"name":"claude","version":"0"}}}'
curl -sS -X POST http://127.0.0.1:<PORT>/squirrel-sql-mcp -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":4,"method":"ping"}'
```

The `getTables` result arrives both as `structuredContent` (the typed `ResultSet`)
and as a JSON text block in `content`.

> **PowerShell note:** single quotes don't reliably protect the inner `"` when
> calling `curl.exe`. Either backslash-escape every inner quote, or (more robust,
> especially when arguments contain spaces) write the body to a UTF-8 **without
> BOM** file and send it with `--data-binary "@file"`. On bash/Git Bash the
> single-quoted form above works as-is.

## Tool: `getTables`

Mirrors `java.sql.DatabaseMetaData.getTables(...)`. All arguments optional:

| Argument           | Type     | Meaning                                           |
|--------------------|----------|---------------------------------------------------|
| `catalog`          | string   | Catalog name; omit/`null` = no catalog filter     |
| `schemaPattern`    | string   | Schema name pattern; `null` = no schema filter    |
| `tableNamePattern` | string   | Table name pattern; `null` = all tables           |
| `types`            | string[] | Table types e.g. `["TABLE","VIEW"]`; `null` = all |

Result: a `ResultSet` (`resultMetaData[]` describing columns + `rows[]` of `cells`).

> **Note — stub:** in the prototype `getTables` returns one synthetic row shaped
> like the standard JDBC columns and ignores the filter. In the SQuirreL
> integration it is backed by the Session's live JDBC connection.

## Session lifecycle

- The server runs only while the SQuirreL Session (and its JDBC connection) is open.
- Each session uses a **distinct port** — always use the one the user gave you for *that* session.
- After the session is closed the port stops answering, so a call simply fails
  with connection-refused. There is nothing to clean up on your side.

## Local prototype (dev/testing without SQuirreL)

Java is **not** on `PATH` here; use the JDK at `C:\openjdk-26+35\`. Jackson jars
are in `C:\Entwicklung\mcptest\lib\`. Compiled with `--release 17`.

Build:
```powershell
$files = Get-ChildItem -Recurse C:\Entwicklung\mcptest\src -Filter *.java |
         ForEach-Object { $_.FullName }
& 'C:\openjdk-26+35\bin\javac.exe' --release 17 `
    -cp 'C:\Entwicklung\mcptest\lib\*' `
    -d C:\Entwicklung\mcptest\bin\production\mcptest $files
```

Run the standalone server (default port `23367` from
[pack/SquirrelMcpConstants.java](pack/SquirrelMcpConstants.java)):
```powershell
& 'C:\openjdk-26+35\bin\java.exe' `
    -cp 'C:\Entwicklung\mcptest\bin\production\mcptest;C:\Entwicklung\mcptest\lib\*' `
    pack.SquirrelMcpHttpServer
```

Drive it with the Java client (`initialize` → `ping` → `tools/list` → `getTables`):
```powershell
& 'C:\openjdk-26+35\bin\java.exe' `
    -cp 'C:\Entwicklung\mcptest\bin\production\mcptest;C:\Entwicklung\mcptest\lib\*' `
    client.SquirrelMcpTestClient
```

## Limitations (by design)

- Minimal non-streaming transport: no SSE, no server-initiated notifications.
- Single-threaded executor: requests are handled strictly one at a time.
- Only `initialize`, `ping`, `tools/list`, `tools/call` are implemented; other
  methods return JSON-RPC `-32601` (method not found).
