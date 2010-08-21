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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.Border;

import de.muntjak.tinylookandfeel.borders.TinyButtonBorder;
import de.muntjak.tinylookandfeel.controlpanel.*;
import de.muntjak.tinylookandfeel.util.ColorRoutines;
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * TinyComboBoxButton
 * 
 * @version 1.4.0
 * @author Hans Bickel
 */
public class TinyComboBoxButton extends JButton {
	
	// cache for already drawn buttons - speeds up drawing by a factor of 3
	// (new in 1.4.0)
	private static final HashMap cache = new HashMap();
	
	protected JComboBox comboBox;
	protected JList listBox;
	protected CellRendererPane rendererPane;
	protected Icon comboIcon;
	protected boolean iconOnly = false;
	private static BufferedImage focusImg;
	
	public static void clearCache() {
    	cache.clear();
    }
	
	public final JComboBox getComboBox() {
		return comboBox;
	}
	
	public final void setComboBox(JComboBox cb) {
		comboBox = cb;
	}
	
	public final Icon getComboIcon() {
		return comboIcon;
	}
	
	public final void setComboIcon(Icon i) {
		comboIcon = i;
	}
	
	public final boolean isIconOnly() {
		return iconOnly;
	}
	
	public final void setIconOnly(boolean isIconOnly) {
		iconOnly = isIconOnly;
	}
	
	TinyComboBoxButton() {
		super("");
		
		DefaultButtonModel model = new DefaultButtonModel() {
			public void setArmed(boolean armed) {
				super.setArmed(isPressed() ? true : armed);
			}
		};
		
		setModel(model);
		
		// Set the background and foreground to the combobox colors.
		setBackground(UIManager.getColor("ComboBox.background"));
		setForeground(UIManager.getColor("ComboBox.foreground"));
		
		if(focusImg == null) {
			ImageIcon icon = TinyLookAndFeel.loadIcon("ComboBoxFocus.png");
			
			if(icon != null) {
				focusImg = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
				Graphics g = focusImg.getGraphics();
				icon.paintIcon(this, g, 0, 0);
			}
		}
	}
	
	public TinyComboBoxButton(JComboBox cb, Icon i,
		boolean onlyIcon, CellRendererPane pane, JList list)
	{
		this();
		comboBox = cb;
		comboIcon = i;
		rendererPane = pane;
		listBox = list;
		setEnabled(comboBox.isEnabled());
	}
	
