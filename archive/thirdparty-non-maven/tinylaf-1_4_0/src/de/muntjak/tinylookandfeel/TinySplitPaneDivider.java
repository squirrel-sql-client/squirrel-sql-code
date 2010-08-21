/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 * @(#)TinySplitPaneDivider.java	1.17 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * .... Thanks a lot to sun for not making this class public ....
 * I guess they don't want us to create look and feels ...
 */

package de.muntjak.tinylookandfeel;

import java.awt.*;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Metal's split pane divider
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @version 1.17 12/03/01
 * @author Steve Wilson
 * @author Ralph kar
 */
class TinySplitPaneDivider extends BasicSplitPaneDivider {
	
	private int inset = 2;
	private Color controlColor = MetalLookAndFeel.getControl();

	public TinySplitPaneDivider(BasicSplitPaneUI ui) {
		super(ui);
		setLayout(new MetalDividerLayout());
	}

	public void paint(Graphics g) {
		g.setColor(controlColor);

		Rectangle clip = g.getClipBounds();
		Insets insets = getInsets();
		g.fillRect(clip.x, clip.y, clip.width, clip.height);
		Dimension size = getSize();
		size.width -= inset * 2;
		size.height -= inset * 2;
		int drawX = inset;
		int drawY = inset;
		
		if(insets != null) {
			size.width -= (insets.left + insets.right);
			size.height -= (insets.top + insets.bottom);
			drawX += insets.left;
			drawY += insets.top;
		}
		
		super.paint(g);
	}

	/**
	 * Creates and return an instance of JButton that can be used to
	 * collapse the left component in the metal split pane.
	 */
	protected JButton createLeftOneTouchButton() {
		JButton b = new JButton() {
			public void setBorder(Border b) {
			}

			public void paint(Graphics g) {
				JSplitPane splitPane = getSplitPaneFromSuper();
				
				// changed this in 1.3
				if(splitPane != null) {
					int oneTouchSize = getOneTouchSizeFromSuper();
					int orientation = getOrientationFromSuper();
					
					// Fill the background first ...
					g.setColor(Theme.backColor.getColor());
					g.fillRect(0, 0, this.getWidth(), this.getHeight());
					
					g.setColor(Theme.splitPaneButtonColor.getColor());
					
					if(orientation == JSplitPane.VERTICAL_SPLIT) {
						g.drawLine(2, 1, 3, 1);
						g.drawLine(1, 2, 4, 2);
						g.drawLine(0, 3, 5, 3);
					}
					else {
						// HORIZONTAL_SPLIT
						g.drawLine(1, 2, 1, 3);
						g.drawLine(2, 1, 2, 4);
						g.drawLine(3, 0, 3, 5);
					}
				}
			}

			// Don't want the button to participate in focus traversable.
			public boolean isFocusTraversable() {
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
	protected JButton createRightOneTouchButton() {
		JButton b = new JButton() {
			public void setBorder(Border border) {
			}

			public void paint(Graphics g) {
				JSplitPane splitPane = getSplitPaneFromSuper();
				
				// changed this in 1.3
				if(splitPane != null) {
					int oneTouchSize = getOneTouchSizeFromSuper();
					int orientation = getOrientationFromSuper();
					
					// Fill the background first ...
					g.setColor(Theme.backColor.getColor());
					g.fillRect(0, 0, this.getWidth(), this.getHeight());

					g.setColor(Theme.splitPaneButtonColor.getColor());
					
					if(orientation == JSplitPane.VERTICAL_SPLIT) {
						g.drawLine(2, 3, 3, 3);
						g.drawLine(1, 2, 4, 2);
						g.drawLine(0, 1, 5, 1);
					}
					else {
						// HORIZONTAL_SPLIT
						g.drawLine(3, 2, 3, 3);
						g.drawLine(2, 1, 2, 4);
						g.drawLine(1, 0, 1, 5);
					}
				}
			}

			// Don't want the button to participate in focus traversable.
			public boolean isFocusTraversable() {
				return false;
			}
		};
		
		b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setRequestFocusEnabled(false);
		return b;
	}

	/**
	 * Used to layout a TinySplitPaneDivider. Layout for the divider
	 * involves appropriately moving the left/right buttons around.
	 * <p>
	 * This inner class is marked &quot;public&quot; due to a compiler bug.
	 * This class should be treated as a &quot;protected&quot; inner class.
	 * Instantiate it only within subclasses of TinySplitPaneDivider.
	 */
	public class MetalDividerLayout implements LayoutManager {
		public void layoutContainer(Container c) {
			JButton leftButton = getLeftButtonFromSuper();
			JButton rightButton = getRightButtonFromSuper();
			JSplitPane splitPane = getSplitPaneFromSuper();
			int orientation = getOrientationFromSuper();
			int oneTouchSize = getOneTouchSizeFromSuper();
			int oneTouchOffset = getOneTouchOffsetFromSuper();
			Insets insets = getInsets();

			// This layout differs from the one used in BasicSplitPaneDivider.
			// It does not center justify the oneTouchExpadable buttons.
			// This was necessary in order to meet the spec of the Metal
			// splitpane divider.
			if(leftButton != null && rightButton != null && c == TinySplitPaneDivider.this) {
				if(splitPane.isOneTouchExpandable()) {
					if(orientation == JSplitPane.VERTICAL_SPLIT) {
						int extraY = (insets != null) ? insets.top : 0;
						int blockSize = getDividerSize();

						if(insets != null) {
							blockSize -= (insets.top + insets.bottom);
						}
						blockSize = Math.min(blockSize, oneTouchSize);
						leftButton.setBounds(oneTouchOffset, extraY, blockSize * 2, blockSize);
						rightButton.setBounds(oneTouchOffset + oneTouchSize * 2, extraY, blockSize * 2, blockSize);
					} else {
						int blockSize = getDividerSize();
						int extraX = (insets != null) ? insets.left : 0;

						if(insets != null) {
							blockSize -= (insets.left + insets.right);
						}
						blockSize = Math.min(blockSize, oneTouchSize);
						leftButton.setBounds(extraX, oneTouchOffset, blockSize, blockSize * 2);
						rightButton.setBounds(extraX, oneTouchOffset + oneTouchSize * 2, blockSize, blockSize * 2);
					}
				} else {
					leftButton.setBounds(-5, -5, 1, 1);
					rightButton.setBounds(-5, -5, 1, 1);
				}
			}
		}

		public Dimension minimumLayoutSize(Container c) {
			return new Dimension(0, 0);
		}

		public Dimension preferredLayoutSize(Container c) {
			return new Dimension(0, 0);
		}

		public void removeLayoutComponent(Component c) {
		}

		public void addLayoutComponent(String string, Component c) {
		}
	}

	/*
	 * The following methods only exist in order to be able to access protected
	 * members in the superclass, because these are otherwise not available
	 * in any inner class.
	 */

	int getOneTouchSizeFromSuper() {
		return super.ONE_TOUCH_SIZE;
	}

	int getOneTouchOffsetFromSuper() {
		return super.ONE_TOUCH_OFFSET;
	}

	int getOrientationFromSuper() {
		return super.orientation;
	}

	JSplitPane getSplitPaneFromSuper() {
		return super.splitPane;
	}

	JButton getLeftButtonFromSuper() {
		return super.leftButton;
	}

	JButton getRightButtonFromSuper() {
		return super.rightButton;
	}
}
