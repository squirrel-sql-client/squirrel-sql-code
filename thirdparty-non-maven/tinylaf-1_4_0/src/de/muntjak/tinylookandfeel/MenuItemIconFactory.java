/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashMap;

/**
 * MenuItemIconFactory
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class MenuItemIconFactory implements Serializable {

	// cache for already drawn radio icons
	private static final HashMap cache = new HashMap();
	
	private static final Dimension CHECK_ICON_SIZE = new Dimension(10, 10);
	private static final Dimension ARROW_ICON_SIZE = new Dimension(4, 8);
	
	// All kinds of system menu icons we provide
	private static final int SYSTEM_CLOSE_ICON 		= 1;
	private static final int SYSTEM_ICONIFY_ICON 	= 2;
	private static final int SYSTEM_MAXIMIZE_ICON 	= 3;
	private static final int SYSTEM_RESTORE_ICON 	= 4;
	
	private static Icon checkBoxMenuItemIcon;
	private static Icon radioButtonMenuItemIcon;
	private static Icon menuArrowIcon;
	
	private static Icon systemCloseIcon;
	private static Icon systemIconifyIcon;
	private static Icon systemMaximizeIcon;
	private static Icon systemRestoreIcon;

	public static Icon getCheckBoxMenuItemIcon() {
		if(checkBoxMenuItemIcon == null) {
			checkBoxMenuItemIcon = new CheckBoxMenuItemIcon();
		}

		return checkBoxMenuItemIcon;
	}

	public static Icon getRadioButtonMenuItemIcon() {
		if(radioButtonMenuItemIcon == null) {
			radioButtonMenuItemIcon = new RadioButtonMenuItemIcon();
		}

		return radioButtonMenuItemIcon;
	}

	public static Icon getMenuArrowIcon() {
		if(menuArrowIcon == null) {
			menuArrowIcon = new MenuArrowIcon();
		}

		return menuArrowIcon;
	}
	
	public static Icon getSystemCloseIcon() {
		if(systemCloseIcon == null) {
			systemCloseIcon = new SystemMenuIcon(SYSTEM_CLOSE_ICON);
		}

		return systemCloseIcon;
	}
	
	public static Icon getSystemIconifyIcon() {
		if(systemIconifyIcon == null) {
			systemIconifyIcon = new SystemMenuIcon(SYSTEM_ICONIFY_ICON);
		}

		return systemIconifyIcon;
	}
	
	public static Icon getSystemMaximizeIcon() {
		if(systemMaximizeIcon == null) {
			systemMaximizeIcon = new SystemMenuIcon(SYSTEM_MAXIMIZE_ICON);
		}

		return systemMaximizeIcon;
	}
	
	public static Icon getSystemRestoreIcon() {
		if(systemRestoreIcon == null) {
			systemRestoreIcon = new SystemMenuIcon(SYSTEM_RESTORE_ICON);
		}

		return systemRestoreIcon;
	}
	
	public static void clearCache() {
    	cache.clear();
    }

	private static class CheckBoxMenuItemIcon implements Icon, UIResource, Serializable {

		public void paintIcon(Component c, Graphics g, int x, int y) {
			JMenuItem item = (JMenuItem)c;
			ButtonModel model = item.getModel();

			boolean isSelected = model.isSelected();
			
			if(!isSelected) return;

			boolean isEnabled = model.isEnabled();
			boolean isPressed = model.isPressed();
			boolean isArmed = model.isArmed();

			g.translate(x, y);

			if(isEnabled) {
				if(model.isArmed() || (c instanceof JMenu && model.isSelected())) {
					// rollover
					g.setColor(Theme.menuIconRolloverColor.getColor());
				}
				else {
					// !rollover
					g.setColor(Theme.menuIconColor.getColor());
				}
			}
			else {
				// disabled
				g.setColor(Theme.menuIconDisabledColor.getColor());
			}

			// paint arrow
			g.drawLine(2, 4, 2, 6);
			g.drawLine(3, 5, 3, 7);
			g.drawLine(4, 6, 4, 8);
			g.drawLine(5, 5, 5, 7);
			g.drawLine(6, 4, 6, 6);
			g.drawLine(7, 3, 7, 5);
			g.drawLine(8, 2, 8, 4);

			g.translate(-x, -y);
		}

		public int getIconWidth() {
			return CHECK_ICON_SIZE.width;
		}

		public int getIconHeight() {
			return CHECK_ICON_SIZE.height;
		}
	}

	private static class RadioButtonMenuItemIcon implements Icon, UIResource, Serializable {
		
		private static final int[][] ALPHA_BORDER = {
			{255, 255, 163, 84, 25, 25, 84, 163, 255, 255},
			{255, 127, 92, 171, 230, 230, 171, 92, 127, 255},
			{163, 92, 255, 255, 255, 255, 255, 255, 92, 163},
			{84, 171, 255, 255, 255, 255, 255, 255, 171, 84},
			{25, 230, 255, 255, 255, 255, 255, 255, 230, 25},
			{25, 230, 255, 255, 255, 255, 255, 255, 230, 25},
			{84, 171, 255, 255, 255, 255, 255, 255, 171, 84},
			{163, 92, 255, 255, 255, 255, 255, 255, 92, 163},
			{255, 127, 92, 171, 230, 230, 171, 92, 127, 255},
			{255, 255, 163, 84, 25, 25, 84, 163, 255, 255}
		};
		
		private static final int[][] ALPHA_CHECK = {
			{255, 255, 163, 84, 25, 25, 84, 163, 255, 255},
			{255, 127, 92, 171, 230, 230, 171, 92, 127, 255},
			{163, 92, 255, 255, 255, 255, 255, 255, 92, 163},
			{84, 171, 255, 170, 63, 63, 170, 255, 171, 84},
			{25, 230, 255, 63, 0, 0, 63, 255, 230, 25},
			{25, 230, 255, 63, 0, 0, 63, 255, 230, 25},
			{84, 171, 255, 170, 63, 63, 170, 255, 171, 84},
			{163, 92, 255, 255, 255, 255, 255, 255, 92, 163},
			{255, 127, 92, 171, 230, 230, 171, 92, 127, 255},
			{255, 255, 163, 84, 25, 25, 84, 163, 255, 255}
		};

		public void paintIcon(Component c, Graphics g, int x, int y) {
			JMenuItem b = (JMenuItem)c;
			ButtonModel model = b.getModel();

			boolean isSelected = model.isSelected();
			boolean isEnabled = model.isEnabled();
			boolean isPressed = model.isPressed();
			boolean isArmed = model.isArmed();
			Color bg = b.getBackground();
			Color col = null;

			g.translate(x, y);

			if(isEnabled) {
				if(isPressed || isArmed) {
					// rollover
					col = Theme.menuIconRolloverColor.getColor();
				}
				else {
					// !rollover
					col = Theme.menuIconColor.getColor();
				}
			}
			else {
				// disabled
				col = Theme.menuIconDisabledColor.getColor();
			}
			
			if(TinyLookAndFeel.controlPanelInstantiated) {
				paintIconNoCache(g, col, isSelected);
			}
			else {
				paintIcon(g, col, b, isSelected);
			}

			g.translate(-x, -y);
		}
		
		private void paintIcon(Graphics g, Color c, JMenuItem b, boolean selected) {
			Color bg = b.getBackground();
			
			if(b.getModel().isArmed()) {
				// Item rollover
				bg = Theme.menuItemRolloverColor.getColor();
			}
			else {
				// Item normal
				if(bg instanceof ColorUIResource) {
					bg = Theme.menuPopupColor.getColor();
				}
			}

			RadioKey key = new RadioKey(c, bg, selected);
			Object value = cache.get(key);

			if(value != null) {
				// image already cached - paint image and return
				g.drawImage((Image)value, 0, 0, b);
				return;
			}
			
			Image img = new BufferedImage(CHECK_ICON_SIZE.width, CHECK_ICON_SIZE.height, BufferedImage.TYPE_INT_ARGB);
			Graphics imgGraphics = img.getGraphics();
			
			imgGraphics.setColor(bg);
			imgGraphics.fillRect(0, 0, CHECK_ICON_SIZE.width, CHECK_ICON_SIZE.height);
			
			Color color;
			
			for(int row = 0; row < 10; row++) {
				for(int col = 0; col < 10; col++) {
					if(ALPHA_BORDER[col][row] == 255) continue;
					
					color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255 - ALPHA_BORDER[col][row]);
					imgGraphics.setColor(color);
					imgGraphics.drawLine(col, row, col, row);
				}
			}

			if(selected) {
				for(int row = 3; row < 7; row++) {
					for(int col = 3; col < 7; col++) {
						color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255 - ALPHA_CHECK[col][row]);
						imgGraphics.setColor(color);
						imgGraphics.drawLine(col, row, col, row);
					}
				}
			}
			
			// dispose of image graphics
			imgGraphics.dispose();
			
			// draw the image
			g.drawImage(img, 0, 0, b);
			
			// add the image to the cache
			cache.put(key, img);
			
			if(TinyLookAndFeel.PRINT_CACHE_SIZES) {
				System.out.println("MenuItemIconFactory.cache.size=" + cache.size());
			}
		}
		
		private void paintIconNoCache(Graphics g, Color c, boolean selected) {
			Color color;
			
			for(int row = 0; row < 10; row++) {
				for(int col = 0; col < 10; col++) {
					if(ALPHA_BORDER[col][row] == 255) continue;
					
					color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255 - ALPHA_BORDER[col][row]);
					g.setColor(color);
					g.drawLine(col, row, col, row);
				}
			}

			if(selected) {
				for(int row = 3; row < 7; row++) {
					for(int col = 3; col < 7; col++) {
						color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255 - ALPHA_CHECK[col][row]);
						g.setColor(color);
						g.drawLine(col, row, col, row);
					}
				}
			}
		}

		public int getIconWidth() {
			return CHECK_ICON_SIZE.width;
		}

		public int getIconHeight() {
			return CHECK_ICON_SIZE.height;
		}
		
		class RadioKey {
			
			private Color c, background;
			private boolean selected;
			
			RadioKey(Color c, Color background, boolean selected) {
				this.c = c;
				this.background = background;
				this.selected = selected;
			}
			
			public boolean equals(Object o) {
				if(o == null) return false;
				if(!(o instanceof RadioKey)) return false;

				RadioKey other = (RadioKey)o;
				
				return selected == other.selected &&
					c.equals(other.c) &&
					background.equals(other.background);
			}
			
			public int hashCode() {
				return c.hashCode() *
					background.hashCode() *
					(selected ? 1 : 2);
			}
		}
	}

	private static class MenuArrowIcon implements Icon, UIResource, Serializable {
		
		public void paintIcon(Component c, Graphics g, int x, int y) {
			JMenuItem b = (JMenuItem)c;
			ButtonModel model = b.getModel();

			g.translate(x, y);

			if(!model.isEnabled()) {
				g.setColor(Theme.menuItemDisabledFgColor.getColor());
			}
			else {
				if(model.isArmed() || (c instanceof JMenu && model.isSelected())) {
					g.setColor(Theme.menuItemSelectedTextColor.getColor());
				}
				else {
					g.setColor(b.getForeground());
				}
			}
			
			if(c.getComponentOrientation().isLeftToRight()) {
				g.drawLine(0, 0, 0, 7);
				g.drawLine(1, 1, 1, 6);
				g.drawLine(2, 2, 2, 5);
				g.drawLine(3, 3, 3, 4);
			}
			else {
				g.drawLine(4, 0, 4, 7);
				g.drawLine(3, 1, 3, 6);
				g.drawLine(2, 2, 2, 5);
				g.drawLine(1, 3, 1, 4);
			}

			g.translate(-x, -y);
		}

		public int getIconWidth() {
			return ARROW_ICON_SIZE.width;
		}

		public int getIconHeight() {
			return ARROW_ICON_SIZE.height;
		}
	}

	private static class SystemMenuIcon implements Icon, UIResource, Serializable {
		
		private int style;
		
		SystemMenuIcon(int style) {
			this.style = style;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			if(c instanceof JMenuItem) {
				JMenuItem item = (JMenuItem)c;
				
				if(!item.isEnabled()) {
					g.setColor(Theme.menuIconDisabledColor.getColor());
				}
				else if(item.isArmed()) {
					g.setColor(Theme.menuIconRolloverColor.getColor());
				}
				else {
					g.setColor(Theme.menuIconColor.getColor());
				}
				
				if(style == SYSTEM_CLOSE_ICON) {
					g.drawLine(x + 1, y + 1, x + 2, y + 1);
					g.drawLine(x + 8, y + 1, x + 9, y + 1);
					g.drawLine(x + 1, y + 2, x + 3, y + 2);
					g.drawLine(x + 7, y + 2, x + 9, y + 2);
					g.drawLine(x + 2, y + 3, x + 4, y + 3);
					g.drawLine(x + 6, y + 3, x + 8, y + 3);
					g.drawLine(x + 3, y + 4, x + 7, y + 4);
					g.drawLine(x + 4, y + 5, x + 6, y + 5);
					g.drawLine(x + 3, y + 6, x + 7, y + 6);
					g.drawLine(x + 2, y + 7, x + 4, y + 7);
					g.drawLine(x + 6, y + 7, x + 8, y + 7);
					g.drawLine(x + 1, y + 8, x + 3, y + 8);
					g.drawLine(x + 7, y + 8, x + 9, y + 8);
					g.drawLine(x + 1, y + 9, x + 2, y + 9);
					g.drawLine(x + 8, y + 9, x + 9, y + 9);
				}
				else if(style == SYSTEM_ICONIFY_ICON) {
					g.fillRect(x + 1, x + 8, 7, 2);
				}
				else if(style == SYSTEM_MAXIMIZE_ICON) {
					g.drawLine(x + 0, y + 0, x + 9, y + 0);
					g.drawRect(x + 0, x + 1, 9, 8);
				}
				else if(style == SYSTEM_RESTORE_ICON) {
					g.fillRect(x + 2, x + 1, 8, 2);
					g.drawLine(x + 9, y + 3, x + 9, y + 6);
					g.drawLine(x + 8, y + 6, x + 8, y + 6);
					g.drawLine(x + 2, y + 3, x + 2, y + 3);
					
					g.drawLine(x + 0, y + 4, x + 7, y + 4);
					g.drawRect(x + 0, x + 5, 7, 4);
				}
			}
		}

		public int getIconWidth() {
			return 10;
		}

		public int getIconHeight() {
			return 10;
		}
	}
}
