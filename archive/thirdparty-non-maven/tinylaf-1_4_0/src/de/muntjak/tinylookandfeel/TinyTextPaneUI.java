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
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.JTextComponent;

import de.muntjak.tinylookandfeel.controlpanel.*;

/**
 * TinyTextPaneUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyTextPaneUI extends BasicTextPaneUI {
	
	JTextComponent editor;

	public static ComponentUI createUI(JComponent c) {
		return new TinyTextPaneUI();
	}
	
	public void installUI(JComponent c) {
        if(c instanceof JTextComponent) {
            editor = (JTextComponent) c;
        }
        
        super.installUI(c);
	}
	
	protected void installDefaults() {
		super.installDefaults();
	}
}
