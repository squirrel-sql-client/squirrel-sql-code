package org.squirrelsql.session.graph;

import org.squirrelsql.table.RowObjectTableLoaderColumn;

public class OrderByPositionRowObject
{
   private ColumnPersistence _columnPersistence;

   public OrderByPositionRowObject(ColumnPersistence columnPersistence)
   {
      _columnPersistence = columnPersistence;
   }

   @RowObjectTableLoaderColumn(columnIndex = 0, columnHeaderI18nKey = "OrderByPositionRowObject.col.header.columnname")
   public String getColumnName()
   {
      return _columnPersistence.getColName();
   }

   @RowObjectTableLoaderColumn(columnIndex = 1, columnHeaderI18nKey = "OrderByPositionRowObject.col.header.tablename")
   public String getTableName()
   {
      return _columnPersistence.getTableName();
   }


   @RowObjectTableLoaderColumn(columnIndex = 2, columnHeaderI18nKey = "OrderByPositionRowObject.col.header.sort.direction")
   public String getSortDirection()
   {
      String orderBy = _columnPersistence.getColumnConfigurationPersistence().getOrderByPersistence().getOrderBy();
      return OrderBy.valueOf(orderBy).name(); // Just to make clear where it comes from.
   }

   public void setOrderByPosition(int index)
   {
      _columnPersistence.getColumnConfigurationPersistence().setOrderByPosition(index);
   }

   public ColumnPersistence getColumnPersistence()
   {
      return _columnPersistence;
   }
}
