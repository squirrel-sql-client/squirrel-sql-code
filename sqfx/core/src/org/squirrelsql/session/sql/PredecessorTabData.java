package org.squirrelsql.session.sql;

import org.squirrelsql.table.TableState;

public class PredecessorTabData
{
   private final int _indexToReplace;
   private final TableState _tableState;

   public PredecessorTabData(int indexToReplace, TableState tableState)
   {
      _indexToReplace = indexToReplace;
      _tableState = tableState;
   }

   public int getIndexToReplace()
   {
      return _indexToReplace;
   }

   public TableState getTableState()
   {
      return _tableState;
   }
}
