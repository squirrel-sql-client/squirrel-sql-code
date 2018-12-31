package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

public enum ChartConfigPanelTabMode
{
   SINGLE_COLUMN("overview.ChartConfigController.tabSingleColChart"),
   TWO_COLUMN("overview.ChartConfigController.tabTwoColumnChart"),
   XY_CHART("overview.ChartConfigController.XYChart"),
   DIFFERENCES_CHART("overview.ChartConfigController.differencesChart");

   private String _tabTitleKey;

   ChartConfigPanelTabMode(String tabTitleKey)
   {
      _tabTitleKey = tabTitleKey;
   }

   public String getTabTitleKey()
   {
      return _tabTitleKey;
   }
}
