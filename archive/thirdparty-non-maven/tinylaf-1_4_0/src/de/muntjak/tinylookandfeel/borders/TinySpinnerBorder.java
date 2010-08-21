/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.borders;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * TinySpinnerBorder
 * @author Hans Bickel
 *
 */
public class TinySpinnerBorder extends AbstractBorder implements UIResource {

    private static final Insets insets = new Insets(2, 2, 2, 2);

	/**
	 * Gets the border insets for a given component.
	 *
	 * @param mainColor The component to get its border insets.
	 * @return Always returns the same insets as defined in <code>insets</code>.
	 */
	public Insets getBorderInsets(Component c) {
		return insets;
	}

	/**
	 * Use the skin to paint the border
	 * @see javax.swing.border.Border#paintBorder(Component, Graphics, int, int, int, int)
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		drawXpBorder(c, g, x, y, w, h);
	}

	private void drawXpBorder(Component c, Graphics g, int x, int y, int w, int h) {
		if(!c.isEnabled()) {
			DrawRoutines.drawBorder(
				g, Theme.spinnerBorderDisabledColor.getColor(), x, y, w, h);
		}
		else {
			DrawRoutines.drawBorder(
				g, Theme.spinnerBorderColor.getColor(), x, y, w, h);
		}
	}
}
