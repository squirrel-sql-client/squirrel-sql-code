package com.digitprop.tonic;


import java.awt.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;
import javax.swing.plaf.*;


/**	The UI delegate for JInternalFrames.
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
public class InternalFrameUI extends MetalInternalFrameUI
{
	/**	The title pane for the JInternalFrame */
	private InternalFrameTitlePane titlePane;

	/**	Listens for property changes of the JInternalFrame */
	private static final PropertyChangeListener metalPropertyChangeListener=
		new MetalPropertyChangeHandler();

	/**	Empty border */
	private static final Border handyEmptyBorder= new EmptyBorder(0, 0, 0, 0);

	/**	Property constant */
	protected static String IS_PALETTE= "JInternalFrame.isPalette";

	/**	Property constant */
	private static String FRAME_TYPE= "JInternalFrame.frameType";
	
	/**	Property constant */
	private static String NORMAL_FRAME= "normal";
	
	/**	Property constant */
	private static String PALETTE_FRAME= "palette";
	
	/**	Property constant */
	private static String OPTION_DIALOG= "optionDialog";


	/**	Creates an instance for the specified JInternalFrame */
	public InternalFrameUI(JInternalFrame b)
	{
		super(b);
	}


	/**	Creates and returns a UI delegate for the specified component */
	public static ComponentUI createUI(JComponent c)
	{
		return new InternalFrameUI((JInternalFrame) c);
	}


	/**	Installs the UI delegate for the specified component */
	public void installUI(JComponent c)
	{
		super.installUI(c);

		Object paletteProp= c.getClientProperty(IS_PALETTE);
		if (paletteProp != null)
		{
			setPalette(((Boolean) paletteProp).booleanValue());
		}

		Container content= frame.getContentPane();
		stripContentBorder(content);
		//c.setOpaque(false);
	}


	/**	Uninstalls the UI delegate for the specified component */
	public void uninstallUI(JComponent c)
	{
		frame= (JInternalFrame) c;

		Container cont= ((JInternalFrame) (c)).getContentPane();
		if (cont instanceof JComponent)
		{
			JComponent content= (JComponent) cont;
			if (content.getBorder() == handyEmptyBorder)
			{
				content.setBorder(null);
			}
		}
		super.uninstallUI(c);
	}


	/**	Installs the property listener with the associated JInternalFrame */
	protected void installListeners()
	{
		super.installListeners();
		frame.addPropertyChangeListener(metalPropertyChangeListener);
	}

	
	/**	Uninstalls any listeners registered with the associated JInternalFrame */
	protected void uninstallListeners()
	{
		frame.removePropertyChangeListener(metalPropertyChangeListener);
		super.uninstallListeners();
	}

	
	/**	Uninstalls any components installed for the associated JInternalFrame */
	protected void uninstallComponents()
	{
		titlePane= null;
		super.uninstallComponents();
	}

	
	/**	Removes the previous content border from the specified component, and
	 * 	sets an empty border if there has been no border or an UIResource instance
	 * 	before.
	 */
	private void stripContentBorder(Object c)
	{
		if (c instanceof JComponent)
		{
			JComponent contentComp= (JComponent) c;
			Border contentBorder= contentComp.getBorder();
			if (contentBorder == null || contentBorder instanceof UIResource)
			{
				contentComp.setBorder(handyEmptyBorder);
			}
		}
	}


	/**	Creates the title pane for the specified JInternalFrame */
	protected JComponent createNorthPane(JInternalFrame w)
	{
		titlePane= new InternalFrameTitlePane(w);
		return titlePane;
	}


	/**	Sets the frame type according to the specified type constant. This must
	 * 	be one of the SwingConstants.
	 */
	private void setFrameType(String frameType)
	{
		if (frameType.equals(OPTION_DIALOG))
		{
			LookAndFeel.installBorder(frame, "InternalFrame.optionDialogBorder");
			titlePane.setPalette(false);
		}
		else if (frameType.equals(PALETTE_FRAME))
		{
			LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
			titlePane.setPalette(true);
		}
		else
		{
			LookAndFeel.installBorder(frame, "InternalFrame.border");
			titlePane.setPalette(false);
		}
	}


	/**	Sets whether this JInternalFrame is to use a palette border or not */
	public void setPalette(boolean isPalette)
	{
		if (isPalette)
		{
			LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
		}
		else
		{
			LookAndFeel.installBorder(frame, "InternalFrame.border");
		}
		titlePane.setPalette(isPalette);

	}


	private static class MetalPropertyChangeHandler
		implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String name= e.getPropertyName();
			JInternalFrame jif= (JInternalFrame) e.getSource();

			if (!(jif.getUI() instanceof InternalFrameUI))
			{
				return;
			}

			InternalFrameUI ui= (InternalFrameUI) jif.getUI();

			if (name.equals(FRAME_TYPE))
			{
				if (e.getNewValue() instanceof String)
				{
					ui.setFrameType((String) e.getNewValue());
				}
			}
			else if (name.equals(IS_PALETTE))
			{
				if (e.getNewValue() != null)
				{
					ui.setPalette(((Boolean) e.getNewValue()).booleanValue());
				}
				else
				{
					ui.setPalette(false);
				}
			}
			else if (name.equals(JInternalFrame.CONTENT_PANE_PROPERTY))
			{
				ui.stripContentBorder(e.getNewValue());
			}
		}
	} // end class MetalPropertyChangeHandler
}
