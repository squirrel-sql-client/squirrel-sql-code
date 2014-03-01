package net.sourceforge.squirrel_sql.fw.util;

import java.util.MissingResourceException;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

/*
 * Copyright (C) 2011 Rob Manning
 * manningr@users.sourceforge.net
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

public interface IResources
{

	public static final String ACCELERATOR_STRING = "SQuirreLAcceleratorString";

	KeyStroke getKeyStroke(Action action);

	JMenuItem addToPopupMenu(Action action, javax.swing.JPopupMenu menu) throws MissingResourceException;

	JCheckBoxMenuItem addToMenuAsCheckBoxMenuItem(Action action, JMenu menu) throws MissingResourceException;

	JCheckBoxMenuItem addToMenuAsCheckBoxMenuItem(Action action, JPopupMenu popupMenu);

	JMenuItem addToMenu(Action action, JMenu menu) throws MissingResourceException;

	JMenu createMenu(String menuKey) throws MissingResourceException;

	/**
	 * Setup the passed action from the resource bundle.
	 * 
	 * @param action
	 *        Action being setup.
	 * 
	 * @throws IllegalArgumentException
	 *         thrown if <TT>null</TT> <TT>action</TT> passed.
	 */
	void setupAction(Action action, boolean showColoricons);

	ImageIcon getIcon(String keyName);

	ImageIcon getIcon(Class<?> objClass, String propName);

	ImageIcon getIcon(String keyName, String propName);

	String getString(String key);

	void configureMenuItem(Action action, JMenuItem item) throws MissingResourceException;

}