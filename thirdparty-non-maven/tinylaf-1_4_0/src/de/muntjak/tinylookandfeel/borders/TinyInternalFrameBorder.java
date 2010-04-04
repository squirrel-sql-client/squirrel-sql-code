/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.borders;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.TinyLookAndFeel;
import de.muntjak.tinylookandfeel.controlpanel.SBChooser;
import de.muntjak.tinylookandfeel.util.ColorRoutines;

/**
 * TinyInternalFrameBorder
 * 
 * @version 1.0
 * @author Hans Bickel
 *
 */
public class TinyInternalFrameBorder extends AbstractBorder implements UIResource {
	
	// cache for already painted captions
	private static final HashMap cache = new HashMap();
	
	public static Color frameUpperColor, frameLowerColor;
	public static Color disabledUpperColor, disabledLowerColor;
	private JInternalFrame frame;
	private boolean isPalette;
	private int titleHeight;
	
    /** indicates whether the internal frame is active */
    private boolean isActive;
    
    public static void clearCache() {
    	cache.clear();
    }

    /**
     * @see javax.swing.border.Border#paintBorder(Component, Graphics, int, int, int, int)
     */
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		frame = (JInternalFrame)c;
		
		frame.setOpaque(false);
		
		isActive = frame.isSelected();
		isPalette = (frame.getClientProperty("isPalette") == Boolean.TRUE);
		
		if(isPalette) {
			titleHeight = TinyFrameBorder.FRAME_PALETTE_TITLE_HEIGHT;
		}
		else {
			titleHeight = TinyFrameBorder.FRAME_INTERNAL_TITLE_HEIGHT;
		}
		
		if(isActive) {
    		g.setColor(Theme.frameBorderColor.getColor());
    	}
    	else {
    		g.setColor(Theme.frameBorderDisabledColor.getColor());
    	}
    	
		drawXpBorder(g, x, y, w, h);
		
		Color col = null;
		if(isActive) {
    		col = Theme.frameCaptionColor.getColor();
    	}
    	else {
    		col = Theme.frameCaptionDisabledColor.getColor();
    	}
    	g.setColor(col);
    	
    	if(TinyLookAndFeel.controlPanelInstantiated) {
			drawXpCaptionNoCache(g, x, y, w, h, col);
		}
		else {
			drawXpCaption(g, x, y, w, h, col);
		}
	}

	private void drawXpBorder(Graphics g, int x, int y, int w, int h) {
		// left
		g.drawLine(x, y + 6, x, y + h - 1);
		g.drawLine(x + 2, y + titleHeight, x + 2, y + h - 3);
		// right
		g.drawLine(x + w - 1, y + 6, x + w - 1, y + h - 1);
		g.drawLine(x + w - 3, y + titleHeight, x + w - 3, y + h - 3);
		// bottom
		g.drawLine(x + 2, y + h - 3, x + w - 3, y + h - 3);
		g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
		
		if(isActive) {
    		g.setColor(Theme.frameCaptionColor.getColor());
    	}
    	else {
    		g.setColor(Theme.frameCaptionDisabledColor.getColor());
    	}
    	
    	// left
		g.drawLine(x + 1, y + titleHeight, x + 1, y + h - 2);
		// right
		g.drawLine(x + w - 2, y + titleHeight, x + w - 2, y + h - 2);
		// bottom
		g.drawLine(x + 1, y + h - 2, x + w - 2, y + h - 2);

		// outer blend over 3 px
		Color c = null;
		if(isActive) {
    		c = Theme.frameBorderColor.getColor();
    	}
    	else {
    		c = Theme.frameBorderDisabledColor.getColor();
    	}
    	
    	g.setColor(ColorRoutines.getAlphaColor(c, 82));
    	g.drawLine(x, y + 3, x, y + 3);
    	g.drawLine(x + w - 1, y + 3, x + w - 1, y + 3);
    	g.setColor(ColorRoutines.getAlphaColor(c, 156));
    	g.drawLine(x, y + 4, x, y + 4);
    	g.drawLine(x + w - 1, y + 4, x + w - 1, y + 4);
    	g.setColor(ColorRoutines.getAlphaColor(c, 215));
    	g.drawLine(x, y + 5, x, y + 5);
    	g.drawLine(x + w - 1, y + 5, x + w - 1, y + 5);
	}

	private void drawXpCaption(Graphics g, int x, int y, int w, int h, Color c) {
		// Note: this method is equal to TinyFrameBorder.drawXpInternalCaption()
		if(isPalette) {
			drawXpPaletteCaption(g, x, y, w, h, c);
			return;
		}
		
		int spread1 = Theme.frameSpreadDarkDisabled.getValue();
		int spread2 = Theme.frameSpreadLightDisabled.getValue();
		int y2 = y;
		Color borderColor = null;

		if(isActive) {
			borderColor = Theme.frameBorderColor.getColor();
    		spread1 = Theme.frameSpreadDark.getValue();
    		spread2 = Theme.frameSpreadLight.getValue();
    	}
    	else {
    		borderColor = Theme.frameBorderDisabledColor.getColor();
    	}
    	
    	// always paint the semi-transparent parts
// 1
		// blend
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 82));
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 156));
		g.drawLine(x + 4, y2, x + 4, y2);
		g.drawLine(x + w - 5, y2, x + w - 5, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 215));
		g.drawLine(x + 5, y2, x + 5, y2);
		g.drawLine(x + w - 6, y2, x + w - 6, y2);		
		y2 ++;
