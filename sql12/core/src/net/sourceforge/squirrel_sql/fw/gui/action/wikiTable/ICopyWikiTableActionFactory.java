/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.gui.action.wikiTable;

import javax.swing.JMenuItem;

/**
 * Factory, which creates the {@link CopyWikiTableAction} menu structure.
 * We can have a some build-in and user-specific configurations for WIKI-Tables. This factory provides a usable {@link JMenuItem} for the action.
 * Maybe, this will be a tree or a just a menu item. Disabled configurations are not used.
 * @author Stefan Willinger
 *
 */
public interface ICopyWikiTableActionFactory {

	/**
	 * Create a menu item, which represents the usable actions for copying as WIKI table.
	 * A configuration is only considered, if it is enabled.
	 * If there is no configuration available, then a disabled JMenueItem is created.
	 * When only one configuration exists, then a JMenueItem is created.
	 * Are more than one configurations available, then it creates a sub menu.
	 * @param callback the callback, which can provide the JTable.
	 * @return A {@link JMenuItem} structure with the available actions.
	 */
	public abstract JMenuItem createMenueItem(ITableActionCallback callback);

}