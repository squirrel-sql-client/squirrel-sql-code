package com.digitprop.tonic;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import java.beans.*;
import javax.swing.plaf.basic.BasicDesktopIconUI;


/**	UI delegate for the icon view for JInternalFrames.
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
public class DesktopIconUI extends BasicDesktopIconUI
{
	/**	The button for the icon view */
	JButton button;
	
	/**	The label for the icon view */
	JLabel label;
	
	/**	Listener for title events */
	TitleListener titleListener;
	
	/**	The width of the desktop icon */
	private int width;


	/**	Creates an instance */
	public DesktopIconUI() 
	{
	}
	
		
	/**	Creates and returns a UI delegate for the specified component */	
	public static ComponentUI createUI(JComponent c)    
	{
		return new DesktopIconUI();
	}


	/**	Installs the defaults */
	protected void installDefaults() 
	{
		super.installDefaults();
		LookAndFeel.installColorsAndFont(desktopIcon, "DesktopIcon.background", "DesktopIcon.foreground", "DesktopIcon.font");
		desktopIcon.setOpaque(true);
		width = UIManager.getInt("DesktopIcon.width");
	}
   
   
   /**	Installs the components for this delegate */
	protected void installComponents() 
	{
		frame = desktopIcon.getInternalFrame();
		Icon icon = frame.getFrameIcon();
		String title = frame.getTitle();

		button = new JButton (title, icon);
		button.addActionListener( new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					deiconize(); 
				}
			});
			
		button.setFont(desktopIcon.getFont());
		button.setBackground(desktopIcon.getBackground());
		button.setForeground(desktopIcon.getForeground());

		int buttonH = button.getPreferredSize().height;

		label = new JLabel("  ");

		label.setBorder( new MatteBorder( 0, 2, 0, 1, desktopIcon.getBackground()) );
		desktopIcon.setLayout(new BorderLayout(2, 0));
		desktopIcon.add(button, BorderLayout.CENTER);
		desktopIcon.add(label, BorderLayout.WEST);
	}

	
	/**	Uninstalls the components from this delegate */
	protected void uninstallComponents() 
	{
		desktopIcon.setLayout(null);
		desktopIcon.remove(label);
		desktopIcon.remove(button);
		button = null;
		frame = null;
	}
 
 
 	/**	Installs the listeners for the desktop icon */
	protected void installListeners()
	{
		super.installListeners();
		desktopIcon.getInternalFrame().addPropertyChangeListener(
			titleListener= new TitleListener());
	}

	
	/**	Uninstall the listeners from the desktop icon */
	protected void uninstallListeners()
	{
		desktopIcon.getInternalFrame().removePropertyChangeListener(titleListener);
		titleListener= null;
		super.uninstallListeners();
	}
        

	/**	Returns the preferred size for the specified component */
	public Dimension getPreferredSize(JComponent c)
	{
		// Metal desktop icons can not be resized.  Their dimensions should
		// always be the minimum size.  See getMinimumSize(JComponent c).
		return getMinimumSize(c);
	}

	/**	Returns the minimum size for the specified component */
	public Dimension getMinimumSize(JComponent c)
	{
		// For the metal desktop icon we will use the layout maanger to
		// determine the correct height of the component, but we want to keep
		// the width consistent according to the jlf spec.
		return new Dimension(width, desktopIcon.getLayout().minimumLayoutSize(desktopIcon).height);
	}

	/**	Returns the maximum size for the specified component */
	public Dimension getMaximumSize(JComponent c)
	{
		// Metal desktop icons can not be resized.  Their dimensions should
		// always be the minimum size.  See getMinimumSize(JComponent c).
		return getMinimumSize(c);
	}


	/**	Listens to changes of the title String, and sets the 
	 * 	button text of the desktop icon accordingly.
	 */
	class TitleListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			if (e.getPropertyName().equals("title"))
			{
				button.setText((String) e.getNewValue());
			}

			if (e.getPropertyName().equals("frameIcon"))
			{
				button.setIcon((Icon) e.getNewValue());
			}
		}
	}
}