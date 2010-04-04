package com.digitprop.tonic;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import javax.swing.plaf.metal.*;


/**	The title pane for JInternalFrames.
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
public class InternalFrameTitlePane extends MetalInternalFrameTitlePane
{
	/**	The border for this panel */
	private OptionalMatteBorder handyEmptyBorder;

	/**	If true, a border is painted around the frame title buttons */
	private boolean drawButtonBorders=false;
	

	/**	Creates an instance for the specified JInternalFrame */
	public InternalFrameTitlePane(JInternalFrame frame)
	{
		super(frame);
		
		addMouseListener(new MouseHandler());
	}


	/**	Paints this component. */
	public void paintComponent(Graphics g)
	{
		Color gradStartColor= null;
		Color gradEndColor= null;
		Color fontColor= null;

		// Draw gradient	
		if (frame.isSelected())
		{
			gradStartColor=
				UIManager.getColor("InternalFrame.activeTitleBackground");
			gradEndColor=
				UIManager.getColor("InternalFrame.activeTitleGradientColor");
			fontColor= UIManager.getColor("InternalFrame.activeTitleForeground");
		}
		else
		{
			gradStartColor=
				UIManager.getColor("InternalFrame.inactiveTitleBackground");
			gradEndColor=
				UIManager.getColor("InternalFrame.inactiveTitleGradientColor");
			fontColor= UIManager.getColor("InternalFrame.inactiveTitleForeground");
		}

		g.setColor(gradEndColor);
		g.fillRect(0, 0, getWidth(), getHeight());

		drawGradient(
			g,
			0,
			Math.max(60, getWidth() - 60),
			0,
			getHeight(),
			gradStartColor,
			gradEndColor);

		// Draw icon and text
		if (!isPalette)
		{
			Icon frameIcon=frame.getFrameIcon();

			frameIcon.paintIcon(
				this,
				g,
				4,
				getHeight() / 2 - frameIcon.getIconHeight() / 2);

			g.setFont(UIManager.getFont("InternalFrame.font"));
			g.setColor(fontColor);
			FontMetrics fm= g.getFontMetrics();
			int yOffset= ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
			g.drawString(frame.getTitle(), 8 + frameIcon.getIconWidth(), yOffset);
		}

		// Draw border
		g.setColor(UIManager.getColor("Button.borderColor"));
		g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
	}


	/**	Draws a horizontal gradient.
	 * 
	 * 	@param	g			Graphics context into which to draw
	 * 	@param	x1			The left edge of the gradient
	 * 	@param	x2			The right edge of the gradient
	 * 	@param	x			The top edge of the gradient
	 * 	@param	w			The height of the gradient
	 * 	@param	c1			The color of the left edge of the gradient
	 * 	@param	c2			The color of the right edge of the gradient
	 */
	private void drawGradient(Graphics g, int x1, int x2, int y, int height, Color c1, Color c2)
	{
		int step= 1;
		if (x2 - x1 + 1 > 256)
			step= (x2 - x1 + 1) / 256;

		for (int i= x1; i < x2; i += step)
		{
			Color c= blendColors(c1, c2, (double) (i - x1) / (x2 - x1));
			g.setColor(c);
			g.fillRect(i, y, step, height);
		}
	}
	

	/**	Calculates a color blended from the specified two colors.
	 * 	
	 * 	@param	c1			First color for blending
	 * 	@param	c2			Second color for blending
	 * 	@param	fraction	The ratio of second to first color. If this is 0.0, 
	 * 							only the first color will be used, if it is 1.0,
	 * 							only the second color will be used.
	 * 
	 * 	@return				A color blended from c1 and c2
	 */
	private Color blendColors(Color c1, Color c2, double fraction)
	{
		if (fraction < 0.0d)
			fraction= 0.0d;
		else if (fraction > 1.0d)
			fraction= 1.0d;

		int r= (int) (c1.getRed() * (1.0 - fraction) + c2.getRed() * fraction);
		int g=
			(int) (c1.getGreen() * (1.0 - fraction) + c2.getGreen() * fraction);
		int b= (int) (c1.getBlue() * (1.0 - fraction) + c2.getBlue() * fraction);

		return new Color(r, g, b);
	}


	/**	Creates a layout for this panel and returns it */
	protected LayoutManager createLayout() 
	{
		 return new TitlePaneLayout();
	}
	
	
	/**	Creates the frame title buttons for minimizing, maximizing, and closing. */
	protected void createButtons()
	{
		handyEmptyBorder=new OptionalMatteBorder(UIManager.getColor("Button.borderColor"), 2);
				
		iconButton= new JButton();
		iconButton.setFocusPainted(false);
		iconButton.setFocusable(false);
		iconButton.setOpaque(true);
		iconButton.addActionListener(iconifyAction);
		iconButton.setBorder(handyEmptyBorder);

		maxButton= new JButton();
		maxButton.setFocusPainted(false);
		maxButton.setFocusable(false);
		maxButton.setOpaque(true);		
		maxButton.addActionListener(maximizeAction);
		maxButton.setBorder(handyEmptyBorder);
		
		closeButton= new JButton();
		closeButton.setFocusPainted(false);
		closeButton.setFocusable(false);
		closeButton.setOpaque(true);		
		closeButton.addActionListener(closeAction);
		closeButton.setBorder(handyEmptyBorder);

		setButtonIcons();
	}
	

	/**	Specialized class of the JButton which cannot have the focus */
	private class NoFocusButton extends JButton
	{
		/**	Creates an instance */
		public NoFocusButton()
		{
			setFocusPainted(false);
		}


		/**	Returns true if this instance can have the focus */
		public boolean isFocusTraversable()
		{
			return false;
		}

		
		/**	Returns the preferred size of this button */
		public Dimension getPreferredSize()
		{
			Dimension result=super.getPreferredSize();
			result.width+=16;
			result.height+=16;
			
			return result;
		}
		
		
		/**	Is called when this component is to request the focus */
		public void requestFocus()
		{
		}


		/**	Returns true if this component is opaque */
		public boolean isOpaque()
		{
			return false;
		}
	}
	
	
	/**	Layout for the InternalFrameTitlePane */
	class TitlePaneLayout implements LayoutManager
	{
		/**	Adds a component which is to be layouted */
		public void addLayoutComponent(String name, Component c)
		{
			// Does nothing
		}
		
		
		/**	Removes a component which is to be layouted */
		public void removeLayoutComponent(Component c)
		{
			// Does nothing
		}
		
		
		/**	Returns the preferred size of this layout for the specified component */
		public Dimension preferredLayoutSize(Container c)
		{
			return minimumLayoutSize(c);
		}


		/**	Returns the minimum size of this layout for the specified component */
		public Dimension minimumLayoutSize(Container c)
		{
			// Compute width.
			int width= 30;
			if (frame.isClosable())
			{
				width += 21;
			}
			if (frame.isMaximizable())
			{
				width += 16 + (frame.isClosable() ? 10 : 4);
			}
			if (frame.isIconifiable())
			{
				width += 16
					+ (frame.isMaximizable() ? 2 : (frame.isClosable() ? 10 : 4));
			}
			FontMetrics fm= getFontMetrics(getFont());
			String frameTitle= frame.getTitle();
			int title_w= frameTitle != null ? fm.stringWidth(frameTitle) : 0;
			int title_length= frameTitle != null ? frameTitle.length() : 0;

			if (title_length > 2)
			{
				int subtitle_w=
					fm.stringWidth(frame.getTitle().substring(0, 2) + "...");
				width += (title_w < subtitle_w) ? title_w : subtitle_w;
			}
			else
			{
				width += title_w;
			}

			// Compute height.
			int height= 0;
			if (isPalette)
			{
				height= paletteTitleHeight;
			}
			else
			{
				int fontHeight= fm.getHeight();
				fontHeight += 7;
				Icon icon= frame.getFrameIcon();
				int iconHeight= 0;
				if (icon != null)
				{
					// SystemMenuBar forces the icon to be 16x16 or less.
					iconHeight= Math.min(icon.getIconHeight(), 16);
				}
				iconHeight += 5;
				height= Math.max(fontHeight, iconHeight);
			}

			return new Dimension(width, height);
		}

		
		/**	Does a layout for the specified container */
		public void layoutContainer(Container c)
		{
			boolean leftToRight= TonicUtils.isLeftToRight(frame);

			int w= getWidth();
			int x= leftToRight ? w : 0;
			int spacing;

			// assumes all buttons have the same dimensions
			// these dimensions include the borders
			int buttonHeight= closeButton.getIcon().getIconHeight()+5;
			int buttonWidth= closeButton.getIcon().getIconWidth()+5;
			int y= getHeight()/2-buttonHeight/2;
			
			if (frame.isClosable())
			{
				if (isPalette)
				{
					spacing= 3;
					x += leftToRight ? -spacing - (buttonWidth + 2) : spacing;
					closeButton.setBounds(x, y, buttonWidth + 2, getHeight() - 4);
					if (!leftToRight)
						x += (buttonWidth + 2);
				}
				else
				{
					spacing= 4;
					x += leftToRight ? -spacing - buttonWidth : spacing;
					closeButton.setBounds(x, y, buttonWidth, buttonHeight);
					if (!leftToRight)
						x += buttonWidth;
				}
			}

			if (frame.isMaximizable() && !isPalette)
			{
				spacing= frame.isClosable() ? 4 : 4;
				x += leftToRight ? -spacing - buttonWidth : spacing;
				maxButton.setBounds(x, y, buttonWidth, buttonHeight);
				if (!leftToRight)
					x += buttonWidth;
			}

			if (frame.isIconifiable() && !isPalette)
			{
				spacing= frame.isMaximizable() ? 4 : (frame.isClosable() ? 4 : 4);
				x += leftToRight ? -spacing - buttonWidth : spacing;
				iconButton.setBounds(x, y, buttonWidth, buttonHeight);
				if (!leftToRight)
					x += buttonWidth;
			}
		}
	}
		

	/**	Handles mouse events for this InternalFrameTitlePane. This class
	 * 	decides about whether the border for the frame title buttons
	 * 	is to be painted or not (depending on whether the mouse is inside
	 * 	the frame title pane or not).
	 */	
	class MouseHandler extends MouseAdapter
	{
		public void mouseEntered(MouseEvent e)
		{
			handyEmptyBorder.setDrawBorder(true);
			repaint();
		}
		
		
		public void mouseExited(MouseEvent e)
		{
			if(!iconButton.getBounds().contains(e.getPoint()) &&
				!maxButton.getBounds().contains(e.getPoint()) &&
				!closeButton.getBounds().contains(e.getPoint()))
			{
				handyEmptyBorder.setDrawBorder(false);
				repaint();
			}
		}
	}
}
