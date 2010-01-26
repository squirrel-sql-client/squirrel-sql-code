package com.digitprop.tonic;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;

import javax.swing.*;

/**
 *	A button with an arrow. This class is used for JScrollBars.
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
class ArrowButton extends JButton implements SwingConstants
{
	/**	The direction into which the arrow points */
	protected int 			direction;

	/**	Shadow color */
	private Color 			shadow;
	
	/**	Dark shadow color */
	private Color 			darkShadow;
	
	/**	Highlighting color */
	private Color 			highlight;
	
	/**	If true, the left edge will be drawn */
	protected boolean 		drawLeftBorder=true;
	
	/**	If true, the right edge will be drawn */
	protected boolean drawRightBorder=true;
	
	/**	If true, the top edge will be drawn */
	protected boolean drawTopBorder=true;
	
	/**	If true, the bottom edge will be drawn */
	protected boolean drawBottomBorder=true;
	
	
	/**	Creates an instance.
	 * 
	 * 	@param	direction		The direction into which the arrow points
	 * 	@param	background		The background color
	 * 	@param	shadow			The shadow color
	 * 	@param	darkShadow		The color of dark shadows
	 * 	@param	highlight		The highlight color
	 */
	public ArrowButton(int direction, Color background, Color shadow, Color darkShadow, Color highlight)
	{
		super();
		
		setRequestFocusEnabled(false);
		setDirection(direction);
		setBackground(background);
		this.shadow= shadow;
		this.darkShadow= darkShadow;
		this.highlight= highlight;
	}


	/**	Creates an instance with the arrow pointing in the specified direction.
	 * 	The colors for the shadow, dark shadow and highlight will be retrieved
	 * 	from the UIManager.
	 * 
	 * 	@param	direction		The direction into which the arrow points 
	 */
	public ArrowButton(int direction)
	{
		this(
			direction,
			UIManager.getColor("control"),
			UIManager.getColor("controlShadow"),
			UIManager.getColor("controlDkShadow"),
			UIManager.getColor("controlLtHighlight"));
	}


	/**	Indicates whether the bottom border is to be drawn or not */
	public void setDrawBottomBorder(boolean drawBottomBorder)
	{
		this.drawBottomBorder=drawBottomBorder;
	}
	
	
	/**	Returns the direction into which the arrow points. This is one
	 * 	of the SwingConstants.
	 * 
	 *  	@see SwingConstants
	 */	
	public int getDirection()
	{
		return direction;
	}
	
	
	/**	Sets the direction into which the arrow points. This is one
	 * 	of the SwingConstants.
	 * 
	 * 	@see SwingConstants
	 */
	public void setDirection(int dir)
	{
		direction= dir;
	}


	/**	Paints the button */
	public void paint(Graphics g)
	{
		Color origColor;
		boolean isPressed, isEnabled;
		int w, h, size;

		w= getSize().width;
		h= getSize().height;
		origColor= g.getColor();
		isPressed= getModel().isPressed();
		isEnabled= isEnabled();

		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);

		// Draw the proper Border
		g.setColor(UIManager.getColor("Button.borderColor"));
		if(drawLeftBorder)
			g.drawLine(0, 0, 0, h);
		if(drawTopBorder)
			g.drawLine(0, 0, w, 0);
		if(drawRightBorder)
			g.drawLine(w-1, 0, w-1, h);
		if(drawBottomBorder)
			g.drawLine(0, h-1, w-1, h-1);

		// If there's no room to draw arrow, bail
		if (h < 5 || w < 5)
		{
			g.setColor(origColor);
			return;
		}

		if (isPressed)
		{
			g.translate(1, 1);
		}

		// Draw the arrow
		size= Math.min((h - 4) / 3, (w - 4) / 3);
		size= Math.max(size, 2);
		paintTriangle(
			g,
			(w - size) / 2,
			(h - size) / 2,
			size,
			direction,
			isEnabled);

		// Reset the Graphics back to it's original settings
		if (isPressed)
		{
			g.translate(-1, -1);
		}
		g.setColor(origColor);

	}


	/**	Returns the preferred size of this button */
	public Dimension getPreferredSize()
	{
		return new Dimension(16, 16);
	}


	/**	Returns the minimum size */
	public Dimension getMinimumSize()
	{
		return new Dimension(5, 5);
	}


	/**	Returns the maximum size of this button */
	public Dimension getMaximumSize()
	{
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

		
	/**	Returns true, if this button can get the focus */
	public boolean isFocusTraversable()
	{
		return false;
	}


	/**	Draws the actual arrow image. 
	 * 
	 * 	@param	g					Graphics context into which to draw
	 * 	@param	x					x position of the arrow
	 * 	@param	y					y position of the arrow
	 * 	@param	size				Size of the arrow
	 * 	@param	direction		Direction into which the arrow points
	 * 	@param	isEnabled		If true, the button is enabled
	 */
	public void paintTriangle(Graphics g, int x, int y, int size, int direction, boolean isEnabled)
	{
		Color oldColor= g.getColor();
		int mid, i, j;

		j= 0;
		size= Math.max(size, 2);
		mid= (size / 2) - 1;

		g.translate(x, y);
		if (isEnabled)
			g.setColor(darkShadow);
		else
			g.setColor(shadow);

		switch (direction)
		{
			case NORTH :
				for (i= 0; i < size; i++)
				{
					g.drawLine(mid - i, i, mid + i, i);
				}
				if (!isEnabled)
				{
					g.setColor(highlight);
					g.drawLine(mid - i + 2, i, mid + i, i);
				}
				break;
				
			case SOUTH :
				if (!isEnabled)
				{
					g.translate(1, 1);
					g.setColor(highlight);
					for (i= size - 1; i >= 0; i--)
					{
						g.drawLine(mid - i, j, mid + i, j);
						j++;
					}
					g.translate(-1, -1);
					g.setColor(shadow);
				}

				j= 0;
				for (i= size - 1; i >= 0; i--)
				{
					g.drawLine(mid - i, j, mid + i, j);
					j++;
				}
				break;
				
			case WEST :
				for (i= 0; i < size; i++)
				{
					g.drawLine(i, mid - i, i, mid + i);
				}
				if (!isEnabled)
				{
					g.setColor(highlight);
					g.drawLine(i, mid - i + 2, i, mid + i);
				}
				break;
				
			case EAST :
				if (!isEnabled)
				{
					g.translate(1, 1);
					g.setColor(highlight);
					for (i= size - 1; i >= 0; i--)
					{
						g.drawLine(j, mid - i, j, mid + i);
						j++;
					}
					g.translate(-1, -1);
					g.setColor(shadow);
				}

				j= 0;
				for (i= size - 1; i >= 0; i--)
				{
					g.drawLine(j, mid - i, j, mid + i);
					j++;
				}
				break;
		}
		
		g.translate(-x, -y);
		g.setColor(oldColor);
	}
}
