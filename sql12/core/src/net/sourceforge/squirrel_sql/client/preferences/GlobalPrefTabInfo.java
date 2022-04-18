package net.sourceforge.squirrel_sql.client.preferences;

import javax.swing.JScrollPane;
import java.awt.Component;

public class GlobalPrefTabInfo
{
   private final int _tabIndex;
   private final Class _componentClass;
   private final Component _componentClassInstance;
   private final JScrollPane _wrappingScrollPane;

   /**
    * @param tabIndex
    * @param componentClass is the class that exterior parts of the application use to reference a UI-tab, e.g. by {@link GlobalPreferencesSheet#showSheet(Class)}
    * @param componentClassInstance is the instance of the componentClass which may be wrapped in a ScrollPane.
    * @param wrappingScrollPane the ScrollPane wrapping the componentClassInstance if it exists else null
    */
   public GlobalPrefTabInfo(int tabIndex, Class componentClass, Component componentClassInstance, JScrollPane wrappingScrollPane)
   {
      _tabIndex = tabIndex;
      _componentClass = componentClass;
      _componentClassInstance = componentClassInstance;
      _wrappingScrollPane = wrappingScrollPane;
   }

   public int getTabIndex()
   {
      return _tabIndex;
   }

   public Component getComponentClassInstance()
   {
      return _componentClassInstance;
   }

   public JScrollPane getWrappingScrollPane()
   {
      return _wrappingScrollPane;
   }

   /**
    *
    * @return As returned by {@link IGlobalPreferencesPanel#getPanelComponent()}
    */
   Component getPanelComponent()
   {
      if(null != _wrappingScrollPane)
      {
         return _wrappingScrollPane;
      }

      return _componentClassInstance;
   }
}
