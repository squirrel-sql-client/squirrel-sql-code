package net.sourceforge.squirrel_sql.client.session.schemainfo.basetabletype;

import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.sql.SQLException;

public class BaseTableTypeHandler
{
   public static final String TABLE_TYPE_BASE_TABLE = "BASE TABLE";

   /**
    * - Frontbase
    * - H2 version >= 2
    * use the table type {@link #TABLE_TYPE_BASE_TABLE} for their "ordinary" tables
    */
   public static boolean isDatabaseUsingTypeBaseTableInsteadOpTable(ISQLDatabaseMetaData md)
   {
      return    DialectFactory.isFrontBase(md)
             || DialectFactory.isH2(md); // Actually it is H2 versions greater 2.* only.
   }

   public static boolean isH2VersionGreaterOrEqual2(ISQLDatabaseMetaData md)
   {
      try
      {
         return !md.getDatabaseProductVersion().startsWith("0.") && !md.getDatabaseProductVersion().startsWith("1.");
      }
      catch (SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
