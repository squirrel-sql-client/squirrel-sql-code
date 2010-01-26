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
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.AbstractButton;

import de.muntjak.tinylookandfeel.util.ColorRoutines;
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * TinyRadioButtonIcon
 * 
 * @version 1.0
 * @author Hans Bickel
 */
class TinyRadioButtonIcon extends TinyCheckBoxIcon {

	// cache for already drawn icons - speeds up drawing by a factor
	// of 80 or more if there are several radio buttons or one radio button
	// is painted several times
	private static final HashMap cache = new HashMap();
	
	private static final int[][] ALPHA = {
		{255, 255, 255, 242, 228, 209, 187, 165, 142, 255, 255},
		{255, 255, 242, 228, 209, 187, 165, 142, 120, 104, 255},
		{255, 242, 228, 209, 187, 165, 142, 120, 104, 86, 72},
		{242, 228, 209, 187, 165, 142, 120, 104, 86, 72, 56},
		{228, 209, 187, 165, 142, 120, 104, 86, 72, 56, 42},
		{209, 187, 165, 142, 120, 104, 86, 72, 56, 42, 28},
		{187, 165, 142, 120, 104, 86, 72, 56, 42, 28, 17},
		{165, 142, 120, 104, 86, 72, 56, 42, 28, 17, 9},
		{142, 120, 104, 86, 72, 56, 42, 28, 17, 9, 0},
		{255, 104, 86, 72, 56, 42, 28, 17, 9, 0, 255},
		{255, 255, 72, 56, 42, 28, 17, 9, 0, 255, 255}
	};
	
	public static void clearCache() {
    	cache.clear();
    }

