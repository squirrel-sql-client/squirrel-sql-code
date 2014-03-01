package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;

public class TableTraverser
{
   private FindService _findService;
   private int _col = -1;
   private int _row = 0;

   public void reset()
   {
      _col = -1;
      _row = 0;
   }

   public void forward()
   {
      if(_col < _findService.getColCount() - 1)
      {
         _col += 1;
      }
      else if(_row < _findService.getRowCount() - 1)
      {
         _row +=1;
         _col = 0;
      }
      else
      {
         reset();
      }
   }

   public void backward()
   {
      if(0 < _col)
      {
         _col -= 1;
      }
      else if(0 <_row)
      {
         _row -=1;
         _col = _findService.getColCount() - 1;
      }
      else
      {
         _col = Math.max(0, _findService.getColCount()-1);
         _row = Math.max(0, _findService.getRowCount()-1);
      }
   }

   public int getRow()
   {
      return _row;
   }

   public int getCol()
   {
      if(-1 == _col)
      {
         forward();
      }

      return _col;
   }

   public void setFindService(FindService findService)
   {
      _findService = findService;
      reset();
   }

   public boolean hasRows()
   {
      return 0 < _findService.getRowCount();
   }

   public int getCellCount()
   {
      return _findService.getColCount() * _findService.getRowCount();
   }
}
