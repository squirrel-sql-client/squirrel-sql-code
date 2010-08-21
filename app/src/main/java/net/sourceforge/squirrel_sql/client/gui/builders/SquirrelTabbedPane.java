package net.sourceforge.squirrel_sql.client.gui.builders;

/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

import javax.swing.*;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.IApplication;

class SquirrelTabbedPane extends JTabbedPane
{
	private static final long serialVersionUID = 3663370280049413647L;

	private SquirrelPreferences _prefs;

	private PropsListener _prefsListener;

	/** Convenient way to refer to Application Preferences property names. */
	private interface IAppPrefPropertynames extends SquirrelPreferences.IPropertyNames
	{
		// Empty block.
	}

	SquirrelTabbedPane(SquirrelPreferences prefs, IApplication app)
	{
		super();

		if (prefs == null) { throw new IllegalArgumentException("SquirrelPreferences == null"); }
		_prefs = prefs;

		int tabLayoutPolicy =
			_prefs.getUseScrollableTabbedPanes() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT;
		setTabLayoutPolicy(tabLayoutPolicy);
	}

	/**
	 * Component is being added to its parent so add a property change listener to application perferences.
	 */
	public void addNotify()
	{
		super.addNotify();
		_prefsListener = new PropsListener(_prefs, this);
		_prefs.addPropertyChangeListener(_prefsListener);
		_prefsListener.propertiesHaveChanged(null);
	}

	/**
	 * Component is being removed from its parent so remove the property change listener from the application
	 * preferences.
	 */
	public void removeNotify()
	{
		super.removeNotify();
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

      private WeakReference<SquirrelTabbedPane> _refSquirrelTabbedPane;

      public PropsListener(SquirrelPreferences prefs, SquirrelTabbedPane squirrelTabbedPane)
      {
         _prefs = prefs;
         _refSquirrelTabbedPane = new WeakReference<SquirrelTabbedPane>(squirrelTabbedPane);
      }

      public void propertyChange(PropertyChangeEvent evt)
		{
			propertiesHaveChanged(evt.getPropertyName());
		}

      void propertiesHaveChanged(String propName)
      {
         SquirrelTabbedPane squirrelTabbedPane = _refSquirrelTabbedPane.get();

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
