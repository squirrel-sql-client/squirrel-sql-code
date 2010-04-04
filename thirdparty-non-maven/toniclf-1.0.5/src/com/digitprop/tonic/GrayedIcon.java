package com.digitprop.tonic;


import java.awt.*;

import javax.swing.*;
import java.awt.image.*;


/**
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
public class GrayedIcon implements Icon
{
	private Icon icon;
	
	private Image grayedImg;
	
	
	public GrayedIcon(Icon icon)
	{
		this.icon=icon;
	}
	
	
public int getIconHeight()
{
	return icon.getIconHeight();
}


public int getIconWidth()
{
return icon.getIconWidth();
}


public void paintIcon(Component c, Graphics g, int x, int y)
{
	if(grayedImg==null)
	{
		Image tmpImg=c.createImage(getIconWidth(), getIconHeight());
		if(tmpImg!=null)
		{
			Graphics g2=tmpImg.getGraphics();
			if(g!=null)
			{
				g2.setColor(new Color(0xff123456));
				
				g2.fillRect(0, 0, getIconWidth(), getIconHeight());
				icon.paintIcon(c, g2, 0, 0);
				g2.dispose();
				
				grayedImg=IconGrayFilter.createDisabledImage(tmpImg);
			}
		}
	}
	
	if(grayedImg!=null)
		g.drawImage(grayedImg, x, y, null);
}



/**
 * An image filter that "disables" an image by turning
 * it into a grayscale image, and brightening the pixels
 * in the image. Used by buttons to create an image for
 * a disabled button.
 *
 * @author      Jeff Dinkins
 * @author      Tom Ball
 * @author      Jim Graham
 * @version     1.13 12/03/01
 */
static class IconGrayFilter extends RGBImageFilter {
	 private boolean brighter;
	 private int percent;
    
	 /**
	  * Creates a disabled image
	  */
	 public static Image createDisabledImage (Image i) {
	IconGrayFilter filter = new IconGrayFilter(true, 50);
	ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
	Image grayImage = Toolkit.getDefaultToolkit().createImage(prod);
	return grayImage;
	 }
    
	 /**
	  * Constructs a GrayFilter object that filters a color image to a 
	  * grayscale image. Used by buttons to create disabled ("grayed out")
	  * button images.
	  *
	  * @param b  a boolean -- true if the pixels should be brightened
	  * @param p  an int in the range 0..100 that determines the percentage
	  *           of gray, where 100 is the darkest gray, and 0 is the lightest
	  */
	 public IconGrayFilter(boolean b, int p) {
		  brighter = b;
		  percent = p;

	// canFilterIndexColorModel indicates whether or not it is acceptable
	// to apply the color filtering of the filterRGB method to the color
	// table entries of an IndexColorModel object in lieu of pixel by pixel
		// filtering.
		  canFilterIndexColorModel = true;
	 }
    
	 /**
	  * Overrides <code>RGBImageFilter.filterRGB</code>.
	  */
	 public int filterRGB(int x, int y, int rgb) {
	 	
	 		if((rgb&0xffffff)==0x123456)
	 			return 0x00ff0000;
	 			
		  // Use NTSC conversion formula.
	int gray = (int)((0.30 * ((rgb >> 16) & 0xff) + 
								 0.59 * ((rgb >> 8) & 0xff) + 
								 0.11 * (rgb & 0xff)) / 3);
	
		  if (brighter) {
				gray = (255 - ((255 - gray) * (100 - percent) / 100));
		  } else {
				gray = (gray * (100 - percent) / 100);
		  }
	
		  if (gray < 0) gray = 0;
		  if (gray > 255) gray = 255;
		  return (rgb & 0xff000000) | (gray << 16) | (gray << 8) | (gray << 0);
	 }
}
}
