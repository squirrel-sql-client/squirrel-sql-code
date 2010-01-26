package com.digitprop.tonic;


import java.awt.*;

import javax.swing.*;


/**	Subclass of JButton for combo boxes.
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
class ComboBoxButton extends JButton
{
	/**	The underlying JComboBox for which this button is used */
	protected JComboBox 			comboBox;
	
	/**	The underlying listbox belonging to the combo box */
	protected JList 				listBox;
	
	/**	The cell renderer for the underlying combo box */
	protected CellRendererPane	rendererPane;
	
	/**	The combo box icon */
	protected Icon 					comboIcon;
	
	/**	If true, only the icon will be painted */
	protected boolean 				iconOnly= false;


	/**	Creates an instance */
	ComboBoxButton()
	{
		super("");
	
		DefaultButtonModel model= new DefaultButtonModel()
		{
			public void setArmed(boolean armed)
			{
				super.setArmed(isPressed() ? true : armed);
			}
		};

		setModel(model);

		// Set the background and foreground to the combobox colors.
		setBackground(UIManager.getColor("Button.background"));
		setForeground(UIManager.getColor("Button.foreground"));
		setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
	}

	
	/**	Creates an instance for the specified combo box, with the
	 * 	specified icon, cell renderer and list.
	 * 
	 * 	@param	cb		The combo box for which to create an instance
	 * 	@param	i		The combo box icon
	 * 	@param	pane	The cell renderer for the combo box
	 * 	@param	list	The underlying list for the combo box
	 */
	public ComboBoxButton(JComboBox cb, Icon i, CellRendererPane pane, JList list)
	{
		this();
	
		comboBox= cb;
		comboIcon= i;
		rendererPane= pane;
		listBox= list;
		setEnabled(comboBox.isEnabled());
	}


	/**	Creates an instance for the specified combo box, with the
	 * 	specified icon, cell renderer and list.
	 * 
	 * 	@param	cb				The combo box for which to create an instance
	 * 	@param	i				The combo box icon
	 * 	@param	iconOnly		If true, only the icon will be painted
	 * 	@param	pane			The cell renderer for the combo box
	 * 	@param	list			The underlying list for the combo box
	 */
	public ComboBoxButton(JComboBox cb, Icon i, boolean iconOnly, CellRendererPane pane, JList list)
	{
		this(cb, i, pane, list);
		this.iconOnly= iconOnly;
	}

	
	/**	Returns the underlying combo box */
	public final JComboBox getComboBox()
	{
		return comboBox;
	}
	
	
	/**	Sets the underlying combo box */
	public final void setComboBox(JComboBox cb)
	{
		comboBox= cb;
	}

	
	/**	Returns the icon for the combo box */
	public final Icon getComboIcon()
	{
		return comboIcon;
	}
	
	
	/**	Sets the icon for the combo box */
	public final void setComboIcon(Icon i)
	{
		comboIcon= i;
	}


	/**	Returns true if only the icon is to be painted */
	public final boolean isIconOnly()
	{
		return iconOnly;
	}
	
	
	/**	Sets whether only the icon is to be painted */
	public final void setIconOnly(boolean isIconOnly)
	{
		iconOnly= isIconOnly;
	}


	/**	Returns true if this button can have the focus */	
	public boolean isFocusTraversable()
	{
		return false;
	}

	
	/**	Paints this button into the specified Graphics context */
	public void paintComponent(Graphics g)
	{
		// Fill background
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(getForeground());
		g.drawLine(0, 0, 0, getHeight()-1);		
		
		int delta=0;
		if (model.isArmed() && model.isPressed())
		{
			g.setColor(UIManager.getColor("controlShadow"));
			g.drawLine(1, 0, getWidth()-1, 0);
			g.drawLine(1, 0, 1, getHeight()-1);
			
			g.setColor(UIManager.getColor("controlHighlight"));
			g.drawLine(1, getHeight()-1, getWidth()-1, getHeight()-1);
			g.drawLine(getWidth()-1, 0, getWidth()-1, getHeight()-1);				
			
			delta=1;
		}
		
		// Draw arrow
		if (comboIcon != null)
		{
			int iconWidth= comboIcon.getIconWidth();
			int iconHeight= comboIcon.getIconHeight();
			
			comboIcon.paintIcon(this, g, getWidth()/2-iconWidth/2+delta, getHeight()/2-iconHeight/2+delta);
		}
	}


	/**	Convenience function for determining the component orientation.
	 * 	Helps us avoid having Munge directives throughout the code.
	 */
	static boolean isLeftToRight(Component c)
	{
		return c.getComponentOrientation().isLeftToRight();
	}
}
