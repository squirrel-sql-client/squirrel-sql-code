package net.sourceforge.squirrel_sql.client.session.schemainfo.synonym;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Originally introduced by the commits:
 *
 * 474ec078 Bartman0 <rkooijman@inergy.nl> on 10.04.16 at 15:59
 *
 * 77017848 Bartman0 <rkooijman@inergy.nl> on 10.04.16 at 08:47
 *
 * Comments of these commits:
 * "Netezza and MacOS enhancements
 *  Netezza SYNONYM support added and enhanced
 *  Shortcut keys improved and fixed for MacOS
 *  High definition icon restored"
 */
class NetezzaSpecifics
{

   private ISQLDatabaseMetaData _metadata;
   private PreparedStatement _pstmtSqlSynonymReference;

   NetezzaSpecifics(ISQLDatabaseMetaData metadata)
   {
      try
      {
         _metadata = metadata;
         _pstmtSqlSynonymReference = NetezzaSynonym.getSqlSynonymReference(_metadata.getJDBCMetaData().getConnection());
      }
      catch (SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   NetezzaSynonym findSynonym(String catalog, String schema, String table)
   {
      ResultSet rs = null;
      try
      {
         _pstmtSqlSynonymReference.setString(1, table);
         rs = _pstmtSqlSynonymReference.executeQuery();
         if (rs.next())
         {
            return new NetezzaSynonym(rs.getString(2), rs.getString(3), rs.getString(4));
         }
      }
      catch (SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
      finally
      {
         SQLUtilities.closeResultSet(rs, false);
      }

      return null;
   }
}
