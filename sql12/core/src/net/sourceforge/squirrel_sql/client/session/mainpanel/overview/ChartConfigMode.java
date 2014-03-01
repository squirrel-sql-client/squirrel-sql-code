package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScale;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.IndexedColumnFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.util.StringManager;

import java.util.ArrayList;

public enum ChartConfigMode
{
   COUNT("overview.ChartConfigMode.modusCount", "overview.ChartConfigMode.modusCountAxisLabel", SquirrelResources.IImageNames.AGG_COUNT),
   SUM("overview.ChartConfigMode.modusSum", "overview.ChartConfigMode.modusSumAxisLabel", SquirrelResources.IImageNames.AGG_SUM),

   XY_COUNT_DISTINCT("overview.ChartConfigMode.modusCountDistinct", "overview.ChartConfigMode.modusCountDistinctAxisLabel", SquirrelResources.IImageNames.AGG_COUNT),
   XY_SUM_COL("overview.ChartConfigMode.modusSum", "overview.ChartConfigMode.modusSumAxisLabelOfColumn", SquirrelResources.IImageNames.AGG_SUM);
//   XY_MIN("overview.ChartConfigMode.modusMin", "overview.ChartConfigMode.modusMinAxisLabel", SquirrelResources.IImageNames.AGG_MIN),
//   XY_MAX("overview.ChartConfigMode.modusMax", "overview.ChartConfigMode.modusMaxAxisLabel", SquirrelResources.IImageNames.AGG_MAX);

   private final String _modusI18nKey;
   private String _axisLabelI18nKey;
   private final String _modusIconName;
   private StringManager _s_stringMgr;

   ChartConfigMode(String modusI18nKey, String axisLabelI18nKey, String modusIconName)
   {
      _modusI18nKey = modusI18nKey;
      _axisLabelI18nKey = axisLabelI18nKey;
      _modusIconName = modusIconName;
   }




   public static ChartConfigMode[] getAvailableValues(ColumnDisplayDefinition columnDisplayDefinition, ChartConfigPanelTabMode chartConfigPanelTabMode, StringManager s_stringMgr)
   {
      ArrayList<ChartConfigMode> ret = new ArrayList<ChartConfigMode>();

      for (ChartConfigMode chartConfigMode : values())
      {
         chartConfigMode.setStringManager(s_stringMgr);
      }

      if (ChartConfigPanelTabMode.SINGLE_COLUMN == chartConfigPanelTabMode)
      {
         ret.add(COUNT);

         if(IndexedColumnFactory.isSumable(columnDisplayDefinition))
         {
            ret.add(SUM);
         }
      }
      else if (ChartConfigPanelTabMode.XY_CHART == chartConfigPanelTabMode)
      {
         ret.add(XY_COUNT_DISTINCT);

         if(IndexedColumnFactory.isSumable(columnDisplayDefinition))
         {
            ret.add(XY_SUM_COL);
         }

//         if(IndexedColumnFactory.isOrderable(columnDisplayDefinition))
//         {
//            ret.add(XY_MIN);
//            ret.add(XY_MAX);
//         }
      }

      return ret.toArray(new ChartConfigMode[ret.size()]);
   }

   private void setStringManager(StringManager s_stringMgr)
   {
      _s_stringMgr = s_stringMgr;
   }

   @Override
   public String toString()
   {
      return _s_stringMgr.getString(_modusI18nKey);
   }

   public String getYAxisLabel(DataScale yAxisDataScale)
   {
      if(null == yAxisDataScale)
      {
         return _s_stringMgr.getString(_axisLabelI18nKey);
      }
      else
      {
         String col = yAxisDataScale.getColumnDisplayDefinition().getTableName();

         if(null != col && 0 < col.trim().length())
         {
            col += "." + yAxisDataScale.getColumnDisplayDefinition().getColumnName();
         }
         else
         {
            col = yAxisDataScale.getColumnDisplayDefinition().getColumnName();
         }

         return _s_stringMgr.getString(_axisLabelI18nKey, col);
      }
   }
}
