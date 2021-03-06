package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScaleTable;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JScrollPane;
import java.util.ArrayList;

public class ChartConfigController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChartConfigController.class);
   private ChartConfigPanel _chartConfigPanel;
   private final ChartConfigPanelTabController[] _chartConfigPanelTabControllers;

   public ChartConfigController(IApplication app, ChartConfigListener chartConfigListener)
   {

      _chartConfigPanelTabControllers = new ChartConfigPanelTabController[]
            {
                  new ChartConfigPanelTabController(app, ChartConfigPanelTabMode.SINGLE_COLUMN),
                  new ChartConfigPanelTabController(app, ChartConfigPanelTabMode.TWO_COLUMN),
                  new ChartConfigPanelTabController(app, ChartConfigPanelTabMode.XY_CHART),
                  new ChartConfigPanelTabController(app, ChartConfigPanelTabMode.SCATTER_CHART),
                  new ChartConfigPanelTabController(app, ChartConfigPanelTabMode.DIFFERENCES_CHART),
            };


      ArrayList<ChartConfigPanelTab> chartConfigPanelTabs = new ArrayList<ChartConfigPanelTab>();

      for (ChartConfigPanelTabController chartConfigPanelTabController : _chartConfigPanelTabControllers)
      {
         chartConfigPanelTabs.add(chartConfigPanelTabController.getPanel());
      }

      _chartConfigPanel = new ChartConfigPanel(chartConfigPanelTabs, chartConfigListener);

   }

   public JScrollPane getPanel()
   {
      return _chartConfigPanel;
   }

   public void setDataScaleTable(DataScaleTable dataScaleTable)
   {
      for (ChartConfigPanelTabController chartConfigPanelTabController : _chartConfigPanelTabControllers)
      {
         chartConfigPanelTabController.setDataScaleTable(dataScaleTable);
      }
   }
}


