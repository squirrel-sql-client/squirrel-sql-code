package net.sourceforge.squirrel_sql.plugins.postgres;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PostgresSQLDatabaseMetaData extends SQLDatabaseMetaData
{
   private final static ILogger s_log = LoggerController.createLogger(PostgresSQLDatabaseMetaData.class);

   private final ISQLConnection _conn;
   private Map<String, Boolean> oidSupportForTable;

   public PostgresSQLDatabaseMetaData(ISQLConnection conn)
   {
      super(conn);
      _conn = conn;
      oidSupportForTable = new HashMap<String, Boolean>();
   }

   @Override
   public String getOptionalPseudoColumnForDataSelection(final ITableInfo ti)
   {
      Boolean supports = oidSupportForTable.get(ti.getQualifiedName());
      if(supports == null)
      {
         supports = false;
         try
         {
            ResultSet rs = _conn.createStatement().executeQuery(
                  "SELECT TRUE FROM   pg_attribute " +
                  "WHERE  attrelid = '" + ti.getQualifiedName() + "'::regclass " +
                  "AND    attname = 'oid' AND NOT attisdropped"
                                                               );
            if(rs.next())
            {
               supports = true;
            }
         }
         catch (SQLException sqlE)
         {
            s_log.error("During oid existance checking", sqlE);
         }
         oidSupportForTable.put(ti.getQualifiedName(), supports);
      }
      return supports ? "oid" : null;
   }

   @Override
   public synchronized String[] getDataTypesSimpleNames() throws SQLException
   {
      String sql = "SELECT t.typname FROM pg_catalog.pg_type t"
                   + " JOIN pg_catalog.pg_namespace n ON (t.typnamespace = n.oid) " + " WHERE n.nspname != 'pg_toast' "
                   + " AND typelem = 0 AND typrelid = 0";

      List<String> retn = new ArrayList<String>();
      ResultSet rs = _conn.createStatement().executeQuery(sql);
      try
      {
         while (rs.next())
         {
            retn.add(rs.getString(1));
         }
      }
      finally
      {
         SQLUtilities.closeResultSet(rs);
      }

      return retn.toArray(new String[retn.size()]);
   }

}
