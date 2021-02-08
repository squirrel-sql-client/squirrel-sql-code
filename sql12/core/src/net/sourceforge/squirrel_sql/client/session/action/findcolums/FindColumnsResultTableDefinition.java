package net.sourceforge.squirrel_sql.client.session.action.findcolums;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum FindColumnsResultTableDefinition
{
   COL_CATALOG_NAME("FindColumnsDlg.column.catalog.name", "catalogName"),
   COL_SCHEMA_NAME("FindColumnsDlg.column.schema.name", "schemaName"),
   COL_OBJECT_NAME("FindColumnsDlg.column.object.name", "objectName"),
   COL_OBJECT_TYPE_NAME("FindColumnsDlg.column.objectType.name", "objectTypeName"),

   COL_COLUMN_NAME("FindColumnsDlg.column.column.name", "columnName"),
   COL_TYPE_NAME("FindColumnsDlg.column.type.name", "columnTypeName"),
   COL_NULLABLE("FindColumnsDlg.column.nullable", "nullable"),
   COL_SIZE("FindColumnsDlg.column.size", "size"),
   COL_NUMBER_PRECISION("FindColumnsDlg.column.number.precision", "precision"),
   COL_DECIMAL_DIGITS("FindColumnsDlg.column.decimal.digits", "decimalDigits"),
   COL_ORDINAL_POSITION("FindColumnsDlg.column.ordinal.position", "ordinalPosition"),
   COL_ORDINAL_REMARKS("FindColumnsDlg.column.remarks", "remarks"),

   COL_JAVA_SQL_TYPE_("FindColumnsDlg.column.javaSqlType", "javaSqlType");

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FindColumnsResultTableDefinition.class);

   private final String _colHeaderI18n;
   private final String _beanPropName;



   FindColumnsResultTableDefinition(String colHeaderI18n, String beanPropName)
   {
      _colHeaderI18n = colHeaderI18n;
      _beanPropName = beanPropName;
   }

   public String getColHeader()
   {
      return s_stringMgr.getString(_colHeaderI18n);
   }

   public String getBeanPropName()
   {
      return _beanPropName;
   }
}