// 2
		Color c2 = ColorRoutines.darken(c, 4 * spread1);
		g.setColor(c2);
		g.drawLine(x + 3, y2, x + 5, y2);	// left
		g.drawLine(x + w - 6, y2, x + w - 4, y2);	// right
		// blend
		g.setColor(ColorRoutines.getAlphaColor(c2, 139));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		g.setColor(ColorRoutines.getAlphaColor(c2, 23));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;
// 3
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 4, y2, x + 5, y2);	// left
		g.drawLine(x + w - 6, y2, x + w - 5, y2);	// right
		// darker border
		g.setColor(c);
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);		
		c2 = ColorRoutines.darken(c, 6 * spread1);
		g.setColor(c2);
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		// blend		
		g.setColor(ColorRoutines.getAlphaColor(c2, 139));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;
// 4
		// darker border
		g.setColor(c);
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);		
		g.setColor(ColorRoutines.darken(c, 6 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);
		g.setColor(ColorRoutines.lighten(c, 7 * spread2));
		g.drawLine(x + 4, y2, x + 4, y2);
		g.drawLine(x + w - 5, y2, x + w - 5, y2);
		g.setColor(ColorRoutines.lighten(c, 3 * spread2));
		g.drawLine(x + 5, y2, x + 5, y2);
		g.drawLine(x + w - 6, y2, x + w - 6, y2);
		g.setColor(c);
		g.drawLine(x + 6, y2, x + 6, y2);
		g.drawLine(x + w - 7, y2, x + w - 7, y2);
		y2 ++;
// 5
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x + 5, y2, x + 6, y2);	// left
		g.drawLine(x + x + w - 7, y2, x + w - 6, y2);	// right
		// darker border
		g.setColor(ColorRoutines.darken(c, 6 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		g.setColor(ColorRoutines.lighten(c, 5 * spread2));
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);
		g.setColor(c);
		g.drawLine(x + 4, y2, x + 4, y2);
		g.drawLine(x + w - 5, y2, x + w - 5, y2);
		y2 ++;
// 6
		// lighten little
		g.setColor(ColorRoutines.darken(c, 4 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		
		// now either paint from cache or create cached image
		CaptionKey key = new CaptionKey(isActive, titleHeight);
		Object value = cache.get(key);
		
		if(value != null) {
			// image is cached - paint and return
			g.drawImage((Image)value,
				x + 6, y, x + w - 6, y + 5,
				0, 0, 1, 5,
				frame);
			g.drawImage((Image)value,
				x + 1, y + 5, x + w - 1, y + titleHeight,
				0, 5, 1, titleHeight,
				frame);
				
			// store button colors
			if(isActive) {
				frameUpperColor = ColorRoutines.darken(c, 4 * spread1);
				frameLowerColor = ColorRoutines.lighten(c, 10 * spread2);
			}
			else {
				disabledUpperColor = ColorRoutines.darken(c, 4 * spread1);
				disabledLowerColor = ColorRoutines.lighten(c, 10 * spread2);
			}
			
			return;
		}

		Image img = new BufferedImage(1, titleHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics imgGraphics = img.getGraphics();
		
// 1
		imgGraphics.setColor(borderColor);
		imgGraphics.drawLine(0, 0, 1, 0);
// 2
		imgGraphics.setColor(ColorRoutines.darken(c, 4 * spread1));
		imgGraphics.drawLine(0, 1, 1, 1);
// 3
		imgGraphics.setColor(ColorRoutines.lighten(c, 10 * spread2));
		imgGraphics.drawLine(0, 2, 1, 2);
// 4
		imgGraphics.setColor(c);
		imgGraphics.drawLine(0, 3, 1, 3);
// 5
		imgGraphics.setColor(ColorRoutines.darken(c, 2 * spread1));
		imgGraphics.drawLine(0, 4, 1, 4);
// 6
		imgGraphics.setColor(ColorRoutines.darken(c, 4 * spread1));
		imgGraphics.drawLine(0, 5, 1, 5);
// 7
		imgGraphics.setColor(ColorRoutines.darken(c, 4 * spread1));
		imgGraphics.drawLine(0, 6, 1, 6);
// 8 - 10
		imgGraphics.setColor(ColorRoutines.darken(c, 3 * spread1));
		imgGraphics.drawLine(0, 7, 1, 7);
		imgGraphics.drawLine(0, 8, 1, 8);
		imgGraphics.drawLine(0, 9, 1, 9);
// 11 - 12
		imgGraphics.setColor(ColorRoutines.darken(c, 2 * spread1));
		imgGraphics.drawLine(0, 10, 1, 10);
		imgGraphics.drawLine(0, 11, 1, 11);
// 13
		imgGraphics.setColor(ColorRoutines.darken(c, spread1));
		imgGraphics.drawLine(0, 12, 1, 12);
// 14 - 15
		imgGraphics.setColor(c);
		imgGraphics.drawLine(0, 13, 1, 13);
		imgGraphics.drawLine(0, 14, 1, 14);
// 16...
		imgGraphics.setColor(ColorRoutines.lighten(c, 2 * spread2));
		imgGraphics.drawLine(0, 15, 1, 15);
		imgGraphics.setColor(ColorRoutines.lighten(c, 4 * spread2));
		imgGraphics.drawLine(0, 16, 1, 16);
		imgGraphics.setColor(ColorRoutines.lighten(c, 5 * spread2));
		imgGraphics.drawLine(0, 17, 1, 17);
		imgGraphics.setColor(ColorRoutines.lighten(c, 6 * spread2));
		imgGraphics.drawLine(0, 18, 1, 18);
		imgGraphics.setColor(ColorRoutines.lighten(c, 8 * spread2));
		imgGraphics.drawLine(0, 19, 1, 19);
		imgGraphics.setColor(ColorRoutines.lighten(c, 9 * spread2));
		imgGraphics.drawLine(0, 20, 1, 20);
		imgGraphics.setColor(ColorRoutines.lighten(c, 10 * spread2));
		imgGraphics.drawLine(0, 21, 1, 21);
// 23
		imgGraphics.setColor(ColorRoutines.lighten(c, 4 * spread2));
		imgGraphics.drawLine(0, 22, 1, 22);
// 24
		imgGraphics.setColor(ColorRoutines.darken(c, 2 * spread1));
		imgGraphics.drawLine(0, 23, 1, 23);
// 25		
		if(isActive) {
    		imgGraphics.setColor(Theme.frameLightColor.getColor());
    	}
    	else {
    		imgGraphics.setColor(Theme.frameLightDisabledColor.getColor());
    	}
		imgGraphics.drawLine(0, 24, 1, 24);
		
		// dispose of image graphics
		imgGraphics.dispose();
		
		// paint image
		g.drawImage(img,
			x + 6, y, x + w - 6, y + 5,
			0, 0, 1, 5,
			frame);
		g.drawImage(img,
			x + 1, y + 5, x + w - 1, y + titleHeight,
			0, 5, 1, titleHeight,
			frame);
		
		// add the image to the cache
		cache.put(key, img);
		
		if(TinyLookAndFeel.PRINT_CACHE_SIZES) {
			System.out.println("TinyInternalFrameBorder.cache.size=" + cache.size());
		}
	}
	
	private void drawXpCaptionNoCache(Graphics g, int x, int y, int w, int h, Color c) {
		if(isPalette) {
			drawXpPaletteCaptionNoCache(g, x, y, w, h, c);
			return;
		}
		
		int y2 = y;
		int spread1 = Theme.frameSpreadDarkDisabled.getValue();
		int spread2 = Theme.frameSpreadLightDisabled.getValue();
		Color borderColor = null;
		
		if(isActive) {
    		borderColor = Theme.frameBorderColor.getColor();
    		spread1 = Theme.frameSpreadDark.getValue();
    		spread2 = Theme.frameSpreadLight.getValue();
    	}
    	else {
    		borderColor = Theme.frameBorderDisabledColor.getColor();
    	}
// 1
		g.setColor(borderColor);
		g.drawLine(x + 6, y2, x + w - 7, y2);
		// blend
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 82));
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 156));
		g.drawLine(x + 4, y2, x + 4, y2);
		g.drawLine(x + w - 5, y2, x + w - 5, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 215));
		g.drawLine(x + 5, y2, x + 5, y2);
		g.drawLine(x + w - 6, y2, x + w - 6, y2);		
		y2 ++;
