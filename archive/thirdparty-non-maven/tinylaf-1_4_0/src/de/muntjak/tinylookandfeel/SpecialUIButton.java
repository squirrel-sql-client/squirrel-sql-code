/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import javax.swing.JButton;
import javax.swing.plaf.ButtonUI;

/**
 * This Button uses the given ButtonUI to paint itself. Useful if a button should have
 * a different look than the standard button. It refuses to change the UI delegate of
 * the button. 
 */
public class SpecialUIButton extends JButton {

    public SpecialUIButton(ButtonUI ui) {
        this.ui = ui;
        ui.installUI(this);
    }
    
    /**
     * refuses to change the UI delegate. It keeps the one set in the constructor.
     * @see javax.swing.AbstractButton#setUI(ButtonUI)
     */
    public void setUI(ButtonUI ui) {}       
}