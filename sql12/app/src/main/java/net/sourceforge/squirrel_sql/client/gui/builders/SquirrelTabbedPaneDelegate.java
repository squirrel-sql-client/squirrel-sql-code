package net.sourceforge.squirrel_sql.client.gui.builders;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

public class SquirrelTabbedPaneDelegate
{
   private SquirrelPreferences _prefs;

   private PropsListener _prefsListener;
   private JTabbedPane _tabbedPane;

   /** Convenient way to refer to Application Preferences property names. */
   private interface IAppPrefPropertynames extends SquirrelPreferences.IPropertyNames
   {
      // Empty block.
   }

   SquirrelTabbedPaneDelegate(SquirrelPreferences prefs, IApplication app, JTabbedPane tabbedPane)
   {
      _tabbedPane = tabbedPane;
      if (prefs == null) { throw new IllegalArgumentException("SquirrelPreferences == null"); }
      _prefs = prefs;

      int tabLayoutPolicy =
            _prefs.getUseScrollableTabbedPanes() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT;
      _tabbedPane.setTabLayoutPolicy(tabLayoutPolicy);
   }

   /**
    * Component is being added to its parent so add a property change listener to application perferences.
    */
   public void addNotify()
   {
      _prefsListener = new PropsListener(_prefs, _tabbedPane);
      _prefs.addPropertyChangeListener(_prefsListener);
      _prefsListener.propertiesHaveChanged(null);
   }

   /**
    * Component is being removed from its parent so remove the property change listener from the application
    * preferences.
    */
   public void removeNotify()
   {
      _tabbedPane.removeNotify();
      if (_prefsListener != null)
      {
         _prefs.removePropertyChangeListener(_prefsListener);
         _prefsListener = null;
      }
   }


   /**
    * Avoids memory leaks.
    *
    * Removing the global listener in removeNotify() did not prove really save.
    * We introduced this listener class to hold a weak reference to the tabbed pane
    * to make sure Sessions get garbage collected:
    *
    * If removeNotify() does not work this listener will remain in the list of the global prefs
    * listener. It will then be the only global reference to the tabbed pane.
    * The tabbed pane then can be garbage collected which will result in garbage collecting the
    * complete Session.
    *
    */
   private static final class PropsListener implements PropertyChangeListener
   {
      private SquirrelPreferences _prefs;

      private WeakReference<JTabbedPane> _refSquirrelTabbedPane;

      public PropsListener(SquirrelPreferences prefs, JTabbedPane squirrelTabbedPane)
      {
         _prefs = prefs;
         _refSquirrelTabbedPane = new WeakReference<JTabbedPane>(squirrelTabbedPane);
      }

      public void propertyChange(PropertyChangeEvent evt)
      {
         propertiesHaveChanged(evt.getPropertyName());
      }

      void propertiesHaveChanged(String propName)
      {
         JTabbedPane squirrelTabbedPane = _refSquirrelTabbedPane.get();

         if(null == squirrelTabbedPane)
         {
            return;
         }


         if (propName == null || propName.equals(IAppPrefPropertynames.SCROLLABLE_TABBED_PANES))
         {
            int tabLayoutPolicy =
                  _prefs.getUseScrollableTabbedPanes() ? JTabbedPane.SCROLL_TAB_LAYOUT
                        : JTabbedPane.WRAP_TAB_LAYOUT;
            squirrelTabbedPane.setTabLayoutPolicy(tabLayoutPolicy);
         }
      }

   }

}
