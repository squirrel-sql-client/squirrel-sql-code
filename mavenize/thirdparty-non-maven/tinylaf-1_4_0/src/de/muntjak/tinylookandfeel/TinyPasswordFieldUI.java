/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.JTextComponent;

/**
 * TinyPasswordFieldUI
 * 
 * @version 1.4
 * @author Hans Bickel
 */
public class TinyPasswordFieldUI extends BasicPasswordFieldUI {

	public static ComponentUI createUI(JComponent c) {
        return new TinyPasswordFieldUI();
    }
 
    protected void paintBackground(Graphics g) {
    	JTextComponent editor = getComponent();

    	if(editor.isEnabled()) {
    		// Note: Was a bug until 1.4.0 (there simply
    		// was no non-editable state for password fields)
    		if(editor.isEditable()) {
    			g.setColor(editor.getBackground());
    		}
    		else {
    			g.setColor(Theme.textNonEditableBgColor.getColor());
    		}
    	}
    	else {
    		g.setColor(Theme.textDisabledBgColor.getColor());
    	}
        
        g.fillRect(0, 0, editor.getWidth(), editor.getHeight());
    }
}
