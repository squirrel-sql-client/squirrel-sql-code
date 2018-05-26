package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import java.awt.Color;

public class FindColorHandler
{
   private FindServiceCallBack _findServiceCallBack;

   public Color getBackgroundForCell(int row, int column)
   {
      if (null == _findServiceCallBack)
      {
         return null;
      }

      return _findServiceCallBack.getBackgroundColor(row, column);
   }

   public void setFindServiceCallBack(FindServiceCallBack findServiceCallBack)
   {
      _findServiceCallBack = findServiceCallBack;
   }
}
