/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import sun.swing.DefaultLookup;

import de.muntjak.tinylookandfeel.util.ColorRoutines;

/**
 * TinyScrollBarUI
 * 
 * @version 1.4.0
 * @author Hans Bickel
 */
public class TinyScrollBarUI extends BasicScrollBarUI {
	
	// cache for already drawn thumbs - speeds up drawing by a factor of 3
	// new in 1.4.0
	private static final HashMap cache = new HashMap();
	
	static final int ALPHA = 92;	// 255 is full opaque

	/** true if thumb is in rollover state */
	protected boolean isRollover=false;
	
	/** true if thumb was in rollover state */
	protected boolean wasRollover=false;

	/**
	 * The free standing property of this scrollbar UI delegate.
	 */
	private boolean freeStanding = false;

	private int scrollBarWidth;
	
	private boolean buttonsEnabled = true;
		
	public static void clearCache() {
    	cache.clear();
    }
	
	private void calculateButtonsEnabled() {
		float extent = scrollbar.getVisibleAmount();
		float range = scrollbar.getMaximum() - scrollbar.getMinimum();
		
		buttonsEnabled = (range > extent);
	}

	protected void layoutVScrollbar(JScrollBar sb) {
		super.layoutVScrollbar(sb);
		calculateButtonsEnabled();
	}

	protected void layoutHScrollbar(JScrollBar sb) {
		super.layoutHScrollbar(sb);
		calculateButtonsEnabled();
	}

	/**
	 * Installs some default values.
	 */
	protected void installDefaults() {
		scrollBarWidth = Theme.scrollSize.getValue();
		
		super.installDefaults();
		scrollbar.setBorder(null);
		
		// If thumb is smaller than minimumThumbSize it
		// makes no sense to paint it
		minimumThumbSize = new Dimension(17, 17);
	}

	/**
	 * Creates the UI delegate for the given component.
	 * 
	 * @param mainColor
	 *            The component to create its UI delegate.
	 * @return The UI delegate for the given component.
	 */
	public static ComponentUI createUI(JComponent c) {
		return new TinyScrollBarUI();
	}

	/**
	 * Creates the decrease button of the scrollbar.
	 * 
	 * @param orientation
	 *            The button's orientation.
	 * @return The created button.
	 */
	protected JButton createDecreaseButton(int orientation) {
		return new TinyScrollButton(orientation, this);
	}

	/**
	 * Creates the increase button of the scrollbar.
	 *
	 * @param orientation The button's orientation.
	 * @return The created button.
	 */
	protected JButton createIncreaseButton(int orientation) {
		return new TinyScrollButton(orientation, this);
	}

	/// From MetalUI
	public Dimension getPreferredSize(JComponent c) {
		if(scrollbar.getOrientation() == JScrollBar.VERTICAL) {
			return new Dimension(scrollBarWidth, scrollBarWidth * 3 + 10);
		}
		else { // Horizontal
			return new Dimension(scrollBarWidth * 3 + 10, scrollBarWidth);
		}

	}
	
	public void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {	
		drawXpTrack(g, trackBounds);
	}

	private void drawXpTrack(Graphics g, Rectangle t) {
		if(isThumbVisible()) {
			g.setColor(Theme.scrollTrackColor.getColor());
			g.fillRect(t.x, t.y,  t.width, t.height);
			g.setColor(Theme.scrollTrackBorderColor.getColor());
		}
		else {
			g.setColor(Theme.scrollTrackDisabledColor.getColor());
			g.fillRect(t.x, t.y,  t.width, t.height);
			g.setColor(Theme.scrollTrackBorderDisabledColor.getColor());
		}

		if(scrollbar.getOrientation() == JScrollBar.VERTICAL) {
			g.drawLine(t.x, t.y, t.x, t.y + t.height - 1);
			g.drawLine(t.x + t.width - 1, t.y, t.x + t.width - 1, t.y + t.height - 1);
		}
		else {
			g.drawLine(t.x, t.y, t.x + t.width - 1, t.y);
			g.drawLine(t.x, t.y + t.height - 1, t.x + t.width - 1, t.y + t.height - 1);
		}
	}
	
	public void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		if(thumbBounds.isEmpty() || !scrollbar.isEnabled())	{
		    return;
		}
		