	/**
	 * Draws the check box icon at the specified location.
	 *
	 * @param c The component to draw on.
	 * @param g The graphics context.
	 * @param x The x coordinate of the top left corner.
	 * @param y The y coordinate of the top left corner.
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		AbstractButton button = (AbstractButton) c;

		// inner space
		Color col = null;

		if(!button.isEnabled()) {
			col = Theme.buttonDisabledColor.getColor();
		}
		else if(button.getModel().isPressed()) {
			if(button.getModel().isRollover() || button.getModel().isArmed()) {
				col = Theme.buttonPressedColor.getColor();
			}
			else {
				col = Theme.buttonNormalColor.getColor();
			}
		}
		else if(button.getModel().isRollover()) {
			col = Theme.buttonRolloverBgColor.getColor();
		}
		else {
			col = Theme.buttonNormalColor.getColor();
		}
				
		g.setColor(col);
		
		if(TinyLookAndFeel.controlPanelInstantiated) {
			drawXpRadioNoCache(g, button, col, x, y, getIconWidth(), getIconHeight());
		}
		else {
			drawXpRadio(g, button, col, x, y, getIconWidth(), getIconHeight());
		}

		// checkmark
		if(!button.isSelected()) return;
		
		if(!button.isEnabled()) {
			col = Theme.buttonCheckDisabledColor.getColor();
		}
		else {
			col = Theme.buttonCheckColor.getColor();
		}
		g.setColor(col);
		
		drawXpCheckMark(g, col, x, y);
	}

	private void drawXpRadio(Graphics g, AbstractButton b, Color c,
		int x, int y, int w, int h)
	{
		boolean pressed = b.getModel().isPressed();
		boolean armed = b.getModel().isArmed();
		boolean enabled = b.isEnabled();
		boolean rollover = b.getModel().isRollover();
		boolean focused = (Theme.buttonFocusBorder.getValue() &&
			!rollover && b.isFocusOwner());

		Color bg = b.getBackground();
		
		if(!b.isOpaque()) {
			Container parent = b.getParent();
			bg = parent.getBackground();
			
			while(parent != null && !parent.isOpaque()) {
				parent = parent.getParent();
				bg = parent.getBackground();
			}
		}
		
		RadioKey key = new RadioKey(c, bg, pressed, enabled, (rollover || armed), focused);
		Object value = cache.get(key);

		if(value != null) {
			// image already cached - paint image and return
			g.drawImage((Image)value, x, y, b);
			return;
		}

		Image img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics imgGraphics = img.getGraphics();
		
		imgGraphics.setColor(bg);
		imgGraphics.fillRect(0, 0, w, h);

		// spread light is between 0 and 20
		int spread1 = Theme.buttonSpreadLight.getValue();
		int spread2 = Theme.buttonSpreadDark.getValue();
		
		if(!enabled) {
			spread1 = Theme.buttonSpreadLightDisabled.getValue();
			spread2 = Theme.buttonSpreadDarkDisabled.getValue();
		}
		
		int spreadStep1 = spread1 * 5;	// 20 -> 100
		// this means, we can never fully darken background,
		// but we also want it bright enough
		int spreadStep2 = spread2 * 4;	// 20 -> 80
		
		if(pressed && (rollover || armed)) {
			spreadStep2 *= 2;
		}

		c = ColorRoutines.lighten(c, spreadStep1);

		imgGraphics.setColor(ColorRoutines.darken(c, spreadStep2));
		imgGraphics.fillRect(2, 2, w - 4, h - 4);
		imgGraphics.fillRect(1, 5, 1, 3);
		imgGraphics.fillRect(11, 5, 1, 3);
		imgGraphics.fillRect(5, 1, 3, 1);
		imgGraphics.fillRect(5, 11, 3, 1);

		Color color;
		
		for (int row = 0; row < 11; row++) {
			for (int col = 0; col < 11; col++) {
				color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255 - ALPHA[col][row]);
				imgGraphics.setColor(color);
				imgGraphics.drawLine(col + 1, row + 1, col + 1, row + 1);
			}
		}

		// border
		if(!enabled) {
			DrawRoutines.drawXpRadioBorder(imgGraphics,
				Theme.buttonBorderDisabledColor.getColor(), 0, 0, w, h);
		}
		else {
			if(rollover && Theme.buttonRolloverBorder.getValue() && !pressed) {
				DrawRoutines.drawXpRadioRolloverBorder(imgGraphics,
					Theme.buttonRolloverColor.getColor(), 0, 0, w, h);
			}
			else if(focused && !pressed) {
				DrawRoutines.drawXpRadioRolloverBorder(imgGraphics,
					Theme.buttonDefaultColor.getColor(), 0, 0, w, h);
			}

			DrawRoutines.drawXpRadioBorder(imgGraphics,
				Theme.buttonBorderColor.getColor(), 0, 0, w, h);
		}
		
		// dispose of image graphics
		imgGraphics.dispose();
		
		// draw the image
		g.drawImage(img, x, y, b);
		
		// add the image to the cache
		cache.put(key, img);
		
		if(TinyLookAndFeel.PRINT_CACHE_SIZES) {
			System.out.println("TinyRadioButtonIcon.cache.size=" + cache.size());
		}
	}
	
	// the old routine - execute this, if we are in the control panel
	// (else border colors not updated)
	private void drawXpRadioNoCache(Graphics g, AbstractButton b, Color c,
		int x, int y, int w, int h)
	{
		boolean pressed = b.getModel().isPressed();
		boolean armed = b.getModel().isArmed();
		boolean enabled = b.isEnabled();
		boolean rollover = b.getModel().isRollover();
		boolean focused = (Theme.buttonFocusBorder.getValue() &&
			!rollover && b.isFocusOwner());
		boolean useCachedImage = !pressed && !armed && !rollover && !focused;
		Image img = null;
		Object key = null;
		
		if(useCachedImage) {
			if(enabled) {
				key = new EnabledRadioKey(c,
					Theme.buttonBorderColor.getColor());
			}
			else {
				key = new DisabledRadioKey(c,
					Theme.buttonBorderDisabledColor.getColor());
			}
			
			Object value = cache.get(key);

			if(value != null) {
				// image already cached - paint image and return
				g.drawImage((Image)value, x, y, b);
				return;
			}
			
			img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		}

		// spread light is between 0 and 20
		int spread1 = Theme.buttonSpreadLight.getValue();
		int spread2 = Theme.buttonSpreadDark.getValue();
		
		if(!enabled) {
			spread1 = Theme.buttonSpreadLightDisabled.getValue();
			spread2 = Theme.buttonSpreadDarkDisabled.getValue();
		}
		
		int spreadStep1 = spread1 * 5;	// 20 -> 100
		// this means, we can never fully darken background,
		// but we also want it bright enough
		int spreadStep2 = spread2 * 4;	// 20 -> 80
		
		if(pressed && (rollover || armed)) {
			spreadStep2 *= 2;
		}

		c = ColorRoutines.lighten(c, spreadStep1);
		
		Graphics graphics = null;
		int bx = x, by = y;
		
		if(img != null) {
			graphics = img.getGraphics();
			
			Color bg = b.getBackground();
			
			if(!b.isOpaque()) {
				Container parent = b.getParent();
				bg = parent.getBackground();
				
				while(parent != null && !parent.isOpaque()) {
					parent = parent.getParent();
					bg = parent.getBackground();
				}
			}
			
			graphics.setColor(bg);
			graphics.fillRect(0, 0, w - 1, h - 1);
			bx = 0; by = 0;
		}
		else {
			graphics = g;
			graphics.translate(x, y);
		}

		graphics.setColor(ColorRoutines.darken(c, spreadStep2));
		graphics.fillRect(2, 2, w - 4, h - 4);
		graphics.fillRect(1, 5, 1, 3);
		graphics.fillRect(11, 5, 1, 3);
		graphics.fillRect(6, 1, 1, 1);
		graphics.fillRect(6, 11, 1, 1);

		Color color;
		
		for(int row = 0; row < 11; row++) {
			for(int col = 0; col < 11; col++) {
				color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255 - ALPHA[col][row]);
				graphics.setColor(color);
				graphics.drawLine(col + 1, row + 1, col + 1, row + 1);
			}
		}

		if(img == null) {
			graphics.translate(-x, -y);
		}

		// border
		if(!enabled) {
			DrawRoutines.drawXpRadioBorder(
				graphics, Theme.buttonBorderDisabledColor.getColor(), bx, by, w, h);
		}
		else {
			if(rollover && Theme.buttonRolloverBorder.getValue() && !pressed) {
				DrawRoutines.drawXpRadioRolloverBorder(
					graphics, Theme.buttonRolloverColor.getColor(), bx, by, w, h);
			}
			else if(focused && !pressed) {
				DrawRoutines.drawXpRadioRolloverBorder(graphics,
					Theme.buttonDefaultColor.getColor(), bx, by, w, h);
			}

			DrawRoutines.drawXpRadioBorder(
				graphics, Theme.buttonBorderColor.getColor(), bx, by, w, h);
		}
		
		if(img != null) {
			// dispose of image graphics
			graphics.dispose();
			
			// draw the image
			g.drawImage(img, x, y, b);
			
			// cache image
			cache.put(key, img);
		}
	}

	private void drawXpCheckMark(Graphics g, Color c, int x, int y) {
		g.translate(x, y);
		
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 224));
		g.fillRect(5, 5, 3, 3);
		
		g.setColor(c);
		g.drawLine(6, 6, 6, 6);
		
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 192));
		g.drawLine(6, 4, 6, 4);
		g.drawLine(4, 6, 4, 6);
		g.drawLine(8, 6, 8, 6);
		g.drawLine(6, 8, 6, 8);
		
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 128));
		g.drawLine(5, 4, 5, 4);
		g.drawLine(7, 4, 7, 4);
		g.drawLine(4, 5, 4, 5);
		g.drawLine(8, 5, 8, 5);
		g.drawLine(4, 7, 4, 7);
		g.drawLine(8, 7, 8, 7);
		g.drawLine(5, 8, 5, 8);
		g.drawLine(7, 8, 7, 8);
		
		g.translate(-x, -y);
	}
	
	/*
	 * EnabledRadioKey is used as key in the cache HashMap.
	 * Overrides equals() and hashCode().
	 * Used only if we are run from ControlPanel.
	 */
	private static class EnabledRadioKey {
		int spread1;
		int spread2;
		Color c, back;
		
