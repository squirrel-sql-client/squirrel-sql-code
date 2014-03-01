package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

   List<Object[]> getRowsForIndexes(ArrayList<Integer> rowsFound);

   Dimension getVisibleSize();
}
