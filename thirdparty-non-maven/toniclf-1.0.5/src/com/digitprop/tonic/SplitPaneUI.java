package com.digitprop.tonic;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;


/**	UI delegate for JSplitPanes.
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
public class SplitPaneUI extends BasicSplitPaneUI
{
	/**	Creates and returns a UI delegate for the specified
	 * 	component.
	 */
	public static ComponentUI createUI(JComponent component) 
	{
		return new SplitPaneUI();
	}
	

	/**	Creates the default divider */
	public BasicSplitPaneDivider createDefaultDivider() 
	{
		return new SplitPaneDivider(this);
	}
    
   
   /**	Installs the UI settings for the specified component */ 
   public void installUI(JComponent c)
	{
		super.installUI(c);
		
		c.addContainerListener(new SplitPaneContListener());
		
		if(c instanceof JSplitPane)
		{
			JSplitPane sp=(JSplitPane)c;
			Component child=sp.getLeftComponent();
			setContentBorder(child);
			
			child=sp.getRightComponent();
			setContentBorder(child);
		}
	}

   
   

	/**	Sets the border for the specified contained component */   
   protected void setContentBorder(Component c)
   {
		if(c instanceof JComponent)
		{
			JComponent jc=(JComponent)c;
			if(jc.getBorder()==null)
				jc.setBorder(new SplitPaneContentBorder());
		}
   }
	

	/**	Listener for content changes to the JSplitPane */	
	protected class SplitPaneContListener implements ContainerListener
	{
		public void componentAdded(ContainerEvent e)
		{
			Component c= e.getChild();
			setContentBorder(c);
		}

		public void componentRemoved(ContainerEvent e)
		{
			Component c= e.getChild();

			if (c instanceof JComponent)
			{
				JComponent jc=(JComponent)c;
				if(jc.getBorder() instanceof SplitPaneContentBorder)
					jc.setBorder(null);
			}
		}
	}    
}
