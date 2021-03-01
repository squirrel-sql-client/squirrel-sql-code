package net.sourceforge.squirrel_sql.client.session.schemainfo.synonym;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
class NetezzaSynonym
{
   private String catalog;
   private String schema;
   private String table;

   private static final String SQL_SYNONYM_REFERENCE =
         "select " +
               "synonym_name, refdatabase, refschema, refobjname " +
               "from _v_synonym " +
               "where synonym_name = ? ";

   NetezzaSynonym(String catalog, String schema, String table)
   {
      this.catalog = catalog;
      this.schema = schema;
      this.table = table;
   }

   static PreparedStatement getSqlSynonymReference(Connection connection) throws SQLException
   {
      PreparedStatement pstmt = connection.prepareStatement(SQL_SYNONYM_REFERENCE);
      return pstmt;
   }

   public String getCatalog()
   {
      return catalog;
   }

   public void setCatalog(String catalog)
   {
      this.catalog = catalog;
   }

   public String getSchema()
   {
      return schema;
   }

   public void setSchema(String schema)
   {
      this.schema = schema;
   }

   public String getTable()
   {
      return table;
   }

   public void setTable(String table)
   {
      this.table = table;
   }
}
