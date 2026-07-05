package net.sourceforge.squirrel_sql.client.session.mcp.server;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetExportedKeysArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetImportedKeysArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetIndexInfoArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetPrimaryKeysArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetTablesArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpNoArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultCell;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultMetaData;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultRow;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultSet;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpSimpleString;
import net.sourceforge.squirrel_sql.client.session.mcp.ui.McpServerContext;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IndexInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

/**
 * Default implementation of {@link SquirrelMcpTools}. Stateless and thread-safe.
 * <p>
 * NOTE: {@link #getTables} is currently a <b>stub</b> that returns a single
 * synthetic row echoing the requested filter, shaped like the standard JDBC
 * {@code getTables} columns. Wire it to a real
 * {@link java.sql.DatabaseMetaData#getTables(String, String, String, String[])}
 * call once a database connection is available.
 */
public final class SquirrelMcpToolsImpl implements SquirrelMcpTools
{
   private final McpServerContext _mcpServerContext;

   public SquirrelMcpToolsImpl(McpServerContext mcpServerContext)
   {
      _mcpServerContext = mcpServerContext;
   }

   @Override
   public McpSimpleString getSessionName(McpNoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.session().getTitle()), true);
   }

   @Override
   public McpSimpleString getDriverClassName(McpNoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.session().getJdbcData().getDriverClassName()), true);
   }

   @Override
   public McpSimpleString getDriverName(McpNoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.getDriverName()), true);
   }

   @Override
   public McpSimpleString getDriverVersion(McpNoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.getDriverVersion()), true);
   }

   @Override
   public McpSimpleString getDatabaseProductName(McpNoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.getDatabaseProductName()), true);
   }

   @Override
   public McpSimpleString getDatabaseProductVersion(McpNoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.getDatabaseProductVersion()), true);
   }

   @Override
   public McpResultSet executeQuery(McpSimpleString sql)
   {
      return McpQueryExecuter.executeQuery(sql, _mcpServerContext);
   }


   @Override
   public McpResultSet getTables(McpGetTablesArgs args)
   {
      try
      {
         // Citation from java.sql.DatabaseMetaData.getTables: "as it is stored in the database"
         String catalogName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveCatalogName(args.catalog());
         String schemaName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveSchemaName(args.schemaPattern());
         String tableName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveTableName(args.tableNamePattern());

         ITableInfo[] tables = _mcpServerContext.session().getMetaData().getTables(catalogName, schemaName, tableName, args.types(), null);

         List<McpResultMetaData> metaData = List.of(
               new McpResultMetaData(1, "TABLE_CAT", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(2, "TABLE_SCHEM", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(3, "TABLE_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(4, "TABLE_TYPE", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(5, "REMARKS", Types.VARCHAR, "VARCHAR"));


         List<McpResultRow> tablesRes = new ArrayList<>();
         for(ITableInfo table : tables)
         {
            McpResultRow tableRow = new McpResultRow(List.of(
                  McpResultCell.ofString(table.getCatalogName()),
                  McpResultCell.ofString(table.getSchemaName()),
                  McpResultCell.ofString(table.getSimpleName()),
                  McpResultCell.ofString(table.getType()),
                  McpResultCell.ofString(table.getRemarks())));

            tablesRes.add(tableRow);
         }

         return McpResultSet.ofResult(metaData, tablesRes);
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpResultSet getPrimaryKeys(McpGetPrimaryKeysArgs args)
   {
      try
      {
         // Citation from java.sql.DatabaseMetaData.getPrimaryKeys: "as it is stored in the database"
         String catalogName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveCatalogName(args.catalog());
         String schemaName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveSchemaName(args.schema());
         String tableName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveTableName(args.table());

         List<McpResultMetaData> metaData = List.of(
               new McpResultMetaData(1, "TABLE_CAT", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(2, "TABLE_SCHEM", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(3, "TABLE_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(4, "COLUMN_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(5, "KEY_SEQ", Types.VARCHAR, "INTEGER"),
               new McpResultMetaData(6, "PK_NAME", Types.VARCHAR, "VARCHAR"));

         PrimaryKeyInfo[] primaryKeyInfos = _mcpServerContext.session().getMetaData().getPrimaryKey(catalogName, schemaName, tableName);

         List<McpResultRow> primaryKeyRes = new ArrayList<>();
         for(PrimaryKeyInfo primaryKeyInfo : primaryKeyInfos)
         {
            McpResultRow primaryKeyInfoRow = new McpResultRow(List.of(
                  McpResultCell.ofString(primaryKeyInfo.getCatalogName()),
                  McpResultCell.ofString(primaryKeyInfo.getSchemaName()),
                  McpResultCell.ofString(primaryKeyInfo.getTableName()),
                  McpResultCell.ofString(primaryKeyInfo.getColumnName()),
                  McpResultCell.ofInt((int) primaryKeyInfo.getKeySequence()),
                  McpResultCell.ofString(primaryKeyInfo.getSimpleName())
            ));

            primaryKeyRes.add(primaryKeyInfoRow);
         }

         return McpResultSet.ofResult(metaData, primaryKeyRes);
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpResultSet getImportedKeys(McpGetImportedKeysArgs args)
   {
      try
      {
         // Citation from java.sql.DatabaseMetaData.getImportedKeys: "as it is stored in the database"
         String catalogName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveCatalogName(args.catalog());
         String schemaName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveSchemaName(args.schema());
         String tableName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveTableName(args.table());

         ForeignKeyInfo[] foreignKeyInfos = _mcpServerContext.session().getMetaData().getImportedKeysInfo(catalogName, schemaName, tableName);

         List<McpResultMetaData> metaData = List.of(
               new McpResultMetaData(1, "PKTABLE_CAT", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(2, "PKTABLE_SCHEM", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(3, "PKTABLE_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(4, "PKCOLUMN_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(5, "FKTABLE_CAT", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(6, "FKTABLE_SCHEM", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(7, "FKTABLE_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(8, "FKCOLUMN_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(10, "FK_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(11, "PK_NAME", Types.VARCHAR, "VARCHAR"));

         List<McpResultRow> foreignKeyInfoRes = new ArrayList<>();
         for(ForeignKeyInfo foreignKeyInfo : foreignKeyInfos)
         {
            for(ForeignKeyColumnInfo foreignKeyColumnInfo : foreignKeyInfo.getForeignKeyColumnInfo())
            {
               McpResultRow foreignKeyInfoRow = new McpResultRow(List.of(
                     McpResultCell.ofString(foreignKeyInfo.getPrimaryKeyCatalogName()),
                     McpResultCell.ofString(foreignKeyInfo.getPrimaryKeySchemaName()),
                     McpResultCell.ofString(foreignKeyInfo.getPrimaryKeyTableName()),
                     McpResultCell.ofString(foreignKeyColumnInfo.getPrimaryKeyColumnName()),
                     McpResultCell.ofString(foreignKeyInfo.getForeignKeyCatalogName()),
                     McpResultCell.ofString(foreignKeyInfo.getForeignKeySchemaName()),
                     McpResultCell.ofString(foreignKeyInfo.getForeignKeyTableName()),
                     McpResultCell.ofString(foreignKeyColumnInfo.getForeignKeyColumnName()),
                     McpResultCell.ofString(foreignKeyInfo.getForeignKeyName()),
                     McpResultCell.ofString(foreignKeyInfo.getPrimaryKeyName())
               ));


               foreignKeyInfoRes.add(foreignKeyInfoRow);
            }
         }

         return McpResultSet.ofResult(metaData, foreignKeyInfoRes);
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpResultSet getExportedKeys(McpGetExportedKeysArgs args)
   {
      try
      {
         // Citation from java.sql.DatabaseMetaData.getExportedKeys: "as it is stored in the database"
         String catalogName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveCatalogName(args.catalog());
         String schemaName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveSchemaName(args.schema());
         String tableName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveTableName(args.table());

         ForeignKeyInfo[] foreignKeyInfos = _mcpServerContext.session().getMetaData().getExportedKeysInfo(catalogName, schemaName, tableName);

         List<McpResultMetaData> metaData = List.of(
               new McpResultMetaData(1, "PKTABLE_CAT", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(2, "PKTABLE_SCHEM", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(3, "PKTABLE_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(4, "PKCOLUMN_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(5, "FKTABLE_CAT", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(6, "FKTABLE_SCHEM", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(7, "FKTABLE_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(8, "FKCOLUMN_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(10, "FK_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(11, "PK_NAME", Types.VARCHAR, "VARCHAR"));

         List<McpResultRow> foreignKeyInfoRes = new ArrayList<>();

         for(ForeignKeyInfo foreignKeyInfo : foreignKeyInfos)
         {
            for(ForeignKeyColumnInfo foreignKeyColumnInfo : foreignKeyInfo.getForeignKeyColumnInfo())
            {
               McpResultRow foreignKeyInfoRow = new McpResultRow(List.of(
                     McpResultCell.ofString(foreignKeyInfo.getPrimaryKeyCatalogName()),
                     McpResultCell.ofString(foreignKeyInfo.getPrimaryKeySchemaName()),
                     McpResultCell.ofString(foreignKeyInfo.getPrimaryKeyTableName()),
                     McpResultCell.ofString(foreignKeyColumnInfo.getPrimaryKeyColumnName()),
                     McpResultCell.ofString(foreignKeyInfo.getForeignKeyCatalogName()),
                     McpResultCell.ofString(foreignKeyInfo.getForeignKeySchemaName()),
                     McpResultCell.ofString(foreignKeyInfo.getForeignKeyTableName()),
                     McpResultCell.ofString(foreignKeyColumnInfo.getForeignKeyColumnName()),
                     McpResultCell.ofString(foreignKeyInfo.getForeignKeyName()),
                     McpResultCell.ofString(foreignKeyInfo.getPrimaryKeyName())
               ));

               foreignKeyInfoRes.add(foreignKeyInfoRow);
            }
         }

         return McpResultSet.ofResult(metaData, foreignKeyInfoRes);
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpResultSet getIndexInfo(McpGetIndexInfoArgs args)
   {
      try
      {
         // Citation from java.sql.DatabaseMetaData.getImportedKeys: "as it is stored in the database"
         String catalogName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveCatalogName(args.catalog());
         String schemaName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveSchemaName(args.schema());
         String tableName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveTableName(args.table());

         List<IndexInfo> indexInfo = _mcpServerContext.session().getMetaData().getIndexInfo(catalogName, schemaName, tableName);

         List<McpResultMetaData> metaData = List.of(
               new McpResultMetaData(1, "TABLE_CAT", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(2, "TABLE_SCHEM", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(3, "TABLE_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(4, "NON_UNIQUE", Types.VARCHAR, "BOOLEAN"),
               new McpResultMetaData(5, "INDEX_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(6, "ORDINAL_POSITION", Types.VARCHAR, "INTEGER"),
               new McpResultMetaData(7, "COLUMN_NAME", Types.VARCHAR, "VARCHAR"));

         List<McpResultRow> indexInfoRes = new ArrayList<>();
         for(IndexInfo ixInfo : indexInfo)
         {
            McpResultRow ixInfoRow = new McpResultRow(List.of(
                  McpResultCell.ofString(ixInfo.getCatalogName()),
                  McpResultCell.ofString(ixInfo.getSchemaName()),
                  McpResultCell.ofString(ixInfo.getTableName()),
                  McpResultCell.ofBool(ixInfo.isNonUnique()),
                  McpResultCell.ofString(ixInfo.getSimpleName()),
                  McpResultCell.ofInt((int) ixInfo.getOrdinalPosition()),
                  McpResultCell.ofString(ixInfo.getColumnName())
            ));
            indexInfoRes.add(ixInfoRow);
         }

         return McpResultSet.ofResult(metaData, indexInfoRes);
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
