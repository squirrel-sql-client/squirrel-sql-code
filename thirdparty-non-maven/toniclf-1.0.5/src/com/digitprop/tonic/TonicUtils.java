package com.digitprop.tonic;


import java.awt.*;

import javax.swing.*;


/**	Utility class for the Tonic look and feel.
 * 
 * 	@author Markus Fischer
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
class TonicUtils
{
	/**	Convenience function for determining ComponentOrientation.  Helps us
	 * 	avoid having Munge directives throughout the code.
	 */
	static boolean isLeftToRight(Component c)
	{
		return c.getComponentOrientation().isLeftToRight();
	}


	/**	Draws an disabled border for the specified area */
	static void drawDisabledBorder(Graphics g, int x, int y, int w, int h)
	{
		g.translate(x, y);
		g.setColor(TonicLookAndFeel.getControlShadow());
		g.drawRect(0, 0, w - 1, h - 1);
	}
	
	
	static int getInt(Object key, int defaultValue) 
	{
		Object value = UIManager.get(key);

		if (value instanceof Integer) 
		{
			return ((Integer)value).intValue();
		}
		
		if (value instanceof String) 
		{
			try 
			{
				return Integer.parseInt((String)value);
			} 
			catch (NumberFormatException nfe) 
			{
			}
		}
		return defaultValue;
	}	
}
