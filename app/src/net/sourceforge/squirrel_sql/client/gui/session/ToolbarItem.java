/*
 * Copyright (C) 2008 Rob Manning
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
package net.sourceforge.squirrel_sql.client.gui.session;

import javax.swing.Action;

/**
 * An immutable helper class that encapsulates different types of objects (Actions and Separators) in a
 * toolbar. This is helpful for storing these different types of objects in a type-safe list.
 */
public class ToolbarItem
{
	private Action action = null;

	/**
	 * @return the action
	 */
	public Action getAction()
	{
		return action;
	}

	/**
	 * @return the isSeparator
	 */
	public boolean isSeparator()
	{
		return isSeparator;
	}

	private boolean isSeparator = true;

	/**
	 * Default constructor indicates this is a separator.
	 */
	public ToolbarItem()
	{

	}

	/**
	 * This constructor indicates the item in an Action.
	 * 
	 * @param action
	 *           the Action that this item represents.
	 */
	public ToolbarItem(Action action)
	{
		this.action = action;
		this.isSeparator = false;
	}

}
