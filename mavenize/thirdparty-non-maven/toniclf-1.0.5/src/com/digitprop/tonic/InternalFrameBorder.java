package com.digitprop.tonic;


import java.awt.*;

import javax.swing.*;


/**	Used as a border for JInternalFrames.
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
public class InternalFrameBorder extends IntelligentLineBorder
{
	/**	The color of the border for inactive JInternalFrames */
	private Color inactiveColor;

	/**	The color of the border for active JInternalFrames */	
	private Color activeColor;
	
	/**	The color of the border's 1-pixel lines */
	private Color	borderColor;
	
	
	/**	Creates an instance.
	 * 
	 * 	@param	borderColor		The border line color
	 * 	@param	inactiveColor	The fill color for inactive JInternalFrames
	 * 	@param	activeColor		The fill color for active JInternalFrames
	 */
	public InternalFrameBorder(Color borderColor, Color inactiveColor, Color activeColor)
	{
		this.borderColor=borderColor;
		this.inactiveColor=inactiveColor;
		this.activeColor=activeColor;
	}
	
	
	/**	Returns the insets for the specified component */
	public Insets getBorderInsets(Component c)
	{
		return new Insets(3, 3, 3, 3);
	}
	
	
	/**	Returns true if this border is opaque */
	public boolean isBorderOpaque()
	{
		return true;
	}
	
	
	/**	Paints this border for the specified component.
	 * 
	 * 	@param	c			The component for which to paint the border
	 * 	@param	g			The graphics context into which to paint
	 * 	@param	x			The left edge of the border
	 * 	@param	y			The right edge of the border
	 * 	@param	width		The width of the border
	 * 	@param	height	The height of the border
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		boolean isActive=false;
		
		if(c instanceof JInternalFrame)
		{
			JInternalFrame f=(JInternalFrame)c;
			
			if(f.isSelected())
				isActive=true;
		}
		
		g.setColor(borderColor);
		g.drawRect(x, y, width-1, height-1);
		g.drawRect(x+2, y+2, width-5, height-5);
		
		if(isActive)
			g.setColor(activeColor);
		else
			g.setColor(inactiveColor);
			
		g.drawRect(x+1, y+1, width-3, height-3);
	}
}
