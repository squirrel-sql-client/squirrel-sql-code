/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalScrollBarUI;
import javax.swing.plaf.metal.MetalScrollPaneUI;

/**
 * TinyScrollPaneUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyScrollPaneUI extends MetalScrollPaneUI implements PropertyChangeListener {
	
	/**
	 * Creates the UI delegate for the given component.
	 *
	 * @param mainColor The component to create its UI delegate.
	 * @return The UI delegate for the given component.
	 */
	public static ComponentUI createUI(JComponent c) {
		return new TinyScrollPaneUI();
	}
	
	/**
	 * Installs some default values for the given scrollpane.
	 * The free standing property is disabled here.
	 *
	 * @param mainColor The reference of the scrollpane to install its default values.
	 */
	public void installUI(JComponent c) {
		super.installUI(c);
		
		// Note: It never happened before Java 1.5 that scrollbar is null
		JScrollBar sb = scrollpane.getHorizontalScrollBar();
		
		if(sb != null) {
			sb.putClientProperty(MetalScrollBarUI.FREE_STANDING_PROP, Boolean.FALSE);
		}
		
		sb = scrollpane.getVerticalScrollBar();
		
		if(sb != null) {
			sb.putClientProperty(MetalScrollBarUI.FREE_STANDING_PROP, Boolean.FALSE);
		}
	}
	
	/**
	 * Creates a property change listener that does nothing inorder to prevent the
	 * free standing scrollbars.
	 *
	 * @return An empty property change listener.
	 */
	protected PropertyChangeListener createScrollBarSwapListener() {
		return this;
	}
	
	/**
	 * Simply ignore any change.
	 *
	 * @param event The property change event.
	 */
	public void propertyChange(PropertyChangeEvent e) {}
}