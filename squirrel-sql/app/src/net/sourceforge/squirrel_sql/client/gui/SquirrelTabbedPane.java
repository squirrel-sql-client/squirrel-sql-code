package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JTabbedPane;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

public class SquirrelTabbedPane extends JTabbedPane
{
	private SquirrelPreferences _prefs;
	private Method _setter;
	private int SCROLL;
	private int WRAP;

	private PropsListener _prefsListener;

	/** Convienient way to refer to Application Preferences property names. */
	private interface IAppPrefPropertynames
							extends SquirrelPreferences.IPropertyNames
	{
	}

	public SquirrelTabbedPane(SquirrelPreferences prefs)
	{
		if (prefs == null)
		{
			throw new IllegalArgumentException("SquirrelPreferences == null");
		}
		_prefs = prefs;

		try
		{
			// Look for the new JDK 1.4 fields indicating tab types.
			Class clazz = getClass();
			SCROLL = clazz.getField("SCROLL_TAB_LAYOUT").getInt(this);
			WRAP = clazz.getField("WRAP_TAB_LAYOUT").getInt(this);
			_setter = clazz.getMethod("setTabLayoutPolicy", new Class[] { int.class });
			Object[] parms = new Object[1];
			parms[0] = new Integer(_prefs.useScrollableTabbedPanes() ? SCROLL : WRAP);
			_setter.invoke(this, parms);
		}
		catch (IllegalAccessException ex)
		{
		}
		catch (NoSuchFieldException ex)
		{
		}
		catch (NoSuchMethodException ex)
		{
		}
		catch (InvocationTargetException ex)
		{
		}
	}

	/**
	 * Component is being added to its parent so add a property change
	 * listener to application perferences.
	 */
	public void addNotify()
	{
		super.addNotify();
		_prefsListener = new PropsListener();
		_prefs.addPropertyChangeListener(_prefsListener);
		propertiesHaveChanged(null);
	}

	/**
	 * Component is being removed from its parent so remove the property change
	 * listener from the application perferences.
	 */
	public void removeNotify()
	{
		if (_prefsListener != null)
		{
			_prefs.removePropertyChangeListener(_prefsListener);
			_prefsListener = null;
		}
		super.removeNotify();
	}

	private void propertiesHaveChanged(String propName)
	{
		if (propName == null || propName.equals(IAppPrefPropertynames.SCROLLABLE_TABBED_PANES))
		{
			if (_setter != null)
			{
				
				{
					try
					{
						final Object[] parms = new Object[1];
						final boolean scroll = _prefs.useScrollableTabbedPanes();
						parms[0] = new Integer(scroll ? SCROLL : WRAP);
						_setter.invoke(this, parms);
					}
					catch (IllegalAccessException ex)
					{
					}
					catch (InvocationTargetException ex)
					{
					}
				}
			}
		}
	}

	private final class PropsListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			propertiesHaveChanged(evt.getPropertyName());
		}
	}
}
