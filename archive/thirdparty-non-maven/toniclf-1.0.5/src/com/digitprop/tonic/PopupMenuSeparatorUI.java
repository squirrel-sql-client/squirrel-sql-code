package com.digitprop.tonic;


import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;


/**	UI delegate for JPopupMenuSeparators.
 * 
 * 	@author	Markus Fischer
 *
 * ------------------------------------------------------------------------
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
public class PopupMenuSeparatorUI extends SeparatorUI
{
	/**	Creates and returns the UI delegate for the specified component */
	public static ComponentUI createUI(JComponent c)
	{
		return new PopupMenuSeparatorUI();
	}


	/**	Paints the specified component */
	public void paint(Graphics g, JComponent c)
	{
		Dimension s= c.getSize();

		g.setColor(c.getBackground());
		g.drawLine(0, 0, s.width, 0);
		g.setColor(c.getForeground());
		g.drawLine(6, 0, s.width-10, 0);
	}


	/**	Returns the preferred size of this component */
	public Dimension getPreferredSize(JComponent c)
	{
		return new Dimension(0, 1);
	}
}