	/**
	 * Mostly taken from the swing sources
	 * @see javax.swing.JComponent#paintComponent(Graphics)
	 */
	public void paintComponent(Graphics g) {
		Color panelBackground = getParent().getParent().getBackground();
		// With non-editable combo box we paint the whole combo,
		// with editable combo box we paint the arrow button only
		int h = getHeight();
		int w = getWidth();
		ButtonKey key = null;
		Image img = null;
		Graphics graphics = g;
		boolean cached = false;
		
		if(!TinyLookAndFeel.controlPanelInstantiated) {
			key = new ButtonKey(
				panelBackground,
				getSize(),
				comboBox.isEnabled(),
				comboBox.isEditable(),
				model.isPressed(),
				model.isRollover());
			Object value = cache.get(key);
			
			if(value != null) {
				// image already cached - paint image
				g.drawImage((Image)value, 0, 0, this);
				// Note: We can't return because the selected
				// value is not yet painted (non-editable combo
				// box only)
				if(comboBox.isEditable()) return;
				
				cached = true;
			}
			else {
				img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				graphics = img.getGraphics();
			}
		}
		
		boolean leftToRight = getComponentOrientation().isLeftToRight();
		
		if(!cached) {
//			System.out.println("Paint to image");
			if(comboBox.isEnabled()) {
				if(comboBox.isEditable()) {
					graphics.setColor(Theme.textBgColor.getColor());
				}
				else {
					graphics.setColor(comboBox.getBackground());
				}
			}
			else {
				graphics.setColor(Theme.textDisabledBgColor.getColor());
			}
			
			graphics.fillRect(1, 1, w - 2, h - 2);
			
			// paint border background - next parent is combo box
			graphics.setColor(panelBackground);
			graphics.drawRect(0, 0, w - 1, h - 1);
			
			Color color = null;	
			
			if(!isEnabled()) {
				color = Theme.comboButtDisabledColor.getColor();
			}
			else if(model.isPressed()) {
				color = Theme.comboButtPressedColor.getColor();
			}
			else if(model.isRollover()) {
				color = Theme.comboButtRolloverColor.getColor();
			}
			else {
				color = Theme.comboButtColor.getColor();
			}
			
			graphics.setColor(color);
			
			Rectangle buttonRect = new Rectangle(
				w - TinyComboBoxUI.COMBO_BUTTON_WIDTH,
				1, TinyComboBoxUI.COMBO_BUTTON_WIDTH, h - 2);
			
			drawXpButton(graphics, buttonRect, color);
			
			// draw border
			Border border = getBorder();
			
			if(border != null && (border instanceof TinyButtonBorder.CompoundBorderUIResource)) {
				if(!isEnabled()) {
					DrawRoutines.drawRoundedBorder(
						graphics, Theme.comboBorderDisabledColor.getColor(), 0, 0, w, h);
				}
				else {	
					DrawRoutines.drawRoundedBorder(
						graphics, Theme.comboBorderColor.getColor(), 0, 0, w, h);
					
					if(!getModel().isPressed() && getModel().isRollover() &&
						Theme.comboRollover.getValue())
					{
						DrawRoutines.drawRolloverBorder(
							graphics, Theme.buttonRolloverColor.getColor(), 0, 0, w, h);
					}
				}
			}
			
			if(key != null) {
				// dispose of image graphics
				graphics.dispose();
				
				// draw the image
				g.drawImage(img, 0, 0, this);
				
				// add the image to the cache
				cache.put(key, img);
				
				if(TinyLookAndFeel.PRINT_CACHE_SIZES) {
					System.out.println("TinyComboBoxButton.cache.size=" + cache.size());
				}
			}
		}
		
		// paint the selected value
		Insets insets = new Insets(
			Theme.comboInsets.top,
			Theme.comboInsets.left,
			Theme.comboInsets.bottom,
			0);
		
		int width = w - (insets.left + insets.right);
		int widthFocus = width;
		int height = h - (insets.top + insets.bottom);
		
		if(height <= 0 || width <= 0) {
			return;
		}
		
		int left = insets.left;
		int top = insets.top;
		int right = left + (width - 1);
		int bottom = top + (height - 1);
		
		int iconWidth = TinyComboBoxUI.COMBO_BUTTON_WIDTH;
		int iconLeft = (leftToRight) ? right : left;
		
		// Let the renderer paint
		Component c = null;
		boolean mustResetOpaque = false;
		boolean savedOpaque = false;
		boolean paintFocus = false;
		
		if(!iconOnly && comboBox != null) {
			ListCellRenderer renderer = comboBox.getRenderer();
			boolean rendererSelected = getModel().isPressed();
			c = renderer.getListCellRendererComponent(listBox,
				comboBox.getSelectedItem(), -1, rendererSelected, false);
			c.setFont(rendererPane.getFont());
			
			if(model.isArmed() && model.isPressed()) {
				if(isOpaque()) {
					// defaults to ColorUIResource[r=167,g=165,b=163]
					c.setBackground(UIManager.getColor("Button.select"));
				}
				
				c.setForeground(comboBox.getForeground());
			}
			else if(!comboBox.isEnabled()) {
				if(isOpaque()) {
					c.setBackground(Theme.textDisabledBgColor.getColor());
				}
				else {
					comboBox.setBackground(Theme.textDisabledBgColor.getColor());
				}
				
				c.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
			}
			else if(comboBox.hasFocus() && !comboBox.isPopupVisible()) {
				if(comboBox.isEditable()) {
					c.setForeground(Theme.mainColor.getColor());
				}
				else {
					c.setForeground(UIManager.getColor("ComboBox.selectionForeground"));
				}
				
				c.setBackground(UIManager.getColor("ComboBox.focusBackground"));
				
				if(c instanceof JComponent) {
					mustResetOpaque = true;
					JComponent jc = (JComponent) c;
					savedOpaque = jc.isOpaque();
					jc.setOpaque(true);
					paintFocus = true;
				}
			}
			else {
				c.setForeground(comboBox.getForeground());
				c.setBackground(comboBox.getBackground());
			}
			
			int cWidth = width - (insets.right + iconWidth);
			
			// Fix for 4238829: should lay out the JPanel.
			boolean shouldValidate = (c instanceof JPanel);
			
			if(leftToRight) {
				rendererPane.paintComponent(g, c, this, left, top, cWidth, height, shouldValidate);
			}
			else {
				rendererPane.paintComponent(g, c, this, left + iconWidth, top, cWidth, height, shouldValidate);
			}
			
			if(paintFocus && Theme.comboFocus.getValue()) {
				g.setColor(Color.black);
				Graphics2D g2d = (Graphics2D) g;
				Rectangle r = new Rectangle(left, top, 2, 2);
				TexturePaint tp = new TexturePaint(focusImg, r);        
				
				g2d.setPaint(tp);
				g2d.draw(new Rectangle(left,top,cWidth, height));
			}
		}
		
		if(mustResetOpaque) {
			JComponent jc = (JComponent) c;
			jc.setOpaque(savedOpaque);
		}
	}

