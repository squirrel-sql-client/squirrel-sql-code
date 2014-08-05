package net.sourceforge.squirrel_sql.client.session.mainpanel.crosstable;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;

import java.awt.*;

public class CrossTableCtrl
{
   public static boolean isCrossTablePanel(Component comp)
   {
      return comp instanceof CrossTablePanel;
   }

   public String getTitle()
   {
      return "Cross table Hallo";
   }

   public CrossTablePanel getPanel()
   {
      return new CrossTablePanel();
   }

   public void init(ResultSetDataSet rsds)
   {

   }
}
