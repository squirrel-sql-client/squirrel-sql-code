package net.sourceforge.squirrel_sql.client.session.mainpanel.findresultcolumn;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;

import java.awt.*;

public class FindResultColumnUtil
{
   public static void findAndShowResultColumns(DataSetViewerTablePanel dataSetViewerTablePanel, Window owningWindow)
   {
      FindResultColumnCtrl findResultColumnCtrl = new FindResultColumnCtrl(owningWindow, dataSetViewerTablePanel);

      if (null != findResultColumnCtrl.getColumnToGoTo())
      {
         dataSetViewerTablePanel.scrollColumnToVisible(findResultColumnCtrl.getColumnToGoTo().getExtTableColumn());
      }
      else if (null != findResultColumnCtrl.getColumnsToMoveToFront())
      {
         dataSetViewerTablePanel.moveColumnsToFront(findResultColumnCtrl.getColumnsToMoveToFront());
      }
   }
}
