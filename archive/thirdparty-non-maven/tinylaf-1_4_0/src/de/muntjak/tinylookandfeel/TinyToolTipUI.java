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
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

import de.muntjak.tinylookandfeel.controlpanel.*;

/**
 * TinyToolTipUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyToolTipUI extends MetalToolTipUI {

	protected static TinyToolTipUI sharedInstance = new TinyToolTipUI();


	public static ComponentUI createUI(JComponent list) {
		return sharedInstance;
	}
	
	protected void installDefaults(JComponent c) {
		super.installDefaults(c);
	}
}
