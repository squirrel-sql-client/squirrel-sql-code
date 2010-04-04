/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.plaf.metal.MetalInternalFrameUI;

import de.muntjak.tinylookandfeel.borders.*;

/**
 * TinyInternalFrameUI
 * 
 * 6.4.06 Removed getDesktopManager() and
 * createDesktopManager() (will now be
 * handled by the base class).
 * 
 * @version 1.3.04
 * @author Hans Bickel
 */
public class TinyInternalFrameUI extends BasicInternalFrameUI {

	private TinyInternalFrameBorder frameBorder;
	
	/**
	 * The TinyLaF version of the internal frame title pane.
	 */
	private TinyInternalFrameTitlePane titlePane;

	/**
	 * Creates the UI delegate for the given frame.
	 *
	 * @param frame The frame to create its UI delegate.
	 */
	public TinyInternalFrameUI(JInternalFrame frame) {
		super(frame);
	}

	/**
	 * Creates the UI delegate for the given component.
	 *
	 * @param mainColor The component to create its UI delegate.
	 * @return The UI delegate for the given component.
	 */
	public static ComponentUI createUI(JComponent c) {
		return new TinyInternalFrameUI((JInternalFrame) c);
	}

	JDesktopPane getDesktopPane(JComponent frame) {
		JDesktopPane pane = null;
		Component c = frame.getParent();

		// Find the JDesktopPane
		while (pane == null) {
			if(c instanceof JDesktopPane) {
				pane = (JDesktopPane) c;
			} else if(c == null) {
				break;
			} else {
				c = c.getParent();
			}
		}

		return pane;
	}

	public void installUI(JComponent c) {
		super.installUI(c);
		
		frameBorder = new TinyInternalFrameBorder();
		frame.setBorder(frameBorder);
		frame.setOpaque(false);
	}

	protected PropertyChangeListener createPropertyChangeListener(){
    	return new TinyInternalFramePropertyChangeListener();
    }
	
	/**
	 * Creates the north pane (the internal frame title pane) for the given frame.
	 *
	 * @param frame The frame to create its north pane.
	 */
	protected JComponent createNorthPane(JInternalFrame frame) {
		super.createNorthPane(frame);
		
		titlePane = new TinyInternalFrameTitlePane(frame);

		return titlePane;
	}
    protected void activateFrame(JInternalFrame f) {
		super.activateFrame(f);
		frameBorder.setActive(true);
		titlePane.activate();
    }
    
    /** This method is called when the frame is no longer selected.
      * This action is delegated to the desktopManager.
      */
    protected void deactivateFrame(JInternalFrame f) {
		super.deactivateFrame(f);
		frameBorder.setActive(false);
		titlePane.deactivate();
    }
    
	/**
	 * Changes this internal frame mode from / to palette mode.
	 * This affect only the title pane.
	 *
	 * @param isPalette The target palette mode.
	 */
	public void setPalette(boolean isPalette) {
		// the following call caused iconify and maximize
		// buttons to disappear for palettes		
		//super.setPalette(isPalette);
		
		titlePane.setPalette(isPalette);
		
		frame.setBorder(frameBorder);
		frame.putClientProperty("isPalette",
			isPalette ? Boolean.TRUE : Boolean.FALSE);
	}
	
	public class TinyInternalFramePropertyChangeListener
		extends InternalFramePropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt) {
            String prop = (String)evt.getPropertyName();
            JInternalFrame f = (JInternalFrame)evt.getSource();

            TinyInternalFrameUI ui = (TinyInternalFrameUI)f.getUI();
            
            if(prop.equals("JInternalFrame.isPalette")) {
			      if(evt.getNewValue() != null) {
			          ui.setPalette(((Boolean)evt.getNewValue()).booleanValue());
			      }
			      else {
				  	ui.setPalette(false);
			      }
	  		}
            
            super.propertyChange(evt);
		}
	}
}