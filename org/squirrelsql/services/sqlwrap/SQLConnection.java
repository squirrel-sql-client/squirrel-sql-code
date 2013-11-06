package org.squirrelsql.services.sqlwrap;

import org.squirrelsql.dialects.DialectFactory;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;
import org.squirrelsql.services.SQLUtil;
import org.squirrelsql.session.DBSchema;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLConnection
{
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

   public ArrayList<String> getCatalogs()
   {
      try
      {
         ArrayList<String> ret = new ArrayList<>();

         ResultSet catalogs = _con.getMetaData().getCatalogs();

         while(catalogs.next())
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

   public ArrayList<DBSchema> getSchemas()
   {
      try
      {
         boolean hasGuest = false;
         boolean hasSysFun = false;

         boolean isMSSQLorSYBASE = DialectFactory.isSyBase(_con) || DialectFactory.isMSSQLServer(_con);

         boolean isDB2 = DialectFactory.isDB2(_con);


         ArrayList<DBSchema> ret = new ArrayList<>();

         ResultSet schemas = _con.getMetaData().getSchemas();

         while(schemas.next())
         {
            DBSchema dbSchema = new DBSchema(schemas.getString("TABLE_SCHEM"), schemas.getString("TABLE_CATALOG"));
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
}