// 2
		Color c2 = ColorRoutines.darken(c, 4 * spread1);
		g.setColor(c2);
		g.drawLine(x + 3, y2, x + w - 4, y2);
		// blend
		g.setColor(ColorRoutines.getAlphaColor(c2, 139));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		g.setColor(ColorRoutines.getAlphaColor(c2, 23));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;
// 3
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 4, y2, x + w - 5, y2);
		// darker border
		g.setColor(c);
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);		
		c2 = ColorRoutines.darken(c, 6 * spread1);
		g.setColor(c2);
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		// blend		
		g.setColor(ColorRoutines.getAlphaColor(c2, 139));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;
// 4
		g.setColor(c);
		g.drawLine(x + 7, y2, x + w - 8, y2);
		// darker border
		g.setColor(c);
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);		
		g.setColor(ColorRoutines.darken(c, 6 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);
		g.setColor(ColorRoutines.lighten(c, 7 * spread2));
		g.drawLine(x + 4, y2, x + 4, y2);
		g.drawLine(x + w - 5, y2, x + w - 5, y2);
		g.setColor(ColorRoutines.lighten(c, 3 * spread2));
		g.drawLine(x + 5, y2, x + 5, y2);
		g.drawLine(x + w - 6, y2, x + w - 6, y2);
		g.setColor(c);
		g.drawLine(x + 6, y2, x + 6, y2);
		g.drawLine(x + w - 7, y2, x + w - 7, y2);
		y2 ++;
// 5
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x + 5, y2, x + w - 6, y2);
		// darker border
		g.setColor(ColorRoutines.darken(c, 6 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		g.setColor(ColorRoutines.lighten(c, 5 * spread2));
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);
		g.setColor(c);
		g.drawLine(x + 4, y2, x + 4, y2);
		g.drawLine(x + w - 5, y2, x + w - 5, y2);
		y2 ++;
// 6
		if(isActive) {
			frameUpperColor = ColorRoutines.darken(c, 4 * spread1);
			g.setColor(frameUpperColor);
		}
		else {
			disabledUpperColor = ColorRoutines.darken(c, 4 * spread1);
			g.setColor(disabledUpperColor);
		}
		
		g.drawLine(x + 2, y2, x + w - 3, y2);
		// lighten little
		g.setColor(ColorRoutines.darken(c, 4 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;
// 7
		g.setColor(ColorRoutines.darken(c, 4 * spread1));
		g.fillRect(x + 1, y2, w - 2, 1);
		y2 += 1;
// 8 - 10
		g.setColor(ColorRoutines.darken(c, 3 * spread1));
		g.fillRect(x + 1, y2, w - 2, 3);
		y2 += 3;
// 11 - 12
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.fillRect(x + 1, y2, w - 2, 2);
		y2 += 2;
// 13
		g.setColor(ColorRoutines.darken(c, 1 * spread1));
		g.fillRect(x + 1, y2, w - 2, 1);
		y2 += 1;
// 14 - 15
		g.setColor(c);
		g.fillRect(x + 1, y2, w - 2, 2);
		y2 += 2;
// 16...
		g.setColor(ColorRoutines.lighten(c, 2 * spread2));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 4 * spread2));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 5 * spread2));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 6 * spread2));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 8 * spread2));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 9 * spread2));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
		
		if(isActive) {
			frameLowerColor = ColorRoutines.lighten(c, 10 * spread2);
			g.setColor(frameLowerColor);
		}
		else {
			disabledLowerColor = ColorRoutines.lighten(c, 10 * spread2);
			g.setColor(disabledLowerColor);
		}
		
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
// 23
		g.setColor(ColorRoutines.lighten(c, 4 * spread2));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