		EnabledRadioKey(Color c, Color back) {
			spread1 = Theme.buttonSpreadLight.getValue();
			spread2 = Theme.buttonSpreadDark.getValue();
			this.c = c;
			this.back = back;
		}
		
		public boolean equals(Object o) {
			if(o == null) return false;
			if(!(o instanceof EnabledRadioKey)) return false;
			
			EnabledRadioKey other = (EnabledRadioKey)o;
			
			return (c.equals(other.c) &&
				back.equals(other.back) &&
				spread1 == other.spread1 &&
				spread2 == other.spread2);
		}
		
		public int hashCode() {
			return c.hashCode() * back.hashCode() * spread1 * spread2;
		}
	}
	
	/*
	 * DisabledRadioKey is used as key in the cache HashMap.
	 * Overrides equals() and hashCode().
	 * Used only if we are run from ControlPanel.
	 */
	private static class DisabledRadioKey {
		int spread1;
		int spread2;
		Color c, back;
		
		DisabledRadioKey(Color c, Color back) {
			spread1 = Theme.buttonSpreadLightDisabled.getValue();
			spread2 = Theme.buttonSpreadDarkDisabled.getValue();
			this.c = c;
			this.back = back;
		}
		
		public boolean equals(Object o) {
			if(o == null) return false;
			if(!(o instanceof DisabledRadioKey)) return false;
			
			DisabledRadioKey other = (DisabledRadioKey)o;
			
			return (c.equals(other.c) &&
				back.equals(other.back) &&
				spread1 == other.spread1 &&
				spread2 == other.spread2);
		}
		
		public int hashCode() {
			return c.hashCode() * back.hashCode() * spread1 * spread2;
		}
	}
	
	/*
	 * RadioKey is used as key in the cache HashMap.
	 * Overrides equals() and hashCode().
	 */
	private static class RadioKey {
		
		private Color c, background;
		private boolean pressed;
		private boolean enabled;
		private boolean rollover;
		private boolean focused;
		
		RadioKey(Color c, Color background,
			boolean pressed, boolean enabled, boolean rollover, boolean focused)
		{
			this.c = c;
			this.background = background;
			this.pressed = pressed;
			this.enabled = enabled;
			this.rollover = rollover;
			this.focused = focused;
		}
		
		public boolean equals(Object o) {
			if(o == null) return false;
			if(!(o instanceof RadioKey)) return false;

			RadioKey other = (RadioKey)o;
			
			return pressed == other.pressed &&
				enabled == other.enabled &&
				rollover == other.rollover &&
				focused == other.focused &&
				c.equals(other.c) &&
				background.equals(other.background);
		}
		
		public int hashCode() {
			return c.hashCode() *
				background.hashCode() *
				(pressed ? 1 : 2) *
				(enabled ? 4 : 8) *
				(rollover ? 16 : 32);
		}
	}
} 

