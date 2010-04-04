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
 * TinyTextFieldBorder
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyTextFieldBorder extends AbstractBorder implements UIResource {

	/**
	 * Gets the border insets for a given component.
	 *
	 * @param c The component to get its border insets.
	 * @return Always returns the same insets as defined in <code>insets</code>.
	 */
	public Insets getBorderInsets(Component c) {
		return Theme.textInsets;
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
				g, Theme.textBorderDisabledColor.getColor(), x, y, w, h);
		}
		else {
			DrawRoutines.drawBorder(
				g, Theme.textBorderColor.getColor(), x, y, w, h);
		}
	}
}