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
import javax.swing.*;

import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;

/**
 * ToolBarSeparatorUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyToolBarSeparatorUI extends BasicToolBarSeparatorUI {

	private static final int YQ_SIZE 	= 7;

	public static ComponentUI createUI(JComponent c) {
		return new TinyToolBarSeparatorUI();
	}

	/**
	 * Overridden to do nothing
	 */
	protected void installDefaults(JSeparator s) {
	}
	
	public Dimension getMinimumSize(JComponent c) {
		JToolBar.Separator sep = (JToolBar.Separator)c;
		
		if(sep.getOrientation() == JSeparator.HORIZONTAL) {
			return new Dimension(0, 1);
		}
		else {
			return new Dimension(1, 0);
		}
	}
	
	public Dimension getMaximumSize(JComponent c) {
		JToolBar.Separator sep = (JToolBar.Separator)c;		
		Dimension size = sep.getSeparatorSize();

		if(sep.getOrientation() == JSeparator.HORIZONTAL) {
			if(size != null) return new Dimension(32767, size.height);
			
			return new Dimension(32767, YQ_SIZE);
		}
		else {
			if(size != null) return new Dimension(32767, size.width);
			
			return new Dimension(YQ_SIZE, 32767);
		}
	}
	
	public Dimension getPreferredSize(JComponent c) {
		JToolBar.Separator sep = (JToolBar.Separator)c;
		
		Dimension size = sep.getSeparatorSize();
		
		if(size != null) return size.getSize();
		
		if(sep.getOrientation() == JSeparator.HORIZONTAL) {
			return new Dimension(0, YQ_SIZE);
		}
		else {
			return new Dimension(YQ_SIZE, 0);
		}
	}

	public void paint(Graphics g, JComponent c) {
		drawXpToolBarSeparator(g, c);
	}

	protected void drawXpToolBarSeparator(Graphics g, JComponent c) {
		JToolBar.Separator sep = (JToolBar.Separator)c;
		
		if(sep.getOrientation() == JSeparator.HORIZONTAL) {
			int y = sep.getHeight() / 2;	// centered if height is odd
			
			g.setColor(Theme.toolSeparatorColor.getColor());
			g.drawLine(0, y, sep.getWidth(), y);
		}
		else {
			int x = sep.getWidth() / 2;	// centered if width is odd

			g.setColor(Theme.toolSeparatorColor.getColor());
			g.drawLine(x, 0, x, sep.getHeight());
		}
	}
}
