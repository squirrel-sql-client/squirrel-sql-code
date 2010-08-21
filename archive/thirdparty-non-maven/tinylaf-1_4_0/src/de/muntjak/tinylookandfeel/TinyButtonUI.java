/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.metal.MetalButtonUI;

import de.muntjak.tinylookandfeel.borders.TinyButtonBorder;
import de.muntjak.tinylookandfeel.borders.TinyToolButtonBorder;
import de.muntjak.tinylookandfeel.util.ColorRoutines;
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * TinyButtonUI. The UI delegate for JButton, JToggleButton, window buttons
 * and arrow buttons of JSpinner and JComboBox.
 *
 * @version 1.4.0
 * @author Hans Bickel
 */
public class TinyButtonUI extends MetalButtonUI {
	
	// cache for already drawn buttons - speeds up drawing by a factor of 3
	// (new in 1.4.0)
	private static final HashMap cache = new HashMap();

	// if a button has not the defined background, it will
	// be darkened resp. lightened by BG_CHANGE amount if
	// pressed or rollover
	public static final int BG_CHANGE_AMOUNT = 10;

	/**
	 * The Cached UI delegate.
	 */
	private static final TinyButtonUI buttonUI = new TinyButtonUI();

	/* the only instance of the stroke for the focus */
	private static final BasicStroke focusStroke =
		new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, new float[] { 1.0f, 1.0f }, 0.0f);

	private boolean graphicsTranslated;
	private boolean isToolBarButton, isFileChooserButton;
	private boolean isDefaultButton;

	public static void clearCache() {
    	cache.clear();
    }
	
	public void installUI(JComponent c) {
		super.installUI(c);
        
        if(!Theme.buttonEnter.getValue()) return;
        if(!c.isFocusable()) return;

        InputMap km = (InputMap)UIManager.get(getPropertyPrefix() + "focusInputMap");

        if(km != null) {
        	// replace SPACE with ENTER (but SPACE will still work, don't know why)
        	km.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
        	km.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
        }
    }

	public void installDefaults(AbstractButton button) {
		super.installDefaults(button);
		button.setRolloverEnabled(true);
	}

	protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
		if(isFileChooserButton ||
			(isToolBarButton && !Theme.toolFocus.getValue()) ||
			!Theme.buttonFocus.getValue())
		{
			return;
		}

		Graphics2D g2d = (Graphics2D)g;
		Rectangle focusRect = b.getBounds();

		g.setColor(Color.black);
		g2d.setStroke(focusStroke);

		int x1 = 2;
		int y1 = 2;
		int x2 = x1 + focusRect.width - 5;
		int y2 = y1 + focusRect.height - 5;

		if(!isToolBarButton) {
			x1++;
			y1++;
			x2--;
			y2--;
		}

		if(graphicsTranslated) {
			g.translate(-1, -1);
		}

		g2d.drawLine(x1, y1, x2, y1);
		g2d.drawLine(x1, y1, x1, y2);
		g2d.drawLine(x1, y2, x2, y2);
		g2d.drawLine(x2, y1, x2, y2);
	}

	/**
	 * Creates the UI delegate for the given component.
	 *
	 * @param mainColor The component to create its UI delegate.
	 * @return The UI delegate for the given component.
	 */
	public static ComponentUI createUI(final JComponent c) {
		return buttonUI;
	}

	protected void paintButtonPressed(Graphics g, AbstractButton button) {
		if(isToolBarButton || isFileChooserButton) return;

		Color col = null;
		
		if(!(button.getBackground() instanceof ColorUIResource)) {
			col = ColorRoutines.darken(button.getBackground(), BG_CHANGE_AMOUNT);
		}
		else if(button instanceof JToggleButton) {
			col = Theme.toggleSelectedBg.getColor();
		}
		else {
			col = Theme.buttonPressedColor.getColor();
		}

		g.setColor(col);

		drawXpButton(g, button, col, false);

		if(!(button instanceof JToggleButton)) {
			// Changed in 1.3.04: If button is icon-only then don't shift
			if(Theme.shiftButtonText.getValue() &&
				button.getText() != null &&
				!"".equals(button.getText()))
			{
				g.translate(1, 1);
				graphicsTranslated = true;
			}
		}
	}

	public void paintToolBarButton(Graphics g, AbstractButton b) {
		Color col = null;
		
		// New in 1.3.7
		boolean isRollover = b.getModel().isRollover() || b.getModel().isArmed();
		Color toolButtColor = null;
		
		if(isFileChooserButton) {
			toolButtColor = b.getParent().getBackground();
		}
		else {
			toolButtColor = Theme.toolButtColor.getColor();
		}

		if(b.getModel().isPressed()) {
			if(isRollover) {
				col = Theme.toolButtPressedColor.getColor();
			}
			else {
				if(b.isSelected()) {
					col = Theme.toolButtSelectedColor.getColor();
				}
				else {
					col = toolButtColor;
				}
			}
		}
		else if(isRollover) {
			if(b.isSelected()) {
				col = Theme.toolButtSelectedColor.getColor();
			}
			else {
				col = Theme.toolButtRolloverColor.getColor();
			}
		}
		else if(b.isSelected()) {
			col = Theme.toolButtSelectedColor.getColor();
		}
		else {
			col = toolButtColor;
		}

		g.setColor(col);

		drawXpToolBarButton(g, b, col, false);
	}

	public void paint(Graphics g, JComponent c) {
		AbstractButton button = (AbstractButton)c;

		if(isToolBarButton || isFileChooserButton) {
			paintToolBarButton(g, button);
		}
		else if((button instanceof JToggleButton) && button.isSelected()) {
			paintButtonPressed(g, button);
		}
		else {
			isDefaultButton = (c instanceof JButton) && (((JButton)c).isDefaultButton());
			boolean isRollover = button.getModel().isRollover();
			boolean isDefinedBackground = c.getBackground().equals(
				Theme.buttonNormalColor.getColor()) ||
				(c.getBackground() instanceof ColorUIResource);
			Color col = null;
	
			if(!button.isEnabled()) {		
				col = Theme.buttonDisabledColor.getColor();
			}
			else if(button.getModel().isPressed()) {
				if(isRollover) {
					if(isDefinedBackground) {
						col = Theme.buttonPressedColor.getColor();
					}
					else {
						col = ColorRoutines.darken(c.getBackground(), BG_CHANGE_AMOUNT);
					}
				}
				else {
					// button pressed but mouse exited
					col = c.getBackground();
				}
			}
			else if(isRollover) {
				if(isDefinedBackground) {
					col = Theme.buttonRolloverBgColor.getColor();
				}
				else {
					col = ColorRoutines.lighten(c.getBackground(), BG_CHANGE_AMOUNT);
				}
			}
			else {
				if(isDefinedBackground) {
					col = Theme.buttonNormalColor.getColor();
				}
				else {
					col = c.getBackground();
				}
			}
	
			g.setColor(col);
	
			if(TinyLookAndFeel.controlPanelInstantiated) {
				drawXpButtonNoCache(g, button, col, isRollover);
			}
			else {
				drawXpButton(g, button, col, isRollover);
			}
		}
		
		// the base class may paint text and/or icons
		super.paint(g, c);
	}

	// this overrides BasicButtonUI.paintIcon(...)
	protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
		if(c instanceof JToggleButton) {
			paintToggleButtonIcon(g, c, iconRect);
		}
		else {
			super.paintIcon(g, c, iconRect);
		}
	}

	protected void paintToggleButtonIcon(Graphics g, JComponent c, Rectangle iconRect) {
		AbstractButton b = (AbstractButton)c;
		ButtonModel model = b.getModel();
		Icon icon = null;

		if(!model.isEnabled()) {
			if(model.isSelected()) {
				icon = (Icon)b.getDisabledSelectedIcon();
			}
			else {
				icon = (Icon)b.getDisabledIcon();
			}
		}
		else if(model.isPressed() && model.isArmed()) {
			icon = (Icon)b.getPressedIcon();
			if(icon == null) {
				// Use selected icon
				icon = (Icon)b.getSelectedIcon();
			}
		}
		else if(model.isSelected()) {
			if(b.isRolloverEnabled() && model.isRollover()) {
				icon = (Icon)b.getRolloverSelectedIcon();
				if(icon == null) {
					icon = (Icon)b.getSelectedIcon();
				}
			}
			else {
				icon = (Icon)b.getSelectedIcon();
			}
		}
		else if(model.isRollover()) {
			icon = (Icon)b.getRolloverIcon();
		}

		if(icon == null) {
			icon = (Icon)b.getIcon();
		}

		icon.paintIcon(b, g, iconRect.x, iconRect.y);
	}

	public void update(Graphics g, JComponent c) {
		isToolBarButton = Boolean.TRUE.equals(
			c.getClientProperty(TinyToolBarUI.IS_TOOL_BAR_BUTTON_KEY));
		isFileChooserButton = Boolean.TRUE.equals(
			c.getClientProperty(TinyFileChooserUI.IS_FILE_CHOOSER_BUTTON_KEY));
		
		paint(g, c);
		
		graphicsTranslated = false;
	}

	private void drawXpButton(Graphics g, AbstractButton b, Color c, boolean isRollover) {
		if(!b.isContentAreaFilled()) return;
		if(!b.isOpaque()) return;

		int w = b.getWidth();
		int h = b.getHeight();
		Color bg = b.getParent().getBackground();

		// With 1.4.0 added hasFocus to the key (bug only revealed with JDK 1.6)
		// and added isBorderPainted to the key.
		ButtonKey key = new ButtonKey(c, bg, h,
			isRollover & Theme.buttonRolloverBorder.getValue(),
			isDefaultButton, b.hasFocus(), b.isBorderPainted());
		Object value = cache.get(key);

		if(value != null) {
			// image already cached - paint image and return
			int width = ((Image)value).getWidth(b);
			
			if(width == w) {
				g.drawImage((Image)value, 0, 0, b);
				return;
			}
			
			// left part
			int sx1 = 0;
			final int sy1 = 0;
			int sx2 = 3;
			final int sy2 = h;
			int dx1 = 0;
			final int dy1 = 0;
			int dx2 = sx2;
			final int dy2 = sy2;
			g.drawImage((Image)value, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, b);
			
			// right part
			sx1 = width - 4;
			sx2 = width;
			dx1 = w - 4;
			dx2 = w;
			g.drawImage((Image)value, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, b);
			
			// mid part - stretched
			sx1 = 3;
			sx2 = width - 4;
			dx1 = sx1;
			dx2 = w - 4;
			g.drawImage((Image)value, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, b);

			return;
		}
		
		Image img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics imgGraphics = img.getGraphics();

		imgGraphics.setColor(bg);
		imgGraphics.drawRect(0, 0, w - 1, h - 1);

		int spread1 = Theme.buttonSpreadLight.getValue();
		int spread2 = Theme.buttonSpreadDark.getValue();
		if(!b.isEnabled()) {
			spread1 = Theme.buttonSpreadLightDisabled.getValue();
			spread2 = Theme.buttonSpreadDarkDisabled.getValue();
		}

		float spreadStep1 = 10.0f * spread1 / (h - 3);
		float spreadStep2 = 10.0f * spread2 / (h - 3);
		int halfY = h / 2;
		int yd;

		for (int y = 1; y < h - 1; y++) {
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

			imgGraphics.drawLine(2, y, w - 3, y);

			if(y == 1) {
				// left vertical line
				imgGraphics.drawLine(1, 1, 1, h - 2);
				
				if(isRollover || isDefaultButton) {
					// right vertical line
					imgGraphics.drawLine(w - 2, 1, w - 2, h - 2);
				}
			}
			else if(y == h - 2 && !(isRollover || isDefaultButton)) {
				// right vertical line
				imgGraphics.drawLine(w - 2, 1, w - 2, h - 2);
			}
		}

		// paint border
		if(b.isBorderPainted()) {
			Border border = b.getBorder();
	
			// Changed in 1.4.0: Moved the following block into these two if-clauses
			// so additional pixels will be painted only if button has a Tiny border.
			if(border != null && (border instanceof TinyButtonBorder.CompoundBorderUIResource)) {
				// Draw 2 background pixels that will shine through the
				// border painted above
				if(isRollover && Theme.buttonRolloverBorder.getValue()) {
					imgGraphics.setColor(Theme.buttonRolloverColor.getColor());
					imgGraphics.drawLine(1, h - 2, 1, h - 2);
					imgGraphics.drawLine(w - 2, h - 2, w - 2, h - 2);
				}
				else if(isDefaultButton && b.isEnabled()) {
					imgGraphics.setColor(Theme.buttonDefaultColor.getColor());
					imgGraphics.drawLine(1, h - 2, 1, h - 2);
					imgGraphics.drawLine(w - 2, h - 2, w - 2, h - 2);
				}
				
				drawXpBorder(b, imgGraphics, 0, 0, w, h);
			}
		}
		
		// dispose of image graphics
		imgGraphics.dispose();
		
		// draw the image
		g.drawImage(img, 0, 0, b);
		
		// add the image to the cache
		cache.put(key, img);
		
		if(TinyLookAndFeel.PRINT_CACHE_SIZES) {
			System.out.println("TinyButtonUI.cache.size=" + cache.size());
		}
	}
	
	private void drawXpBorder(AbstractButton b, Graphics g, int x, int y, int w, int h) {
		boolean isComboBoxButton =
			Boolean.TRUE.equals(b.getClientProperty("isComboBoxButton"));

		if(isComboBoxButton) {
			if(!b.isEnabled()) {
				DrawRoutines.drawRoundedBorder(
					g, Theme.comboBorderDisabledColor.getColor(), x, y, w, h);
			}
			else {	
				DrawRoutines.drawRoundedBorder(
					g, Theme.comboBorderColor.getColor(), x, y, w, h);
	
				if(b.getModel().isPressed()) return;

				if(b.getModel().isRollover() && Theme.comboRollover.getValue()) {
					DrawRoutines.drawRolloverBorder(
						g, Theme.buttonRolloverColor.getColor(), x, y, w, h);
				}
			}
		}
		else {	// it's a JButton or a JSpinner button
			boolean isDefault = (b instanceof JButton) && (((JButton)b).isDefaultButton());
			boolean isSpinnerButton =
				Boolean.TRUE.equals(b.getClientProperty("isSpinnerButton"));
			boolean paintRolloverBorder =
				(isSpinnerButton && Theme.spinnerRollover.getValue()) ||
				(!isSpinnerButton && Theme.buttonRolloverBorder.getValue());
			// New in 1.4.0: If isFocusPainted is false, no focus border
			// will be painted
			boolean paintFocusBorder =
				isDefault ||
				(isSpinnerButton && Theme.buttonFocusBorder.getValue() && b.isFocusOwner()) ||
				(!isSpinnerButton && Theme.buttonFocusBorder.getValue() && b.isFocusOwner() && b.isFocusPainted());
			
			if(!b.isEnabled()) {
				DrawRoutines.drawRoundedBorder(
					g, Theme.buttonBorderDisabledColor.getColor(), x, y, w, h);
			}
			else {	
				DrawRoutines.drawRoundedBorder(
					g, Theme.buttonBorderColor.getColor(), x, y, w, h);
	
				if(b.getModel().isPressed()) return;
				
				if(b.getModel().isRollover() && paintRolloverBorder) {
					DrawRoutines.drawRolloverBorder(
						g, Theme.buttonRolloverColor.getColor(), x, y, w, h);
				}
				else if(paintFocusBorder) {
					DrawRoutines.drawRolloverBorder(
						g, Theme.buttonDefaultColor.getColor(), x, y, w, h);
				}
			}
		}
	}

	private void drawXpButtonNoCache(Graphics g, AbstractButton b, Color c, boolean isRollover) {
		if(!b.isContentAreaFilled()) return;
		if(!b.isOpaque()) return;

		int w = b.getWidth();
		int h = b.getHeight();
		
		// paint border background
		Color bg = b.getParent().getBackground();
		g.setColor(bg);
		g.drawRect(0, 0, w - 1, h - 1);

		int spread1 = Theme.buttonSpreadLight.getValue();
		int spread2 = Theme.buttonSpreadDark.getValue();
		if(!b.isEnabled()) {
			spread1 = Theme.buttonSpreadLightDisabled.getValue();
			spread2 = Theme.buttonSpreadDarkDisabled.getValue();
		}

		float spreadStep1 = 10.0f * spread1 / (h - 3);
		float spreadStep2 = 10.0f * spread2 / (h - 3);
		int halfY = h / 2;
		int yd;

		for (int y = 1; y < h - 1; y++) {
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

			g.drawLine(2, y, w - 3, y);

			if(y == 1) {
				// left vertical line
				g.drawLine(1, 1, 1, h - 2);
				
				if(isRollover || isDefaultButton) {
					// right vertical line
					g.drawLine(w - 2, 1, w - 2, h - 2);
				}
			}
			else if(y == h - 2 && !isRollover && !isDefaultButton) {
				// right vertical line
				g.drawLine(w - 2, 1, w - 2, h - 2);
			}
		}

		// Changed in 1.4.0: Added if-clause
		if(b.isBorderPainted()) {
			// Draw 2 background pixels that will shine through the
			// border painted above
			if(isRollover && Theme.buttonRolloverBorder.getValue()) {
				g.setColor(Theme.buttonRolloverColor.getColor());
				g.drawLine(1, h - 2, 1, h - 2);
				g.drawLine(w - 2, h - 2, w - 2, h - 2);
			}
			else if(isDefaultButton && b.isEnabled()) {
				g.setColor(Theme.buttonDefaultColor.getColor());
				g.drawLine(1, h - 2, 1, h - 2);
				g.drawLine(w - 2, h - 2, w - 2, h - 2);
			}
		}
	}

	private void drawXpToolBarButton(Graphics g,
		AbstractButton b, Color c, boolean isPressed)
	{
		int w = b.getWidth();
		int h = b.getHeight();

		if(b.isContentAreaFilled()) {
			g.fillRect(1, 1, w - 2, h - 2);
		}
		
		// paint border background
		Color bg = b.getParent().getBackground();

		// Note: Was bug before 1.4.0 - (isFileChooserButton not considered)
		if((bg instanceof ColorUIResource) && !isFileChooserButton) {
			g.setColor(Theme.toolBarColor.getColor());
		}
		else {
			g.setColor(bg);
		}
		
		g.drawRect(0, 0, w - 1, h - 1);
	}
	
	private static class ButtonKey {
		
		private Color background;
		private Color parentBackground;
		private int height;
		private boolean rollover;
		private boolean isDefault;
		private boolean hasFocus;
		private boolean isBorderPainted;
		
		ButtonKey(Color background, Color parentBackground,
			int height, boolean rollover, boolean isDefault,
			boolean hasFocus, boolean isBorderPainted)
		{
			this.background = background;
			this.parentBackground = parentBackground;
			this.height = height;
			this.rollover = rollover;
			this.isDefault = isDefault;
			this.hasFocus = hasFocus;
			this.isBorderPainted = isBorderPainted;
		}
		
		public boolean equals(Object o) {
			if(o == null) return false;
			if(!(o instanceof ButtonKey)) return false;

			ButtonKey other = (ButtonKey)o;
			
			return
				height == other.height &&
				rollover == other.rollover &&
				isDefault == other.isDefault &&
				hasFocus == other.hasFocus &&
				isBorderPainted == other.isBorderPainted &&
				background.equals(other.background) &&
				parentBackground.equals(other.parentBackground);
		}
		
		public int hashCode() {
			return background.hashCode() *
				parentBackground.hashCode() *
				height *
				(rollover ? 2 : 1) *
				(isDefault ? 8 : 4) *
				(hasFocus ? 16 : 8) *
				(isBorderPainted ? 32 : 16);
		}
	}
}