	private void drawXpButton(Graphics g, Rectangle buttonRect, Color c) {
		int x2 = buttonRect.x + buttonRect.width;
		int y2 = buttonRect.y + buttonRect.height;
		
		int spread1 = Theme.comboSpreadLight.getValue();
		int spread2 = Theme.comboSpreadDark.getValue();
		if(!isEnabled()) {
			spread1 = Theme.comboSpreadLightDisabled.getValue();
			spread2 = Theme.comboSpreadDarkDisabled.getValue();
		}
		
		int h = buttonRect.height - 2;
		float spreadStep1 = 10.0f * spread1 / (h - 3);
		float spreadStep2 = 10.0f * spread2 / (h - 3);
		int halfY = h / 2;
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
			
			g.drawLine(buttonRect.x + 1, buttonRect.y + y + 1, buttonRect.x + buttonRect.width - 3, buttonRect.y + y + 1);
		}
		
		// draw the button border
		Color col = null;	
		if(!isEnabled()) {
			col = Theme.comboButtBorderDisabledColor.getColor();
		}
		else {
			col = Theme.comboButtBorderColor.getColor();
		}
		g.setColor(col);
		g.drawLine(buttonRect.x + 2, buttonRect.y + 1, x2 - 4, buttonRect.y + 1);
		g.drawLine(buttonRect.x + 1, buttonRect.y + 2, buttonRect.x + 1, y2 - 3);
		g.drawLine(x2 - 3, buttonRect.y + 2, x2 - 3, y2 - 3);
		g.drawLine(buttonRect.x + 2, y2 - 2, x2 - 4, y2 - 2);
		
		// ecken
		col = new Color(col.getRed(), col.getGreen(), col.getBlue(), 128);
		g.setColor(col);
		g.drawLine(buttonRect.x + 1, buttonRect.y + 1, buttonRect.x + 1, buttonRect.y + 1);
		g.drawLine(x2 - 3, buttonRect.y + 1, x2 - 3, buttonRect.y + 1);
		g.drawLine(buttonRect.x + 1, y2 - 2, buttonRect.x + 1, y2 - 2);
		g.drawLine(x2 - 3, y2 - 2, x2 - 3, y2 - 2);
		
		// draw arrow
		if(isEnabled()) {
			g.setColor(Theme.comboArrowColor.getColor());
		}
		else {
			g.setColor(Theme.comboArrowDisabledColor.getColor());
		}
		
		drawXpArrow(g, buttonRect);
	}

	private void drawXpArrow(Graphics g, Rectangle r) {
		int x = r.x + (r.width - 8) / 2 - 1;
		int y = r.y + (r.height - 6) / 2 + 1;
		
		g.drawLine(x + 1, y, x + 1, y);
		g.drawLine(x + 7, y, x + 7, y);
		g.drawLine(x, y + 1, x + 2, y + 1);
		g.drawLine(x + 6, y + 1, x + 8, y + 1);
		g.drawLine(x + 1, y + 2, x + 3, y + 2);
		g.drawLine(x + 5, y + 2, x + 7, y + 2);
		g.drawLine(x + 2, y + 3, x + 6, y + 3);
		g.drawLine(x + 3, y + 4, x + 5, y + 4);
		g.drawLine(x + 4, y + 5, x + 4, y + 5);
	}
	
	private static class ButtonKey {
		
		private Color panelBackground;
		private Dimension size;
		private boolean enabled;
		private boolean editable;
		private boolean pressed;
		private boolean rollover;
		
		ButtonKey(Color background, Dimension size,
			boolean enabled, boolean editable, boolean pressed, boolean rollover)
			{
			this.panelBackground = background;
			this.size = size;
			this.enabled = enabled;
			this.editable = editable;
			this.pressed = pressed;
			this.rollover = rollover;
			}
		
		public boolean equals(Object o) {
			if(o == null) return false;
			if(!(o instanceof ButtonKey)) return false;
			
			ButtonKey other = (ButtonKey)o;
			
			return
			enabled == other.enabled &&
			editable == other.editable &&
			pressed == other.pressed &&
			rollover == other.rollover &&
			panelBackground.equals(other.panelBackground) &&
			size.equals(other.size);
		}
		
		public int hashCode() {
			return panelBackground.hashCode() *
			size.hashCode() *
			(enabled ? 2 : 1) *
			(editable ? 8 : 4) *
			(pressed ? 32 : 16) *
			(rollover ? 128 : 64);
		}
	}
}
