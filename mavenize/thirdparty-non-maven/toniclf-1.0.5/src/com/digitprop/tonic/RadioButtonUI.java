package com.digitprop.tonic;


import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.View;


/**	UI delegate for JRadioButtons.
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
public class RadioButtonUI extends ToggleButtonUI
{
	/** Color for focus border (lazily instantiated in paintFocus() */
	private static Color			focusColor;
	
	/**	Cached rectangle for painting */
	private static Rectangle prefViewRect= new Rectangle();
	
	/**	Cached rectangle for painting */
	private static Rectangle prefIconRect= new Rectangle();
	
	/**	Cached rectangle for painting */
	private static Rectangle prefTextRect= new Rectangle();
	
	/**	Cached rectangle for painting */
	private static Insets prefInsets= new Insets(0, 0, 0, 0);

	/**	Cached rectangle for painting */
	private static Dimension size= new Dimension();
	
	/**	Cached rectangle for painting */
	private static Rectangle viewRect= new Rectangle();
	
	/**	Cached rectangle for painting */
	private static Rectangle iconRect= new Rectangle();
	
	/**	Cached rectangle for painting */
	private static Rectangle textRect= new Rectangle();

	private final static RadioButtonUI radioButtonUI= new RadioButtonUI();

	protected Icon icon;

	private boolean defaults_initialized= false;

	private final static String propertyPrefix= "RadioButton" + ".";

	protected static Icon selectedEnabledIcon;

	protected static Icon selectedDisabledIcon;

	protected static Icon unselectedEnabledIcon;

	protected static Icon unselectedDisabledIcon;



	/**	Creates and returns the UI delegate for the specified component */
	public static ComponentUI createUI(JComponent component)
	{
		return radioButtonUI;
	}
	
	
	/**	Returns the property prefix for RadioButtons */
	protected String getPropertyPrefix()
	{
		return propertyPrefix;
	}

	
	/**	Installs the UI defaults for the specified button */
	public void installDefaults(AbstractButton b)
	{
		super.installDefaults(b);
		if (!defaults_initialized)
		{
			icon= UIManager.getIcon(getPropertyPrefix() + "icon");
			
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


	/**	Uninstalls the defaults for the specified button */
	public void uninstallDefaults(AbstractButton b)
	{
		super.uninstallDefaults(b);
		defaults_initialized= false;
	}


	/**	Returns the icon for a selected, enabled icon */	
	public Icon getSelectedEnabledIcon()
	{
		return selectedEnabledIcon;
	}
	
	
	/**	Returns the icon for a selected, disabled icon */
	public Icon getSelectedDisabledIcon()
	{
		return selectedDisabledIcon;
	}
	
	
	/**	Returns the icon for an unselected, enabled icon */
	public Icon getUnselectedEnabledIcon()
	{
		return unselectedEnabledIcon;
	}
	
	
	/**	Returns the icon for an unselected, disabled icon */
	public Icon getUnselectedDisabledIcon()
	{
		return unselectedDisabledIcon;
	}	
	
	
	/**	Returns the default icon */
	public Icon getDefaultIcon()
	{
		return icon;
	}


	/**	Paints the specified component */
	public synchronized void paint(Graphics g, JComponent c)
	{
		AbstractButton b= (AbstractButton) c;
		ButtonModel model= b.getModel();

		Font f= c.getFont();
		g.setFont(f);
		FontMetrics fm= g.getFontMetrics();

		size= b.getSize(size);
		viewRect.x= viewRect.y= 0;
		viewRect.width= size.width;
		viewRect.height= size.height;
		iconRect.x= iconRect.y= iconRect.width= iconRect.height= 0;
		textRect.x= textRect.y= textRect.width= textRect.height= 0;

		Icon altIcon= b.getIcon();
		Icon selectedIcon= null;
		Icon disabledIcon= null;

		String text=
			SwingUtilities.layoutCompoundLabel(
				c,
				fm,
				b.getText(),
				altIcon != null ? altIcon : getDefaultIcon(),
				b.getVerticalAlignment(),
				b.getHorizontalAlignment(),
				b.getVerticalTextPosition(),
				b.getHorizontalTextPosition(),
				viewRect,
				iconRect,
				textRect,
				b.getText() == null ? 0 : b.getIconTextGap());

		// fill background
		if (c.isOpaque())
		{
			g.setColor(b.getBackground());
			g.fillRect(0, 0, size.width, size.height);
		}

		// Paint the radio button
		if (b.getIcon() != null)
		{
			altIcon= b.getIcon();

			if (!model.isEnabled())
			{
				if (model.isSelected())
				{
					altIcon= b.getDisabledSelectedIcon();
				}
				else
				{
					altIcon= b.getDisabledIcon();
				}
			}
			else if (model.isPressed() && model.isArmed())
			{
				altIcon= b.getPressedIcon();
				if (altIcon == null)
				{
					// Use selected icon
					altIcon= b.getSelectedIcon();
				}
			}
			else if (model.isSelected())
			{
				if (b.isRolloverEnabled() && model.isRollover())
				{
					altIcon= (Icon) b.getRolloverSelectedIcon();
					if (altIcon == null)
					{
						altIcon= (Icon) b.getSelectedIcon();
					}
				}
				else
				{
					altIcon= (Icon) b.getSelectedIcon();
				}
			}
			else if (b.isRolloverEnabled() && model.isRollover())
			{
				altIcon= (Icon) b.getRolloverIcon();
			}

			if (altIcon == null)
			{
				altIcon= b.getIcon();
			}

			altIcon.paintIcon(c, g, iconRect.x, iconRect.y);
		}
		else
		{
			Icon icon= null;
			if (model.isEnabled())
			{
				if (model.isSelected())
					icon= getSelectedEnabledIcon();
				else
					icon= getUnselectedEnabledIcon();
			}
			else
			{
				if (model.isSelected())
					icon= getSelectedDisabledIcon();
				else
					icon= getUnselectedDisabledIcon();
			}

			icon.paintIcon(c, g, iconRect.x, iconRect.y);
		}

		// Draw the Text
		if (text != null)
		{
			View v= (View) c.getClientProperty(BasicHTML.propertyKey);
			if (v != null)
			{
				v.paint(g, textRect);
			}
			else
			{
				paintText(g, b, textRect, text);
				if (b.hasFocus()
					&& b.isFocusPainted()
					&& textRect.width > 0
					&& textRect.height > 0)
				{
					paintFocus(g, textRect, size);
				}
			}
		}
	}

	protected void paintFocus(Graphics g, Rectangle textRect, Dimension size)
	{
		// Overridden to prevent superclass from painting anything
		int left=textRect.x-2;
		int top=textRect.y+1;
		int right=textRect.x+textRect.width;
		int bottom=textRect.y+textRect.height-2;
		
		if(focusColor==null)
			focusColor=UIManager.getColor("RadioButton.focusColor");
		
		if(focusColor!=null)
		{	
			g.setColor(focusColor);
			BasicGraphicsUtils.drawDashedRect(g, left, top, right-left+1, bottom-top+1);
		}
	}


	/**	Returns the preferred size for the specified component */
	public Dimension getPreferredSize(JComponent c)
	{
		if (c.getComponentCount() > 0)
		{
			return null;
		}

		AbstractButton b= (AbstractButton) c;

		String text= b.getText();

		Icon buttonIcon= (Icon) b.getIcon();
		if (buttonIcon == null)
		{
			buttonIcon= getDefaultIcon();
		}

		Font font= b.getFont();
		// XXX - getFontMetrics has been deprecated but there isn't a 
		// suitable replacement
		FontMetrics fm= b.getToolkit().getFontMetrics(font);

		prefViewRect.x= prefViewRect.y= 0;
		prefViewRect.width= Short.MAX_VALUE;
		prefViewRect.height= Short.MAX_VALUE;
		prefIconRect.x=
			prefIconRect.y= prefIconRect.width= prefIconRect.height= 0;
		prefTextRect.x=
			prefTextRect.y= prefTextRect.width= prefTextRect.height= 0;

		SwingUtilities.layoutCompoundLabel(
			c,
			fm,
			text,
			buttonIcon,
			b.getVerticalAlignment(),
			b.getHorizontalAlignment(),
			b.getVerticalTextPosition(),
			b.getHorizontalTextPosition(),
			prefViewRect,
			prefIconRect,
			prefTextRect,
			text == null ? 0 : b.getIconTextGap());

		prefTextRect.width+=2;
		
		// find the union of the icon and text rects (from Rectangle.java)
		int x1= Math.min(prefIconRect.x, prefTextRect.x);
		int x2=
			Math.max(
				prefIconRect.x + prefIconRect.width,
				prefTextRect.x + prefTextRect.width);
		int y1= Math.min(prefIconRect.y, prefTextRect.y);
		int y2=
			Math.max(
				prefIconRect.y + prefIconRect.height,
				prefTextRect.y + prefTextRect.height);
		int width= x2 - x1;
		int height= y2 - y1;

		//prefInsets= b.getInsets(prefInsets);
		width += prefInsets.left + prefInsets.right;
		height += prefInsets.top + prefInsets.bottom;
		return new Dimension(width, height);
	}
}
