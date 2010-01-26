package com.digitprop.tonic;


import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;


/**	Utility class which provides borders for the Tonic look and feel.
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
class Borders extends BasicBorders
{
	/**	Returs the default border for JButtons */
	public static Border getButtonBorder()
	{
		// return new com.digitprop.tonic.ButtonBorder();
		return BorderFactory.createLineBorder(UIManager.getColor("Button.borderColor"));
	}
}
