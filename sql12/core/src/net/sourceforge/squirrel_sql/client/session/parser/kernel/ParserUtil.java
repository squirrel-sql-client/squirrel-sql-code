package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.sql.Types;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ColumnQualifier;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

public class ParserUtil
{
   public static TableColumnInfo createTableColumnInfoFromName(ISession session, String columnName, int ordinalPositon)
   {

      ColumnQualifier qualifier = new ColumnQualifier(columnName);
      return new TableColumnInfo(qualifier.getCatalog(), qualifier.getSchema(), qualifier.getTableName(), qualifier.getColumnName(), Types.OTHER, "OTHER", 10, 0, 0, 1, null,
                                 null, 0, ordinalPositon, null, null, session.getMetaData());
   }

}
