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
 * TinyCheckBoxMenuItemUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyCheckBoxMenuItemUI extends TinyMenuItemUI {

    public static ComponentUI createUI(JComponent c) {
        return new TinyCheckBoxMenuItemUI();
    }

    protected String getPropertyPrefix() {
		return "CheckBoxMenuItem";
    }
}
