package org.squirrelsql.session;

import org.squirrelsql.session.schemainfo.StructItemSchema;
import org.squirrelsql.table.TableLoader;

import java.util.List;

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

   public List<ColumnInfo> getColumns()
   {
      if(null == _columnsAsTableLoader)
      {
         throw new IllegalStateException();
      }

      List<ColumnInfo> ret = ColumnInfo.createColumnInfosFromTableMetaData(_columnsAsTableLoader);

      return ret;
   }

   public StructItemSchema getStructItemSchema()
   {
      return new StructItemSchema(_schema, _catalog);
   }
}
