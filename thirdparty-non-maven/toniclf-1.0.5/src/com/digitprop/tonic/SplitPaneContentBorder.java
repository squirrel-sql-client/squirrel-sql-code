package com.digitprop.tonic;


import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;


/**	Border which can be used for the two components nested inside a JSplitPane.
 * 	This border has a shadowed left and bottom edge, making the components contained
 * 	in the JSplitPane appear to be raised.
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
public class SplitPaneContentBorder implements Border
{
	/**	The first color for the shadow gradient */
	private Color fromColor;
	
	/**	The second color for the shadow gradient */
	private Color toColor;
	
	/**	The background color */
	private Color bg;
	
	/**	If true, draws a line around the bordered component */
	private boolean drawBoundaryLine;

	
	/**	Creates an instance. The colors will be the default colors provided
	 * 	by the Tonic Look and Feel, and there will be no additional single-pixel
	 * 	line drawn around the enclosed component.
	 */
	public SplitPaneContentBorder()
	{
		this(null, null, null, false);
	}
	
	
	/**	Creates an instance. The colors will be the default colors provided
	 * 	by the Tonic Look and Feel.
	 * 
	 * 	@param	drawBoundaryLine	If true, there will be an additional 
	 * 										single-pixel line drawn around the 
	 * 										enclosed component.
	 */
	public SplitPaneContentBorder(boolean drawBoundaryLine)
	{
		this(null, null, null, drawBoundaryLine);
	}
	

	/**	Creates an instance. There will be no additional single-pixel
	 * 	line drawn around the enclosed component.
	 * 
	 * 	@param	bg						Background color of the JSplitPane
	 * 	@param	fromColor			First (dark) shadow gradient color
	 * 	@param	toColor				Second (light) shadow gradient color
	 */	
	public SplitPaneContentBorder(Color bg, Color fromColor, Color toColor)
	{
		this(bg, fromColor, toColor, false);
	}
	

	/**	Creates an instance.
	 * 
	 * 	@param	bg						Background color of the JSplitPane
	 * 	@param	fromColor			First (dark) shadow gradient color
	 * 	@param	toColor				Second (light) shadow gradient color
	 * 	@param	drawBoundaryLine	If true, there will be an additional 
	 * 										single-pixel line drawn around the 
	 * 										enclosed component.
	 */		
	public SplitPaneContentBorder(Color bg, Color fromColor, Color toColor, boolean drawBoundaryLine)
	{
		this.drawBoundaryLine=drawBoundaryLine;
		
		if(bg!=null)
			this.bg=bg;
		else
			this.bg=UIManager.getColor("Button.borderColor");
			
		if(fromColor!=null)
			this.fromColor=fromColor;
		else
			this.fromColor=UIManager.getColor("Button.borderColor");
			
		if(toColor!=null)
			this.toColor=toColor;
		else
			this.toColor=UIManager.getColor("control");
	}
	

	/**	Returns the insets of this border */		
	public Insets getBorderInsets(Component c)
	{
		int leftTop=(drawBoundaryLine ? 1 : 0);
		int rightBottom=(drawBoundaryLine ? 4 : 3);
		
		return new Insets(leftTop, leftTop, rightBottom, rightBottom);
	}


	/**	Returns true if this border is opaque */
	public boolean isBorderOpaque()
	{
		return false;
	}

	
	/**	Paints this border.
	 * 	
	 * 	@param	c			The component for which to draw the border
	 * 	@param	g			The graphics context into which to paint
	 * 	@param	x			The left edge of the border
	 * 	@param	y			The top edge of the border
	 * 	@param	width		The width of the border
	 * 	@param	height	The height of the border
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		g.setColor(toColor);
		g.fillRect(x, y+height-3, 3, 3);
		g.fillRect(x+width-3, y, 3, 3);
		
		for(int i=0; i<3; i++)
		{
			g.setColor(blendColors(fromColor, toColor, (double)(i+2)/(double)4));
			g.drawLine(x+width-3+i, y+i+1, x+width-3+i, y+height-3+i);
			g.drawLine(x+i+1, y+height-3+i, x+width-3+i, y+height-3+i);
		}
		
		if(drawBoundaryLine)
		{
			g.setColor(bg);
			g.drawRect(x, y, x+width-4, y+height-4);
		}
	}
	
	
	/**	Blends two colors.
	 * 
	 * 	@param	c1			The first color
	 * 	@param	c2			The second color
	 * 	@param	factor	The ratio between the first and second color. If this is 0.0,
	 * 							the result will be c1, if it is 1.0, the result will be c2.
	 * 
	 * 	@return				A color resulting from blending c1 and c2
	 */
	private Color blendColors(Color c1, Color c2, double factor)
	{
		if(c1==null || c2==null)
		{
			if(c1!=null)
				return c1;
			else if(c2!=null)
				return c2;
			else
				return Color.BLACK;
		}
		
		int r=(int)(c2.getRed()*factor+c1.getRed()*(1.0-factor));
		int g=(int)(c2.getGreen()*factor+c1.getGreen()*(1.0-factor));
		int b=(int)(c2.getBlue()*factor+c1.getBlue()*(1.0-factor));
		
		return new Color(r,g,b);
	}
}
