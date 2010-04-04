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

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalRadioButtonUI;

/**
 * TinyRadioButtonUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyRadioButtonUI extends MetalRadioButtonUI {

	/** the only instance of the radiobuttonUI */
	private static final TinyRadioButtonUI radioButtonUI = new TinyRadioButtonUI();
	
	/* the only instance of the stroke for the focus */
	private static BasicStroke focusStroke =
		new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
		1.0f, new float[] { 1.0f, 1.0f }, 0.0f);
		
	/* the only instance of the radiobutton icon*/
	private static TinyRadioButtonIcon radioButton;

	/**
	 * Creates the singleton for the UI
	 * @see javax.swing.plaf.ComponentUI#createUI(JComponent)
	 */
	public static ComponentUI createUI(JComponent c) {
		if(c instanceof JRadioButton) {
			JRadioButton jb = (JRadioButton) c;
			jb.setRolloverEnabled(true);
		}
		
		return radioButtonUI;
	}

	/**
	 * Installs the icon for the UI
	 * @see javax.swing.plaf.ComponentUI#installUI(JComponent)
	 */
	public void installUI(JComponent c) {
		super.installUI(c);
		
		icon = getRadioButton();
		
		if(!Theme.buttonEnter.getValue()) return;
        if(!c.isFocusable()) return;

        InputMap km = (InputMap)UIManager.get(getPropertyPrefix() + "focusInputMap");

        if(km != null) {
        	km.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
        	km.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
        }
	}

	/**
	 * Returns the skinned Icon 
	 * @return TinyRadioButtonIcon
	 */
	protected TinyRadioButtonIcon getRadioButton() {
		if(radioButton==null) radioButton = new TinyRadioButtonIcon();
		
		return radioButton;
	}


	/**
	 * Paints the focus for the radiobutton
	 * @see javax.swing.plaf.metal.MetalRadioButtonUI#paintFocus(java.awt.Graphics, java.awt.Rectangle, java.awt.Dimension)
	 */
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