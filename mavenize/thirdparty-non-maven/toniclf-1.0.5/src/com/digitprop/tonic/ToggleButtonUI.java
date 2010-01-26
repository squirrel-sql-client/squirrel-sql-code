package com.digitprop.tonic;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.*;


/**	UI delegate for JToggleButtons.
 * 
 * 	@author	Markus Fischer 
 *
 *  	<p>This software is under the <a href="http://www.gnu.org/copyleft/lesser.html" target="_blank">GNU Lesser General Public License</a>
 */

/*
 * ------------------------------------------------------------------------
 * Copyright (C) 2004 Markus Fischer
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 * MA 02111-1307  USA
 * 
 * You can contact the author at:
 *    Markus Fischer
 *    www.digitprop.com
 *    info@digitprop.com
 * ------------------------------------------------------------------------
 */
public class ToggleButtonUI extends com.digitprop.tonic.ButtonUI 
{
	private final static Border defaultBorder =Borders.getButtonBorder(); 

	private final static ToggleButtonUI toggleButtonUI = new ToggleButtonUI();

	private final static String propertyPrefix = "ToggleButton" + ".";
	

	/**	Creates and returns a UI delegate for the specified component */
	public static ComponentUI createUI(JComponent component)
	{
		return toggleButtonUI;
	}
	
	
	/**	Returns the property prefix for ToggleButtons */
	protected String getPropertyPrefix()
	{
		return propertyPrefix;
	}


	/**	Installs the UI settings for the specified component */
	public void installUI(JComponent c)
	{
		super.installUI(c);
		
		if(c.getBorder()==null || (c.getBorder() instanceof UIResource))
			c.setBorder(defaultBorder);
	}
	

	/**	Overriden so that the text will not be rendered as shifted for
	 * 	Toggle buttons and subclasses.
	 */
	protected int getTextShiftOffset()
	{
		return 0;
	}

}
