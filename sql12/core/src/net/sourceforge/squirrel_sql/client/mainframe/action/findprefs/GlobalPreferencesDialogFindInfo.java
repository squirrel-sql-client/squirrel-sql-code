package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JTabbedPane;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

public class GlobalPreferencesDialogFindInfo
{
   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobalPreferencesDialogFindInfo.class);

   private final JTabbedPane _globalPrefsTabPane;
   private final HashMap<Integer, Component> _tabComponentByTabIndex;

   public GlobalPreferencesDialogFindInfo(JTabbedPane globalPrefsTabPane)
   {
      _globalPrefsTabPane = globalPrefsTabPane;
      _tabComponentByTabIndex = new HashMap<>();

      for (int i = 0; i < _globalPrefsTabPane.getTabCount(); i++)
      {
         _tabComponentByTabIndex.put(i, _globalPrefsTabPane.getComponentAt(i));
      }

   }

   public Map<Integer, Component> getTabComponentByTabIndex()
   {
      return _tabComponentByTabIndex;
   }

   public String getTabName(int tabIx)
   {
      return s_stringMgr.getString("GlobalPreferencesDialogFindInfo.tab", _globalPrefsTabPane.getTitleAt(tabIx));
   }
}
