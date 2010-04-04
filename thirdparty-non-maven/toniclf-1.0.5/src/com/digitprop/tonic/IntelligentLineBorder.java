package com.digitprop.tonic;


import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;



/**	A single-pixel line border, which displays each edge only if that edge does not
 * 	touch the edge of the parent component. This prevents double borders for
 * 	nested components.<p>
 * 
 * 	This behaviour can be optionally switched on and off by instantiating this
 * 	class with the appropriate constructor parameters.
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
public class IntelligentLineBorder implements Border
{
	/**	The color of this border */
	private Color			color;
	
	/**	If true, hide edges touching the parent component's edges */
	private boolean		isHidingEdges;
	
	
	/**	Creates an instance with BLACK color.
	 * 
	 * 	@see	#IntelligentLineBorder(Color) 
	 */
	public IntelligentLineBorder()
	{
		this(Color.BLACK);
	}
	

	/**	Creates an instance with the specified color.
	 * 	
	 * 	@param	color					The color of this border
	 */
	public IntelligentLineBorder(Color color)
	{
		this(color, true);
	}
	

	/**	Creates an instance with BLACK color.
	 * 	
	 *		@param	isHidingEdges		If true, this border is hiding thos edges
	 *											for which the underlying component touches
	 *											an edge of the parent component. Otherwise,
	 *											the border will paint all edges. 
	 */
	public IntelligentLineBorder(boolean isHidingEdges)
	{
		this(Color.BLACK, isHidingEdges);
	}

		
	/**	Creates an instance with the specified color.
	 * 	
	 * 	@param	color					The color of this border
	 *		@param	isHidingEdges		If true, this border is hiding thos edges
	 *											for which the underlying component touches
	 *											an edge of the parent component. Otherwise,
	 *											the border will paint all edges. 
	 */
	public IntelligentLineBorder(Color color, boolean isHidingEdges)
	{
		this.color=color;
		this.isHidingEdges=isHidingEdges;
	}
	
	
	/**	Returns the insets of this border for the specified component. The insets
	 * 	depend upon whether edges of the specified component touch any edges
	 * 	of the parent component.
	 */
	public Insets getBorderInsets(Component c)
	{
		int left=!isTouchingLeftEdge(c) ? 1 : 0;
		int right=!isTouchingRightEdge(c) ? 1 : 0;
		int top=!isTouchingTopEdge(c) ? 1 : 0;
		int bottom=!isTouchingBottomEdge(c) ? 1 : 0;
		
		return new Insets(top, left, bottom, right);
	}
	
		
	/**	Returns true if the specified component's left edge touches
	 * 	the left edge of the parent component.
	 */
	protected boolean isTouchingLeftEdge(Component c)
	{
		if(!isHidingEdges)
			return false;
			
		Component parent=getParent(c);
		if(parent==null)
			return false;
			
		return c.getX()==c.getParent().getInsets().left;
	}
	

	/**	Returns true if the specified component's top edge touches
	 * 	the top edge of the parent component.
	 */	
	protected boolean isTouchingTopEdge(Component c)
	{
		if(!isHidingEdges)
			return false;
			
		Component parent=getParent(c);
		if(parent==null)
			return false;
			
		return c.getY()==c.getParent().getInsets().top;
	}	
	
	
	/**	Returns true if the specified component's right edge touches
	 * 	the left edge of the parent component.
	 */
	protected boolean isTouchingRightEdge(Component c)
	{
		if(!isHidingEdges)
			return false;
			
		Component parent=getParent(c);
		if(parent==null)
			return false;
			
		return c.getX()+c.getWidth()==c.getParent().getWidth()-c.getParent().getInsets().right;
	}


	/**	Returns true if the specified component's bottom edge touches
	 * 	the bottom edge of the parent component.
	 */
	protected boolean isTouchingBottomEdge(Component c)
	{
		if(!isHidingEdges)
			return false;
			
		Component parent=getParent(c);
		if(parent==null)
			return false;
			
		return c.getY()+c.getHeight()==c.getParent().getHeight()-c.getParent().getInsets().bottom;
	}	
	
	
	/**	Returns the parent component for the specified component, or null
	 * 	if the parent component could not be found. 
	 */
	protected Component getParent(Component c)
	{
		Component parent=c.getParent();
		while(true)
		{
			if(parent==null || parent==c)
				return null;
				
			if(parent instanceof JComponent)
			{
				Border border=((JComponent)parent).getBorder();
				if(border!=null)
					return parent;
			}
			
			Component newParent=parent.getParent();
			if(newParent==null)
				return parent;
			else
				parent=newParent;
		}
	}
	
	
	/**	Returns true if this border is opaque */
	public boolean isBorderOpaque()
	{
		return true;
	}
	
	
	/**	Paints this border.
	 * 
	 * 	@param	c			The component which contains this border
	 * 	@param	g			The graphics context into which to paint
	 * 	@param	x			The left edge of the border
	 * 	@param	y			The top edge of the border
	 * 	@param	width		The width of the border
	 * 	@param	height	The height of the border
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		g.setColor(color);
		if(!isTouchingLeftEdge(c))
			g.drawLine(x, y, x, y+height);
		if(!isTouchingTopEdge(c))
			g.drawLine(x, y, x+width, y);
		if(!isTouchingRightEdge(c))
			g.drawLine(x+width-1, y, x+width-1, y+height);
		if(!isTouchingBottomEdge(c))
			g.drawLine(x, y+height-1, x+width, y+height-1);	
	}
}