// 24
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
// 25		
		if(isActive) {
    		g.setColor(Theme.frameLightColor.getColor());
    	}
    	else {
    		g.setColor(Theme.frameLightDisabledColor.getColor());
    	}
		g.drawLine(x + 1, y2, x + w - 2, y2);
	}
	
	private void drawXpPaletteCaption(Graphics g, int x, int y, int w, int h, Color c) {
		int y2 = y;
		int spread1 = Theme.frameSpreadDarkDisabled.getValue();
		int spread2 = Theme.frameSpreadLightDisabled.getValue();
		Color borderColor = null;
		
		if(isActive) {
    		borderColor = Theme.frameBorderColor.getColor();
    		spread1 = Theme.frameSpreadDark.getValue();
    		spread2 = Theme.frameSpreadLight.getValue();
    	}
    	else {
    		borderColor = Theme.frameBorderDisabledColor.getColor();
    	}
    	
    	// always paint the semi-transparent parts
// 1
		// blend
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 82));
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 156));
		g.drawLine(x + 4, y2, x + 4, y2);
		g.drawLine(x + w - 5, y2, x + w - 5, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 215));
		g.drawLine(x + 5, y2, x + 5, y2);
		g.drawLine(x + w - 6, y2, x + w - 6, y2);		
		y2 ++;
