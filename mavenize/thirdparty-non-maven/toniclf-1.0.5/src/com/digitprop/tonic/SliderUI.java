package com.digitprop.tonic;


import java.awt.*;

import java.beans.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicSliderUI;


/**	UI delegate for JSliders
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
public class SliderUI extends BasicSliderUI
{
	protected static Color thumbColor;
	protected static Color highlightColor;
	protected static Color darkShadowColor;
	protected static int   trackWidth;
	protected static int   tickLength;
	protected static Icon  horizThumbIcon;
	protected static Icon  vertThumbIcon;
	protected final int    TICK_BUFFER=4;
	protected boolean		  filledSlider=false;
	protected final String SLIDER_FILL="JSlider.isFilled";


	/**	Creates an instance */ 
	public SliderUI()
	{
		super(null);
	}


	/**	Creates and returns a UI delegate for the specified component */
	public static ComponentUI createUI(JComponent c)
	{
		return new SliderUI();
	}


	/**	Installs the UI settings for the specified component */
	public void installUI(JComponent c)
	{
		trackWidth=((Integer)UIManager.get("Slider.trackWidth")).intValue();
		tickLength=((Integer)UIManager.get("Slider.majorTickLength")).intValue();
		horizThumbIcon=UIManager.getIcon("Slider.horizontalThumbIcon");
		vertThumbIcon=UIManager.getIcon("Slider.verticalThumbIcon");

		super.installUI(c);

		thumbColor=UIManager.getColor("Slider.thumb");
		highlightColor=UIManager.getColor("Slider.highlight");
		darkShadowColor=UIManager.getColor("Slider.darkShadow");

		scrollListener.setScrollByBlock(false);

		Object sliderFillProp=c.getClientProperty(SLIDER_FILL);
		if(sliderFillProp!=null)
		{
			filledSlider=((Boolean)sliderFillProp).booleanValue();
		}
	}


	protected PropertyChangeListener createPropertyChangeListener(JSlider slider)
	{
		return new MetalPropertyListener();
	}


	/**	Paints the thumb for this JSlider */
	public void paintThumb(Graphics g)
	{
		Rectangle knobBounds=thumbRect;

		g.translate(knobBounds.x, knobBounds.y);

		if(slider.getOrientation()==JSlider.HORIZONTAL)
		{
			horizThumbIcon.paintIcon(slider, g, 0, 0);
		}
		else
		{
			vertThumbIcon.paintIcon(slider, g, 0, 0);
		}

		g.translate(-knobBounds.x, -knobBounds.y);
	}


	/**	Paints the track for this JSlider */
	public void paintTrack(Graphics g)
	{
		Color   trackColor=!slider.isEnabled() ? TonicLookAndFeel.getControlShadow()
															: slider.getForeground();

		boolean leftToRight=TonicUtils.isLeftToRight(slider);

		g.translate(trackRect.x, trackRect.y);

		int trackLeft=0;
		int trackTop=0;
		int trackRight=0;
		int trackBottom=0;

		// Draw the track
		if(slider.getOrientation()==JSlider.HORIZONTAL)
		{
			trackBottom=(trackRect.height-1)-getThumbOverhang();
			trackTop=trackBottom-(getTrackWidth()-1);
			trackRight=trackRect.width-1;
		}
		else
		{
			if(leftToRight)
			{
				trackLeft=(trackRect.width-getThumbOverhang())-getTrackWidth();
				trackRight=(trackRect.width-getThumbOverhang())-1;
			}
			else
			{
				trackLeft=getThumbOverhang();
				trackRight=getThumbOverhang()+getTrackWidth()-1;
			}
			trackBottom=trackRect.height-1;
		}

		if(slider.isEnabled())
		{
			g.setColor(UIManager.getColor("Slider.trackColor"));
			g.fillRect(trackLeft, trackTop, (trackRight-trackLeft)-1,
						  (trackBottom-trackTop)-1);
						  			
			g.setColor(TonicLookAndFeel.getControlDarkShadow());
			g.drawRect(trackLeft, trackTop, (trackRight-trackLeft)-1,
						  (trackBottom-trackTop)-1);
		}
		else
		{
			g.setColor(TonicLookAndFeel.getControlShadow());
			g.drawRect(trackLeft, trackTop, (trackRight-trackLeft)-1,
						  (trackBottom-trackTop)-1);
		}

		// Draw the fill
		if(filledSlider)
		{
			int middleOfThumb=0;
			int fillTop=0;
			int fillLeft=0;
			int fillBottom=0;
			int fillRight=0;

			if(slider.getOrientation()==JSlider.HORIZONTAL)
			{
				middleOfThumb=thumbRect.x+(thumbRect.width/2);
				middleOfThumb-=trackRect.x;   // To compensate for the g.translate()
				fillTop=!slider.isEnabled() ? trackTop : trackTop+1;
				fillBottom=!slider.isEnabled() ? trackBottom-1 : trackBottom-2;

				if(!drawInverted())
				{
					fillLeft=!slider.isEnabled() ? trackLeft : trackLeft+1;
					fillRight=middleOfThumb;
				}
				else
				{
					fillLeft=middleOfThumb;
					fillRight=!slider.isEnabled() ? trackRight-1 : trackRight-2;
				}
			}
			else
			{
				middleOfThumb=thumbRect.y+(thumbRect.height/2);
				middleOfThumb-=trackRect.y;   // To compensate for the g.translate()
				fillLeft=!slider.isEnabled() ? trackLeft : trackLeft+1;
				fillRight=!slider.isEnabled() ? trackRight-1 : trackRight-2;

				if(!drawInverted())
				{
					fillTop=middleOfThumb;
					fillBottom=!slider.isEnabled() ? trackBottom-1 : trackBottom-2;
				}
				else
				{
					fillTop=!slider.isEnabled() ? trackTop : trackTop+1;
					fillBottom=middleOfThumb;
				}
			}

			if(slider.isEnabled())
			{
				g.setColor(slider.getBackground());
				g.drawLine(fillLeft, fillTop, fillRight, fillTop);
				g.drawLine(fillLeft, fillTop, fillLeft, fillBottom);

				g.setColor(TonicLookAndFeel.getControlShadow());
				g.fillRect(fillLeft+1, fillTop+1, fillRight-fillLeft,
							  fillBottom-fillTop);
			}
			else
			{
				g.setColor(TonicLookAndFeel.getControlShadow());
				g.fillRect(fillLeft, fillTop, fillRight-fillLeft,
							  trackBottom-trackTop);
			}
		}

		g.translate(-trackRect.x, -trackRect.y);
	}


	/**	Paints the focus for this JSlider */
	public void paintFocus(Graphics g) 
	{
	}


	/**	returns the size of the thumb for the associated JSlider */
	protected Dimension getThumbSize()
	{
		Dimension size=new Dimension();

		if(slider.getOrientation()==JSlider.VERTICAL)
		{
			size.width=vertThumbIcon.getIconWidth();
			size.height=vertThumbIcon.getIconHeight();
		}
		else
		{
			size.width=horizThumbIcon.getIconWidth();
			size.height=horizThumbIcon.getIconHeight();
		}

		return size;
	}


	/**	Gets the height of the tick area for horizontal sliders and the width of 
	 * 	the tick area for vertical sliders. BasicSliderUI uses the returned value 
	 * 	to determine the tick area rectangle.
	 */
	public int getTickLength()
	{
		return slider.getOrientation()==JSlider.HORIZONTAL
				 ? tickLength+TICK_BUFFER+1 : tickLength+TICK_BUFFER+3;
	}


	/**	Returns the shorter dimension of the track. */
	protected int getTrackWidth()
	{
		// This strange calculation is here to keep the
		// track in proportion to the thumb.
		final double kIdealTrackWidth=7.0;
		final double kIdealThumbHeight=16.0;
		final double kWidthScalar=kIdealTrackWidth/kIdealThumbHeight;

		if(slider.getOrientation()==JSlider.HORIZONTAL)
		{
			return (int)(kWidthScalar*thumbRect.height);
		}
		else
		{
			return (int)(kWidthScalar*thumbRect.width);
		}
	}


	/**	Returns the longer dimension of the slide bar.  (The slide bar is 
	 * 	only the part that runs directly under the thumb) 
	 */
	protected int getTrackLength()
	{
		if(slider.getOrientation()==JSlider.HORIZONTAL)
		{
			return trackRect.width;
		}

		return trackRect.height;
	}


	/**	Returns the amount that the thumb goes past the slide bar. */
	protected int getThumbOverhang()
	{
		return (int)(getThumbSize().getHeight()-getTrackWidth())/2;
	}


	protected void scrollDueToClickInTrack(int dir)
	{
		scrollByUnit(dir);
	}


	protected void paintMinorTickForHorizSlider(Graphics g,
															  Rectangle tickBounds, int x)
	{
		g.setColor(slider.isEnabled() ? slider.getForeground()
												: TonicLookAndFeel.getControlShadow());
		g.drawLine(x, TICK_BUFFER, x, TICK_BUFFER+(tickLength/2));
	}


	protected void paintMajorTickForHorizSlider(Graphics g,
															  Rectangle tickBounds, int x)
	{
		g.setColor(slider.isEnabled() ? slider.getForeground()
												: TonicLookAndFeel.getControlShadow());
		g.drawLine(x, TICK_BUFFER, x, TICK_BUFFER+(tickLength-1));
	}


	protected void paintMinorTickForVertSlider(Graphics g, Rectangle tickBounds,
															 int y)
	{
		g.setColor(slider.isEnabled() ? slider.getForeground()
												: TonicLookAndFeel.getControlShadow());

		if(TonicUtils.isLeftToRight(slider))
		{
			g.drawLine(TICK_BUFFER, y, TICK_BUFFER+(tickLength/2), y);
		}
		else
		{
			g.drawLine(0, y, tickLength/2, y);
		}
	}


	protected void paintMajorTickForVertSlider(Graphics g, Rectangle tickBounds,
															 int y)
	{
		g.setColor(slider.isEnabled() ? slider.getForeground()
												: TonicLookAndFeel.getControlShadow());

		if(TonicUtils.isLeftToRight(slider))
		{
			g.drawLine(TICK_BUFFER, y, TICK_BUFFER+tickLength, y);
		}
		else
		{
			g.drawLine(0, y, tickLength, y);
		}
	}

	protected class MetalPropertyListener
		extends BasicSliderUI.PropertyChangeHandler
	{
		public void propertyChange(PropertyChangeEvent e)
		{   // listen for slider fill
			super.propertyChange(e);

			String name=e.getPropertyName();
			if(name.equals(SLIDER_FILL))
			{
				if(e.getNewValue()!=null)
				{
					filledSlider=((Boolean)e.getNewValue()).booleanValue();
				}
				else
				{
					filledSlider=false;
				}
			}
		}
	}
}
