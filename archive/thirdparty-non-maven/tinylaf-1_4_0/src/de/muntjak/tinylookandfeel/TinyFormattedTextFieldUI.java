/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * TinyFormattedTextFieldUI
 * 
 * @version 1.4.0
 * @author Hans Bickel
 *
 */
public class TinyFormattedTextFieldUI extends TinyTextFieldUI {

	/**
     * Creates a UI for a JFormattedTextField.
     *
     * @param c the formatted text field
     * @return a new instance of TinyFormattedTextFieldUI
     */
    public static ComponentUI createUI(JComponent c) {
        return new TinyFormattedTextFieldUI();
    }
    
    protected String getPropertyPrefix() {
    	// New in 1.4.0: Specifying a property prefix
    	// enables FormattedTextField specific actions.
    	return "FormattedTextField";
    }
}
