package com.digitprop.tonic;


import java.awt.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;


/**	The look and feel of AbstractButtons.
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
public class ButtonUI extends MetalButtonUI
{
	/**	The default border for this button */
	private final static BorderUIResource defaultBorder =new BorderUIResource(Borders.getButtonBorder());
	
	/**	Keeps track of the previous opacity settings for buttons */
	private static Hashtable		 	opaqueTable=new Hashtable();

	private Color							highlightColor;
	
	private Color							focusColor;
	
		
	/**	Creates the UI delegate for the specified component. If the
	 * 	component is not an instance of JButton, this call is handled
	 * 	by the MetalButtonUI class to return the default delegate.
	 */
	public static ComponentUI createUI(JComponent c)
	{
		if(c instanceof JButton)
			return new ButtonUI();
		else	
			return MetalButtonUI.createUI(c);
	}
	

	public ButtonUI()
	{
		highlightColor=UIManager.getColor("Button.highlight");
		focusColor=UIManager.getColor("Button.focusBorderColor");
	}
	
	
	/**	Returns the appropriate listener for the specified button */
	protected BasicButtonListener createButtonListener(AbstractButton b) 
	{
		 return new BasicButtonListener(b);
	}
	
	
	/**	Returns the shift offset for the button text */
	protected int getTextShiftOffset() 
	{
		return 1;
	}
		 
	
	/**	Installs the UI delegate for the specified component */	 	
	public void installUI(JComponent c)
	{
		super.installUI(c);
		
		if(c.getBorder()==null || (c.getBorder() instanceof UIResource))
			c.setBorder(defaultBorder);

		if(opaqueTable.get(c)==null)
			opaqueTable.put(c, new Boolean(c.isOpaque()));
						
		c.setOpaque(false);
	}
	

	/**	Uninstalls the UI settings for the specified component */	
	public void uninstallUI(JComponent c)
	{
		super.uninstallUI(c);
		
		Boolean isOpaque=(Boolean)opaqueTable.get(c);
		if(isOpaque!=null)
			c.setOpaque(isOpaque.booleanValue());
	}



	/**	Paints the specified button.
	 * 	
	 * 	@param	g		Graphics context into which to paint
	 * 	@param	c		The button which is to be painted
	 */
	public void paint(Graphics g, JComponent c)
	{
		g.setColor(c.getBackground());
		
		Insets insets=c.getInsets();
		g.fillRect(insets.left, insets.top, c.getWidth()-insets.left-insets.right, c.getHeight()-insets.top-insets.bottom);
				
		if(c instanceof AbstractButton)
		{
			AbstractButton button=(AbstractButton)c;
			
			if(!button.getModel().isPressed() && button.isEnabled() && !button.isSelected())
			{
				Border border=button.getBorder();
				if(border==null || !(border instanceof ToolBarBorder))
				{
					g.setColor(highlightColor);
					g.drawLine(insets.left, insets.top, c.getWidth()-insets.left-insets.right, insets.top);
					g.drawLine(insets.left, insets.top, insets.left, c.getHeight()-insets.top-insets.bottom);
				}
			}
			else if(button.isSelected())
				paintButtonPressed(g, button);
		}
			
		super.paint(g, c);
	}


	/**	Returns the preferred size for the specified component. */
	public  Dimension getPreferredSize(JComponent c)
	{
		Dimension result=super.getPreferredSize(c);
		if(c.getBorder()!=null && (c.getBorder() instanceof ToolBarBorder))
		{
			result.width+=4;
			result.height+=4;
		}
		else
		{ 
			result.width+=4;
			result.height+=6;
			
			if(c instanceof JButton)
			{
				Insets m=((JButton)c).getMargin();
				if(m!=null)
				{	
					result.width+=m.left+m.right;
					result.height+=m.top+m.bottom;
				}
			}
			
//			if(result.width<75)
//			{
//				if(c instanceof JButton)
//				{
//					JButton b=(JButton)c;
//
//					if(b.getText()!=null && b.getText().length()>0 && b.getIcon()==null)
//						result.width=75;
//				}
//			}
		}
			
		return result;
	}


	/**	Paints the specified button in the pressed state.
	 * 	
	 * 	@param	g		Graphics context into which to paint
	 * 	@param	c		The button which is to be painted
	 */	
	protected void paintButtonPressed(Graphics g, AbstractButton b)
	{			
		Border border=b.getBorder();
		if(border==null || !(border instanceof ToolBarBorder))
		{			
			Insets insets=b.getInsets();
		
			g.setColor(highlightColor);
			g.drawLine(insets.left, b.getHeight()-insets.top-insets.bottom, b.getWidth()-insets.left-insets.right, b.getHeight()-insets.top-insets.bottom);
			g.drawLine(b.getWidth()-insets.left-insets.right, insets.top, b.getWidth()-insets.left-insets.right, b.getHeight()-insets.top-insets.bottom);
		}
	}

	
	/**	Paints the focus for the specified button
	 * 	
	 * 	@param	g				Graphics context into which to paint
	 * 	@param	b				Button for which to paint the focus
	 * 	@param	viewRect		Rectangle of the button view
	 * 	@param	textRect		Rectangle of the button text
	 * 	@param	iconRect		Rectangle of the button icon
	 */	
	protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect)
	{
		// Overridden to prevent superclass from painting anything
		if(!b.getModel().isPressed())
		{
			Border border=b.getBorder();
			if(border==null || !(border instanceof ToolBarBorder))
			{			
				Insets insets=b.getInsets();
				
				int left=insets.left+2;
				int top=insets.top+2;
				int right=b.getWidth()-insets.left-insets.right-2;
				int bottom=b.getHeight()-insets.top-insets.bottom-2;
				
				g.setColor(focusColor);
				BasicGraphicsUtils.drawDashedRect(g, left, top, right-left+1, bottom-top+1);
			}
		}		
	}


	/**	Paints the text for the specified component, IF that component
	 * 	is an instance of AbstractButton. Otherwise, this method will do
	 * 	nothing.
	 * 
	 * 	@param	g				Graphics context into which to paint
	 * 	@param	c				The component for which to draw text
	 * 	@param	textRect		The rectangle enclosing the text
	 * 	@param	text			The text to be drawn
	 */	
	protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text)
	{
		if(c instanceof AbstractButton)
			paintText(g, (AbstractButton)c, textRect, text);
	}
	

	/**	Paints the text for the specified button.
	 * 
	 * 	@param	g				Graphics context into which to paint
	 * 	@param	b				The button for which to draw text
	 * 	@param	textRect		The rectangle enclosing the text
	 * 	@param	text			The text to be drawn
	 */	
	protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text)	
	{
		ButtonModel model = b.getModel();
		FontMetrics fm = g.getFontMetrics();
		int mnemIndex = b.getDisplayedMnemonicIndex();

		if(model.isPressed())
		{
			textRect.x+=getTextShiftOffset();
			textRect.y+=getTextShiftOffset();
		}
		
		// Draw the text
		if(model.isEnabled()) 
		{
			// Pain the text normally
			g.setColor(b.getForeground());
			BasicGraphicsUtils.drawStringUnderlineCharAt(g,text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
		}
		else 
		{
			// Paint the text disabled
			g.setColor(Color.WHITE);
			BasicGraphicsUtils.drawStringUnderlineCharAt(g,text,mnemIndex, textRect.x+1, textRect.y + fm.getAscent() +1);
			g.setColor(getDisabledTextColor());
			BasicGraphicsUtils.drawStringUnderlineCharAt(g,text,mnemIndex, textRect.x, textRect.y + fm.getAscent());
		}
	}
	
	
	public static void main(String args[])
	{
		try
				{
					UIManager.setLookAndFeel(new TonicLookAndFeel());
				}
				catch(UnsupportedLookAndFeelException e)
				{
					e.printStackTrace();
				}
				
		JWindow win=new JWindow();
		
		win.getContentPane().setLayout(new FlowLayout());
		win.setBounds(10, 10, 300, 300);
		
		Icon icon=TonicLookAndFeel.getTonicIcon("open.gif");
		JButton button=new JButton(icon);
		button.setEnabled(false);
		win.getContentPane().add(button);
		
		win.setVisible(true);
	}
}
