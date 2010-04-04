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

import javax.swing.JToolBar;
import javax.swing.plaf.metal.MetalBorders.ToolBarBorder;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.TinyToolBarUI;
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * TinyToolBarBorder
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyToolBarBorder extends ToolBarBorder {

	public Insets getBorderInsets(Component c) {
		return getBorderInsets(c, new Insets(0, 0, 0, 0));
	}

	public Insets getBorderInsets(Component c, Insets newInsets) {
		newInsets.top = newInsets.left = newInsets.bottom = newInsets.right = 2;
		
		// we cannot assume that c is a JToolBar
		if(!(c instanceof JToolBar)) return newInsets;

		if(((JToolBar)c).isFloatable()) {
			if(((JToolBar)c).getOrientation() == HORIZONTAL) {
				if(c.getComponentOrientation().isLeftToRight()) {
					newInsets.left = TinyToolBarUI.FLOATABLE_GRIP_SIZE + 2;
				} else {
					newInsets.right = TinyToolBarUI.FLOATABLE_GRIP_SIZE + 2;
				}
			} else { // vertical
				newInsets.top = TinyToolBarUI.FLOATABLE_GRIP_SIZE + 2;
			}
		}

		Insets margin = ((JToolBar)c).getMargin();

		if(margin != null) {
			newInsets.left += margin.left;
			newInsets.top += margin.top;
			newInsets.right += margin.right;
			newInsets.bottom += margin.bottom;
		}

		return newInsets;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		if(!(c instanceof JToolBar)) return;
		
		drawXPBorder(c, g, x, y, w, h);

		if(((JToolBar)c).getOrientation() == HORIZONTAL) {
			g.setColor(Theme.toolBarLightColor.getColor());
			g.drawLine(x, y, w - 1, y);				// top
			g.setColor(Theme.toolBarDarkColor.getColor());
			g.drawLine(x, h - 1, w - 1, h - 1);		// bottom
		}
		else {
			g.setColor(Theme.toolBarLightColor.getColor());
			g.drawLine(x, y, x, h - 1);				// left
			g.setColor(Theme.toolBarDarkColor.getColor());
			g.drawLine(w - 1, y, w - 1, h - 1);		// right
		}
	}

	protected void drawXPBorder(Component c, Graphics g, int x, int y, int w, int h) {
		g.translate(x, y);

		if(((JToolBar)c).isFloatable()) {
			// paint grip
			if(((JToolBar)c).getOrientation() == HORIZONTAL) {
				int xoff = 3;
				if(!c.getComponentOrientation().isLeftToRight()) {
                    xoff = c.getBounds().width - TinyToolBarUI.FLOATABLE_GRIP_SIZE + 3;
                }
                
				g.setColor(Theme.toolGripLightColor.getColor());
				g.drawLine(xoff, 3, xoff + 1, 3);
				g.drawLine(xoff, 3, xoff, h - 5);
				g.setColor(Theme.toolGripDarkColor.getColor());
				g.drawLine(xoff, h - 4, xoff + 1, h - 4);
				g.drawLine(xoff + 2, 3, xoff + 2, h - 4);
			} else { // vertical
				g.setColor(Theme.toolGripLightColor.getColor());
				g.drawLine(3, 3, 3, 4);
				g.drawLine(3, 3, w - 4, 3);
				g.setColor(Theme.toolGripDarkColor.getColor());
				g.drawLine(w - 4, 4, w - 4, 5);
				g.drawLine(3, 5, w - 4, 5);
			}
		}

		g.translate(-x, -y);
	}
}
