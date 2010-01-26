package com.digitprop.tonic;


import javax.swing.*;

import javax.swing.plaf.*;


/**	UI delegate for JCheckBoxes.
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
public class CheckBoxUI extends RadioButtonUI 
{
	/** 	The delegate for checkboxes */
	private final static CheckBoxUI checkboxUI = new CheckBoxUI();

	/**	The property prefix for checkboxes */
	private final static String propertyPrefix = "CheckBox" + "."; 

	/**	The icon for a selected, enabled checkbox */
	protected static Icon selectedEnabledIcon;

	/**	The icon for a selected, disabled checkbox */
	protected static Icon selectedDisabledIcon;

	/**	The icon for an unselected, enabled checkbox */
	protected static Icon unselectedEnabledIcon;

	/**	The icon for an unselected, disabled checkbox */
	protected static Icon unselectedDisabledIcon;
	
	/**	If true, the defaults have already been initialized */
	private static boolean defaults_initialized=false;

	 
	/**	Creates and returns the UI delegate for the specified
	 * 	component.
	 */
	public static ComponentUI createUI(JComponent b) 
	{
		return checkboxUI;
	}
	 

	/**	Installs the defaults for the specified AbstractButton */
	public void installDefaults(AbstractButton b)
	{
		super.installDefaults(b);
		
		if (!defaults_initialized)
		{		
			// Get icons
			selectedEnabledIcon=
					UIManager.getIcon(getPropertyPrefix()+"selectedEnabledIcon");
			selectedDisabledIcon=
					UIManager.getIcon(getPropertyPrefix()+"selectedDisabledIcon");
			unselectedEnabledIcon=
					UIManager.getIcon(getPropertyPrefix()+"unselectedEnabledIcon");
			unselectedDisabledIcon=
					UIManager.getIcon(getPropertyPrefix()+"unselectedDisabledIcon");			
					
			defaults_initialized= true;
		}
	}


	/**	Returns the icon for selected, enabled checkboxes */
	public Icon getSelectedEnabledIcon()
	{
		return selectedEnabledIcon;
	}
	
	
	/**	Returns the icon for selected, disabled checkboxes */
	public Icon getSelectedDisabledIcon()
	{
		return selectedDisabledIcon;
	}
	
	
	/**	Returns the icon for unselected, enabled checkboxes */
	public Icon getUnselectedEnabledIcon()
	{
		return unselectedEnabledIcon;
	}
	
	
	/**	Returns the icon for unselected, disabled checkboxes */
	public Icon getUnselectedDisabledIcon()
	{
		return unselectedDisabledIcon;
	}	
	
	
	/**	Returns the property prefix for checkboxes */
	public String getPropertyPrefix() 
	{
		return propertyPrefix;
	}
}
