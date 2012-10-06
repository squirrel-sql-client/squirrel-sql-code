package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

public enum ChartConfigPanelTabMode
{
   SINGLE_COLUMN("overview.ChartConfigController.tabSingleColChart"),
   XY_CHART("overview.ChartConfigController.tabXYChart"),
   XYZ_CHART("overview.ChartConfigController.tabXYZChart");

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
