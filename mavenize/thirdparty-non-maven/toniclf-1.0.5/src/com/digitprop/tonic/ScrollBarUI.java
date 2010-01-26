package com.digitprop.tonic;


import java.awt.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicScrollBarUI;


/**	UI delegate for JScrollBars.
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
public class ScrollBarUI extends BasicScrollBarUI
{
	private static Color shadowColor;
	private static Color highlightColor;
	private static Color darkShadowColor;
	private static Color thumbColor;
	private static Color thumbShadow;
	private static Color thumbHighlightColor;
	private static Color thumbStripeColor;

	protected TonicBumps bumps;

	protected ScrollButton increaseButton;
	protected ScrollButton decreaseButton;

	protected int scrollBarWidth;

	public static final String FREE_STANDING_PROP= "JScrollBar.isFreeStanding";
	protected boolean isFreeStanding= true;


	/*
	 * Method for scrolling by a block increment.
	 * Added for mouse wheel scrolling support, RFE 4202656.
	 */
	static void scrollTonicByBlock(JScrollBar scrollbar, int direction) {
		 // This method is called from BasicScrollPaneUI to implement wheel
		 // scrolling, and also from scrollByBlock().
		int oldValue = scrollbar.getValue();
		int blockIncrement = scrollbar.getBlockIncrement(direction);
		int delta = blockIncrement * ((direction > 0) ? +1 : -1);

		scrollbar.setValue(oldValue + delta);			
	}

   
	/*
	 * Method for scrolling by a unit increment.
	 * Added for mouse wheel scrolling support, RFE 4202656.
	 */
	static void scrollTonicByUnits(JScrollBar scrollbar, int direction,
											 int units) {
		 // This method is called from BasicScrollPaneUI to implement wheel
		 // scrolling, as well as from scrollByUnit().
		 int delta = units;

		 if (direction > 0) {
			  delta *= scrollbar.getUnitIncrement(direction);
		 }
		 else {
			  delta *= -scrollbar.getUnitIncrement(direction);
		 }

		 int oldValue = scrollbar.getValue();
		 int newValue = oldValue + delta;

		 // Check for overflow.
		 if (delta > 0 && newValue < oldValue) {
			  newValue = scrollbar.getMaximum();
		 }
		 else if (delta < 0 && newValue > oldValue) {
			  newValue = scrollbar.getMinimum();
		 }
		 scrollbar.setValue(newValue);
	}
	
	
	/**	Creates and returns the UI delegate for the specified component */
	public static ComponentUI createUI(JComponent c)
	{
		return new ScrollBarUI();
	}


	/**	Installs the UI defaults for the associated JScrollBar */
	protected void installDefaults()
	{
		scrollBarWidth= ((Integer) (UIManager.get("ScrollBar.width"))).intValue();
		super.installDefaults();
		bumps= new TonicBumps(10, 10, thumbHighlightColor, thumbShadow, thumbColor);
	}


	/**	Installs the UI settings for the specified component */
	public void installUI(JComponent c)
	{
		super.installUI(c);
		
		if((c instanceof JScrollBar) && (c.getBorder()==null || c.getBorder() instanceof BorderUIResource))
			c.setBorder(new IntelligentLineBorder(UIManager.getColor("Button.borderColor"), true));
	}


	/**	Installs the listeners with the associated JScrollBar */
	protected void installListeners()
	{
		super.installListeners();
		((ScrollBarListener) propertyChangeListener).handlePropertyChange(
			scrollbar.getClientProperty(FREE_STANDING_PROP));
	}

	
	/**	Creates and returns a property change listener for the associated JScrollBar */
	protected PropertyChangeListener createPropertyChangeListener()
	{
		return new ScrollBarListener();
	}

	
	/**	Configures the scroll bar colors */
	protected void configureScrollBarColors()
	{
		super.configureScrollBarColors();
		shadowColor= UIManager.getColor("ScrollBar.shadow");
		highlightColor= UIManager.getColor("ScrollBar.highlight");
		darkShadowColor= UIManager.getColor("ScrollBar.darkShadow");
		thumbColor= UIManager.getColor("ScrollBar.thumb");
		thumbShadow= UIManager.getColor("ScrollBar.thumbShadow");
		thumbHighlightColor= UIManager.getColor("ScrollBar.thumbHighlight");
		thumbStripeColor=UIManager.getColor("ScrollBar.thumbStripes");
	}


	/**	Returns the preferred size for the specified component */
	public Dimension getPreferredSize(JComponent c)
	{
		if (scrollbar.getOrientation() == JScrollBar.VERTICAL)
		{
			return new Dimension(scrollBarWidth, scrollBarWidth * 3 + 10);
		}
		else // Horizontal
			{
			return new Dimension(scrollBarWidth * 3 + 10, scrollBarWidth);
		}

	}


	/** Returns the view that represents the decrease view. */
	protected JButton createDecreaseButton(int orientation)
	{
		decreaseButton=
			new ScrollButton(orientation, scrollBarWidth, isFreeStanding);
		return decreaseButton;
	}

	
	/** Returns the view that represents the increase view. */
	protected JButton createIncreaseButton(int orientation)
	{
		increaseButton=
			new ScrollButton(orientation, scrollBarWidth, isFreeStanding);
		return increaseButton;
	}


	/**	Paints the track area.
	 * 
	 * 	@param	g					The graphics context into which to paint
	 * 	@param	c					The component for which to draw the track area
	 * 	@param	trackBounds		The bounds of the track area to be painted
	 */
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
	{
		g.setColor(c.getBackground());
		g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
		// Check all four sides to see whether the scrollbar is adjacent
		// to the edge of the parent container
		Container container=c.getParent();
		
		boolean leftTouch=c.getX()==0;
		boolean rightTouch=c.getX()+c.getWidth()+1==container.getWidth();
		boolean topTouch=c.getY()==0;
		boolean bottomTouch=c.getY()+c.getHeight()+1==container.getHeight();
		
		g.setColor(UIManager.getColor("Button.borderColor"));
		if(scrollbar.getOrientation()==JScrollBar.VERTICAL)
		{
			// Separate buttons from track
			g.drawLine(trackBounds.x, trackBounds.y, trackBounds.x+trackBounds.width, trackBounds.y);
			g.drawLine(trackBounds.x, trackBounds.y+trackBounds.height-1, trackBounds.x+trackBounds.width, trackBounds.y+trackBounds.height-1);
		}
		else
		{
			// Separate buttons from track
			g.drawLine(trackBounds.x, trackBounds.y, trackBounds.x, trackBounds.y+trackBounds.height);
			g.drawLine(trackBounds.x+trackBounds.width-1, trackBounds.y, trackBounds.x+trackBounds.width-1, trackBounds.y+trackBounds.height);	
		}
	}


	/**	Paints the thumb area.
	 * 
	 * 	@param	g				The graphics context into which to paint
	 * 	@param	c				The component for which to draw the area
	 * 	@param	thumbBounds	The rectangle of the area
	 */
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
	{
		if(c.isEnabled())
		{
			boolean isVertical=(scrollbar.getOrientation()==JScrollBar.VERTICAL);
			int dx=0;
			int dy=0;
			
			if(isVertical)
			{
				g.setColor(thumbColor);
				g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
				
				g.setColor(UIManager.getColor("Button.borderColor"));
				g.drawLine(thumbBounds.x, thumbBounds.y, thumbBounds.x+thumbBounds.width, thumbBounds.y);
				g.drawLine(thumbBounds.x, thumbBounds.y+thumbBounds.height-1, thumbBounds.x+thumbBounds.width, thumbBounds.y+thumbBounds.height-1);
				dx=1;
			}	
			else
			{
				g.setColor(thumbColor);
				g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
				
				g.setColor(UIManager.getColor("Button.borderColor"));
				g.drawLine(thumbBounds.x, thumbBounds.y, thumbBounds.x, thumbBounds.y+thumbBounds.height);
				g.drawLine(thumbBounds.x+thumbBounds.width-1, thumbBounds.y, thumbBounds.x+thumbBounds.width-1, thumbBounds.y+thumbBounds.height);				
				dy=1;
			}
			
			if(isVertical)
			{
				if(thumbBounds.height>=60)
				{
					g.setColor(thumbStripeColor);
					paintStripes(g, true, thumbBounds.x+2, thumbBounds.y+thumbBounds.height/2-15, thumbBounds.width-5, 30);
				}
			}
			else
			{
				if(thumbBounds.width>=60)
				{
					g.setColor(thumbStripeColor);
					paintStripes(g, false, thumbBounds.x+thumbBounds.width/2-15, thumbBounds.y+2, 30, thumbBounds.height-5);
				}
			}			
		}
	}


	private void paintStripes(Graphics g, boolean isVertical, int x, int y, int w, int h)
	{
		for(int i=(isVertical ? y : x); i<=(isVertical ? y+h : x+w); i+=2)
		{
			if(isVertical)
				g.drawLine(x, i, x+w, i);
			else
				g.drawLine(i, y, i, y+h);			
		}
	}
	
	
	/**	Returns the minimum thumb size */
	protected Dimension getMinimumThumbSize()
	{
		return new Dimension(scrollBarWidth, scrollBarWidth);
	}
	
	
	/**	This is overridden only to increase the invalid area.  This
	 * 	ensures that the "Shadow" below the thumb is invalidated
	 */
	protected void setThumbBounds(int x, int y, int width, int height)
	{
		/* If the thumbs bounds haven't changed, we're done.
		 */
		if ((thumbRect.x == x)
			&& (thumbRect.y == y)
			&& (thumbRect.width == width)
			&& (thumbRect.height == height))
		{
			return;
		}

		/* Update thumbRect, and repaint the union of x,y,w,h and 
		 * the old thumbRect.
		 */
		int minX= Math.min(x, thumbRect.x);
		int minY= Math.min(y, thumbRect.y);
		int maxX= Math.max(x + width, thumbRect.x + thumbRect.width);
		int maxY= Math.max(y + height, thumbRect.y + thumbRect.height);

		thumbRect.setBounds(x, y, width, height);
		scrollbar.repaint(minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
	}


	/**	Listens to ScrollBar events */
	class ScrollBarListener extends BasicScrollBarUI.PropertyChangeHandler
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String name= e.getPropertyName();
			if (name.equals(FREE_STANDING_PROP))
			{
				handlePropertyChange(e.getNewValue());
			}
			else
			{
				super.propertyChange(e);
			}
		}

		public void handlePropertyChange(Object newValue)
		{
			if (newValue != null)
			{
				boolean temp= ((Boolean) newValue).booleanValue();
				boolean becameFlush= temp == false && isFreeStanding == true;
				boolean becameNormal= temp == true && isFreeStanding == false;

				isFreeStanding= temp;

				if (becameFlush)
				{
					toFlush();
				}
				else if (becameNormal)
				{
					toFreeStanding();
				}
			}
			else
			{

				if (!isFreeStanding)
				{
					isFreeStanding= true;
					toFreeStanding();
				}

				// This commented-out block is used for testing flush scrollbars.
				/*
							  if ( isFreeStanding ) {
							 isFreeStanding = false;
							 toFlush();
						}
				*/
			}

			if (increaseButton != null)
			{
				increaseButton.setFreeStanding(isFreeStanding);
			}
			if (decreaseButton != null)
			{
				decreaseButton.setFreeStanding(isFreeStanding);
			}
		}

		protected void toFlush()
		{
			scrollBarWidth -= 2;
		}

		protected void toFreeStanding()
		{
			scrollBarWidth += 2;
		}
	} // end class ScrollBarListener
}
