package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JTabbedPane;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

public class SquirrelTabbedPane extends JTabbedPane {
	private SquirrelPreferences _prefs;
	private Method _setter;
	private int SCROLL;
	private int WRAP;

	public SquirrelTabbedPane(SquirrelPreferences prefs) {
		if (prefs == null) {
			throw new IllegalArgumentException("SquirrelPreferences == null");
		}
		_prefs = prefs;
		setupFromPrefs();
	}

	private void setupFromPrefs() {
		try {
			/** Look for the new JDK 1.4 fields indicating tab types. */
			Class clazz = getClass();
			SCROLL = clazz.getField("SCROLL_TAB_LAYOUT").getInt(this);
			WRAP = clazz.getField("WRAP_TAB_LAYOUT").getInt(this);
			_setter = clazz.getMethod("setTabLayoutPolicy", new Class[] {int.class});
			_setter.invoke(this, new Object[] {
					new Integer(_prefs.useScrollableTabbedPanes() ? SCROLL : WRAP)});

			_prefs.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					String propName = evt.getPropertyName();
					if (propName == null ||
							propName.equals(SquirrelPreferences.IPropertyNames.SCROLLABLE_TABBED_PANES)) {
						try {
							_setter.invoke(SquirrelTabbedPane.this, new Object[] {
									new Integer(_prefs.useScrollableTabbedPanes() ? SCROLL : WRAP)});
									
						} catch (IllegalAccessException ex) {
						} catch (InvocationTargetException ex) {
						}
					}
				}
			});
		} catch (IllegalAccessException ex) {
		} catch (NoSuchFieldException ex) {
		} catch (NoSuchMethodException ex) {
		} catch (InvocationTargetException ex) {
		}
	}
}
