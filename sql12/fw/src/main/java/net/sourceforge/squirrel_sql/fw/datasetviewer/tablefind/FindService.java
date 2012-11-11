package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

public interface FindService
{
   int getRowCount();

   int getColCount();

   String getViewDataAsString(int row, int col);

   void scrollToVisible(int viewRow, int viewCol);

   void setFindServiceRenderCallBack(FindServiceRenderCallBack findServiceRenderCallBack);

   void repaintCell(int viewRow, int viewCol);

   void repaintAll();
}
