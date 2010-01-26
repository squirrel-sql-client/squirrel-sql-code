package com.digitprop.tonic;


import java.awt.*;


/**
 * This layout manager is quite similiar to FlowLayout, but has a vertical
 * orientation.
 * 
 * @author Markus Fischer
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
public class VerticalFlowLayout implements LayoutManager, java.io.Serializable
{

	/**
	 * This value indicates that each row of components should be
	 * left-justified.
	 */
	public static final int TOP = 0;

	/**
	 * This value indicates that each row of components should be centered.
	 */
	public static final int CENTER = 1;

	/**
	 * This value indicates that each row of components should be
	 * right-justified.
	 */
	public static final int BOTTOM = 2;

	/**
	 * This value indicates that each row of components should be justified to
	 * the leading edge of the container's orientation, for example, to the left
	 * in left-to-right orientations.
	 * 
	 * @see java.awt.Component#getComponentOrientation
	 * @see java.awt.ComponentOrientation
	 * @since 1.2 Package-private pending API change approval
	 */
	public static final int LEADING = 3;

	/**
	 * This value indicates that each row of components should be justified to
	 * the trailing edge of the container's orientation, for example, to the
	 * right in left-to-right orientations.
	 * 
	 * @see java.awt.Component#getComponentOrientation
	 * @see java.awt.ComponentOrientation
	 * @since 1.2 Package-private pending API change approval
	 */
	public static final int TRAILING = 4;

	/**
	 * <code>align</code> is the property that determines how each row
	 * distributes empty space. It can be one of the following values:
	 * <ul>
	 * <code>LEFT</code>
	 * <code>RIGHT</code>
	 * <code>CENTER</code>
	 * <code>LEADING</code>
	 * <code>TRAILING</code>
	 * </ul>
	 * 
	 * @serial @see #getAlignment
	 * @see #setAlignment
	 */
	int align; // This is for 1.1 serialization compatibility

	/**
	 * <code>newAlign</code> is the property that determines how each row
	 * distributes empty space for the Java 2 platform, v1.2 and greater. It can
	 * be one of the following three values:
	 * <ul>
	 * <code>LEFT</code>
	 * <code>RIGHT</code>
	 * <code>CENTER</code>
	 * <code>LEADING</code>
	 * <code>TRAILING</code>
	 * </ul>
	 * 
	 * @serial @since 1.2
	 * @see #getAlignment
	 * @see #setAlignment
	 */
	int newAlign; // This is the one we actually use

	/**
	 * The flow layout manager allows a seperation of components with gaps. The
	 * horizontal gap will specify the space between components.
	 * 
	 * @serial @see #getHgap()
	 * @see #setHgap(int)
	 */
	int hgap;

	/**
	 * The flow layout manager allows a seperation of components with gaps. The
	 * vertical gap will specify the space between rows.
	 * 
	 * @serial @see #getHgap()
	 * @see #setHgap(int)
	 */
	int vgap;

	/*
	 * JDK 1.1 serialVersionUID
	 */
	private static final long serialVersionUID = -7262534875583282631L;

	/**
	 * Constructs a new <code>FlowLayout</code> with a centered alignment and
	 * a default 5-unit horizontal and vertical gap.
	 */
	public VerticalFlowLayout()
	{
		this(CENTER, 5, 5);
	}

	/**
	 * Constructs a new <code>FlowLayout</code> with the specified alignment
	 * and a default 5-unit horizontal and vertical gap. The value of the
	 * alignment argument must be one of <code>FlowLayout.LEFT</code>,<code>FlowLayout.RIGHT</code>,
	 * or <code>FlowLayout.CENTER</code>.
	 * 
	 * @param align
	 *           the alignment value
	 */
	public VerticalFlowLayout(int align)
	{
		this(align, 5, 5);
	}

	/**
	 * Creates a new flow layout manager with the indicated alignment and the
	 * indicated horizontal and vertical gaps.
	 * <p>
	 * The value of the alignment argument must be one of <code>FlowLayout.LEFT</code>,
	 * <code>FlowLayout.RIGHT</code>, or <code>FlowLayout.CENTER</code>.
	 * 
	 * @param align
	 *           the alignment value
	 * @param hgap
	 *           the horizontal gap between components
	 * @param vgap
	 *           the vertical gap between components
	 */
	public VerticalFlowLayout(int align, int hgap, int vgap)
	{
		this.hgap = hgap;
		this.vgap = vgap;
		setAlignment(align);
	}

	/**
	 * Gets the alignment for this layout. Possible values are <code>FlowLayout.LEFT</code>,
	 * <code>FlowLayout.RIGHT</code>,<code>FlowLayout.CENTER</code>,
	 * <code>FlowLayout.LEADING</code>, or <code>FlowLayout.TRAILING</code>.
	 * 
	 * @return the alignment value for this layout
	 * @see java.awt.FlowLayout#setAlignment
	 * @since JDK1.1
	 */
	public int getAlignment()
	{
		return newAlign;
	}

	/**
	 * Sets the alignment for this layout. Possible values are
	 * <ul>
	 * <li><code>FlowLayout.LEFT</code>
	 * <li><code>FlowLayout.RIGHT</code>
	 * <li><code>FlowLayout.CENTER</code>
	 * <li><code>FlowLayout.LEADING</code>
	 * <li><code>FlowLayout.TRAILING</code>
	 * </ul>
	 * 
	 * @param align
	 *           one of the alignment values shown above
	 * @see #getAlignment()
	 * @since JDK1.1
	 */
	public void setAlignment(int align)
	{
		this.newAlign = align;

		// this.align is used only for serialization compatibility,
		// so set it to a value compatible with the 1.1 version
		// of the class

		switch (align)
		{
			case LEADING :
				this.align = TOP;
				break;
			case TRAILING :
				this.align = BOTTOM;
				break;
			default :
				this.align = align;
				break;
		}
	}

	/**
	 * Gets the horizontal gap between components.
	 * 
	 * @return the horizontal gap between components
	 * @see java.awt.FlowLayout#setHgap
	 * @since JDK1.1
	 */
	public int getHgap()
	{
		return hgap;
	}

	/**
	 * Sets the horizontal gap between components.
	 * 
	 * @param hgap
	 *           the horizontal gap between components
	 * @see java.awt.FlowLayout#getHgap
	 * @since JDK1.1
	 */
	public void setHgap(int hgap)
	{
		this.hgap = hgap;
	}

	/**
	 * Gets the vertical gap between components.
	 * 
	 * @return the vertical gap between components
	 * @see java.awt.FlowLayout#setVgap
	 * @since JDK1.1
	 */
	public int getVgap()
	{
		return vgap;
	}

	/**
	 * Sets the vertical gap between components.
	 * 
	 * @param vgap
	 *           the vertical gap between components
	 * @see java.awt.FlowLayout#getVgap
	 * @since JDK1.1
	 */
	public void setVgap(int vgap)
	{
		this.vgap = vgap;
	}

	/**
	 * Adds the specified component to the layout. Not used by this class.
	 * 
	 * @param name
	 *           the name of the component
	 * @param comp
	 *           the component to be added
	 */
	public void addLayoutComponent(String name, Component comp)
	{
	}

	/**
	 * Removes the specified component from the layout. Not used by this class.
	 * 
	 * @param comp
	 *           the component to remove
	 * @see java.awt.Container#removeAll
	 */
	public void removeLayoutComponent(Component comp)
	{
	}

	/**
	 * Returns the preferred dimensions for this layout given the <i>visible
	 * </i> components in the specified target container.
	 * 
	 * @param target
	 *           the component which needs to be laid out
	 * @return the preferred dimensions to lay out the subcomponents of the
	 *         specified container
	 * @see Container
	 * @see #minimumLayoutSize
	 * @see java.awt.Container#getPreferredSize
	 */
	public Dimension preferredLayoutSize(Container target)
	{
		synchronized (target.getTreeLock())
		{
			Dimension dim = new Dimension(0, 0);
			int nmembers = target.getComponentCount();
			boolean firstVisibleComponent = true;

			for (int i = 0; i < nmembers; i++)
			{
				Component m = target.getComponent(i);
				if (m.isVisible())
				{
					Dimension d = m.getPreferredSize();
					dim.width = Math.max(dim.width, d.width);
					if (firstVisibleComponent)
					{
						firstVisibleComponent = false;
					}
					else
					{
						dim.height += hgap;
					}
					dim.height += d.height;
				}
			}
			
			Insets insets = target.getInsets();
			dim.width += insets.left + insets.right + hgap * 2;
			dim.height += insets.top + insets.bottom + vgap * 2;
			
			return dim;
		}
	}

	/**
	 * Returns the minimum dimensions needed to layout the <i>visible</i>
	 * components contained in the specified target container.
	 * 
	 * @param target
	 *           the component which needs to be laid out
	 * @return the minimum dimensions to lay out the subcomponents of the
	 *         specified container
	 * @see #preferredLayoutSize
	 * @see java.awt.Container
	 * @see java.awt.Container#doLayout
	 */
	public Dimension minimumLayoutSize(Container target)
	{
		synchronized (target.getTreeLock())
		{
			Dimension dim = new Dimension(0, 0);
			int nmembers = target.getComponentCount();

			for (int i = 0; i < nmembers; i++)
			{
				Component m = target.getComponent(i);
				if (m.isVisible())
				{
					Dimension d = m.getMinimumSize();
					dim.width = Math.max(dim.width, d.width);
					if (i > 0)
					{
						dim.height += hgap;
					}
					dim.height += d.height;
				}
			}
			
			Insets insets = target.getInsets();
			dim.width += insets.left + insets.right + hgap * 2;
			dim.height += insets.top + insets.bottom + vgap * 2;
			
			return dim;
		}
	}

	/**
	 * Centers the elements in the specified row, if there is any slack.
	 * 
	 * @param target
	 *           the component which needs to be moved
	 * @param x
	 *           the x coordinate
	 * @param y
	 *           the y coordinate
	 * @param width
	 *           the width dimensions
	 * @param height
	 *           the height dimensions
	 * @param rowStart
	 *           the beginning of the row
	 * @param rowEnd
	 *           the the ending of the row
	 */
	private void moveComponents(
		Container target,
		int x,
		int y,
		int width,
		int height,
		int rowStart,
		int rowEnd,
		boolean ltr)
	{
		synchronized (target.getTreeLock())
		{
			switch (newAlign)
			{
				case TOP :
					y += ltr ? 0 : height;
					break;
				case CENTER :
					y += height / 2;
					break;
				case BOTTOM :
					y += ltr ? height : 0;
					break;
				case LEADING :
					break;
				case TRAILING :
					y += height;
					break;
			}
			
			for (int i = rowStart; i < rowEnd; i++)
			{
				Component m = target.getComponent(i);
				if (m.isVisible())
				{
					if (ltr)
					{
						m.setLocation(x +(width-m.getWidth())/2, y );
					}
					else
					{
						m.setLocation(
							x+(width-m.getWidth())/2, 
								
							target.getHeight() - y - m.getHeight());
					}
					
					y += m.getHeight() + vgap;
				}
			}
		}
	}

	
	/**
	 * Lays out the container. This method lets each component take its
	 * preferred size by reshaping the components in the target container in
	 * order to satisfy the alignment of this <code>FlowLayout</code> object.
	 * 
	 * @param target
	 *           the specified component being laid out
	 * @see Container
	 * @see java.awt.Container#doLayout
	 */
	public void layoutContainer(Container target)
	{
		synchronized (target.getTreeLock())
		{
			Insets insets = target.getInsets();
			int maxheight =
				target.getHeight() - (insets.top + insets.bottom + vgap * 2);
			int nmembers = target.getComponentCount();
			int x = insets.left+hgap, y = 0;
			int colw = 0, start = 0;

			boolean ltr = target.getComponentOrientation().isLeftToRight();

			for (int i = 0; i < nmembers; i++)
			{
				Component m = target.getComponent(i);
				if (m.isVisible())
				{
					Dimension d = m.getPreferredSize();
					m.setSize(d.width, d.height);

					if ((y == 0) || ((y + d.height) <= maxheight))
					{
						if (y > 0)
						{
							y += vgap;
						}
						y += d.height;
						colw = Math.max(colw, d.width);
					}
					else
					{
						moveComponents(
							target,
							x,
							insets.top+vgap,
							colw, 
							maxheight-y,
							start,
							i,
							ltr);
						y = d.height;
						x += hgap + colw;
						colw = d.width;
						start = i;
					}
				}
			}
			
			moveComponents(
				target,
				x,
				insets.top+vgap,
				colw,
				maxheight-y,
				start,
				nmembers,
				ltr);
		}
	}

	//
	// the internal serial version which says which version was written
	// - 0 (default) for versions before the Java 2 platform, v1.2
	// - 1 for version >= Java 2 platform v1.2, which includes "newAlign" field
	//
	private static final int currentSerialVersion = 1;
	/**
	 * This represent the <code>currentSerialVersion</code> which is bein
	 * used. It will be one of two values :<code>0</code> versions before
	 * Java 2 platform v1.2.. <code>1</code> versions after Java 2 platform
	 * v1.2..
	 * 
	 * @serial @since 1.2
	 */
	private int serialVersionOnStream = currentSerialVersion;

	/**
	 * Returns a string representation of this <code>FlowLayout</code> object
	 * and its values.
	 * 
	 * @return a string representation of this layout
	 */
	public String toString()
	{
		String str = "";
		switch (align)
		{
			case TOP :
				str = ",align=left";
				break;
			case CENTER :
				str = ",align=center";
				break;
			case BOTTOM :
				str = ",align=right";
				break;
			case LEADING :
				str = ",align=leading";
				break;
			case TRAILING :
				str = ",align=trailing";
				break;
		}
		return getClass().getName()
			+ "[hgap="
			+ hgap
			+ ",vgap="
			+ vgap
			+ str
			+ "]";
	}

}
