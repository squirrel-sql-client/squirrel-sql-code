package net.sourceforge.squirrel_sql.client.gui.builders;
/*
 * Copyright (C) 2003 Colin Bell
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
import javax.swing.JTabbedPane;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
/**
 * This singleton factory creates UI objects.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class UIFactory
{
	/** Singleton instance. */
	private static UIFactory s_instance;

	/** Application preferences. */
	private SquirrelPreferences _prefs;

	/**
	 * Retrieve the single instance of this class.
	 * 
	 * @return	the single instance of this class.
	 */
	public static UIFactory getInstance()
	{
		if (s_instance == null)
		{
			throw new IllegalArgumentException("UIFactory has not been initialized");
		}

		return s_instance;
	}

	/**
	 * Initialize the single instance of this class.
	 * 
	 * @param	prefs	Application preferences.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>SquirrelPreferences</TT> passed.
	 * 
	 * @throws	IllegalStateException
	 * 			Thrown if initialization has already been done.
	 */
	public synchronized static void initialize(SquirrelPreferences prefs)
	{
		if (s_instance != null)
		{
			throw new IllegalStateException("UIFactory has alerady been initialized");
		}
		s_instance = new UIFactory(prefs);
	}

	/**
	 * Default ctot. private as class is a singleton.
	 */
	private UIFactory(SquirrelPreferences prefs)
	{
		super();
		if (prefs == null)
		{
			throw new IllegalArgumentException("SquirrelPreferences == null");
		}
		_prefs = prefs;
	}

	/**
	 * Create a tabbed pane with a tab placement of <TT>JTabbedPane.TOP</TT>.
	 * 
	 * @erturn	The new tabbed pane.
	 */
	public JTabbedPane createTabbedPane()
	{
		return createTabbedPane(JTabbedPane.TOP);
	}

	/**
	 * Create a tabbed pane specifying the tab placement.
	 * 
	 * @param	Tab Placement. See <TT>JTabbedPane</TT> javadoc for more
	 *			information.
	 * 
	 * @erturn	The new tabbed pane.
	 */
	public JTabbedPane createTabbedPane(int tabPlacement)
	{
		final JTabbedPane pnl = new SquirrelTabbedPane(_prefs);
		pnl.setTabPlacement(tabPlacement);
		return pnl;
	}
}
