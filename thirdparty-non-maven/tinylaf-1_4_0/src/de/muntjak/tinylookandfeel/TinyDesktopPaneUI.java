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
import javax.swing.plaf.basic.BasicDesktopPaneUI;

/**
 * TinyDesktopPaneUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyDesktopPaneUI extends BasicDesktopPaneUI {

	public static ComponentUI createUI(JComponent c) {
        return new TinyDesktopPaneUI();
    }
	
	public void installUI(JComponent c)   {
		super.installUI(c);
	}
}