// 2
		Color c2 = ColorRoutines.darken(c, 4 * spread1);
		g.setColor(c2);
		g.drawLine(x + 3, y2, x + 5, y2);	// left
		g.drawLine(x + w - 6, y2, x + w - 4, y2);	// right
		// blend
		g.setColor(ColorRoutines.getAlphaColor(c2, 139));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		g.setColor(ColorRoutines.getAlphaColor(c2, 23));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;
// 3
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 4, y2, x + 5, y2);	// left
		g.drawLine(x + w - 6, y2, x + w - 5, y2);	// right
		// darker border
		g.setColor(c);
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);		
		c2 = ColorRoutines.darken(c, 6 * spread1);
		g.setColor(c2);
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		// blend		
		g.setColor(ColorRoutines.getAlphaColor(c2, 139));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;
// 4
		// darker border
		g.setColor(c);
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);		
		g.setColor(ColorRoutines.darken(c, 6 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);
		g.setColor(ColorRoutines.lighten(c, 7 * spread2));
		g.drawLine(x + 4, y2, x + 4, y2);
		g.drawLine(x + w - 5, y2, x + w - 5, y2);
		g.setColor(ColorRoutines.lighten(c, 3 * spread2));
		g.drawLine(x + 5, y2, x + 5, y2);
		g.drawLine(x + w - 6, y2, x + w - 6, y2);
		g.setColor(c);
		g.drawLine(x + 6, y2, x + 6, y2);
		g.drawLine(x + w - 7, y2, x + w - 7, y2);
		y2 ++;
// 5
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x + 5, y2, x + 6, y2);	// left
		g.drawLine(x + x + w - 7, y2, x + w - 6, y2);	// right
		// darker border
		g.setColor(ColorRoutines.darken(c, 6 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		g.setColor(ColorRoutines.lighten(c, 5 * spread2));
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);
		g.setColor(c);
		g.drawLine(x + 4, y2, x + 4, y2);
		g.drawLine(x + w - 5, y2, x + w - 5, y2);
		y2 ++;
// 6
		// lighten little
		g.setColor(ColorRoutines.darken(c, 4 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		
		// now either paint from cache or create cached image
		CaptionKey key = new CaptionKey(isActive, titleHeight);
		Object value = cache.get(key);
		
		if(value != null) {
			// image is cached - paint and return
			g.drawImage((Image)value,
				x + 6, y, x + w - 6, y + 5,
				0, 0, 1, 5,
				frame);
			g.drawImage((Image)value,
				x + 1, y + 5, x + w - 1, y + titleHeight,
				0, 5, 1, titleHeight,
				frame);
				
			// store button colors
			if(isActive) {
				frameUpperColor = ColorRoutines.darken(c, 4 * spread1);
				frameLowerColor = ColorRoutines.lighten(c, 10 * spread2);
			}
			else {
				disabledUpperColor = ColorRoutines.darken(c, 4 * spread1);
				disabledLowerColor = ColorRoutines.lighten(c, 10 * spread2);
			}
			
			return;
		}

		Image img = new BufferedImage(1, titleHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics imgGraphics = img.getGraphics();
// 1
		imgGraphics.setColor(borderColor);
		imgGraphics.drawLine(0, 0, 1, 0);
// 2
		imgGraphics.setColor(ColorRoutines.darken(c, 4 * spread1));
		imgGraphics.drawLine(0, 1, 1, 1);
// 3
		imgGraphics.setColor(ColorRoutines.lighten(c, 10 * spread2));
		imgGraphics.drawLine(0, 2, 1, 2);
// 4
		imgGraphics.setColor(c);
		imgGraphics.drawLine(0, 3, 1, 3);
// 5
		imgGraphics.setColor(ColorRoutines.darken(c, 2 * spread1));
		imgGraphics.drawLine(0, 4, 1, 4);
// 6
		imgGraphics.setColor(ColorRoutines.darken(c, 4 * spread1));
		imgGraphics.drawLine(0, 5, 1, 5);
// 7
		imgGraphics.drawLine(0, 6, 1, 6);
// 8
		imgGraphics.setColor(ColorRoutines.darken(c, 3 * spread1));
		imgGraphics.drawLine(0, 7, 1, 7);
// 9
		imgGraphics.setColor(ColorRoutines.darken(c, 2 * spread1));
		imgGraphics.drawLine(0, 8, 1, 8);
// 10
		imgGraphics.setColor(ColorRoutines.darken(c, spread1));
		imgGraphics.drawLine(0, 9, 1, 9);
// 11
		imgGraphics.setColor(c);
		imgGraphics.drawLine(0, 10, 1, 10);
// 12...
		imgGraphics.setColor(ColorRoutines.lighten(c, 2 * spread2));
		imgGraphics.drawLine(0, 11, 1, 11);
		imgGraphics.setColor(ColorRoutines.lighten(c, 4 * spread2));
		imgGraphics.drawLine(0, 12, 1, 12);
		imgGraphics.setColor(ColorRoutines.lighten(c, 5 * spread2));
		imgGraphics.drawLine(0, 13, 1, 13);
		imgGraphics.setColor(ColorRoutines.lighten(c, 6 * spread2));
		imgGraphics.drawLine(0, 14, 1, 14);
		imgGraphics.setColor(ColorRoutines.lighten(c, 8 * spread2));
		imgGraphics.drawLine(0, 15, 1, 15);
		imgGraphics.setColor(ColorRoutines.lighten(c, 9 * spread2));
		imgGraphics.drawLine(0, 16, 1, 16);
		imgGraphics.setColor(ColorRoutines.lighten(c, 10 * spread2));
		imgGraphics.drawLine(0, 17, 1, 17);
// 19
		imgGraphics.setColor(ColorRoutines.lighten(c, 4 * spread2));
		imgGraphics.drawLine(0, 18, 1, 18);
// 20
		imgGraphics.setColor(ColorRoutines.darken(c, 2 * spread1));
		imgGraphics.drawLine(0, 19, 1, 19);
// 21		
		if(isActive) {
    		imgGraphics.setColor(Theme.frameLightColor.getColor());
    	}
    	else {
    		imgGraphics.setColor(Theme.frameLightDisabledColor.getColor());
    	}
		imgGraphics.drawLine(0, 20, 1, 20);
		
		// dispose of image graphics
		imgGraphics.dispose();
		
		// paint image
		g.drawImage(img,
			x + 6, y, x + w - 6, y + 5,
			0, 0, 1, 5,
			frame);
		g.drawImage(img,
			x + 1, y + 5, x + w - 1, y + titleHeight,
			0, 5, 1, titleHeight,
			frame);
		
		// add the image to the cache
		cache.put(key, img);
		
		if(TinyLookAndFeel.PRINT_CACHE_SIZES) {
			System.out.println("TinyInternalFrameBorder.cache.size=" + cache.size());
		}
	}
	
	private void drawXpPaletteCaptionNoCache(Graphics g, int x, int y, int w, int h, Color c) {
		int y2 = y;
		int spread1 = Theme.frameSpreadDarkDisabled.getValue();
		int spread2 = Theme.frameSpreadLightDisabled.getValue();
		Color borderColor = null;
		
		if(isActive) {
    		borderColor = Theme.frameBorderColor.getColor();
    		spread1 = Theme.frameSpreadDark.getValue();
    		spread2 = Theme.frameSpreadLight.getValue();
    	}
    	else {
    		borderColor = Theme.frameBorderDisabledColor.getColor();
    	}
// 1
		g.setColor(borderColor);
		g.drawLine(x + 6, y2, x + w - 7, y2);
		// blend
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 82));
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 156));
		g.drawLine(x + 4, y2, x + 4, y2);
		g.drawLine(x + w - 5, y2, x + w - 5, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 215));
		g.drawLine(x + 5, y2, x + 5, y2);
		g.drawLine(x + w - 6, y2, x + w - 6, y2);		
		y2 ++;
