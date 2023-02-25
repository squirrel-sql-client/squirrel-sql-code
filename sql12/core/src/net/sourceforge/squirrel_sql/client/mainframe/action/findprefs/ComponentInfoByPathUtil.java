package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.AliasPropertiesController;
import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesSheet;
import net.sourceforge.squirrel_sql.client.preferences.NewSessionPropertiesSheet;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionMoreCtrlSingleton;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ComponentInfoByPathUtil
{
   public static PrefsFindInfo createPrefsFindInfo()
   {
      return createPrefsFindInfo(DialogToOpen.NONE);
   }

   public static PrefsFindInfo createPrefsFindInfo(DialogToOpen dialogToOpen)
   {

      TreeMap<List<String>, List<PrefComponentInfo>> componentInfoByPath = new TreeMap<>( (p1, p2) -> comparePaths(p1,p2) );

      // Global Preferences
      GlobalPreferencesDialogFindInfo globalPreferencesDialogFindInfo =
            GlobalPreferencesSheet.getPreferencesFindSupport().createFindInfo(dialogToOpen == DialogToOpen.GLOBAL_PREFERENCES);
      for (Map.Entry<Integer, Component> entry : globalPreferencesDialogFindInfo.getTabComponentByTabIndex().entrySet())
      {
         final String tabName = globalPreferencesDialogFindInfo.getTabName(entry.getKey());
         PrefsPanelVisitor.visit(globalPreferencesDialogFindInfo, tabName, entry.getValue(), vi -> onVisitFindableComponent(vi, componentInfoByPath));
      }

      // New Session Properties
      SessionPropertiesDialogFindInfo sessionPropertiesDialogFindInfo =
            NewSessionPropertiesSheet.getPreferencesFindSupport().createFindInfo(dialogToOpen == DialogToOpen.SESSION_PROPERTIES);
      for (Map.Entry<Integer, Component> entry : sessionPropertiesDialogFindInfo.getTabComponentByTabIndex().entrySet())
      {
         final String tabName = sessionPropertiesDialogFindInfo.getTabName(entry.getKey());
         PrefsPanelVisitor.visit(sessionPropertiesDialogFindInfo, tabName, entry.getValue(), vi -> onVisitFindableComponent(vi, componentInfoByPath));
      }

      // Alias Properties
      AliasPropertiesDialogFindInfo aliasPropertiesDialogFindInfo =
            AliasPropertiesController.getPreferencesFindSupport().createFindInfo(dialogToOpen == DialogToOpen.ALIAS_PROPERTIES);

      if(null != aliasPropertiesDialogFindInfo) // Happens when no Alias was defined yet.
      {
         for (Map.Entry<Integer, Component> entry : aliasPropertiesDialogFindInfo.getTabComponentByTabIndex().entrySet())
         {
            final String tabName = aliasPropertiesDialogFindInfo.getTabName(entry.getKey());
            PrefsPanelVisitor.visit(aliasPropertiesDialogFindInfo, tabName, entry.getValue(), vi -> onVisitFindableComponent(vi, componentInfoByPath));
         }
      }

      // Saved Sessions
      SavedSessionMoreDialogFindInfo savedSessionFindInfo =
            SavedSessionMoreCtrlSingleton.getPreferencesFindSupport().createFindInfo(dialogToOpen == DialogToOpen.SAVED_SESSION);
      PrefsPanelVisitor.visit(savedSessionFindInfo, null, savedSessionFindInfo.getContentPane(), vi -> onVisitFindableComponent(vi, componentInfoByPath));



      return new PrefsFindInfo(globalPreferencesDialogFindInfo,
                               sessionPropertiesDialogFindInfo,
                               aliasPropertiesDialogFindInfo,
                               savedSessionFindInfo,
                               componentInfoByPath
      );
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
