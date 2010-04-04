/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;
import javax.swing.plaf.metal.MetalToolBarUI;

/**
 * TinyMenuBarUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyMenuBarUI extends BasicMenuBarUI {
	
	private static final boolean DEBUG = true;
	private static final String CLOSE_OPENED_MENU_KEY = "closeOpenedMenu";

	/**
	 * Creates the UI delegate for the given component.
	 * Because in normal application there is usually only one menu bar, the UI
	 * delegate isn't cached here.
	 *
	 * @param mainColor The component to create its UI delegate.
	 * @return The UI delegate for the given component.
	 */
	public static ComponentUI createUI(JComponent c) {
		return new TinyMenuBarUI();
	}

	/**
	 * Paints the menu bar background.
	 *
	 * @param g The graphics context to use.
	 * @param mainColor The component to paint.
	 */
	public void paint(Graphics g, JComponent c) {
		if(!c.isOpaque()) return;

		Color bg = c.getBackground();
		
		if(bg instanceof ColorUIResource) {
			bg = Theme.menuBarColor.getColor();
		}
		
		g.setColor(bg);
		g.fillRect(0, 0, c.getWidth(), c.getHeight());
	}
}