// 2
		Color c2 = ColorRoutines.darken(c, 4 * spread1);
		g.setColor(c2);
		g.drawLine(x + 3, y2, x + w - 4, y2);
		// blend
		g.setColor(ColorRoutines.getAlphaColor(c2, 139));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		g.setColor(ColorRoutines.getAlphaColor(c2, 23));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;
// 3
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 4, y2, x + w - 5, y2);
		// darker border
		g.setColor(c);
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);		
		c2 = ColorRoutines.darken(c, 6 * spread1);
		g.setColor(c2);
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		// blend		
		g.setColor(ColorRoutines.getAlphaColor(c2, 139));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;
// 4
		g.setColor(c);
		g.drawLine(x + 7, y2, x + w - 8, y2);
		// darker border
		g.setColor(c);
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);		
		g.setColor(ColorRoutines.darken(c, 6 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);
		g.setColor(ColorRoutines.lighten(c, 7 * spread2));
		g.drawLine(x + 4, y2, x + 4, y2);
		g.drawLine(x + w - 5, y2, x + w - 5, y2);
		g.setColor(ColorRoutines.lighten(c, 3 * spread2));
		g.drawLine(x + 5, y2, x + 5, y2);
		g.drawLine(x + w - 6, y2, x + w - 6, y2);
		g.setColor(c);
		g.drawLine(x + 6, y2, x + 6, y2);
		g.drawLine(x + w - 7, y2, x + w - 7, y2);
		y2 ++;
// 5
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x + 5, y2, x + w - 6, y2);
		// darker border
		g.setColor(ColorRoutines.darken(c, 6 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		g.setColor(ColorRoutines.lighten(c, 5 * spread2));
		g.drawLine(x + 3, y2, x + 3, y2);
		g.drawLine(x + w - 4, y2, x + w - 4, y2);
		g.setColor(c);
		g.drawLine(x + 4, y2, x + 4, y2);
		g.drawLine(x + w - 5, y2, x + w - 5, y2);
		y2 ++;
// 6
		if(isActive) {
			frameUpperColor = ColorRoutines.darken(c, 4 * spread1);
			g.setColor(frameUpperColor);
		}
		else {
			disabledUpperColor = ColorRoutines.darken(c, 4 * spread1);
			g.setColor(disabledUpperColor);
		}
		
		g.drawLine(x + 2, y2, x + w - 3, y2);
		// lighten little
		g.setColor(ColorRoutines.darken(c, 4 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;
// 7
		g.setColor(ColorRoutines.darken(c, 4 * spread1));
		g.fillRect(x + 1, y2, w - 2, 1);
		y2 += 1;
// 8
		g.setColor(ColorRoutines.darken(c, 3 * spread1));
		g.fillRect(x + 1, y2, w - 2, 1);
		y2 += 1;
// 9
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.fillRect(x + 1, y2, w - 2, 1);
		y2 += 1;
// 10
		g.setColor(ColorRoutines.darken(c, 1 * spread1));
		g.fillRect(x + 1, y2, w - 2, 1);
		y2 += 1;
// 11
		g.setColor(c);
		g.fillRect(x + 1, y2, w - 2, 1);
		y2 += 1;
// 12...
		g.setColor(ColorRoutines.lighten(c, 2 * spread2));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 4 * spread2));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 5 * spread2));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 6 * spread2));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 8 * spread2));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 9 * spread2));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
		
		if(isActive) {
			frameLowerColor = ColorRoutines.lighten(c, 10 * spread2);
			g.setColor(frameLowerColor);
		}
		else {
			disabledLowerColor = ColorRoutines.lighten(c, 10 * spread2);
			g.setColor(disabledLowerColor);
		}
		
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
// 19
		g.setColor(ColorRoutines.lighten(c, 4 * spread2));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
