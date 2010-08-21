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
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.UIResource;

import de.muntjak.tinylookandfeel.*;
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * TinyButtonBorder is the border for JButton, JToggleButton and JSpinner buttons.
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyButtonBorder extends AbstractBorder implements UIResource {
	
	protected final Insets borderInsets = new Insets(2, 2, 2, 2);

	/**
	 * Draws the button border for the given component.
	 *
	 * @param c The component to draw its border.
	 * @param g The graphics context.
	 * @param x The x coordinate of the top left corner.
	 * @param y The y coordinate of the top left corner.
	 * @param w The width.
	 * @param h The height.
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		// new in 1.4.0 - if we are not inside the control panel,
		// components paint their own border, so it can be cached
		if(!TinyLookAndFeel.controlPanelInstantiated) return;
		
		AbstractButton b = (AbstractButton)c;
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
			boolean isSpinnerButton =
				Boolean.TRUE.equals(b.getClientProperty("isSpinnerButton"));
			boolean paintRollover =
				(isSpinnerButton && Theme.spinnerRollover.getValue()) ||
				(!isSpinnerButton && Theme.buttonRolloverBorder.getValue());
			
			if(isSpinnerButton) {
				// Because spinner buttons are small, we paint
				// a simple and fast border
				// New in 1.4.0: Instead of using button border colors
				// we use spinner border colors

				// paint background for edges
				g.setColor(TinySpinnerButtonUI.getSpinnerParent(b).getBackground());
				g.drawRect(0, 0, w - 1, h - 1);
				
				// left/top resp. left/bottom pixel should be painted
				// with spinner background
				g.setColor(TinySpinnerButtonUI.getSpinner(b).getBackground());

				if(Boolean.TRUE.equals(b.getClientProperty("isNextButton"))) {
					// left/bottom
					g.drawLine(0, h - 1, 0, h - 1);
				}
				else {
					// left/top
					g.drawLine(0, 0, 0, 0);
				}
				
				if(!b.isEnabled()) {
					g.setColor(Theme.spinnerBorderDisabledColor.getColor());
				}
				else {
					g.setColor(Theme.spinnerBorderColor.getColor());
				}

				g.drawLine(x + 1, y, x + w - 2, y);
				g.drawLine(x + 1, y + h - 1, x + w - 2, y + h - 1);
				g.drawLine(x, y + 1, x, y + h - 2);
				g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 2);
				
				if(b.getModel().isPressed()) return;
				
				if(b.getModel().isRollover() && paintRollover) {
					DrawRoutines.drawRolloverBorder(
						g, Theme.buttonRolloverColor.getColor(), x, y, w, h);
				}
			}
			else {	// it's a JButton or a JToggleButton
				boolean isDefault = (c instanceof JButton) && ((JButton)c).isDefaultButton();

				if(!b.isEnabled()) {
					DrawRoutines.drawRoundedBorder(
						g, Theme.buttonBorderDisabledColor.getColor(), x, y, w, h);
				}
				else {	
					DrawRoutines.drawRoundedBorder(
						g, Theme.buttonBorderColor.getColor(), x, y, w, h);
		
					if(b.getModel().isPressed()) return;
					
					if(b.getModel().isRollover() && paintRollover) {
						DrawRoutines.drawRolloverBorder(
							g, Theme.buttonRolloverColor.getColor(), x, y, w, h);
					}
					// New in 1.4.0: If isFocusPainted is false, no focus border
					// will be painted
					else if(isDefault ||
						(Theme.buttonFocusBorder.getValue() &&
						b.isFocusOwner() && b.isFocusPainted()))
					{
						DrawRoutines.drawRolloverBorder(
							g, Theme.buttonDefaultColor.getColor(), x, y, w, h);
					}
				}
			}
		}
	}

	/**
	 * Gets the border insets for a given component.
	 *
	 * @param c The component to get its border insets.
	 * @return Always returns the same insets as defined in <code>insets</code>.
	 */
	public Insets getBorderInsets(Component c) {
		return borderInsets;
	}
	
	public static class CompoundBorderUIResource extends CompoundBorder implements UIResource {
        public CompoundBorderUIResource(Border outsideBorder, Border insideBorder) {
            super(outsideBorder, insideBorder);
        }
    }
}