/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 * 
 * This is an almost unchanged version of MetalFileChooserUI.
 * 
 * 
 * @(#)MetalFileChooserUI.java  1.45 02/04/11
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package de.muntjak.tinylookandfeel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.plaf.metal.MetalFileChooserUI;

import de.muntjak.tinylookandfeel.borders.TinyToolButtonBorder;

/**
 * TinyFileChooserUI
 * 
 * Rewrote this in 1.4.0 to get rid of the dependency on
 * sun.awt.shell.ShellFolder class.
 * 
 * @author Hans Bickel
 * @version 1.4.0
 *
 */
public class TinyFileChooserUI extends MetalFileChooserUI {
	
	/** Key for a button's isFileChooserButton client property */
	public static final String IS_FILE_CHOOSER_BUTTON_KEY = "isFileChooserButton";
	private static final Dimension hstrut1 = new Dimension(1, 1);
	
    public static ComponentUI createUI(JComponent c) {
        return new TinyFileChooserUI((JFileChooser) c);
    }

    private TinyFileChooserUI(JFileChooser c) {
        super(c);
    }

    public void installComponents(JFileChooser fc) {
    	super.installComponents(fc);

    	// We want to access the icon-only buttons and
    	// their parent panel which were constructed in
    	// the super call. This is a hack, but MetalFileChooserUI
    	// would need a rewrite to be used correctly.
    	
    	// Note: The code makes assumptions about the layout
    	// of the file chooser component which might fail with
    	// future swing releases.
    	
    	// Search for topButtonPanel which is assumed to be
    	// a child of a child of the file chooser component
    	Component[] ci = fc.getComponents();
    	
    	for(int i = 0; i < ci.length; i++) {
    		if(ci[i] instanceof JPanel) {
    			Component[] cj = ((Container)ci[i]).getComponents();
    			
    			for(int j = 0; j < cj.length; j++) {
    	    		if(cj[j] instanceof JPanel) {
    	    			Component[] ck = ((Container)cj[j]).getComponents();
    	    			Vector buttons = new Vector();
    	    			
    	    			for(int k = 0; k < ck.length; k++) {
    	    				if(ck[k] instanceof AbstractButton) {
    	    					Icon icon = ((AbstractButton)ck[k]).getIcon();
    	    					
    	    					if(isFileChooserIcon(icon)) {
    	    						buttons.add(ck[k]);
    	    					}
    	    				}
    	    			}
    	    			
    	    			// newFolder button might be present or not
    	    			if(buttons.size() >= 4) {
    	    				// ok, we have the topButtonPanel and
    	    				// we have all icon-only buttons
    	    				JPanel topButtonPanel = (JPanel)cj[j];
    	    				Border toolButtonBorder = new TinyToolButtonBorder();

    	    				// we want other gaps between button 
    	    				topButtonPanel.removeAll();
    	    				
    	    				// order of buttons should already be left to right
    	    				Iterator ii = buttons.iterator();
    	    				while(ii.hasNext()) {
    	    					AbstractButton b = (AbstractButton)ii.next();
    	    					
    	    					b.putClientProperty(IS_FILE_CHOOSER_BUTTON_KEY, Boolean.TRUE);
    	    					b.setOpaque(false);
    	    					b.setBorder(toolButtonBorder);
    	    					
    	    					if(b instanceof JToggleButton) {
    	    						topButtonPanel.add(Box.createRigidArea(hstrut1));
    	    						b.setMargin(new Insets(4, 2, 5, 2));
    	    					}
    	    					else {
    	    						b.setMargin(new Insets(2, 2, 2, 2));
    	    					}
    	    					
    	    					topButtonPanel.add(b);
    	    				}
    	    			}
    	    		}
    			}
    		}
    	}
    }
    
    private boolean isFileChooserIcon(Icon icon) {
    	if(icon == null) return false;
    	
    	return 	icon.equals(upFolderIcon) ||
    			icon.equals(homeFolderIcon) ||
    			icon.equals(newFolderIcon) ||
    			icon.equals(listViewIcon) ||
    			icon.equals(detailsViewIcon);
    }
}