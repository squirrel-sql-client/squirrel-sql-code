/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.borders;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.util.ColorRoutines;

/**
 * TinyTableHeaderBorder is the border displayed for table headers
 * of non-sortable table models and of sortable table models if
 * the column in question is not the rollover column.
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyTableHeaderBorder extends AbstractBorder implements UIResource {
	
	protected static final Insets insetsXP = new Insets(3, 0, 3, 2);
	protected Color color1, color2, color3, color4, color5;

	public Insets getBorderInsets(Component c) {
		return insetsXP;
	}
	
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.left = insetsXP.left;
        insets.top = insetsXP.top;
        insets.right = insetsXP.right;
        insets.bottom = insetsXP.bottom;

        return insets;
    }

	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		if(color1 == null) {
			color1 = ColorRoutines.darken(c.getBackground(), 5);
		    color2 = ColorRoutines.darken(c.getBackground(), 10);
			color3 = ColorRoutines.darken(c.getBackground(), 15);
		    color4 = Theme.tableHeaderDarkColor.getColor();
		    color5 = Theme.tableHeaderLightColor.getColor();
		}
		
		// paint 3 bottom lines
		g.setColor(color1);
        g.drawLine(x, y + h - 3, x + w - 1, y + h - 3);
        
        g.setColor(color2);
        g.drawLine(x, y + h - 2, x + w - 1, y + h - 2);
        
        g.setColor(color3);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        
        // paint separator
        g.setColor(color4);
        g.drawLine(x + w - 2, y + 3, x + w - 2, y + h - 5);
        
        g.setColor(color5);
        g.drawLine(x + w - 1, y + 3, x + w - 1, y + h - 5);
	}
}
