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
 * TinyEditorPaneUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyEditorPaneUI extends BasicEditorPaneUI {
	
	JTextComponent editor;

	public static ComponentUI createUI(JComponent c) {
		return new TinyEditorPaneUI();
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
