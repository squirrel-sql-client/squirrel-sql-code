package org.squirrelsql.session.graph;

import org.squirrelsql.table.RowObjectTableLoaderColumn;

public class SelectPositionRowObject
{
   private ColumnPersistence _columnPersistence;

   public SelectPositionRowObject(ColumnPersistence columnPersistence)
   {
      _columnPersistence = columnPersistence;
   }

   @RowObjectTableLoaderColumn(columnIndex = 0, columnHeaderI18nKey = "SelectPositionRowObject.col.header.columnname")
   public String getColumnName()
   {
      return _columnPersistence.getColName();
   }

   @RowObjectTableLoaderColumn(columnIndex = 1, columnHeaderI18nKey = "SelectPositionRowObject.col.header.tablename")
   public String getTableName()
   {
      return _columnPersistence.getTableName();
   }

   public void setSelectPosition(int index)
   {
      _columnPersistence.getColumnConfigurationPersistence().setSelectPosition(index);
   }

   public ColumnPersistence getColumnPersistence()
   {
      return _columnPersistence;
   }
}
