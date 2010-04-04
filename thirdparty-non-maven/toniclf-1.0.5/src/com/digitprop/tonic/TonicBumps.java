package com.digitprop.tonic;


import java.awt.*;
import java.awt.image.*;

import java.util.*;

import javax.swing.*;



/**	Implements the bumps used for the Tonic Look and Feel.
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
class TonicBumps implements Icon
{
	/** Number of bumps in x direction */
	protected int xBumps;
	
	/** Number of bumps in y direction */
	protected int yBumps;
	
	/**	Highlight color for the bumps */
	protected Color topColor;
	
	/**	Shadow color for the bumps */
	protected Color shadowColor;
	
	/**	Background color for the bumps */
	protected Color backColor;

	/**	List of all cached bump buffers */
	protected static Vector buffers= new Vector();
	
	/**	Current bump buffer */
	protected BumpBuffer buffer;


	/**	Creates an instance for the specified area */
	public TonicBumps(Dimension bumpArea)
	{
		this(bumpArea.width, bumpArea.height);
	}


	/**	Creates an instance for the specified area */
	public TonicBumps(int width, int height)
	{
		this(width, height, TonicLookAndFeel.getPrimaryControlHighlight(),
			TonicLookAndFeel.getPrimaryControlDarkShadow(), TonicLookAndFeel.getPrimaryControlShadow());
	}

	
	/**	Creates an instance for the specified area.
	 * 
	 * 	@param	width 			The width of the area
	 * 	@param	height			The height of the area
	 * 	@param	newTopColor		The highlight color of the bumps
	 * 	@param	newShadowColor	The shadow color of the bumps
	 * 	@param	newBackColor	The background color of the bumps
	 */
	public TonicBumps(int width, int height, Color newTopColor, Color newShadowColor, Color newBackColor)
	{
		setBumpArea(width, height);
		setBumpColors(newTopColor, newShadowColor, newBackColor);
	}
	
	
	/**	Returns the buffer for the specified graphics configuration and colors */
	private BumpBuffer getBuffer(GraphicsConfiguration gc, Color aTopColor, Color aShadowColor, Color aBackColor)
	{
		if (buffer != null && buffer.hasSameConfiguration(gc, aTopColor, aShadowColor, aBackColor))
		{
			return buffer;
		}
		
		BumpBuffer result= null;

		Enumeration elements= buffers.elements();
		while (elements.hasMoreElements())
		{
			BumpBuffer aBuffer= (BumpBuffer) elements.nextElement();
			if (aBuffer.hasSameConfiguration(gc, aTopColor, aShadowColor, aBackColor))
			{
				result= aBuffer;
				break;
			}
		}
		
		if (result == null)
		{
			result= new BumpBuffer(gc, topColor, shadowColor, backColor);
			buffers.addElement(result);
		}
		return result;
	}


	/**	Sets the area to be drawn */
	public void setBumpArea(Dimension bumpArea)
	{
		setBumpArea(bumpArea.width, bumpArea.height);
	}

	
	/**	Sets the area to be drawn */
	public void setBumpArea(int width, int height)
	{
		xBumps= width / 2;
		yBumps= height / 2;
	}


	/**	Sets new colors for the bumps */
	public void setBumpColors(Color newTopColor, Color newShadowColor, Color newBackColor)
	{
		topColor= newTopColor;
		shadowColor= newShadowColor;
		backColor= newBackColor;
	}


	/**	Paint the icon for the specified component */
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		GraphicsConfiguration gc=
			(g instanceof Graphics2D)
				? (GraphicsConfiguration) ((Graphics2D) g).getDeviceConfiguration()
				: null;

		buffer= getBuffer(gc, topColor, shadowColor, backColor);

		int bufferWidth= buffer.getImageSize().width;
		int bufferHeight= buffer.getImageSize().height;
		int iconWidth= getIconWidth();
		int iconHeight= getIconHeight();
		int x2= x + iconWidth;
		int y2= y + iconHeight;
		int savex= x;

		while (y < y2)
		{
			int h= Math.min(y2 - y, bufferHeight);
			for (x= savex; x < x2; x += bufferWidth)
			{
				int w= Math.min(x2 - x, bufferWidth);
				g.drawImage(
					buffer.getImage(),
					x,
					y,
					x + w,
					y + h,
					0,
					0,
					w,
					h,
					null);
			}
			y += bufferHeight;
		}
	}


	/**	Returns the width of one bump */
	public int getIconWidth()
	{
		return xBumps * 2;
	}


	/**	Returns the height of one bump */
	public int getIconHeight()
	{
		return yBumps * 2;
	}
}


/**	Buffers the visual appearance of a bump area */
class BumpBuffer
{
	static final int IMAGE_SIZE= 64;
	static Dimension imageSize= new Dimension(IMAGE_SIZE, IMAGE_SIZE);

	transient Image image;
	Color topColor;
	Color shadowColor;
	Color backColor;
	private GraphicsConfiguration gc;

	public BumpBuffer(
		GraphicsConfiguration gc,
		Color aTopColor,
		Color aShadowColor,
		Color aBackColor)
	{
		this.gc= gc;
		topColor= aTopColor;
		shadowColor= aShadowColor;
		backColor= aBackColor;
		createImage();
		fillBumpBuffer();
	}

	public boolean hasSameConfiguration(
		GraphicsConfiguration gc,
		Color aTopColor,
		Color aShadowColor,
		Color aBackColor)
	{
		if (this.gc != null)
		{
			if (!this.gc.equals(gc))
			{
				return false;
			}
		}
		else if (gc != null)
		{
			return false;
		}
		return topColor.equals(aTopColor)
			&& shadowColor.equals(aShadowColor)
			&& backColor.equals(aBackColor);
	}

	/**
	 * Returns the Image containing the bumps appropriate for the passed in
	 * <code>GraphicsConfiguration</code>.
	 */
	public Image getImage()
	{
		return image;
	}

	public Dimension getImageSize()
	{
		return imageSize;
	}

	/**
	 * Paints the bumps into the current image.
	 */
	private void fillBumpBuffer()
	{
		Graphics g= image.getGraphics();

		g.setColor(backColor);
		g.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);

		g.setColor(topColor);
		for (int x= 0; x < IMAGE_SIZE; x += 4)
		{
			for (int y= 0; y < IMAGE_SIZE; y += 4)
			{
				g.drawLine(x, y, x, y);
				g.drawLine(x + 2, y + 2, x + 2, y + 2);
			}
		}

		g.setColor(shadowColor);
		for (int x= 0; x < IMAGE_SIZE; x += 4)
		{
			for (int y= 0; y < IMAGE_SIZE; y += 4)
			{
				g.drawLine(x + 1, y + 1, x + 1, y + 1);
				g.drawLine(x + 3, y + 3, x + 3, y + 3);
			}
		}
		g.dispose();
	}

	/**
	 * Creates the image appropriate for the passed in
	 * <code>GraphicsConfiguration</code>, which may be null.
	 */
	private void createImage()
	{
		if (gc != null)
		{
			image= gc.createCompatibleImage(IMAGE_SIZE, IMAGE_SIZE);
		}
		else
		{
			int cmap[]=
				{ backColor.getRGB(), topColor.getRGB(), shadowColor.getRGB()};
			IndexColorModel icm=
				new IndexColorModel(8, 3, cmap, 0, false, -1, DataBuffer.TYPE_BYTE);
			image=
				new BufferedImage(
					IMAGE_SIZE,
					IMAGE_SIZE,
					BufferedImage.TYPE_BYTE_INDEXED,
					icm);
		}
	}
}
