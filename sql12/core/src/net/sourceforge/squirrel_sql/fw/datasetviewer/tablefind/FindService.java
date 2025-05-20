package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.awt.Dimension;
import java.awt.Window;
import java.util.List;

public interface FindService
{
   int getRowCount();

   int getColCount();

   String getViewDataAsString(int row, int col);

   /**
    * @param adjustScrollPositonDueToTableSearchPanelBeingDisplayedLater Needed to cope with table search panel being displayed after the result table
    *                                                                    was scrolled to the positon of a finding.
    *                                                                    The preferable way turned out to introduce the parameter
    *                                                                    adjustScrollPositonDueToTableSearchPanelBeingDisplayedLater.
    */
   void scrollToVisible(int viewRow, int viewCol, boolean selectCell, boolean adjustScrollPositonDueToTableSearchPanelBeingDisplayedLater);

   void setFindServiceCallBack(FindServiceCallBack findServiceCallBack);

   void repaintCell(int viewRow, int viewCol);

   void repaintAll();

   ColumnDisplayDefinition[] getColumnDisplayDefinitions();

   List<Object[]> getRowsForViewIndexes(List<Integer> rowsFound);

   Dimension getVisibleSize();

   Window getParentWindow();

   ColumnDisplayDefinition getColumnDisplayDefinitionByViewIndex(int columnViewIx);
}
