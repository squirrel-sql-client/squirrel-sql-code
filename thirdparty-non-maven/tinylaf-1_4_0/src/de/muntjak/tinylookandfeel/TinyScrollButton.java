/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.metal.MetalScrollButton;

import de.muntjak.tinylookandfeel.controlpanel.*;
import de.muntjak.tinylookandfeel.util.ColorRoutines;

/**
 * TinyScrollButton
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyScrollButton extends BasicArrowButton {
	
	// cache for already drawn icons - speeds up drawing by a factor
	// of 3 if there are several scroll buttons or one scroll button
	// is painted several times
	private static final HashMap cache = new HashMap();

	private boolean isRollover;
	
	private TinyScrollBarUI scrollbarUI;
	
	public static void clearCache() {
    	cache.clear();
    }

	/**
	 * Create a new ScrollButton.
	 * @see javax.swing.plaf.metal.MetalScrollButton#MetalScrollButton(int, int, boolean)
	 */
	public TinyScrollButton(int direction,TinyScrollBarUI scrollbarUI) {
		super(direction);

		this.scrollbarUI = scrollbarUI;
		setBorder(null);
		setRolloverEnabled(true);
		setMargin(new Insets(0, 0, 0, 0));
		setSize(getPreferredSize());
	}

	/**
	 * Paints the button
	 * @see java.awt.Component#paint(Graphics)
	 */
	public void paint(Graphics g) {
		boolean enabled = scrollbarUI.isThumbVisible();
		isRollover = false;
		Color c = null;

		if(!enabled) {
			c = Theme.scrollButtDisabledColor.getColor();
		}
		else if(getModel().isPressed()) {
			c = Theme.scrollButtPressedColor.getColor();
		}
		else if(getModel().isRollover() && Theme.scrollRollover.getValue()) {
			c = Theme.scrollButtRolloverColor.getColor();
			isRollover = true;
		}
		else {
			c = Theme.scrollButtColor.getColor();
		}
		
		g.setColor(c);
		
		if(TinyLookAndFeel.controlPanelInstantiated) {
			drawXpButtonNoCache(g, getSize(), c);
		}
		else {
			drawXpButton(g, getSize(), c);
		}
		
		if(!enabled) {
			g.setColor(Theme.scrollArrowDisabledColor.getColor());
		}
		else {
			g.setColor(Theme.scrollArrowColor.getColor());
		}
		
		drawXpArrow(g, getSize());
	}

	private void drawXpButton(Graphics g, Dimension size, Color c) {
		boolean enabled = scrollbarUI.isThumbVisible();
		boolean pressed = getModel().isPressed();
		boolean rollover = getModel().isRollover() && Theme.scrollRollover.getValue();
		ScrollButtonKey key = new ScrollButtonKey(size,
			(direction == NORTH || direction == SOUTH),
			c, pressed, enabled, rollover);
		
		Object value = cache.get(key);
		
		if(value != null) {
			// image was cached - paint image and return
			g.drawImage((Image)value, 0, 0, this);
			return;
		}
		
		Image img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		Graphics imgGraphics = img.getGraphics();
		int h = size.height;
		int w = size.width;
		int spread1 = Theme.scrollSpreadLight.getValue();
		int spread2 = Theme.scrollSpreadDark.getValue();
		
		if(!enabled) {
			spread1 = Theme.scrollSpreadLightDisabled.getValue();
			spread2 = Theme.scrollSpreadDarkDisabled.getValue();
		}
			
		switch (direction) {
			case SwingConstants.NORTH:
			case SwingConstants.SOUTH:
				float spreadStep1 = 10.0f * spread1 / 10;
				float spreadStep2 = 10.0f * spread2 / 10;
				int halfY = h * 3 / 8;

				int yd;
				
				for(int y = 1; y < h - 1; y++) {
					if(y < halfY) {
						yd = halfY - y;
						imgGraphics.setColor(ColorRoutines.lighten(c, (int)(yd * spreadStep1)));
					}
					else if(y == halfY) {
						imgGraphics.setColor(c);
					}
					else {
						yd = y - halfY;
						imgGraphics.setColor(ColorRoutines.darken(c, (int)(yd * spreadStep2)));
					}
			
					imgGraphics.drawLine(3, y, w - 3, y);
				}

				imgGraphics.setColor(Theme.scrollTrackBorderColor.getColor());
				imgGraphics.drawLine(0, 0, 0, h - 1);
				
				if(!enabled) {
					c = Theme.scrollLightDisabledColor.getColor();
				}
				else {
					c = Theme.scrollBorderLightColor.getColor();
				}
				
				imgGraphics.setColor(c);
				imgGraphics.drawLine(2, 1, 2, h - 2);
				imgGraphics.drawLine(w - 2, 1, w - 2, h - 2);
				Color lightAlpha = new Color(c.getRed(), c.getGreen(), c.getBlue(), TinyScrollBarUI.ALPHA);
				
				if(!enabled) {
					c = Theme.scrollBorderDisabledColor.getColor();
				}
				else {
					c = Theme.scrollBorderColor.getColor();
				}
				
				imgGraphics.setColor(c);
				imgGraphics.drawRect(1, 0, w - 2, h - 1);
				
				// edges - blend borderColor with lightColor
				imgGraphics.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), TinyScrollBarUI.ALPHA));				
				imgGraphics.drawLine(2, 1, 2, 1);
				imgGraphics.drawLine(w - 2, 1, w - 2, 1);
				imgGraphics.drawLine(2, h - 2, 2, h - 2);
				imgGraphics.drawLine(w - 2, h - 2, w - 2, h - 2);
				
				// blend lightColor with borderColor
				imgGraphics.setColor(lightAlpha);				
				imgGraphics.drawLine(1, 0, 1, 0);
				imgGraphics.drawLine(1, h - 1, 1, h - 1);
				imgGraphics.drawLine(w - 1, 0, w - 1, 0);
				imgGraphics.drawLine(w - 1, h - 1, w - 1, h - 1);
				break;
			case SwingConstants.EAST:
			case SwingConstants.WEST:
				spreadStep1 = 10.0f * spread1 / 10;
				spreadStep2 = 10.0f * spread2 / 10;
				halfY = h / 2;
				
				for(int y = 1; y < w - 1; y++) {
					if(y < halfY) {
						yd = halfY - y;
						imgGraphics.setColor(ColorRoutines.lighten(c, (int)(yd * spreadStep1)));
					}
					else if(y == halfY) {
						imgGraphics.setColor(c);
					}
					else {
						yd = y - halfY;
						imgGraphics.setColor(ColorRoutines.darken(c, (int)(yd * spreadStep2)));
					}
			
					imgGraphics.drawLine(2, y + 1, w - 3, y + 1);
				}
				
				imgGraphics.setColor(Theme.scrollTrackBorderColor.getColor());
				imgGraphics.drawLine(0, 0, w - 1, 0);
				
				if(!enabled) {
					c = Theme.scrollLightDisabledColor.getColor();
				}
				else {
					c = Theme.scrollBorderLightColor.getColor();
				}
				
				imgGraphics.setColor(c);
				imgGraphics.drawLine(1, 2, 1, h - 2);
				imgGraphics.drawLine(w - 2, 2, w - 2, h - 2);
				lightAlpha = new Color(c.getRed(), c.getGreen(), c.getBlue(), TinyScrollBarUI.ALPHA);
				
				if(!enabled) {
					c = Theme.scrollBorderDisabledColor.getColor();
				}
				else {
					c = Theme.scrollBorderColor.getColor();
				}
				
				imgGraphics.setColor(c);
				imgGraphics.drawRect(0, 1, w - 1, h - 2);
				
				// edges - blend borderColor with lightColor
				imgGraphics.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), TinyScrollBarUI.ALPHA));				
				imgGraphics.drawLine(1, 2, 1, 2);
				imgGraphics.drawLine(w - 2, 2, w - 2, 2);
				imgGraphics.drawLine(1, h - 2, 1, h - 2);
				imgGraphics.drawLine(w - 2, h - 2, w - 2, h - 2);
				
				// blend lightColor with borderColor
				imgGraphics.setColor(lightAlpha);				
				imgGraphics.drawLine(0, 1, 0, 1);
				imgGraphics.drawLine(w - 1, 1, w - 1, 1);
				imgGraphics.drawLine(0, h - 1, 0, h - 1);
				imgGraphics.drawLine(w - 1, h - 1, w - 1, h - 1);
				break;
		}
		
