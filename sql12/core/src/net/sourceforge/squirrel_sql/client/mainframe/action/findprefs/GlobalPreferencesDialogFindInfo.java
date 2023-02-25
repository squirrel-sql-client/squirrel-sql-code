package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JTabbedPane;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

public class GlobalPreferencesDialogFindInfo implements DialogFindInfo
{
   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobalPreferencesDialogFindInfo.class);

   private String _dialogTitle;
   private final JTabbedPane _globalPrefsTabPane;
   private final HashMap<Integer, Component> _tabComponentByTabIndex = new HashMap<>();
   private final HashMap<Component, Integer> _tabIndexByTabComponent = new HashMap<>();

   public GlobalPreferencesDialogFindInfo(String dialogTitle, JTabbedPane globalPrefsTabPane)
   {
      _dialogTitle = dialogTitle;
      _globalPrefsTabPane = globalPrefsTabPane;

      for (int i = 0; i < _globalPrefsTabPane.getTabCount(); i++)
      {
         _tabComponentByTabIndex.put(i, _globalPrefsTabPane.getComponentAt(i));
         _tabIndexByTabComponent.put(_globalPrefsTabPane.getComponentAt(i), i);
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

   @Override
   public String getDialogTitle()
   {
      return s_stringMgr.getString("GlobalPreferencesDialogFindInfo.dialog.title", _dialogTitle);
   }

   @Override
   public DialogToOpen getDialogToOpenConstant()
   {
      return DialogToOpen.GLOBAL_PREFERENCES;
   }

   public void selectTabOfPathComponent(Component tabComponent)
   {
      _globalPrefsTabPane.setSelectedIndex(_tabIndexByTabComponent.get(tabComponent));
   }
}
