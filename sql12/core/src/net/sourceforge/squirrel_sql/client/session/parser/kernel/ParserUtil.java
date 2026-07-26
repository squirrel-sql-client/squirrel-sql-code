package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.sql.Types;
import net.sf.jsqlparser.schema.Column;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ColumnQualifier;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

public class ParserUtil
{
   public static final String COLUMN_TYPE_INNER_SELECT_COL = "INNER_SELECT_COLUMN";

   public static TableColumnInfo createSubSelectTableColumnInfoFromName(ISession session, String columnName, int ordinalPositon)
   {
      ColumnQualifier qualifier = new ColumnQualifier(columnName);
      return _createTableColumnInfo(qualifier.getCatalog(), qualifier.getSchema(), qualifier.getTableName(), qualifier.getColumnName(), ordinalPositon, session);
   }


   public static TableColumnInfo createSubSelectTableColumnInfoFromName(ISession session, Column jsqlColumn, int i)
   {
      String catalogName = null != jsqlColumn.getTable() ? jsqlColumn.getTable().getCatalogName() : null;
      String schemaName = null != jsqlColumn.getTable() ? jsqlColumn.getTable().getSchemaName() : null;
      String tableName = null != jsqlColumn.getTable() ? jsqlColumn.getTable().getName() : null;

      return _createTableColumnInfo(catalogName, schemaName, tableName, jsqlColumn.getColumnName(), i, session);
   }


   private static TableColumnInfo _createTableColumnInfo(String catalog, String schema, String tableName, String columnName, int ordinalPositon, ISession session)
   {
      TableColumnInfo ret = new TableColumnInfo(catalog, schema, tableName, columnName, Types.OTHER, COLUMN_TYPE_INNER_SELECT_COL, -1, 0, 0, 1, null,
                                                null, 0, ordinalPositon, "YES", "NO", session.getMetaData());
      return ret;
   }

}
