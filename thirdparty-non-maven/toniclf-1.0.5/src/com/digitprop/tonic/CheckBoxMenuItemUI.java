package com.digitprop.tonic;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.plaf.*;


/**	UI delegate for CheckBoxMenuItems.
 * 
 * 	@author	Markus Fischer
 *
 *  	<p>This software is under the <a href="http://www.gnu.org/copyleft/lesser.html" target="_blank">GNU Lesser General Public License</a>
 */

/*
 * ------------------------------------------------------------------------
 * Copyright (C) 2004 Markus Fischer
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 * MA 02111-1307  USA
 * 
 * You can contact the author at:
 *    Markus Fischer
 *    www.digitprop.com
 *    info@digitprop.com
 * ------------------------------------------------------------------------
 */
public class CheckBoxMenuItemUI extends com.digitprop.tonic.MenuItemUI
{
	/**	Returns the UI delegate for the specified component */
	public static ComponentUI createUI(JComponent c)
	{
		return new com.digitprop.tonic.CheckBoxMenuItemUI();
	}


	/**	Returns the property prefix for checkbox menu items */
	protected String getPropertyPrefix()
	{
		return "CheckBoxMenuItem";
	}


	/**	Processes the mouse event for the specified menu item.
	 * 
	 * 	@param	item			The item for which to process a mouse event
	 * 	@param	e				The mouse event to be processed
	 * 	@param	path			The menu path to the item 
	 * 	@param	manager		The menu selection manager which applies
	 *  								to the item
	 */
	public void processMouseEvent(JMenuItem item, MouseEvent e, MenuElement path[], MenuSelectionManager manager)
	{
		Point p= e.getPoint();
		if (p.x >= 0
			&& p.x < item.getWidth()
			&& p.y >= 0
			&& p.y < item.getHeight())
		{
			if (e.getID() == MouseEvent.MOUSE_RELEASED)
			{
				manager.clearSelectedPath();
				item.doClick(0);
			}
			else
				manager.setSelectedPath(path);
		}
		else if (item.getModel().isArmed())
		{
			MenuElement newPath[]= new MenuElement[path.length - 1];
			int i, c;
			for (i= 0, c= path.length - 1; i < c; i++)
				newPath[i]= path[i];
			manager.setSelectedPath(newPath);
		}
	}
}
