package com.digitprop.tonic;


import java.awt.*;

import javax.swing.*;


/**	Represents the standard border for AbstractButtons. This border 
 * 	determines whether the underlying button touches the edge of
 * 	the parent component, and draws an edge only where the button
 * 	is not touching the parent's edge.<p>
 * 
 * 	In this way, it avoids double edges.
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
public class ButtonBorder extends IntelligentLineBorder
{
	/**	Creates an instance */
	public ButtonBorder()
	{
		super(false);
	}
	
	
	/**	Returns the insets of this button. They depend upon whether
	 * 	the button touches the parent's edges or not .
	 */
	public Insets getBorderInsets(Component c)
	{
		Insets m=null;
		if(c instanceof JButton)
		{
			m=((JButton)c).getMargin();			
		}
			
		int left=(!isTouchingLeftEdge(c) ? 4 : 3) + m.left;
		int right=(!isTouchingRightEdge(c) ? 4 : 3) +m.right;
		int top=(!isTouchingTopEdge(c) ? 4 : 3) + m.top;
		int bottom=(!isTouchingBottomEdge(c) ? 4 : 3) +m.bottom;
		
		return new Insets(top, left, bottom, right);
	}
	
	
	/**	Returns true if this border is opaque, false otherwise */
	public boolean isBorderOpaque()
	{
		return true;
	}
	
	
	/**	Paints this border */
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		if(c instanceof AbstractButton)
		{
			AbstractButton b=(AbstractButton)c;
					
			int left=!isTouchingLeftEdge(c) ? 1 : 0;
			int right=!isTouchingRightEdge(c) ? 1 : 0;
			int top=!isTouchingTopEdge(c) ? 1 : 0;
			int bottom=!isTouchingBottomEdge(c) ? 1 : 0;					
						
			if(!b.getModel().isPressed() && !((c instanceof JToggleButton) && ((JToggleButton)c).isSelected()))
			{
				right++;
				bottom++;
			}			
			
			g.setColor(c.getBackground());
			g.fillRect(x+left, y+top, width-left-right, 3);
			g.fillRect(x+left, y+top, 3, height-top-bottom);
			g.fillRect(x+width-4, y+top, 3, height-top-bottom);
			g.fillRect(x+left, y+height-4, width-left-right, 3);
			
			if(b.hasFocus())
				g.setColor(UIManager.getColor("Button.focusBorderColor"));
			else if(b.isEnabled())
				g.setColor(UIManager.getColor("Button.borderColor"));
			else
				g.setColor(UIManager.getColor("Button.disabledBorderColor"));
			
			boolean drawPressed=(b.getModel().isPressed() || ((c instanceof JToggleButton) && ((JToggleButton)c).isSelected()));
					
			if(!isTouchingLeftEdge(c))
			{
				if(drawPressed)
					g.drawLine(x+1, y+1, x+1, y+height-2);
				else
					g.drawLine(x, y, x, y+height-2);
			}
			if(!isTouchingTopEdge(c))
			{
				if(drawPressed)
					g.drawLine(x+1, y+1, x+width-2, y+1);
				else
					g.drawLine(x, y, x+width-2, y);
			}
			if(!isTouchingRightEdge(c))
			{
				if(drawPressed)	
					g.drawLine(x+width-1, y+1, x+width-1, y+height);
				else
					g.drawLine(x+width-2, y+1, x+width-2, y+height);
			}
			if(!isTouchingBottomEdge(c))
			{
				if(drawPressed)
					g.drawLine(x+1, y+height-1, x+width, y+height-1);
				else
					g.drawLine(x+1, y+height-2, x+width-1, y+height-2);
			}					
			
			if(b.getModel().isPressed() || ((c instanceof JToggleButton) && ((JToggleButton)c).isSelected()))
			{
			}
			else						
			{			
				g.setColor(UIManager.getColor("Button.borderColor2"));
				g.drawLine(x+1, y+height-1, x+width, y+height-1);
				g.drawLine(x+width-1, y+1, x+width-1, x+height);				
			}
		}
	}
}