// 20
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x + 1, y2, x + w - 2, y2);
		y2 ++;
// 21		
		if(isActive) {
    		g.setColor(Theme.frameLightColor.getColor());
    	}
    	else {
    		g.setColor(Theme.frameLightDisabledColor.getColor());
    	}
		g.drawLine(x + 1, y2, x + w - 2, y2);
	}

    /**
     * 
     * @see javax.swing.border.Border#getBorderInsets(Component)
     */
	public Insets getBorderInsets(Component c) {
		JInternalFrame frame = (JInternalFrame)c;
		
		// if the frame is maximized, the border should not be visible
		if(frame.isMaximum()) {
			return new Insets(0, 0, 0, 0);
		}

		return new Insets(0,
			TinyFrameBorder.FRAME_BORDER_WIDTH,
			TinyFrameBorder.FRAME_BORDER_WIDTH,
			TinyFrameBorder.FRAME_BORDER_WIDTH);
	}

    /**
     *  inform the border whether the internal frame is active or not
     * @param isActive
     */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	/**
	 * CaptionKey is used as key in the cache HashMap.
	 * Overrides equals() and hashCode().
	 */
	private static class CaptionKey {

		private boolean isActive;
		private int titleHeight;

		CaptionKey(boolean isActive, int titleHeight) {
			this.isActive = isActive;
			this.titleHeight = titleHeight;
		}
		
		public boolean equals(Object o) {
			if(o == null) return false;
			if(!(o instanceof CaptionKey)) return false;

			CaptionKey other = (CaptionKey)o;
			
			return isActive == other.isActive &&
				titleHeight == other.titleHeight;
		}
		
		public int hashCode() {
			return (isActive ? 1 : 2) * titleHeight;
		}
	}
}