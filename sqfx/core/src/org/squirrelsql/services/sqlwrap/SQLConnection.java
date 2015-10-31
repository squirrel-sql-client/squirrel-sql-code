package org.squirrelsql.services.sqlwrap;

import org.squirrelsql.AppState;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.dialects.DialectFactory;
import org.squirrelsql.services.*;
import org.squirrelsql.session.DBSchema;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.UDTInfo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SQLConnection
{
   String AS400_DRIVER_NAME = "AS/400 Toolbox for Java JDBC Driver";

   String FREE_TDS_DRIVER_NAME = "InternetCDS Type 4 JDBC driver for MS SQLServer";


   private MessageHandler _mhLog = new MessageHandler(this.getClass(), MessageHandlerDestination.MESSAGE_LOG);

   private Connection _con;


   public SQLConnection(Connection con)
   {
      _con = con;
   }

   public void close()
   {
      SQLUtil.close(_con);
   }

   public List<String> getCatalogs()
   {
      try
      {
         List<String> ret = new ArrayList<>();

         ResultSet catalogs = _con.getMetaData().getCatalogs();

         while (catalogs.next())
         {
            ret.add(catalogs.getString("TABLE_CAT"));
         }

         SQLUtil.close(catalogs);

         return ret;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public boolean supportsCatalogs()
   {
      try
      {
         DatabaseMetaData metaData = _con.getMetaData();

         return metaData.supportsCatalogsInTableDefinitions() || metaData.supportsCatalogsInDataManipulation()
               || metaData.supportsCatalogsInProcedureCalls();
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public List<DBSchema> getSchemas()
   {
      try
      {
         boolean hasGuest = false;
         boolean hasSysFun = false;

         boolean isMSSQLorSYBASE = DialectFactory.isSyBase(_con) || DialectFactory.isMSSQLServer(_con);

         boolean isDB2 = DialectFactory.isDB2(_con);


         List<DBSchema> ret = new ArrayList<>();

         ResultSet schemas = _con.getMetaData().getSchemas();
         String schemaCatalog = null;

         while (schemas.next())
         {
        	//Some Oracle versions error out since Oracle doesn't support catalogs, thus can't connect to DB
        	if(supportsCatalogs())
         {
        		schemaCatalog = schemas.getString("TABLE_CATALOG");

            if(null == schemaCatalog)
            {
               // Without this tables are missing on MSSQL
               schemaCatalog = _con.getCatalog();
            }

        	}
            DBSchema dbSchema = new DBSchema(schemas.getString("TABLE_SCHEM"), schemaCatalog);
            ret.add(dbSchema);

            if (isMSSQLorSYBASE && "guest".equals(dbSchema.getSchema()))
            {
               hasGuest = true;
            }
            if (isDB2 && "SYSFUN".equals(dbSchema.getSchema()))
            {
               hasSysFun = true;
            }

         }

         SQLUtil.close(schemas);


         // Some drivers for both MS SQL and Sybase don't return guest as
         // a schema name.
         if (isMSSQLorSYBASE && !hasGuest)
         {
            ret.add(new DBSchema("guest", null));
         }

         // Some drivers for DB2 don't return SYSFUN as a schema name. A
         // number of system stored procs are kept in this schema.
         if (isDB2 && !hasSysFun)
         {
            ret.add(new DBSchema("SYSFUN", null));
         }


         return ret;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public boolean supportsSchemas() throws SQLException
   {
      return supportsSchemasInDataManipulation() || supportsSchemasInTableDefinitions();
   }

   public synchronized boolean supportsSchemasInDataManipulation() throws SQLException
   {
      boolean ret = false;
      try
      {
         ret = _con.getMetaData().supportsSchemasInDataManipulation();
      }
      catch (SQLException ex)
      {
         boolean isSQLServer = DialectFactory.isSyBase(_con) || DialectFactory.isMSSQLServer(_con);

         if (isSQLServer)
         {
            ret = true;
         }
         throw ex;
      }


      return ret;
   }

   public synchronized boolean supportsSchemasInTableDefinitions() throws SQLException
   {
      boolean ret = false;
      try
      {
         ret = _con.getMetaData().supportsSchemasInTableDefinitions();
      }
      catch (SQLException ex)
      {
         boolean isSQLServer = DialectFactory.isSyBase(_con) || DialectFactory.isMSSQLServer(_con);

         if (isSQLServer)
         {
            ret = true;
         }
         throw ex;
      }


      return ret;
   }

   public List<String> getTableTypes()
   {
      try
      {
         DatabaseMetaData metaData = _con.getMetaData();
         ResultSet tableTypes = metaData.getTableTypes();

         List<String> ret = new ArrayList<>();

         while (tableTypes.next())
         {
            ret.add(tableTypes.getString(1));
         }

         return ret;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public boolean supportsStoredProcedures()
   {
      try
      {
         DatabaseMetaData metaData = _con.getMetaData();

         return metaData.supportsStoredProcedures();
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public List<TableInfo> getTableInfos(String catalog, String schema, String tableType, String tableName)
   {
      try
      {
         final String dbDriverName = getDriverName();

         if (dbDriverName.equals(FREE_TDS_DRIVER_NAME) && schema == null)
         {
            schema = "dbo";
         }
         if (dbDriverName.equals(AS400_DRIVER_NAME) && schema == null)
         {
            schema = "*ALLUSR";
         }


         List<TableInfo> ret = new ArrayList<>();

         ResultSet tables;
         if (null == tableType)
         {
            tables = _con.getMetaData().getTables(catalog, schema, tableName, null);
         }
         else
         {
            tables = _con.getMetaData().getTables(catalog, schema, tableName, new String[]{tableType});
         }

         while (tables.next())
         {
            String table_cat = tables.getString("TABLE_CAT");
            String table_schem = tables.getString("TABLE_SCHEM");
            String table_name = tables.getString("TABLE_NAME");

            String qualifiedName = SQLUtil.generateQualifiedName(_con, table_cat, table_schem, table_name, DatabaseObjectType.TABLE);

            TableInfo t = new TableInfo(table_cat, table_schem, tables.getString("TABLE_TYPE"), table_name, qualifiedName);
            ret.add(t);
         }

         SQLUtil.close(tables);

         return ret;
      }
      catch (SQLException e)
      {
         // MSSQL used to break here when access rights where missing. That's why we log instead of throw.
         String msg = "Failed to load tables for: Catalog=" + catalog + "; Schema=" + schema + "; TableType=" + tableType;
         new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_LOG).warning(msg, e);
         return Collections.emptyList();
      }
   }

   public List<ProcedureInfo> getProcedureInfos(String catalog, String schema, String procedureName)
   {
      try
      {
         List<ProcedureInfo> list = new ArrayList<>();
         ResultSet procedures = _con.getMetaData().getProcedures(catalog, schema, procedureName);

         while (procedures.next())
         {
            // Sybase IQ using jdbc3 driver returns null for some procedure return types - this is probably
            // outside the JDBC spec.
            // The safest solution seems to be to set it to Unknown result type.
            int procedureType = DatabaseMetaData.procedureResultUnknown;

            if (null != procedures.getString("PROCEDURE_TYPE"))
            {
               procedureType = procedures.getInt("PROCEDURE_TYPE");
            }

            ProcedureInfo procedureInfo =
                  new ProcedureInfo(procedures.getString("PROCEDURE_CAT"), procedures.getString("PROCEDURE_SCHEM"), procedures.getString("PROCEDURE_NAME"), procedureType);

            list.add(procedureInfo);
         }

         return list;
      }
      catch (SQLException e)
      {
         // MSSQL used to break here when access rights where missing. That's why we log instead of throw.
         String msg = "Failed to load procedures for criteria: Catalog=" + catalog + "; Schema=" + schema;
         new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_LOG).warning(msg, e);
         return Collections.emptyList();
      }
   }

   public List<UDTInfo> getUDTInfos(String catalog, String schema, String udtName)
   {
      try
      {
         List<UDTInfo> list = new ArrayList<>();
         ResultSet udts = _con.getMetaData().getUDTs(catalog, schema, udtName, null);

         while (udts.next())
         {

            UDTInfo udtInfo =
                  new UDTInfo(udts.getString("TYPE_CAT"), udts.getString("TYPE_SCHEM"), udts.getString("TYPE_NAME"), udts.getInt("DATA_TYPE"));

            list.add(udtInfo);
         }

         return list;
      }
      catch (SQLException e)
      {
         // MSSQL used to break here when access rights where missing. That's why we log instead of throw.
         String msg = "Failed to load UDTs for: Catalog=" + catalog + "; Schema=" + schema;
         new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_LOG).warning(msg, e);
         return Collections.emptyList();
      }
   }


   public synchronized String getDriverName() throws SQLException
   {
      return _con.getMetaData().getDriverName();
   }

   public DatabaseMetaData getDatabaseMetaData()
   {
      try
      {
         return _con.getMetaData();
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }

   }


   public Connection getConnection()
   {
      return _con;
   }

   public void reconnect(DbConnectorResult dbConnectorResult, boolean autoCommit)
   {
      try
      {
         Connection oldCon = _con;

         _con = JDBCUtil.createJDBCConnection(dbConnectorResult.getAliasDecorator().getAlias(), dbConnectorResult.getUser(), dbConnectorResult.getPassword());


         _con.setAutoCommit(autoCommit);


         Future<?> submit = Executors.newSingleThreadExecutor().submit(() -> SQLUtil.close(oldCon));

         I18n i18n = new I18n(getClass());


         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), i18n.t("reconnect.done"));


         MessageHandler mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);

         try
         {
            submit.get(2000, TimeUnit.MILLISECONDS);
         }
         catch (TimeoutException e)
         {
            mh.warning(i18n.t("closing.old.connection.after.reconnect.timeout"));
         }
         catch (Exception e)
         {
            mh.warning(i18n.t("closing.old.connection.after.reconnect.failed", e));
         }
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }
}
