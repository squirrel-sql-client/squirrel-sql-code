package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesSheet;
import net.sourceforge.squirrel_sql.client.preferences.NewSessionPropertiesSheet;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ComponentInfoByPathUtil
{
   public static PrefsFindInfo createPrefsFindInfo()
   {

      TreeMap<List<String>, List<PrefComponentInfo>> componentInfoByPath = new TreeMap<>( (p1, p2) -> comparePaths(p1,p2) );

      GlobalPreferencesDialogFindInfo globalPreferencesDialogFindInfo = GlobalPreferencesSheet.createPreferencesFinderInfo();
      for (Map.Entry<Integer, Component> entry : globalPreferencesDialogFindInfo.getTabComponentByTabIndex().entrySet())
      {
         final String tabName = globalPreferencesDialogFindInfo.getTabName(entry.getKey());
         PrefsPanelVisitor.visit(globalPreferencesDialogFindInfo, tabName, entry.getValue(), vi -> onVisitFindableComponent(vi, componentInfoByPath));
      }

      SessionPropertiesDialogFindInfo sessionPropertiesDialogFindInfo = NewSessionPropertiesSheet.createPropertiesFinderInfo();
      for (Map.Entry<Integer, Component> entry : sessionPropertiesDialogFindInfo.getTabComponentByTabIndex().entrySet())
      {
         final String tabName = sessionPropertiesDialogFindInfo.getTabName(entry.getKey());
         PrefsPanelVisitor.visit(sessionPropertiesDialogFindInfo, tabName, entry.getValue(), vi -> onVisitFindableComponent(vi, componentInfoByPath));
      }

      return new PrefsFindInfo(globalPreferencesDialogFindInfo, sessionPropertiesDialogFindInfo, componentInfoByPath);
   }

   private static void onVisitFindableComponent(PrefComponentInfo componentInfo, TreeMap<List<String>, List<PrefComponentInfo>> componentInfoByPath)
   {
      if(componentInfo.hasEmptyText())
      {
         return;
      }

      List<String> path = componentInfo.getPath();

      List<PrefComponentInfo> buf = componentInfoByPath.get(path);

      if(null == buf)
      {
         buf = new ArrayList<>();
         componentInfoByPath.put(path, buf);
      }

      buf.add(componentInfo);
   }

   private static int comparePaths(List<String> p1, List<String> p2)
   {
      for (int i = 0; i < Math.min(p1.size(), p2.size()); i++)
      {
         final int res = p1.get(i).compareTo(p2.get(i));
         if(0 != res)
         {
            return res;
         }
      }

      return Integer.compare(p1.size(), p2.size());
   }
}