		if(TinyLookAndFeel.controlPanelInstantiated) {
			drawXpThumbNoCache(g, c, thumbBounds);
		}
		else {
			drawXpThumb(g, c, thumbBounds);
		}
	}

	private void drawXpThumb(Graphics g, JComponent comp, Rectangle t) {
		int spread1 = Theme.scrollSpreadLight.getValue();
		int spread2 = Theme.scrollSpreadDark.getValue();
		Color c = null;
		
		if(isDragging && isRollover) {
			c = Theme.scrollThumbPressedColor.getColor();
		}
		else if(isRollover && Theme.scrollRollover.getValue()) {
			c = Theme.scrollThumbRolloverColor.getColor();
		}
		else {
			c = Theme.scrollThumbColor.getColor();
		}
		
		ThumbKey key = new ThumbKey(c, spread1, spread2,
			(scrollbar.getOrientation() == JScrollBar.VERTICAL),
			t.getSize());
		
		Object value = cache.get(key);
		
		if(value != null) {
			// image was cached - paint image and return
			g.drawImage((Image)value, t.x, t.y, comp);
			return;
		}
		
		Image img = new BufferedImage(t.width, t.height, BufferedImage.TYPE_INT_ARGB);
		Graphics imgGraphics = img.getGraphics();

		imgGraphics.setColor(c);

		int x1 = 0;
		int y1 = 0;
		int x2 = t.width - 1;
		int y2 = t.height - 1;
		float spreadStep1 = 10.0f * spread1 / 10;
		float spreadStep2 = 10.0f * spread2 / 10;

		switch(scrollbar.getOrientation()) {
			case JScrollBar.VERTICAL:
				int h = t.width - 2;
				int halfY = t.width * 3 / 8;
				int yd;
				
				for(int y = 1; y < h; y++) {
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
			
					imgGraphics.drawLine(x1 + y, y1 + 2, x1 + y, y2 - 1);
				}

				imgGraphics.setColor(Theme.scrollBorderLightColor.getColor());
				imgGraphics.drawLine(x1 + 3, y1 + 1, x1 + t.width - 3, y1 + 1);
				imgGraphics.drawLine(x1 + t.width - 2, y1 + 2, x1 + t.width - 2, y2 - 2);
				
				imgGraphics.setColor(Theme.scrollBorderColor.getColor());
				imgGraphics.drawRect(x1 + 1, y1, x1 + t.width - 2, y2 - y1);

				// edges - blend borderColor with lightColor
				Color a = Theme.scrollBorderColor.getColor();
				imgGraphics.setColor(new Color(a.getRed(), a.getGreen(), a.getBlue(), ALPHA));				
				imgGraphics.drawLine(x1 + 2, y1 + 1, x1 + 2, y1 + 1);
				imgGraphics.drawLine(x1 + t.width - 2, y1 + 1, x1 + t.width - 2, y1 + 1);
				imgGraphics.drawLine(x1 + 2, y2 - 1, x1 + 2, y2 - 1);
				imgGraphics.drawLine(x1 + t.width - 2, y2 - 1, x1 + t.width - 2, y2 - 1);
				
				// blend lightColor with borderColor
				a = Theme.scrollBorderLightColor.getColor();
				imgGraphics.setColor(new Color(a.getRed(), a.getGreen(), a.getBlue(), ALPHA));
				imgGraphics.drawLine(x1 + 1, y1, x1 + 1, y1);
				imgGraphics.drawLine(x1 + t.width - 1, y1, x1 + t.width - 1, y1);
				imgGraphics.drawLine(x1 + 1, y2, x1 + 1, y2);
				imgGraphics.drawLine(x1 + t.width - 1, y2, x1 + t.width - 1, y2);
				break;
			case JScrollBar.HORIZONTAL:
				h = t.height - 2;
				halfY = t.height * 3 / 8;
				
				for(int y = 1; y < h; y++) {
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
			
					imgGraphics.drawLine(x1 + 1, y1 + y, x2 - 2, y1 + y);
				}
				
				imgGraphics.setColor(Theme.scrollBorderLightColor.getColor());
				imgGraphics.drawLine(x1 + 2, y1 + t.height - 2, x2 - 2, y1 + t.height - 2);
				imgGraphics.drawLine(x2 - 1, y1 + 3, x2 - 1, y1 + 14);
				
				imgGraphics.setColor(Theme.scrollBorderColor.getColor());
				imgGraphics.drawRect(x1, y1 + 1, x2 - x1, y1 + t.height - 2);
				
				imgGraphics.setColor(Theme.scrollTrackBorderColor.getColor());
				imgGraphics.drawLine(x1, y1, x2, y1);
				
				// edges - blend borderColor with lightColor
				a = Theme.scrollBorderColor.getColor();
				imgGraphics.setColor(new Color(a.getRed(), a.getGreen(), a.getBlue(), ALPHA));				
				imgGraphics.drawLine(x1 + 1, y1 + 2, x1 + 1, y1 + 2);
				imgGraphics.drawLine(x1 + 1, y1 + t.height - 2, x1 + 1, y1 + t.height - 2);
				imgGraphics.drawLine(x2 - 1, y1 + 2, x2 - 1, y1 + 2);
				imgGraphics.drawLine(x2 - 1, y1 + t.height - 2, x2 - 1, y1 + t.height - 2);
				
				// blend lightColor with borderColor
				a = Theme.scrollBorderLightColor.getColor();
				imgGraphics.setColor(new Color(a.getRed(), a.getGreen(), a.getBlue(), ALPHA));
				imgGraphics.drawLine(x1, y1 + 1, x1, y1 + 1);
				imgGraphics.drawLine(x1, y1 + t.height - 1, x1, y1 + t.height - 1);
				imgGraphics.drawLine(x2, y1 + 1, x2, y1 + 1);
				imgGraphics.drawLine(x2, y1 + t.height - 1, x2, y1 + t.height - 1);
				break;
		}
		
		// draw Grip
		if(t.height < 13) return;
		
		if(scrollbar.getOrientation() == JScrollBar.VERTICAL) {
			y1 = (t.height) / 2 - 4;
			y2 = Math.min(y1 + 8, t.height - 3);
			x1 = t.width / 4 + 1;
			x2 = t.width - t.width / 4 - 2;
			int y = y1 + 1;
			// we take only saturation & brightness and apply them
			// to the background color (normal/rollover/pressed)
			imgGraphics.setColor(ColorRoutines.getAdjustedColor(c,
				Theme.scrollGripLightColor.getSaturation(),
				Theme.scrollGripLightColor.getBrightness()));
			
			while(y < y2) {
				imgGraphics.drawLine(x1, y, x2, y);
				y += 2;
			}
			
			y = y1;
			x1 = t.width / 4 + 2;
			x2 = t.width - t.width / 4 - 1;
			imgGraphics.setColor(ColorRoutines.getAdjustedColor(c,
				Theme.scrollGripDarkColor.getSaturation(),
				Theme.scrollGripDarkColor.getBrightness()));
			
			while(y < y2) {
				imgGraphics.drawLine(x1, y, x2, y);
				y += 2;
			}
		}
		else {	// HORIZONTAL
			x1 = (t.width) / 2 - 4;
			x2 = Math.min(x1 + 8, t.width - 3);
			y1 = t.height / 4 + 1;
			y2 = t.height - t.height / 4 - 2;
			int x = x1 + 1;
			// we take only saturation & brightness and apply them
			// to the background color (normal/rollover/pressed)
			imgGraphics.setColor(ColorRoutines.getAdjustedColor(c,
				Theme.scrollGripLightColor.getSaturation(),
				Theme.scrollGripLightColor.getBrightness()));
			
			while(x < x2) {
				imgGraphics.drawLine(x, y1, x, y2);
				x += 2;
			}
			
			x = x1;
			y1 = t.height / 4 + 2;
			y2 = t.height - t.height / 4 - 1;
			imgGraphics.setColor(ColorRoutines.getAdjustedColor(c,
				Theme.scrollGripDarkColor.getSaturation(),
				Theme.scrollGripDarkColor.getBrightness()));
			
			while(x < x2) {
				imgGraphics.drawLine(x, y1, x, y2);
				x += 2;
			}
		}

//		int x1 = 0;
//		int y1 = 0;
//		int x2 = t.width - 1;
//		int y2 = t.height - 1;
//		
//		int spread1 = Theme.scrollSpreadLight.getValue();
//		int spread2 = Theme.scrollSpreadDark.getValue();
//		
//		int h = 15;
//		float spreadStep1 = 10.0f * spread1 / 10;
//		float spreadStep2 = 10.0f * spread2 / 10;
//		int halfY = h / 2;
//		int yd;
//
//		if(scrollbar.getOrientation() == JScrollBar.VERTICAL) {
//			for(int y = 1; y < h; y++) {
//				if(y < halfY) {
//					yd = halfY - y;
//					imgGraphics.setColor(ColorRoutines.lighten(c, (int)(yd * spreadStep1)));
//				}
//				else if(y == halfY) {
//					imgGraphics.setColor(c);
//				}
//				else {
//					yd = y - halfY;
//					imgGraphics.setColor(ColorRoutines.darken(c, (int)(yd * spreadStep2)));
//				}
//		
//				imgGraphics.drawLine(y, 2, y, y2 - 1);
//			}
//
//			imgGraphics.setColor(Theme.scrollBorderLightColor.getColor());
//			imgGraphics.drawLine(3, 1, 14, 1);
//			imgGraphics.drawLine(15, 2, 15, y2 - 2);
//			
//			imgGraphics.setColor(Theme.scrollBorderColor.getColor());
//			imgGraphics.drawRect(1, y1, 15, y2 - y1);
//
//			// edges - blend borderColor with lightColor
//			Color a = Theme.scrollBorderColor.getColor();
//			imgGraphics.setColor(new Color(a.getRed(), a.getGreen(), a.getBlue(), alpha));				
//			imgGraphics.drawLine(2, 1, 2, 1);
//			imgGraphics.drawLine(15, 1, 15, 1);
//			imgGraphics.drawLine(2, y2 - 1, 2, y2 - 1);
//			imgGraphics.drawLine(15, y2 - 1, 15, y2 - 1);
//			
//			// blend lightColor with borderColor
//			a = Theme.scrollBorderLightColor.getColor();
//			imgGraphics.setColor(new Color(a.getRed(), a.getGreen(), a.getBlue(), alpha));
//			imgGraphics.drawLine(1, y1, 1, y1);
//			imgGraphics.drawLine(16, y1, 16, y1);
//			imgGraphics.drawLine(1, y2, 1, y2);
//			imgGraphics.drawLine(16, y2, 16, y2);
//			
//			// paint grip
//			y1 = t.height / 2 - 3;
//			y2 = Math.min(y1 + 8, t.height - 5);
//			
//			int y = y1;
//			// we take only saturation & brightness and apply them
//			// to the background color (normal/rollover/pressed)
//			imgGraphics.setColor(ColorRoutines.getAdjustedColor(c,
//				Theme.scrollGripLightColor.getSaturation(),
//				Theme.scrollGripLightColor.getBrightness()));
//			
//			while(y < y2) {
//				imgGraphics.drawLine(5, y, 11, y);
//				y += 2;
//			}
//			
//			y = y1 + 1;
//			
//			imgGraphics.setColor(ColorRoutines.getAdjustedColor(c,
//				Theme.scrollGripDarkColor.getSaturation(),
//				Theme.scrollGripDarkColor.getBrightness()));
//			while(y < y2) {
//				imgGraphics.drawLine(6, y, 12, y);
//				y += 2;
//			}
//		}
//		else {
//			for(int y = 1; y < h; y++) {
//				if(y < halfY) {
//					yd = halfY - y;
//					imgGraphics.setColor(ColorRoutines.lighten(c, (int)(yd * spreadStep1)));
//				}
//				else if(y == halfY) {
//					imgGraphics.setColor(c);
//				}
//				else {
//					yd = y - halfY;
//					imgGraphics.setColor(ColorRoutines.darken(c, (int)(yd * spreadStep2)));
//				}
//		
//				imgGraphics.drawLine(1, y, x2 - 2, y);
//			}
//			
//			imgGraphics.setColor(Theme.scrollBorderLightColor.getColor());
//			imgGraphics.drawLine(2, 15, x2 - 2, 15);
//			imgGraphics.drawLine(x2 - 1, 3, x2 - 1, 14);
//			
//			imgGraphics.setColor(Theme.scrollBorderColor.getColor());
//			imgGraphics.drawRect(x1, 1, x2 - x1, 15);
//			
//			imgGraphics.setColor(Theme.scrollTrackBorderColor.getColor());
//			imgGraphics.drawLine(x1, y1, x2, y1);
//			
//			// edges - blend borderColor with lightColor
//			Color a = Theme.scrollBorderColor.getColor();
//			imgGraphics.setColor(new Color(a.getRed(), a.getGreen(), a.getBlue(), alpha));				
//			imgGraphics.drawLine(1, 2, 1, 2);
//			imgGraphics.drawLine(1, 15, 1, 15);
//			imgGraphics.drawLine(x2 - 1, 2, x2 - 1, 2);
//			imgGraphics.drawLine(x2 - 1, 15, x2 - 1, 15);
//			
//			// blend lightColor with borderColor
//			a = Theme.scrollBorderLightColor.getColor();
//			imgGraphics.setColor(new Color(a.getRed(), a.getGreen(), a.getBlue(), alpha));
//			imgGraphics.drawLine(x1, 1, x1, 1);
//			imgGraphics.drawLine(x1, 16, x1, 16);
//			imgGraphics.drawLine(x2, 1, x2, 1);
//			imgGraphics.drawLine(x2, 16, x2, 16);
//			
//			// paint grip
//			x1 = t.width / 2 - 3;
//			x2 = Math.min(x1 + 8, t.width - 5);
//			
//			int x = x1 + 1;
//			// we take only saturation & brightness and apply them
//			// to the background color (normal/rollover/pressed)
//			imgGraphics.setColor(ColorRoutines.getAdjustedColor(c,
//				Theme.scrollGripLightColor.getSaturation(),
//				Theme.scrollGripLightColor.getBrightness()));
//			
//			while(x < x2) {
//				imgGraphics.drawLine(x, 5, x, 11);
//				x += 2;
//			}
//			
//			x = x1;
//			imgGraphics.setColor(ColorRoutines.getAdjustedColor(c,
//				Theme.scrollGripDarkColor.getSaturation(),
//				Theme.scrollGripDarkColor.getBrightness()));
//			
//			while(x < x2) {
//				imgGraphics.drawLine(x, 6, x, 12);
//				x += 2;
//			}
//		}

		// dispose of image graphics
		imgGraphics.dispose();
		
		// draw the image
		g.drawImage(img, t.x, t.y, comp);
		
		// add the image to the cache
		cache.put(key, img);
		
		if(TinyLookAndFeel.PRINT_CACHE_SIZES) {
			System.out.println("TinyScrollBarUI.cache.size=" + cache.size());
		}
	}
	
	private void drawXpThumbNoCache(Graphics g, JComponent comp, Rectangle t) {
		Color c = null;
		
		if(isDragging && isRollover) {
			c = Theme.scrollThumbPressedColor.getColor();
		}
		else if(isRollover && Theme.scrollRollover.getValue()) {
			c = Theme.scrollThumbRolloverColor.getColor();
		}
		else {
			c = Theme.scrollThumbColor.getColor();
		}

		g.setColor(c);

		int x2 = t.x + t.width - 1;
		int y2 = t.y + t.height - 1;
		int spread1 = Theme.scrollSpreadLight.getValue();
		int spread2 = Theme.scrollSpreadDark.getValue();
		float spreadStep1 = 10.0f * spread1 / 10;
		float spreadStep2 = 10.0f * spread2 / 10;

		switch(scrollbar.getOrientation()) {
			case JScrollBar.VERTICAL:
				int h = t.width - 2;
				int halfY = t.width * 3 / 8;
				int yd;
				
				for(int y = 1; y < h; y++) {
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
			
					g.drawLine(t.x + y, t.y + 2, t.x + y, y2 - 1);
				}

				g.setColor(Theme.scrollBorderLightColor.getColor());
				g.drawLine(t.x + 3, t.y + 1, t.x + t.width - 3, t.y + 1);
				g.drawLine(t.x + t.width - 2, t.y + 2, t.x + t.width - 2, y2 - 2);
				
				g.setColor(Theme.scrollBorderColor.getColor());
				g.drawRect(t.x + 1, t.y, t.x + t.width - 2, y2 - t.y);

				// edges - blend borderColor with lightColor
				Color a = Theme.scrollBorderColor.getColor();
				g.setColor(new Color(a.getRed(), a.getGreen(), a.getBlue(), ALPHA));				
				g.drawLine(t.x + 2, t.y + 1, t.x + 2, t.y + 1);
				g.drawLine(t.x + t.width - 2, t.y + 1, t.x + t.width - 2, t.y + 1);
				g.drawLine(t.x + 2, y2 - 1, t.x + 2, y2 - 1);
				g.drawLine(t.x + t.width - 2, y2 - 1, t.x + t.width - 2, y2 - 1);
				
				// blend lightColor with borderColor
				a = Theme.scrollBorderLightColor.getColor();
				g.setColor(new Color(a.getRed(), a.getGreen(), a.getBlue(), ALPHA));
				g.drawLine(t.x + 1, t.y, t.x + 1, t.y);
				g.drawLine(t.x + t.width - 1, t.y, t.x + t.width - 1, t.y);
				g.drawLine(t.x + 1, y2, t.x + 1, y2);
				g.drawLine(t.x + t.width - 1, y2, t.x + t.width - 1, y2);
				break;
			case JScrollBar.HORIZONTAL:
				h = t.height - 2;
				halfY = t.height * 3 / 8;
				
				for(int y = 1; y < h; y++) {
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
			
					g.drawLine(t.x + 1, t.y + y, x2 - 2, t.y + y);
				}
				
				g.setColor(Theme.scrollBorderLightColor.getColor());
				g.drawLine(t.x + 2, t.y + t.height - 2, x2 - 2, t.y + t.height - 2);
				g.drawLine(x2 - 1, t.y + 3, x2 - 1, t.y + 14);
				
				g.setColor(Theme.scrollBorderColor.getColor());
				g.drawRect(t.x, t.y + 1, x2 - t.x, t.y + t.height - 2);
				
				g.setColor(Theme.scrollTrackBorderColor.getColor());
				g.drawLine(t.x, t.y, x2, t.y);
				
				// edges - blend borderColor with lightColor
				a = Theme.scrollBorderColor.getColor();
				g.setColor(new Color(a.getRed(), a.getGreen(), a.getBlue(), ALPHA));				
				g.drawLine(t.x + 1, t.y + 2, t.x + 1, t.y + 2);
				g.drawLine(t.x + 1, t.y + t.height - 2, t.x + 1, t.y + t.height - 2);
				g.drawLine(x2 - 1, t.y + 2, x2 - 1, t.y + 2);
				g.drawLine(x2 - 1, t.y + t.height - 2, x2 - 1, t.y + t.height - 2);
				
				// blend lightColor with borderColor
				a = Theme.scrollBorderLightColor.getColor();
				g.setColor(new Color(a.getRed(), a.getGreen(), a.getBlue(), ALPHA));
				g.drawLine(t.x, t.y + 1, t.x, t.y + 1);
				g.drawLine(t.x, t.y + t.height - 1, t.x, t.y + t.height - 1);
				g.drawLine(x2, t.y + 1, x2, t.y + 1);
				g.drawLine(x2, t.y + t.height - 1, x2, t.y + t.height - 1);
				break;
		}
		
		// draw Grip
		if(t.height < 13) return;
		
		if(scrollbar.getOrientation() == JScrollBar.VERTICAL) {
			int y1 = t.y + (t.height) / 2 - 4;
			y2 = Math.min(y1 + 8, t.y + t.height - 3);
			int x1 = t.x + t.width / 4 + 1;
			x2 = t.x + t.width - t.width / 4 - 2;
			int y = y1 + 1;
			// we take only saturation & brightness and apply them
			// to the background color (normal/rollover/pressed)
			g.setColor(ColorRoutines.getAdjustedColor(c,
				Theme.scrollGripLightColor.getSaturation(),
				Theme.scrollGripLightColor.getBrightness()));
			
			while(y < y2) {
				g.drawLine(x1, y, x2, y);
				y += 2;
			}
			
			x1 = t.x + t.width / 4 + 2;
			x2 = t.x + t.width - t.width / 4 - 1;
			y = y1;
			g.setColor(ColorRoutines.getAdjustedColor(c,
				Theme.scrollGripDarkColor.getSaturation(),
				Theme.scrollGripDarkColor.getBrightness()));
			
			while(y < y2) {
				g.drawLine(x1, y, x2, y);
				y += 2;
			}
		}
		else {	// HORIZONTAL
			int x1 = t.x + (t.width) / 2 - 4;
			x2 = Math.min(x1 + 8, t.x + t.width - 3);
			int y1 = t.y + t.height / 4 + 1;
			y2 = t.y + t.height - t.height / 4 - 2;
			int x = x1 + 1;
			// we take only saturation & brightness and apply them
			// to the background color (normal/rollover/pressed)
			g.setColor(ColorRoutines.getAdjustedColor(c,
				Theme.scrollGripLightColor.getSaturation(),
				Theme.scrollGripLightColor.getBrightness()));
			
			while(x < x2) {
				g.drawLine(x, y1, x, y2);
				x += 2;
			}
			
			y1 = t.y + t.height / 4 + 2;
			y2 = t.y + t.height - t.height / 4 - 1;
			x = x1;
			g.setColor(ColorRoutines.getAdjustedColor(c,
				Theme.scrollGripDarkColor.getSaturation(),
				Theme.scrollGripDarkColor.getBrightness()));
			
			while(x < x2) {
				g.drawLine(x, y1, x, y2);
				x += 2;
			}
		}
	}

	/**
	 * A return value of <code>true</code> signals that
	 * scrollbar buttons shall be painted enabled whereas
	 * a return value of <code>false</code> signals that
	 * scrollbar buttons shall be painted disabled.
	 * @return <code>true</code> if thumb is visible,
	 * <code>false</code> otherwise.
	 */
	public boolean isThumbVisible() {
		return buttonsEnabled;
	}

	// From BasicUI
    protected TrackListener createTrackListener(){
		return new MyTrackListener();
    }

	/**
	 * Basically does BasicScrollBarUI.TrackListener the right job, it just needs
	 * an additional repaint and rollover management
	 */
	protected class MyTrackListener extends BasicScrollBarUI.TrackListener {
		
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			scrollbar.repaint();
		}

		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			scrollbar.repaint();
		}
		
		public void mouseEntered(MouseEvent e) {
			isRollover=false;
			wasRollover=false;
		    if(getThumbBounds().contains(e.getPoint())) {
		    	isRollover = true;
		    	wasRollover = isRollover;
		    	scrollbar.repaint();
		    }
		}
		
		public void mouseExited(MouseEvent e) {
			isRollover=false;
	    	if(isRollover != wasRollover) {
		    	wasRollover = isRollover;
		    	scrollbar.repaint();
	    	}
		}
		
		public void mouseDragged(MouseEvent e) {
		    if(getThumbBounds().contains(e.getPoint())) {
		    	isDragging=true;
		    }
			super.mouseDragged(e);
		}
		
		public void mouseMoved(MouseEvent e) {
		    if(getThumbBounds().contains(e.getPoint())) {
		    	isRollover=true;
		    	if(isRollover != wasRollover) {
			    	scrollbar.repaint();
			    	wasRollover = isRollover;
		    	}
		    } else {
		    	isRollover=false;
		    	if(isRollover != wasRollover) {
			    	scrollbar.repaint();
			    	wasRollover = isRollover;
		    	}
		    }
		}
	}

	protected class OrientationChangeListener implements PropertyChangeListener {
		
		public void propertyChange(PropertyChangeEvent e) {
			Integer orient = (Integer)e.getNewValue();

            if(scrollbar.getComponentOrientation().isLeftToRight()) { 
                if(incrButton instanceof TinyScrollButton) {
                    ((TinyScrollButton)incrButton).setDirection(
                    	orient.intValue() == HORIZONTAL? EAST : SOUTH);
                }
                if(decrButton instanceof TinyScrollButton) {
                    ((TinyScrollButton)decrButton).setDirection(
                    	orient.intValue() == HORIZONTAL? WEST : NORTH);
                }
            }
            else {
                if(incrButton instanceof TinyScrollButton) {
                    ((TinyScrollButton)incrButton).setDirection(
                    	orient.intValue() == HORIZONTAL? WEST : SOUTH);
                }
                if(decrButton instanceof TinyScrollButton) {
                    ((TinyScrollButton)decrButton).setDirection(
                    	orient.intValue() == HORIZONTAL? EAST : NORTH);
                }
            }
		}
	}
	
	/*
	 * ThumbKey is used as key in the cache HashMap.
	 * Overrides equals() and hashCode().
	 */
	private static class ThumbKey {

		private Color c;
		private int spread1;
		private int spread2;
		private Dimension size;
		private boolean vertical;

		ThumbKey(Color c, int spread1, int spread2, boolean vertical, Dimension size) {
			this.c = c;
			this.spread1 = spread1;
			this.spread2 = spread2;
			this.vertical = vertical;
			this.size = size;
		}
		
		public boolean equals(Object o) {
			if(o == null) return false;
			if(!(o instanceof ThumbKey)) return false;

			ThumbKey other = (ThumbKey)o;
			
			return spread1 == other.spread1 &&
				spread2 == other.spread2 &&
				vertical == other.vertical &&
				size.equals(other.size) &&
				c.equals(other.c);
		}
		
		public int hashCode() {
			return c.hashCode() *
				size.hashCode() *
				(vertical ? 1 : 2) *
				spread1 *
				spread2;
		}
	}
}