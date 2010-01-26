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
import javax.swing.border.Border;

import de.muntjak.tinylookandfeel.Theme;

/**
 * TinyToolTipBorder
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyToolTipBorder implements Border {
	
	private static final Insets insets = new Insets(3, 3, 3, 3);
	private boolean active;
	
	public TinyToolTipBorder(boolean b) {
		active = b;
	}

	public boolean isBorderOpaque() {
		return false;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {		
		if(active) {
			g.setColor(Theme.tipBorderColor.getColor());
		}
		else {
			g.setColor(Theme.tipBorderDis.getColor());
		}

		g.drawRect(x, y, w - 1, h - 1);
	}

	public Insets getBorderInsets(Component c) {
		return insets;
	}
}
