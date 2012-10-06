package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactoryAdapter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.util.ArrayList;

public class ChartConfigPanel extends JScrollPane
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChartConfigController.class);


   public ChartConfigPanel(ArrayList<ChartConfigPanelTab> chartConfigPanelTabs)
   {

      JTabbedPane tabbedPane = UIFactory.getInstance().createTabbedPane();

      for (ChartConfigPanelTab chartConfigPanelTab : chartConfigPanelTabs)
      {
         tabbedPane.add(chartConfigPanelTab.getTabTitle(), chartConfigPanelTab);
      }

      setViewportView(tabbedPane);
   }

}
