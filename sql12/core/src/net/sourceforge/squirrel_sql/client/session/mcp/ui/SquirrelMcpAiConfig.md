# SQuirreL SQL — MCP access for AI assistants

This document tells an AI assistant how to talk to the MCP server built into the
[SQuirreL SQL client](https://squirrelsql.org). Through it you can read database
metadata (tables, columns, keys, indexes) and run SQL against the database
connection of one open **SQuirreL Session**.

## What it is

- Each SQuirreL **Session** (one open JDBC connection) can start its **own** MCP
  server on its **own** TCP port. The Session UI shows that port; the user gives
  it to you.
- Transport: **JSON-RPC 2.0 over HTTP POST** ("Streamable HTTP", non-streaming —
  every request gets a single `application/json` response).
- Endpoint: `http://<host>:<PORT>/squirrel-sql-mcp`
  - `<host>` = `127.0.0.1` when SQuirreL runs on the **same machine** as you
    (the usual case; the server may even be bound to loopback only). If SQuirreL
    runs elsewhere, use the machine name / IP the user gives you.
  - `<PORT>` = the number the user gave you (different for every session).

## How to call it

Talk to the endpoint **directly over HTTP** (e.g. with `curl`). You do **not**
need to register it as a native MCP server — the port is per-session and
dynamic, so ad-hoc HTTP calls are the right approach.

- The server is **stateless**: you may call `tools/list` and `tools/call`
  directly. `initialize` is optional (only to read `serverInfo` / protocol version).
- Every request needs an `"id"`. A request without one is treated as a
  notification and answered with HTTP `202` and no body.
- **Discover before you call:** run `tools/list` to get the current tools and
  their JSON input schemas, then `tools/call`. Treat `tools/list` as the source
  of truth — the tool set can grow, and each tool's exact argument fields are
  defined there.

### Argument convention

Each tool takes a **single JSON object** passed as `params.arguments`. Tools that
need no input take an empty object `{}`. The field names of `arguments` are given
by the tool's `inputSchema` from `tools/list`.

### Result convention

A `tools/call` response carries the result twice:
- `result.structuredContent` — the typed result object (use this).
- `result.content[0].text` — the same object serialized as JSON text.

Metadata/SQL tools return an **McpResultSet**:
`{ resultMetaData:[{column,columnName,sqlType,sqlTypeName}], rows:[{cells:[...]}],
rowsLimitedTo, errorMessage, updateMessage }`. When `errorMessage` is set the call
failed logically; when `updateMessage` is set the SQL was an update/DDL rather
than a query; `rowsLimitedTo` (if set) means the result was truncated to that many
rows. Simple string tools return `{ "stringContent": "..." }`.

## Available tools

Run `tools/list` for the authoritative list and schemas. At the time of writing:

| Tool | Arguments | Returns |
|------|-----------|---------|
| `getSessionName` | none (`{}`) | session name |
| `getDriverClassName` | none | JDBC driver class |
| `getDriverName` | none | JDBC driver name |
| `getDriverVersion` | none | driver version |
| `getDatabaseProductName` | none | database product name |
| `getDatabaseProductVersion` | none | database product version |
| `executeQuery` | `{ "stringContent": "<SQL>" }` | result set / update message |
| `getTables` | `catalog?, schemaPattern?, tableNamePattern?, types?[]` | tables |
| `getColumns` | `catalog?, schemaPattern?, tableNamePattern?` | columns |
| `getPrimaryKeys` | `catalog?, schema?, table` | primary keys |
| `getImportedKeys` | `catalog?, schema?, table` | imported (foreign) keys |
| `getExportedKeys` | `catalog?, schema?, table` | exported keys |
| `getIndexInfo` | `catalog?, schema?, table, unique, approximate` | indexes |

## Examples

Confirm the connection (prints the session name):
```bash
curl -sS -X POST http://127.0.0.1:<PORT>/squirrel-sql-mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":1,"method":"tools/call","params":{"name":"getSessionName","arguments":{}}}'
```

Discover the tools:
```bash
curl -sS -X POST http://127.0.0.1:<PORT>/squirrel-sql-mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":2,"method":"tools/list"}'
```

List tables in a schema:
```bash
curl -sS -X POST http://127.0.0.1:<PORT>/squirrel-sql-mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"getTables","arguments":{"schemaPattern":"PUBLIC","tableNamePattern":"%","types":["TABLE","VIEW"]}}}'
```

Run a query:
```bash
curl -sS -X POST http://127.0.0.1:<PORT>/squirrel-sql-mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":4,"method":"tools/call","params":{"name":"executeQuery","arguments":{"stringContent":"SELECT * FROM CUSTOMERS ORDER BY ID"}}}'
```

## Good behaviour

- **The user controls access through SQuirreL.** Calls may require the user to
  approve them in a SQuirreL dialog, and SQL may be restricted to read-only. If a
  call is declined or blocked you will get an error / `errorMessage` — report it
  and do not try to circumvent it.
- The server serves **one call at a time**; issue calls sequentially.
- The server lives only while the SQuirreL Session is open. Once the user closes
  the session the port stops answering and calls fail with connection-refused —
  ask the user to restart the MCP server (and for the new port) if that happens.
- Prefer the metadata tools (`getTables`, `getColumns`, …) to inspect the schema
  before writing SQL, and quote identifiers as the target database requires.