//		int spread1 = Theme.scrollSpreadLight.getValue();
//		int spread2 = Theme.scrollSpreadDark.getValue();
//		
//		if(!scrollbarUI.isThumbVisible()) {
//			spread1 = Theme.scrollSpreadLightDisabled.getValue();
//			spread2 = Theme.scrollSpreadDarkDisabled.getValue();
//		}
//			
//		switch (direction) {
//			case NORTH:
//			case SOUTH:
//				int h = 17;
//				float spreadStep1 = 10.0f * spread1 / 11;
//				float spreadStep2 = 10.0f * spread2 / 11;
//				int halfY = 6;
//				int yd;
//				
//				for(int y = 1; y < h - 1; y++) {
//					if(y < halfY) {
//						yd = halfY - y;
//						imgGraphics.setColor(ColorRoutines.lighten(
//							c, (int)(yd * spreadStep1)));
//					}
//					else if(y == halfY) {
//						imgGraphics.setColor(c);
//					}
//					else {
//						yd = y - halfY;
//						imgGraphics.setColor(ColorRoutines.darken(c, (int)(yd * spreadStep2)));
//					}
//			
//					imgGraphics.drawLine(3, y, 14, y);
//				}
//
//				imgGraphics.setColor(Theme.scrollTrackBorderColor.getColor());
//				imgGraphics.drawLine(0, 0, 0, 16);
//				
//				if(!scrollbarUI.isThumbVisible()) {
//					c = Theme.scrollLightDisabledColor.getColor();
//				}
//				else {
//					c = Theme.scrollBorderLightColor.getColor();
//				}
//				imgGraphics.setColor(c);
//				imgGraphics.drawLine(2, 1, 2, 15);
//				imgGraphics.drawLine(15, 1, 15, 15);
//				Color lightAlpha = new Color(c.getRed(), c.getGreen(), c.getBlue(), TinyScrollBarUI.ALPHA);
//				
//				if(!scrollbarUI.isThumbVisible()) {
//					c = Theme.scrollBorderDisabledColor.getColor();
//				}
//				else {
//					c = Theme.scrollBorderColor.getColor();
//				}
//				imgGraphics.setColor(c);
//				imgGraphics.drawRect(1, 0, 15, 16);
//				
//				// edges - blend borderColor with lightColor
//				imgGraphics.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), TinyScrollBarUI.ALPHA));				
//				imgGraphics.drawLine(2, 1, 2, 1);
//				imgGraphics.drawLine(15, 1, 15, 1);
//				imgGraphics.drawLine(2, 15, 2, 15);
//				imgGraphics.drawLine(15, 15, 15, 15);
//				
//				// blend lightColor with borderColor
//				imgGraphics.setColor(lightAlpha);				
//				imgGraphics.drawLine(1, 0, 1, 0);
//				imgGraphics.drawLine(1, 16, 1, 16);
//				imgGraphics.drawLine(16, 0, 16, 0);
//				imgGraphics.drawLine(16, 16, 16, 16);
//				break;
//			case EAST:
//			case WEST:
//				spreadStep1 = 10.0f * spread1 / 10;
//				spreadStep2 = 10.0f * spread2 / 10;
//				halfY = 6;
//				
//				for(int y = 1; y < 15; y++) {
//					if(y < halfY) {
//						yd = halfY - y;
//						imgGraphics.setColor(ColorRoutines.lighten(c, (int)(yd * spreadStep1)));
//					}
//					else if(y == halfY) {
//						imgGraphics.setColor(c);
//					}
//					else {
//						yd = y - halfY;
//						imgGraphics.setColor(ColorRoutines.darken(c, (int)(yd * spreadStep2)));
//					}
//			
//					imgGraphics.drawLine(2, y + 1, 14, y + 1);
//				}
//				
//				imgGraphics.setColor(Theme.scrollTrackBorderColor.getColor());
//				imgGraphics.drawLine(0, 0, 16, 0);
//				
//				if(!scrollbarUI.isThumbVisible()) {
//					c = Theme.scrollLightDisabledColor.getColor();
//				}
//				else {
//					c = Theme.scrollBorderLightColor.getColor();
//				}
//				imgGraphics.setColor(c);
//				imgGraphics.drawLine(1, 2, 1, 15);
//				imgGraphics.drawLine(15, 2, 15, 15);
//				lightAlpha = new Color(c.getRed(), c.getGreen(), c.getBlue(), TinyScrollBarUI.ALPHA);
//				
//				if(!scrollbarUI.isThumbVisible()) {
//					c = Theme.scrollBorderDisabledColor.getColor();
//				}
//				else {
//					c = Theme.scrollBorderColor.getColor();
//				}
//				imgGraphics.setColor(c);
//				imgGraphics.drawRect(0, 1, 16, 15);
//				
//				// edges - blend borderColor with lightColor
//				imgGraphics.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), TinyScrollBarUI.ALPHA));				
//				imgGraphics.drawLine(1, 2, 1, 2);
//				imgGraphics.drawLine(15, 2, 15, 2);
//				imgGraphics.drawLine(1, 15, 1, 15);
//				imgGraphics.drawLine(15, 15, 15, 15);
//				
//				// blend lightColor with borderColor
//				imgGraphics.setColor(lightAlpha);				
//				imgGraphics.drawLine(0, 1, 0, 1);
//				imgGraphics.drawLine(16, 1, 16, 1);
//				imgGraphics.drawLine(0, 16, 0, 16);
//				imgGraphics.drawLine(16, 16, 16, 16);
//				break;
//		}
		
		// dispose of image graphics
		imgGraphics.dispose();
		
		// draw the image
		g.drawImage(img, 0, 0, this);
		
		// add the image to the cache
		cache.put(key, img);
		
		if(TinyLookAndFeel.PRINT_CACHE_SIZES) {
			System.out.println("TinyScrollButton.cache.size=" + cache.size());
		}
	}
	
	private void drawXpButtonNoCache(Graphics g, Dimension size, Color c) {
		boolean enabled = scrollbarUI.isThumbVisible();
		int h = size.height;
		int w = size.width;
		int spread1 = Theme.scrollSpreadLight.getValue();
		int spread2 = Theme.scrollSpreadDark.getValue();
		
		if(!enabled) {
			spread1 = Theme.scrollSpreadLightDisabled.getValue();
			spread2 = Theme.scrollSpreadDarkDisabled.getValue();
		}
			
		switch (direction) {
			case SwingConstants.NORTH:
			case SwingConstants.SOUTH:
				float spreadStep1 = 10.0f * spread1 / 10;
				float spreadStep2 = 10.0f * spread2 / 10;
				int halfY = h * 3 / 8;

				int yd;
				
				for(int y = 1; y < h - 1; y++) {
					if(y < halfY) {
						yd = halfY - y;
						g.setColor(ColorRoutines.lighten(c, (int)(yd * spreadStep1)));
					}
					else if(y == halfY) {
						g.setColor(c);
					}
					else {
						yd = y - halfY;
						g.setColor(ColorRoutines.darken(c, (int)(yd * spreadStep2)));
					}
			
					g.drawLine(3, y, w - 3, y);
				}

				g.setColor(Theme.scrollTrackBorderColor.getColor());
				g.drawLine(0, 0, 0, h - 1);
				
				if(!enabled) {
					c = Theme.scrollLightDisabledColor.getColor();
				}
				else {
					c = Theme.scrollBorderLightColor.getColor();
				}
				
				g.setColor(c);
				g.drawLine(2, 1, 2, h - 2);
				g.drawLine(w - 2, 1, w - 2, h - 2);
				Color lightAlpha = new Color(c.getRed(), c.getGreen(), c.getBlue(), TinyScrollBarUI.ALPHA);
				
				if(!enabled) {
					c = Theme.scrollBorderDisabledColor.getColor();
				}
				else {
					c = Theme.scrollBorderColor.getColor();
				}
				
				g.setColor(c);
				g.drawRect(1, 0, w - 2, h - 1);
				
				// edges - blend borderColor with lightColor
				g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), TinyScrollBarUI.ALPHA));				
				g.drawLine(2, 1, 2, 1);
				g.drawLine(w - 2, 1, w - 2, 1);
				g.drawLine(2, h - 2, 2, h - 2);
				g.drawLine(w - 2, h - 2, w - 2, h - 2);
				
				// blend lightColor with borderColor
				g.setColor(lightAlpha);				
				g.drawLine(1, 0, 1, 0);
				g.drawLine(1, h - 1, 1, h - 1);
				g.drawLine(w - 1, 0, w - 1, 0);
				g.drawLine(w - 1, h - 1, w - 1, h - 1);
				break;
			case SwingConstants.EAST:
			case SwingConstants.WEST:
				spreadStep1 = 10.0f * spread1 / 10;
				spreadStep2 = 10.0f * spread2 / 10;
				halfY = h / 2;
				
				for(int y = 1; y < w - 1; y++) {
					if(y < halfY) {
						yd = halfY - y;
						g.setColor(ColorRoutines.lighten(c, (int)(yd * spreadStep1)));
					}
					else if(y == halfY) {
						g.setColor(c);
					}
					else {
						yd = y - halfY;
						g.setColor(ColorRoutines.darken(c, (int)(yd * spreadStep2)));
					}
			
					g.drawLine(2, y + 1, w - 3, y + 1);
				}
				
				g.setColor(Theme.scrollTrackBorderColor.getColor());
				g.drawLine(0, 0, w - 1, 0);
				
				if(!enabled) {
					c = Theme.scrollLightDisabledColor.getColor();
				}
				else {
					c = Theme.scrollBorderLightColor.getColor();
				}
				
				g.setColor(c);
				g.drawLine(1, 2, 1, h - 2);
				g.drawLine(w - 2, 2, w - 2, h - 2);
				lightAlpha = new Color(c.getRed(), c.getGreen(), c.getBlue(), TinyScrollBarUI.ALPHA);
				
				if(!enabled) {
					c = Theme.scrollBorderDisabledColor.getColor();
				}
				else {
					c = Theme.scrollBorderColor.getColor();
				}
				
				g.setColor(c);
				g.drawRect(0, 1, w - 1, h - 2);
				
				// edges - blend borderColor with lightColor
				g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), TinyScrollBarUI.ALPHA));				
				g.drawLine(1, 2, 1, 2);
				g.drawLine(w - 2, 2, w - 2, 2);
				g.drawLine(1, h - 2, 1, h - 2);
				g.drawLine(w - 2, h - 2, w - 2, h - 2);
				
				// blend lightColor with borderColor
				g.setColor(lightAlpha);				
				g.drawLine(0, 1, 0, 1);
				g.drawLine(w - 1, 1, w - 1, 1);
				g.drawLine(0, h - 1, 0, h - 1);
				g.drawLine(w - 1, h - 1, w - 1, h - 1);
				break;
		}
	}

	private void drawXpArrow(Graphics g, Dimension size) {
		// We assume that size is quadratic
		int x = 0;
		int y = 0;
		
		switch(direction) {
			case SwingConstants.NORTH:
				x = (size.width - 8) / 2;
				y = (size.height - 5) / 2;
				g.translate(x, y);
				
				g.drawLine(4, 0, 4, 0);
				g.drawLine(3, 1, 5, 1);
				g.drawLine(2, 2, 6, 2);
				g.drawLine(1, 3, 3, 3);
				g.drawLine(5, 3, 7, 3);
				g.drawLine(0, 4, 2, 4);
				g.drawLine(6, 4, 8, 4);
				g.drawLine(1, 5, 1, 5);
				g.drawLine(7, 5, 7, 5);
				break; 
			case SwingConstants.SOUTH:
				x = (size.width - 8) / 2;
				y = (size.height - 5) / 2;
				g.translate(x, y);
				
				g.drawLine(1, 0, 1, 0);
				g.drawLine(7, 0, 7, 0);
				g.drawLine(0, 1, 2, 1);
				g.drawLine(6, 1, 8, 1);
				g.drawLine(1, 2, 3, 2);
				g.drawLine(5, 2, 7, 2);
				g.drawLine(2, 3, 6, 3);
				g.drawLine(3, 4, 5, 4);
				g.drawLine(4, 5, 4, 5);
				break;
			case SwingConstants.EAST:
				x = (size.width - 5) / 2;
				y = (size.height - 8) / 2;
				g.translate(x, y);
				
				g.drawLine(0, 1, 0, 1);
				g.drawLine(0, 7, 0, 7);
				g.drawLine(1, 0, 1, 2);
				g.drawLine(1, 6, 1, 8);
				g.drawLine(2, 1, 2, 3);
				g.drawLine(2, 5, 2, 7);
				g.drawLine(3, 2, 3, 6);
				g.drawLine(4, 3, 4, 5);
				g.drawLine(5, 4, 5, 4);
				break;
			case SwingConstants.WEST:
				x = (size.width - 5) / 2;
				y = (size.height - 8) / 2;
				g.translate(x, y);
				
				g.drawLine(0, 4, 0, 4);
				g.drawLine(1, 3, 1, 5);
				g.drawLine(2, 2, 2, 6);
				g.drawLine(3, 1, 3, 3);
				g.drawLine(3, 5, 3, 7);
				g.drawLine(4, 0, 4, 2);
				g.drawLine(4, 6, 4, 8);
				g.drawLine(5, 1, 5, 1);
				g.drawLine(5, 7, 5, 7);
				break;
		}
		
		g.translate(-x, -y);
	}

	/**
	 * Returns the preferred size of the component wich is the size of the skin
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return new Dimension(Theme.scrollSize.getValue(), Theme.scrollSize.getValue());
	}
	
	/*
	 * ScrollButtonKey is used as key in the cache HashMap.
	 * Overrides equals() and hashCode().
	 */
	private static class ScrollButtonKey {

		private Dimension size;
		private Color c;
		private boolean vertical;
		private boolean pressed;
		private boolean enabled;
		private boolean rollover;
		
		private ScrollButtonKey(Dimension size, boolean vertical,
			Color c, boolean pressed, boolean enabled, boolean rollover)
		{
			this.size = size;
			this.vertical = vertical;
			this.c = c;
			this.pressed = pressed;
			this.enabled = enabled;
			this.rollover = rollover;
		}
		
		public boolean equals(Object o) {
			if(o == null) return false;
			if(!(o instanceof ScrollButtonKey)) return false;

			ScrollButtonKey other = (ScrollButtonKey)o;
			
			return size.equals(other.size) &&
				vertical == other.vertical &&
				pressed == other.pressed &&
				enabled == other.enabled &&
				rollover == other.rollover &&
				c.equals(other.c);
		}
		
		public int hashCode() {
			return size.hashCode() *
				c.hashCode() *
				(pressed ? 1 : 2) *
				(enabled ? 4 : 8) *
				(rollover ? 16 : 32) *
				(vertical ? 64 : 128);
		}
	}
}