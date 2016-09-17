package org.squirrelsql.session.graph;

import org.squirrelsql.table.RowObjectTableLoaderColumn;

public class ColumnPairRow
{

   private final GraphColumn _fkCol;
   private final GraphColumn _pkCol;

   public ColumnPairRow(GraphColumn fkCol, GraphColumn pkCol)
   {
      _fkCol = fkCol;
      _pkCol = pkCol;
   }

   @RowObjectTableLoaderColumn(columnIndex = 0, columnHeaderI18nKey = "ColumnPairRow.col.header.fk")
   public String getFkColumn()
   {
      return _fkCol.getColumnInfo().getColName();
   }

   @RowObjectTableLoaderColumn(columnIndex = 1, columnHeaderI18nKey = "ColumnPairRow.col.header.pk")
   public String getPkColumn()
   {
      return _pkCol.getColumnInfo().getColName();
   }

   public GraphColumn getFkGraphColumn()
   {
      return _fkCol;
   }

   public GraphColumn getPkGraphColumn()
   {
      return _pkCol;
   }
}
