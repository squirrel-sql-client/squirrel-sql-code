package net.sourceforge.squirrel_sql.client.session.mcp.server;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetColumnsArgs;
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
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLSchema;
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
@SuppressWarnings("PointlessBooleanExpression")
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
      McpCall call = McpCall.getSessionName;
      try
      {

         if( false == _mcpServerContext.callStart(call, none))
         {
            return call.createDisapprovedMsg();
         }
         McpSimpleString ret = GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.getSession().getTitle()), true);
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, none, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpSimpleString getDriverClassName(McpNoArgs none)
   {
      McpCall call = McpCall.getDriverClassName;
      try
      {

         if( false == _mcpServerContext.callStart(call, none))
         {
            return new McpSimpleString(McpCall.DISAPPROVED);
         }

         McpSimpleString ret = GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.getSession().getJdbcData().getDriverClassName()), true);
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, none, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpSimpleString getDriverName(McpNoArgs none)
   {
      McpCall call = McpCall.getDriverName;
      try
      {

         if( false == _mcpServerContext.callStart(call, none))
         {
            return new McpSimpleString(McpCall.DISAPPROVED);
         }

         McpSimpleString ret = GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.getDriverName()), true);
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, none, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpSimpleString getDriverVersion(McpNoArgs none)
   {
      McpCall call = McpCall.getDriverVersion;
      try
      {

         if( false == _mcpServerContext.callStart(call, none))
         {
            return new McpSimpleString(McpCall.DISAPPROVED);
         }

         McpSimpleString ret = GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.getDriverVersion()), true);
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, none, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpSimpleString getDatabaseProductName(McpNoArgs none)
   {
      McpCall call = McpCall.getDatabaseProductName;
      try
      {

         if( false == _mcpServerContext.callStart(call, none))
         {
            return new McpSimpleString(McpCall.DISAPPROVED);
         }

         McpSimpleString ret = GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.getDatabaseProductName()), true);
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, none, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpSimpleString getDatabaseProductVersion(McpNoArgs none)
   {
      McpCall call = McpCall.getDatabaseProductVersion;
      try
      {
         if( false == _mcpServerContext.callStart(call, none))
         {
            return new McpSimpleString(McpCall.DISAPPROVED);
         }

         McpSimpleString ret = GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.getDatabaseProductVersion()), true);
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, none, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpResultSet executeQuery(McpSimpleString sql)
   {
      McpCall call = McpCall.executeQuery;
      try
      {

         if(false == _mcpServerContext.callStart(call, sql))
         {
            return McpResultSet.ofError(McpCall.DISAPPROVED);
         }

         McpResultSet ret = McpQueryExecuter.executeQuery(sql, _mcpServerContext);
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, sql, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpResultSet getCatalogs(McpNoArgs none)
   {
      McpCall call = McpCall.getCatalogs;
      try
      {

         if( false == _mcpServerContext.callStart(call, none))
         {
            return McpResultSet.ofError(McpCall.DISAPPROVED);
         }

         McpResultSet ret = _getCatalogs();
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, none, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpResultSet getSchemas(McpNoArgs none)
   {
      McpCall call = McpCall.getSchemas;
      try
      {

         if( false == _mcpServerContext.callStart(call, none))
         {
            return McpResultSet.ofError(McpCall.DISAPPROVED);
         }

         McpResultSet ret = _getSchemas();
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, none, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpSimpleString getCurrentSchema(McpNoArgs none)
   {
      McpCall call = McpCall.getCurrentSchema;
      try
      {

         if( false == _mcpServerContext.callStart(call, none))
         {
            return new McpSimpleString(McpCall.DISAPPROVED);
         }

         McpSimpleString ret = _mcpServerContext.getCurrentSchema();
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, none, e);
         throw Utilities.wrapRuntime(e);
      }
   }


   @Override
   public McpResultSet getTables(McpGetTablesArgs args)
   {
      McpCall call = McpCall.getTables;
      try
      {

         if( false == _mcpServerContext.callStart(call, args))
         {
            return McpResultSet.ofError(McpCall.DISAPPROVED);
         }

         McpResultSet ret = _getTables(args);
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, args, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpResultSet getPrimaryKeys(McpGetPrimaryKeysArgs args)
   {
      McpCall call = McpCall.getPrimaryKeys;
      try
      {

         if( false == _mcpServerContext.callStart(call, args))
         {
            return McpResultSet.ofError(McpCall.DISAPPROVED);
         }

         McpResultSet ret = _getPrimaryKeys(args);
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, args, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpResultSet getImportedKeys(McpGetImportedKeysArgs args)
   {
      McpCall call = McpCall.getImportedKeys;
      try
      {

         if( false == _mcpServerContext.callStart(call, args))
         {
            return McpResultSet.ofError(McpCall.DISAPPROVED);
         }

         McpResultSet ret = _getImportedKeys(args);
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, args, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpResultSet getExportedKeys(McpGetExportedKeysArgs args)
   {
      McpCall call = McpCall.getExportedKeys;
      try
      {

         if( false == _mcpServerContext.callStart(call, args))
         {
            return call.createDisapprovedMsg();
         }

         McpResultSet ret = _getExportedKeys(args);
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, args, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override

   public McpResultSet getIndexInfo(McpGetIndexInfoArgs args)
   {
      McpCall call = McpCall.getIndexInfo;
      try
      {
         if( false == _mcpServerContext.callStart(call, args))
         {
            return McpResultSet.ofError(McpCall.DISAPPROVED);
         }

         McpResultSet ret = _getIndexInfo(args);
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, args, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpResultSet getColumns(McpGetColumnsArgs args)
   {
      McpCall call = McpCall.getColumns;
      try
      {
         if( false == _mcpServerContext.callStart(call, args))
         {
            return McpResultSet.ofError(McpCall.DISAPPROVED);
         }

         McpResultSet ret = _getColumns(args);
         _mcpServerContext.callFinished(call);

         return ret;
      }
      catch(Exception e)
      {
         _mcpServerContext.callFailed(call, args, e);
         throw Utilities.wrapRuntime(e);
      }
   }

   public McpResultSet _getCatalogs()
   {
      try
      {
         String[] catalogs = _mcpServerContext.getSession().getSQLConnection().getSQLMetaData().getCatalogs();

         List<McpResultMetaData> metaData = List.of(
               new McpResultMetaData(1, "TABLE_CAT", Types.VARCHAR, "VARCHAR"));

         List<McpResultRow> catalogRes = new ArrayList<>();
         for( String catalog : catalogs )
         {
            McpResultRow tableRow = new McpResultRow(List.of(McpResultCell.ofString(catalog)));
            catalogRes.add(tableRow);
         }

         return McpResultSet.ofResult(metaData, catalogRes);
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public McpResultSet _getSchemas()
   {
      try
      {
         List<SQLSchema> schemas = _mcpServerContext.getSession().getSQLConnection().getSQLMetaData().getSchemas();

         List<McpResultMetaData> metaData = List.of(
               new McpResultMetaData(1, "TABLE_CAT", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(2, "TABLE_SCHEM", Types.VARCHAR, "VARCHAR"));

         List<McpResultRow> schemaRes = new ArrayList<>();

         for( SQLSchema schema : schemas )
         {
            McpResultRow tableRow = new McpResultRow(List.of(
                  McpResultCell.ofString(schema.getCatalog()),
                  McpResultCell.ofString(schema.getSchema())));
            schemaRes.add(tableRow);
         }

         return McpResultSet.ofResult(metaData, schemaRes);
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }


   private McpResultSet _getTables(McpGetTablesArgs args)
   {
      try
      {
         // Citation from java.sql.DatabaseMetaData.getTables: "as it is stored in the database"
         String catalogName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveCatalogName(args.catalog());
         String schemaName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveSchemaName(args.schemaPattern());
         String tableName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveTableName(args.tableNamePattern());

         ITableInfo[] tables = _mcpServerContext.getSession().getMetaData().getTables(catalogName, schemaName, tableName, args.types(), null);

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

   private McpResultSet _getPrimaryKeys(McpGetPrimaryKeysArgs args)
   {
      try
      {
         // Citation from java.sql.DatabaseMetaData.getPrimaryKeys: "as it is stored in the database"
         String catalogName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveCatalogName(args.catalog());
         String schemaName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveSchemaName(args.schema());
         String tableName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveTableName(args.table());

         List<McpResultMetaData> metaData = List.of(
               new McpResultMetaData(1, "TABLE_CAT", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(2, "TABLE_SCHEM", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(3, "TABLE_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(4, "COLUMN_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(5, "KEY_SEQ", Types.INTEGER, "INTEGER"),
               new McpResultMetaData(6, "PK_NAME", Types.VARCHAR, "VARCHAR"));

         PrimaryKeyInfo[] primaryKeyInfos = _mcpServerContext.getSession().getMetaData().getPrimaryKey(catalogName, schemaName, tableName);

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

   private McpResultSet _getImportedKeys(McpGetImportedKeysArgs args)
   {
      try
      {
         // Citation from java.sql.DatabaseMetaData.getImportedKeys: "as it is stored in the database"
         String catalogName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveCatalogName(args.catalog());
         String schemaName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveSchemaName(args.schema());
         String tableName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveTableName(args.table());

         ForeignKeyInfo[] foreignKeyInfos = _mcpServerContext.getSession().getMetaData().getImportedKeysInfo(catalogName, schemaName, tableName);

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

   private McpResultSet _getExportedKeys(McpGetExportedKeysArgs args)
   {
      try
      {
         // Citation from java.sql.DatabaseMetaData.getExportedKeys: "as it is stored in the database"
         String catalogName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveCatalogName(args.catalog());
         String schemaName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveSchemaName(args.schema());
         String tableName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveTableName(args.table());

         ForeignKeyInfo[] foreignKeyInfos = _mcpServerContext.getSession().getMetaData().getExportedKeysInfo(catalogName, schemaName, tableName);

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

   private McpResultSet _getIndexInfo(McpGetIndexInfoArgs args)
   {
      try
      {
         // Citation from java.sql.DatabaseMetaData.getImportedKeys: "as it is stored in the database"
         String catalogName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveCatalogName(args.catalog());
         String schemaName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveSchemaName(args.schema());
         String tableName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveTableName(args.table());

         List<IndexInfo> indexInfo = _mcpServerContext.getSession().getMetaData().getIndexInfo(catalogName, schemaName, tableName);

         List<McpResultMetaData> metaData = List.of(
               new McpResultMetaData(1, "TABLE_CAT", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(2, "TABLE_SCHEM", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(3, "TABLE_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(4, "NON_UNIQUE", Types.BOOLEAN, "BOOLEAN"),
               new McpResultMetaData(5, "INDEX_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(6, "ORDINAL_POSITION", Types.INTEGER, "INTEGER"),
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

   private McpResultSet _getColumns(McpGetColumnsArgs args)
   {
      try
      {
         // Citation from java.sql.DatabaseMetaData.getImportedKeys: "as it is stored in the database"
         String catalogName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveCatalogName(args.catalog());
         String schemaName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveSchemaName(args.schema());
         String tableName = _mcpServerContext.getSession().getSchemaInfo().getCaseSensitiveTableName(args.table());

         TableColumnInfo[] columnInfo = _mcpServerContext.getSession().getMetaData().getColumnInfo(catalogName, schemaName, tableName);

         List<McpResultMetaData> metaData = List.of(
               new McpResultMetaData(1, "TABLE_CAT", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(2, "TABLE_SCHEM", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(3, "TABLE_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(4, "COLUMN_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(5, "REMARKS", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(6, "DATA_TYPE", Types.INTEGER, "INTEGER"),
               new McpResultMetaData(7, "TYPE_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(8, "DECIMAL_DIGITS", Types.INTEGER, "INTEGER"),
               new McpResultMetaData(9, "COLUMN_SIZE", Types.INTEGER, "INTEGER"),
               new McpResultMetaData(10, "ORDINAL_POSITION", Types.INTEGER, "INTEGER"),
               new McpResultMetaData(11, "NULLABLE", Types.INTEGER, "INTEGER"),
               new McpResultMetaData(12, "IS_AUTOINCREMENT", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(13, "COLUMN_DEF", Types.VARCHAR, "VARCHAR")
         );

         List<McpResultRow> columnsRes = new ArrayList<>();
         for( TableColumnInfo ci : columnInfo )
         {
            McpResultRow columnRow = new McpResultRow(List.of(
                  McpResultCell.ofString(ci.getCatalogName()),
                  McpResultCell.ofString(ci.getSchemaName()),
                  McpResultCell.ofString(ci.getTableName()),
                  McpResultCell.ofString(ci.getColumnName()),
                  McpResultCell.ofString(ci.getRemarks()),
                  McpResultCell.ofInt(ci.getDataType()),
                  McpResultCell.ofString(ci.getTypeName()),
                  McpResultCell.ofInt(ci.getDecimalDigits()),
                  McpResultCell.ofInt(ci.getColumnSize()),
                  McpResultCell.ofInt(ci.getOrdinalPosition()),
                  McpResultCell.ofInt(ci.isNullAllowed()),
                  McpResultCell.ofString(ci.isAutoIncrement()),
                  McpResultCell.ofString(ci.getDefaultValue())
            ));
            columnsRes.add(columnRow);
         }

         return McpResultSet.ofResult(metaData, columnsRes);
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

}
