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

import javax.swing.*;

import javax.swing.border.AbstractBorder;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.UIResource;

import de.muntjak.tinylookandfeel.*;
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * TinyToolButtonBorder is the border for JButton, JToggleButton and JSpinner buttons.
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyToolButtonBorder extends AbstractBorder {
	
	protected static final Insets insets = new Insets(1, 1, 1, 1);

	/**
	 * Draws the button border for the given component.
	 *
	 * @param mainColor The component to draw its border.
	 * @param g The graphics context.
	 * @param x The x coordinate of the top left corner.
	 * @param y The y coordinate of the top left corner.
	 * @param w The width.
	 * @param h The height.
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		drawXpBorder(c, g, x, y, w, h);
	}

	private void drawXpBorder(Component c, Graphics g, int x, int y, int w, int h) {
		AbstractButton b = (AbstractButton)c;
		Color col = null;
		boolean isFileChooserButton = Boolean.TRUE.equals(
			b.getClientProperty(TinyFileChooserUI.IS_FILE_CHOOSER_BUTTON_KEY));

		// New in 1.3.7 (previously only b.getModel().isRollover() evaluated)
		boolean isRollover =  b.getModel().isRollover() || b.getModel().isArmed();
		
		if(b.getModel().isPressed()) {
			if(isRollover) {
				col = Theme.toolBorderPressedColor.getColor();
			}
			else {
				if(b.isSelected()) {
					col = Theme.toolBorderSelectedColor.getColor();
				}
				else {
					if(isFileChooserButton) return;	// no border painted
					
					col = Theme.toolBorderColor.getColor();
				}
			}
		}
		else if(isRollover) {
			if(b.isSelected()) {
				col = Theme.toolBorderSelectedColor.getColor();
			}
			else {
				col = Theme.toolBorderRolloverColor.getColor();
			}
		}
		else if(b.isSelected()) {
			col = Theme.toolBorderSelectedColor.getColor();
		}
		else {
			if(isFileChooserButton) return;	// no border painted
			
			col = Theme.toolBorderColor.getColor();
		}

		DrawRoutines.drawRoundedBorder(g, col, x, y, w, h);
	}

	/**
	 * Gets the border insets for a given component.
	 * 
	 * @return some insets...
	 */
	public Insets getBorderInsets(Component c) {
		if(!(c instanceof AbstractButton)) return insets;
		
		AbstractButton b = (AbstractButton)c;
		
		if(b.getMargin() == null || (b.getMargin() instanceof UIResource)) {
			return Theme.toolMargin;
		}
		else {
			Insets margin = b.getMargin();
			
			return new Insets(
				margin.top + 1,
				margin.left + 1,
				margin.bottom + 1,
				margin.right + 1);
		}
	}
}