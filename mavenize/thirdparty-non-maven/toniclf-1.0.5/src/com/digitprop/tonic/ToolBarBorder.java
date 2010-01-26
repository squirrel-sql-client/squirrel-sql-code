package com.digitprop.tonic;


import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.*;
import javax.swing.border.*;


/**	Border for tool bars.
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
public class ToolBarBorder implements Border
{
	/**	Returns the insets for the specified component */
	public Insets getBorderInsets(Component c) 
	{
		return new Insets(1,1,1,1); 
	}
	
	
	/**	Returns true if this border is opaque */
	public boolean isBorderOpaque()
	{
		return true;
	}


	/**	Paints this border for the specified component.
	 * 
	 * 	@param	c				The component for which to paint the border
	 * 	@param	g				The graphics context into which to paint
	 * 	@param	x				The left edge of the border
	 * 	@param	y				The top edge of the border
	 * 	@param	width			The width of the border
	 * 	@param	height		The height of the border
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		if(c instanceof AbstractButton)
		{
			AbstractButton b=(AbstractButton)c;
			

			if(b.getModel().isPressed() || ((c instanceof JToggleButton) && ((JToggleButton)c).isSelected()))
			{
				g.setColor(UIManager.getColor("ToolButton.activeBorder"));				
				g.drawLine(x, y, x+width-1, y);
				g.drawLine(x, y, x, y+height-1);
			
				g.drawLine(x, y+height-1, x+width-1, y+height-1);
				g.drawLine(x+width-1, y, x+width-1, y+height-1);
			}
			else if(b.getModel().isRollover())
			{
				g.setColor(UIManager.getColor("ToolButton.activeBorder"));
				g.drawLine(x, y, x+width-1, y);
				g.drawLine(x, y, x, y+height-1);
			
				g.drawLine(x, y+height-1, x+width-1, y+height-1);
				g.drawLine(x+width-1, y, x+width-1, y+height-1);
			}
		}
	}
}
