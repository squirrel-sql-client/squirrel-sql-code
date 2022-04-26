package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JTabbedPane;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

public class SessionPropertiesDialogFindInfo implements DialogFindInfo
{
   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionPropertiesDialogFindInfo.class);

   private String _dialogTitle;
   private final HashMap<Integer, Component> _tabComponentByTabIndex;
   private final HashMap<Component, Integer> _tabIndexByTabComponent;
   private JTabbedPane _newSessionPropertiesTabbedPane;

   public SessionPropertiesDialogFindInfo(String dialogTitle, JTabbedPane newSessionPropertiesTabbedPane)
   {
      _dialogTitle = dialogTitle;
      _newSessionPropertiesTabbedPane = newSessionPropertiesTabbedPane;

      _tabComponentByTabIndex = new HashMap<>();
      _tabIndexByTabComponent = new HashMap<>();

      for (int i = 0; i < _newSessionPropertiesTabbedPane.getTabCount(); i++)
      {
         _tabComponentByTabIndex.put(i, _newSessionPropertiesTabbedPane.getComponentAt(i));
         _tabIndexByTabComponent.put(_newSessionPropertiesTabbedPane.getComponentAt(i), i);
      }
   }

   public Map<Integer, Component> getTabComponentByTabIndex()
   {
      return _tabComponentByTabIndex;
   }

   public String getTabName(int tabIx)
   {
      return s_stringMgr.getString("SessionPropertiesFinderInfo.tab", _newSessionPropertiesTabbedPane.getTitleAt(tabIx));
   }

   @Override
   public String getDialogTitle()
   {
      return s_stringMgr.getString("GlobalPreferencesDialogFindInfo.dialog.title", _dialogTitle);
   }

   public void selectTabOfPathComponent(Component tabComponent)
   {
      _newSessionPropertiesTabbedPane.setSelectedIndex(_tabIndexByTabComponent.get(tabComponent));
   }
}
