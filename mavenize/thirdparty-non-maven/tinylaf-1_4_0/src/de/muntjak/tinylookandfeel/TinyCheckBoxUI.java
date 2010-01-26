/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalCheckBoxUI;

/**
 * TinyCheckBoxUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyCheckBoxUI extends MetalCheckBoxUI {

	/**
	 * The Cached UI delegate.
	 */
	private static final TinyCheckBoxUI SHARED_INSTANCE = new TinyCheckBoxUI();
	private static final TinyCheckBoxIcon checkIcon = new TinyCheckBoxIcon();
	private static final BasicStroke focusStroke = new BasicStroke(
		1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, new float[] { 1.0f/ 1.0f }, 1.0f);

	/**
	 * Creates the UI delegate for the given component.
	 *
	 * @param c The component to create its UI delegate.
	 * @return The UI delegate for the given component.
	 */
	public static ComponentUI createUI(JComponent c) {
		return SHARED_INSTANCE;
	}

	/**
	 * Installs some default values for the given button.
	 *
	 * @param button The reference of the button to install its default values.
	 */
	public void installDefaults(AbstractButton button) {
		super.installDefaults(button);
		icon = checkIcon;
		button.setRolloverEnabled(true);
		
		if(!Theme.buttonEnter.getValue()) return;
        if(!button.isFocusable()) return;

        InputMap km = (InputMap)UIManager.get(getPropertyPrefix() + "focusInputMap");

        if(km != null) {
        	km.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
        	km.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
        }
	}

	protected void paintFocus(Graphics g, Rectangle t, Dimension arg2) {
		if(!Theme.buttonFocus.getValue()) return;
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.black);
		g2d.setStroke(focusStroke);
		
		int x1 = t.x -1;
		int y1 = t.y -1;
		int x2 = x1 + t.width + 1;
		int y2 = y1 + t.height + 1;
		
		g2d.drawLine(x1, y1, x2, y1);
		g2d.drawLine(x1, y1, x1, y2);
		g2d.drawLine(x1, y2, x2, y2);
		g2d.drawLine(x2, y1, x2, y2);
	}
}