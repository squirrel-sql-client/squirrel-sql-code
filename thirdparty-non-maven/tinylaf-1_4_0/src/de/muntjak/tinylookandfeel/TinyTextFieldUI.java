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
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.plaf.metal.MetalTextFieldUI;
import javax.swing.text.JTextComponent;

import de.muntjak.tinylookandfeel.controlpanel.*;

/**
 * TinyTextFieldUI is the UI delegate for JTextField
 * and JFormattedTextField
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyTextFieldUI extends MetalTextFieldUI {

	/**
	 * 
	 * @param c
	 * @return ComponentUI
	 */
	public static ComponentUI createUI(JComponent c) {
		return new TinyTextFieldUI();
	}

	protected void paintBackground(Graphics g) {
		JTextComponent editor = getComponent();
		// We will only be here if editor is opaque, so we don't have to test

		if(editor.isEnabled()) {
			if(editor.isEditable()) {
				g.setColor(editor.getBackground());
			}
			else {
				// not editable
				// Note: Since 1.4.0, we have an explicit background color
				// for non-editable text components (in previous releases
				// this was "unspecified behaviour", the color used was
				// "TextField.inactiveBackground" from UI defaults table)
				if(editor.getBackground() instanceof ColorUIResource) {
					g.setColor(Theme.textNonEditableBgColor.getColor());
				}
				else {
					g.setColor(editor.getBackground());
				}
			}

			g.fillRect(0, 0, editor.getWidth(), editor.getHeight());
		}
		else {
			// disabled
			if(editor.getBackground() instanceof ColorUIResource) {
				g.setColor(Theme.textDisabledBgColor.getColor());
			}
			else {
				g.setColor(editor.getBackground());
			}
			
			g.fillRect(0, 0, editor.getWidth(), editor.getHeight());
			
			// If editor is editing a JSpinner, its border will
			// be null. So, because there are no border insets,
			// we return, else the text would be painted
			// above the background
			if(editor.getBorder() == null) return;

			g.setColor(Theme.backColor.getColor());
			g.drawRect(1, 1, editor.getWidth() - 3, editor.getHeight() - 3);
			g.drawRect(2, 2, editor.getWidth() - 5, editor.getHeight() - 5);
		}
	}
}