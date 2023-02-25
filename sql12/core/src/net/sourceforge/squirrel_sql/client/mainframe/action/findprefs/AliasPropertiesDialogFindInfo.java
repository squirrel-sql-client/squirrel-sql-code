package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JTabbedPane;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

public class AliasPropertiesDialogFindInfo implements DialogFindInfo
{
   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasPropertiesDialogFindInfo.class);
   private String _dialogTitle;
   private JTabbedPane _aliasPropertiesTabbedPane;

   private final HashMap<Integer, Component> _tabComponentByTabIndex = new HashMap<>();
   private final HashMap<Component, Integer> _tabIndexByTabComponent = new HashMap<>();


   public AliasPropertiesDialogFindInfo(String dialogTitle, JTabbedPane aliasPropertiesTabbedPane)
   {
      _dialogTitle = dialogTitle;
      _aliasPropertiesTabbedPane = aliasPropertiesTabbedPane;

      for (int i = 0; i < _aliasPropertiesTabbedPane.getTabCount(); i++)
      {
         _tabComponentByTabIndex.put(i, _aliasPropertiesTabbedPane.getComponentAt(i));
         _tabIndexByTabComponent.put(_aliasPropertiesTabbedPane.getComponentAt(i), i);
      }
   }

   public Map<Integer, Component> getTabComponentByTabIndex()
   {
      return _tabComponentByTabIndex;
   }

   public String getTabName(Integer tabIx)
   {
      return s_stringMgr.getString("AliasPropertiesDialogFindInfo.tab", _aliasPropertiesTabbedPane.getTitleAt(tabIx));
   }

   public void selectTabOfPathComponent(Component tabComponent)
   {
      _aliasPropertiesTabbedPane.setSelectedIndex(_tabIndexByTabComponent.get(tabComponent));
   }

   @Override
   public String getDialogTitle()
   {
      return s_stringMgr.getString("AliasPropertiesDialogFindInfo.dialog.title", _dialogTitle);
   }

   @Override
   public DialogToOpen getDialogToOpenConstant()
   {
      return DialogToOpen.ALIAS_PROPERTIES;
   }
}
