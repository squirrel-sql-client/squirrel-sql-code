package com.digitprop.tonic;


import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;


/**	A matte border which can be switched on and off.
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
public class OptionalMatteBorder implements Border
{
	/**	If true, the border will be painted */
	private boolean drawBorder=false;
	
	/**	Color of the border */
	private Color color;
	
	/**	The insets for this border */
	private int insets;
	
	
	/**	Creates an instance.
	 * 
	 * 	@param	color		The color of this border
	 * 	@param	insets	The width of the insets in pixels
	 */
	public OptionalMatteBorder(Color color, int insets)
	{
		this.color=color;
		this.insets=insets;
	}
	
	
	/**	Returns the insets for the specified component */
	public Insets getBorderInsets(Component c)
	{
		return new Insets(insets+1, insets+1, insets+2, insets+2);
	}
	
	
	/**	Returns true if this border is opaque */
	public boolean isBorderOpaque()
	{
		return true;
	}
	
	
	/**	Sets whether this border is to be painted or not */
	public void setDrawBorder(boolean drawBorder)
	{
		this.drawBorder=drawBorder;
	}
	
	
	/**	Paints this border.
	 * 
	 * 	@param	c			The component for which to draw the border
	 * 	@param	g			The graphics context into which to draw
	 * 	@param	x			The left edge of the border
	 * 	@param	y			The top edge of the border
	 * 	@param	width		The width of the border
	 * 	@param	height	The height of the border
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		if(drawBorder)
		{
			int shift=0;
			boolean drawPressed=false;
			if(c instanceof AbstractButton)
			{
				AbstractButton b=(AbstractButton)c;
				drawPressed=(b.getModel().isPressed() || ((b instanceof JToggleButton) && ((JToggleButton)b).isSelected()));
			}
				
			g.setColor(color);
			if(drawPressed)
				g.drawRect(x+1, y+1, width-2, width-2);
			else
				g.drawRect(x, y, width-2, height-2);	
		}
	}
}
