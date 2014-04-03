package org.squirrelsql.session;

import org.squirrelsql.session.schemainfo.StructItemSchema;
import org.squirrelsql.table.TableLoader;

import java.util.ArrayList;

public class TableInfo
{
   private final String _catalog;
   private final String _schema;
   private final String _tableType;
   private final String _name;
   private String _qualifiedName;
   private TableLoader _columnsAsTableLoader;

   public TableInfo(String catalog, String schema, String tableType, String name, String qualifiedName)
   {
      _catalog = catalog;
      _schema = schema;
      _tableType = tableType;
      _name = name;
      _qualifiedName = qualifiedName;
   }

   public String getCatalog()
   {
      return _catalog;
   }

   public String getSchema()
   {
      return _schema;
   }

   public String getTableType()
   {
      return _tableType;
   }

   public String getName()
   {
      return _name;
   }

   public String getQualifiedName()
   {
      return _qualifiedName;
   }

   public TableLoader getColumnsAsTableLoader()
   {
      return _columnsAsTableLoader;
   }

   public void setColumnsAsTableLoader(TableLoader columnsAsTableLoader)
   {
      _columnsAsTableLoader = columnsAsTableLoader;
   }

   public ArrayList<ColumnInfo> getColumns()
   {
      if(null == _columnsAsTableLoader)
      {
         throw new IllegalStateException();
      }

      ArrayList<ColumnInfo> ret = new ArrayList<>();

      for (int i = 0; i < _columnsAsTableLoader.size(); i++)
      {
         String colName = _columnsAsTableLoader.getCellAsString(ColumnMetaProps.COLUMN_NAME.getPropName(), i);
         int colType = _columnsAsTableLoader.getCellAsInt(ColumnMetaProps.DATA_TYPE.getPropName(), i);
         String colTypeName = _columnsAsTableLoader.getCellAsString(ColumnMetaProps.TYPE_NAME.getPropName(), i);
         Integer colSize = _columnsAsTableLoader.getCellAsInteger(ColumnMetaProps.COLUMN_SIZE.getPropName(), i);
         Integer decDigits = _columnsAsTableLoader.getCellAsInteger(ColumnMetaProps.DECIMAL_DIGITS.getPropName(), i);

         String nullablePropValue = _columnsAsTableLoader.getCellAsString(ColumnMetaProps.IS_NULLABLE.getPropName(), i);
         boolean nullable = ColumnMetaProps.isYes(nullablePropValue);

         String remarks = _columnsAsTableLoader.getCellAsString(ColumnMetaProps.REMARKS.getPropName(), i);

         ret.add(new ColumnInfo(colName, colType, colTypeName, colSize, decDigits, nullable, remarks));

      }

      return ret;
   }

   public StructItemSchema getStructItemSchema()
   {
      return new StructItemSchema(_schema, _catalog);
   }
}
