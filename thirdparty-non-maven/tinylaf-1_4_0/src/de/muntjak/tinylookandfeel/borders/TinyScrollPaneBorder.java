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

/**
 * TinyScrollPaneBorder
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyScrollPaneBorder extends AbstractBorder implements UIResource {

	private static final Insets defaultInsets = new Insets(1, 1, 1, 1);   

	public Insets getBorderInsets(Component c) {
		return defaultInsets;
	}

	/**
	 * @see javax.swing.border.Border#paintBorder(Component, Graphics, int, int, int, int)
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        g.setColor(Theme.scrollPaneBorderColor.getColor());
        g.drawRect(x, y, w - 1, h - 1);
	}
}