package com.digitprop.tonic;


import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;


/**	Divider for JSplitPanes. 
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
class SplitPaneDivider extends BasicSplitPaneDivider
{
	private int   inset=2;
	private Color controlColor=TonicLookAndFeel.getControl();
	private Color primaryControlColor=TonicLookAndFeel.getPrimaryControl();
	private BasicSplitPaneUI ui;
	
	private int oneTouchSize;
	
	private int standardSize;
	
	
	public SplitPaneDivider(BasicSplitPaneUI ui)
	{
		super(ui);
		
		this.ui=ui;
		standardSize=UIManager.getInt("SplitPane.dividerSize");
		oneTouchSize=UIManager.getInt("SplitPane.oneTouchDividerSize");
		
		setLayout(new MetalDividerLayout());
	}

	
	protected void oneTouchExpandableChanged()
	{
		super.oneTouchExpandableChanged();
		
		if(ui.getSplitPane()!=null && ui.getSplitPane().isOneTouchExpandable())
			setDividerSize(oneTouchSize);
		else
			setDividerSize(standardSize);
	}
	
	
	public void paint(Graphics g)
	{
		g.setColor(UIManager.getColor("SplitPane.background"));
		g.fillRect(0, 0, getWidth(), getHeight());
		
//		if(splitPane.hasFocus())
//		{
//			g.setColor(primaryControlColor);
//		}
//		else
//		{
//			g.setColor(controlColor);
//		}
//		Rectangle clip=g.getClipBounds();
//		Insets    insets=getInsets();
//		g.fillRect(clip.x, clip.y, clip.width, clip.height);
//		Dimension size=getSize();
//		size.width-=inset*2;
//		size.height-=inset*2;
//		int drawX=inset;
//		int drawY=inset;
//		if(insets!=null)
//		{
//			size.width-=(insets.left+insets.right);
//			size.height-=(insets.top+insets.bottom);
//			drawX+=insets.left;
//			drawY+=insets.top;
//		}
//		super.paint(g);
		paintComponents(g);
	}


	/**
	 * Creates and return an instance of JButton that can be used to
	 * collapse the left component in the metal split pane.
	 */
	protected JButton createLeftOneTouchButton()
	{
		JButton b=new JButton()
		{
			// Sprite buffer for the arrow image of the left button
			int buffer[][]=
								{
									{ 0, 0, 0, 2, 2, 0, 0, 0, 0 },
									{ 0, 0, 2, 1, 1, 1, 0, 0, 0 },
									{ 0, 2, 1, 1, 1, 1, 1, 0, 0 },
									{ 2, 1, 1, 1, 1, 1, 1, 1, 0 },
									{ 0, 3, 3, 3, 3, 3, 3, 3, 3 }
								};

			public void setBorder(Border b) {}


			public void paint(Graphics g)
			{
				JSplitPane splitPane=getSplitPaneFromSuper();
				if(splitPane!=null)
				{
					int oneTouchSize=getOneTouchSizeFromSuper();
					int orientation=getOrientationFromSuper();
					int blockSize=Math.min(getDividerSize(), oneTouchSize);

					// Initialize the color array
					Color colors[]={ this.getBackground(), TonicLookAndFeel
																		.getPrimaryControlDarkShadow(), TonicLookAndFeel
																												  .getPrimaryControlInfo(), TonicLookAndFeel
																																					 .getPrimaryControlHighlight() };

					// Fill the background first ...
					g.setColor(this.getBackground());
					g.fillRect(0, 0, this.getWidth(), this.getHeight());

					// ... then draw the arrow.
					if(getModel().isPressed())
					{
						// Adjust color mapping for pressed button state
						colors[1]=colors[2];
					}
					if(orientation==JSplitPane.VERTICAL_SPLIT)
					{
						// Draw the image for a vertical split
						for(int i=1; i<=buffer[0].length; i++)
						{
							for(int j=1; j<blockSize; j++)
							{
								if(buffer[j-1][i-1]==0)
								{
									continue;
								}
								else
								{
									g.setColor(colors[buffer[j-1][i-1]]);
								}
								g.drawLine(i, j, i, j);
							}
						}
					}
					else
					{
						// Draw the image for a horizontal split
						// by simply swaping the i and j axis.
						// Except the drawLine() call this code is
						// identical to the code block above. This was done
						// in order to remove the additional orientation
						// check for each pixel.
						for(int i=1; i<=buffer[0].length; i++)
						{
							for(int j=1; j<blockSize; j++)
							{
								if(buffer[j-1][i-1]==0)
								{
									// Nothing needs
									// to be drawn
									continue;
								}
								else
								{
									// Set the color from the
									// color map
									g.setColor(colors[buffer[j-1][i-1]]);
								}

								// Draw a pixel
								g.drawLine(j, i, j, i);
							}
						}
					}
				}
			}


			// Don't want the button to participate in focus traversable.
			public boolean isFocusTraversable()
			{
				return false;
			}
		};
		b.setRequestFocusEnabled(false);
		b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		b.setFocusPainted(false);
		b.setBorderPainted(false);

		return b;
	}


	/**
	 * Creates and return an instance of JButton that can be used to
	 * collapse the right component in the metal split pane.
	 */
	protected JButton createRightOneTouchButton()
	{
		JButton b=new JButton()
		{
			// Sprite buffer for the arrow image of the right button
			int buffer[][]=
								{
									{ 2, 2, 2, 2, 2, 2, 2, 2 },
									{ 0, 1, 1, 1, 1, 1, 1, 3 },
									{ 0, 0, 1, 1, 1, 1, 3, 0 },
									{ 0, 0, 0, 1, 1, 3, 0, 0 },
									{ 0, 0, 0, 0, 3, 0, 0, 0 }
								};

			public void setBorder(Border border) {}


			public void paint(Graphics g)
			{
				JSplitPane splitPane=getSplitPaneFromSuper();
				if(splitPane!=null)
				{
					int oneTouchSize=getOneTouchSizeFromSuper();
					int orientation=getOrientationFromSuper();
					int blockSize=Math.min(getDividerSize(), oneTouchSize);

					// Initialize the color array
					Color colors[]={ this.getBackground(), TonicLookAndFeel
																		.getPrimaryControlDarkShadow(), TonicLookAndFeel
																												  .getPrimaryControlInfo(), TonicLookAndFeel
																																					 .getPrimaryControlHighlight() };

					// Fill the background first ...
					g.setColor(this.getBackground());
					g.fillRect(0, 0, this.getWidth(), this.getHeight());

					// ... then draw the arrow.
					if(getModel().isPressed())
					{
						// Adjust color mapping for pressed button state
						colors[1]=colors[2];
					}
					if(orientation==JSplitPane.VERTICAL_SPLIT)
					{
						// Draw the image for a vertical split
						for(int i=1; i<=buffer[0].length; i++)
						{
							for(int j=1; j<blockSize; j++)
							{
								if(buffer[j-1][i-1]==0)
								{
									continue;
								}
								else
								{
									g.setColor(colors[buffer[j-1][i-1]]);
								}
								g.drawLine(i, j, i, j);
							}
						}
					}
					else
					{
						// Draw the image for a horizontal split
						// by simply swaping the i and j axis.
						// Except the drawLine() call this code is
						// identical to the code block above. This was done
						// in order to remove the additional orientation
						// check for each pixel.
						for(int i=1; i<=buffer[0].length; i++)
						{
							for(int j=1; j<blockSize; j++)
							{
								if(buffer[j-1][i-1]==0)
								{
									// Nothing needs
									// to be drawn
									continue;
								}
								else
								{
									// Set the color from the
									// color map
									g.setColor(colors[buffer[j-1][i-1]]);
								}

								// Draw a pixel
								g.drawLine(j, i, j, i);
							}
						}
					}
				}
			}


			// Don't want the button to participate in focus traversable.
			public boolean isFocusTraversable()
			{
				return false;
			}
		};
		b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setRequestFocusEnabled(false);

		return b;
	}


	/*
	 * The following methods only exist in order to be able to access protected
	 * members in the superclass, because these are otherwise not available
	 * in any inner class.
	 */
	int getOneTouchSizeFromSuper()
	{
		return super.ONE_TOUCH_SIZE;
	}


	int getOneTouchOffsetFromSuper()
	{
		return super.ONE_TOUCH_OFFSET;
	}


	int getOrientationFromSuper()
	{
		return super.orientation;
	}


	JSplitPane getSplitPaneFromSuper()
	{
		return super.splitPane;
	}


	JButton getLeftButtonFromSuper()
	{
		return super.leftButton;
	}


	JButton getRightButtonFromSuper()
	{
		return super.rightButton;
	}

	/**
	 * Used to layout a MetalSplitPaneDivider. Layout for the divider
	 * involves appropriately moving the left/right buttons around.
	 * <p>
	 * This inner class is marked &quot;public&quot; due to a compiler bug.
	 * This class should be treated as a &quot;protected&quot; inner class.
	 * Instantiate it only within subclasses of MetalSplitPaneDivider.
	 */
	public class MetalDividerLayout implements LayoutManager
	{
		public void layoutContainer(Container c)
		{
			JButton    leftButton=getLeftButtonFromSuper();
			JButton    rightButton=getRightButtonFromSuper();
			JSplitPane splitPane=getSplitPaneFromSuper();
			int		  orientation=getOrientationFromSuper();
			int		  oneTouchSize=getOneTouchSizeFromSuper();
			int		  oneTouchOffset=getOneTouchOffsetFromSuper();
			Insets	  insets=getInsets();


			// This layout differs from the one used in BasicSplitPaneDivider.
			// It does not center justify the oneTouchExpadable buttons.
			// This was necessary in order to meet the spec of the Metal
			// splitpane divider.
			if(leftButton!=null && rightButton!=null && c==SplitPaneDivider.this)
			{
				if(splitPane.isOneTouchExpandable())
				{
					if(orientation==JSplitPane.VERTICAL_SPLIT)
					{
						int extraY=(insets!=null) ? insets.top : 0;
						int blockSize=getDividerSize();

						if(insets!=null)
						{
							blockSize-=(insets.top+insets.bottom);
						}
						blockSize=Math.min(blockSize, oneTouchSize);
						leftButton.setBounds(oneTouchOffset, extraY, blockSize*2,
													blockSize);
						rightButton.setBounds(oneTouchOffset+oneTouchSize*2, extraY,
													 blockSize*2, blockSize);
					}
					else
					{
						int blockSize=getDividerSize();
						int extraX=(insets!=null) ? insets.left : 0;

						if(insets!=null)
						{
							blockSize-=(insets.left+insets.right);
						}
						blockSize=Math.min(blockSize, oneTouchSize);
						leftButton.setBounds(extraX, oneTouchOffset, blockSize,
													blockSize*2);
						rightButton.setBounds(extraX, oneTouchOffset+oneTouchSize*2,
													 blockSize, blockSize*2);
					}
				}
				else
				{
					leftButton.setBounds(-5, -5, 1, 1);
					rightButton.setBounds(-5, -5, 1, 1);
				}
			}
		}


		public Dimension minimumLayoutSize(Container c)
		{
			return new Dimension(0, 0);
		}


		public Dimension preferredLayoutSize(Container c)
		{
			return new Dimension(0, 0);
		}


		public void removeLayoutComponent(Component c) {}


		public void addLayoutComponent(String string, Component c) {}
	}
}
