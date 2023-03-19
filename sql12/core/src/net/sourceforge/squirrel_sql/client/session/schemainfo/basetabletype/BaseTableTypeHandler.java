package net.sourceforge.squirrel_sql.client.session.schemainfo.basetabletype;

import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

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
}
