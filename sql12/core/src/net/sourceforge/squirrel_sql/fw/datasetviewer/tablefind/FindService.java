package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import java.awt.Dimension;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

public interface FindService
{
   int getRowCount();

   int getColCount();

   String getViewDataAsString(int row, int col);

   void scrollToVisible(int viewRow, int viewCol);

   void setFindServiceCallBack(FindServiceCallBack findServiceCallBack);

   void repaintCell(int viewRow, int viewCol);

   void repaintAll();

   ColumnDisplayDefinition[] getColumnDisplayDefinitions();

   List<Object[]> getRowsForViewIndexes(List<Integer> rowsFound);

   Dimension getVisibleSize();
}
