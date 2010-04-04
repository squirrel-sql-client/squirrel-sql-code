package com.digitprop.tonic;


import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;


/**	UI delegate for JSeparators
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
public class SeparatorUI extends BasicSeparatorUI
{
	/**	The shadow of this separator */
	protected Color shadow;
	
	/**	The highlight of this separator */
	protected Color highlight;


	/**	Creates and returns a UI delegate for the specified component */
	public static ComponentUI createUI(JComponent c)
	{
		return new SeparatorUI();
	}


	/**	Installs the UI settings for the specified component */
	public void installUI(JComponent c)
	{
		installDefaults((JSeparator) c);
		installListeners((JSeparator) c);
	}


	/**	Uninstalls the UI settings for the specified component */
	public void uninstallUI(JComponent c)
	{
		uninstallDefaults((JSeparator) c);
		uninstallListeners((JSeparator) c);
	}


	protected void installDefaults(JSeparator s)
	{
		LookAndFeel.installColors(
			s,
			"Separator.background",
			"Separator.foreground");
	}


	protected void uninstallDefaults(JSeparator s)
	{
	}


	protected void installListeners(JSeparator s)
	{
	}


	protected void uninstallListeners(JSeparator s)
	{
	}


	/**	Paints the specified component */
	public void paint(Graphics g, JComponent c)
	{
		Dimension s= c.getSize();

		if (((JSeparator) c).getOrientation() == JSeparator.VERTICAL)
		{
			//g.setColor(c.getBackground());
			//g.drawLine(0, 0, 0, s.height);

			g.setColor(c.getForeground());
			g.drawLine(5, 2, 5, s.height-3);
		}
		else // HORIZONTAL
		{
			g.setColor(c.getBackground());
			g.drawLine(0, 0, s.width, 0);			
			g.setColor(c.getForeground());
			g.drawLine(6, 0, s.width-10, 0);
		}
	}


	/**	Returns the preferred size for the specified component */
	public Dimension getPreferredSize(JComponent c)
	{
		if (((JSeparator) c).getOrientation() == JSeparator.VERTICAL)
			return new Dimension(10, 20);
		else
			return new Dimension(25, 1);
	}


	/**	Returns the minimum size for the specified component */
	public Dimension getMinimumSize(JComponent c)
	{
		return null;
	}


	/**	Returns the maximum size for the specified component */
	public Dimension getMaximumSize(JComponent c)
	{
		return null;
	}
}
