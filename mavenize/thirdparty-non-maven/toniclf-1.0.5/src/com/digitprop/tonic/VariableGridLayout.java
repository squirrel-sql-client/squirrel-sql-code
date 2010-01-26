package com.digitprop.tonic;


import java.awt.*;


/**
 * This layout manager is somewhat similiar to the GridLayout, with the
 * difference that the rows and columns need not all have the same size.
 * <p>
 * 
 * Each row has the height of highest component in it, and each column has the
 * width of the widest component in it, but neighbouring columns can (and will)
 * have different widths, and neighbouring rows can (and will) have different
 * heights.
 * <p>
 * 
 * In this way, it is easy to align components for tabular layouts, without
 * adding unwanted extra white space.
 * <p>
 * 
 * Other than that, this layout works similiar to the GridLayout.
 * 
 * @see GridLayout
 * 
 * @author Markus Fischer
 * 
 * This software is under the <a
 * href="http://www.gnu.org/copyleft/lesser.html">GNU Lesser General Public
 * License </a>
 */

/*
 * ------------------------------------------------------------------------
 * Copyright (C) 2004 Markus Fischer
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 2.1 as
 * published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * You can contact the author at: Markus Fischer www.digitprop.com
 * info@digitprop.com
 * ------------------------------------------------------------------------
 */
public class VariableGridLayout extends GridLayout
{
	/**
	 * This is the horizontal gap (in pixels) which specifies the space between
	 * columns. They can be changed at any time. This should be a non-negative
	 * integer.
	 */
	int	hgap;

	/**
	 * This is the vertical gap (in pixels) which specifies the space between
	 * rows. They can be changed at any time. This should be a non negative
	 * integer.
	 */
	int	vgap;

	/**
	 * This is the number of rows specified for the grid. The number of rows can
	 * be changed at any time. This should be a non negative integer, where '0'
	 * means 'any number' meaning that the number of Rows in that dimension
	 * depends on the other dimension.
	 */
	int	rows;

	/**
	 * This is the number of columns specified for the grid. The number of
	 * columns can be changed at any time. This should be a non negative
	 * integer, where '0' means 'any number' meaning that the number of Columns
	 * in that dimension depends on the other dimension.
	 */
	int	cols;


	/**
	 * Creates an instance with a default of one column per component, in a
	 * single row.
	 */
	public VariableGridLayout()
	{
		this(1, 0, 0, 0);
	}


	/**
	 * Creates an instance with the specified number of rows and columns.
	 * <p>
	 * 
	 * One, but not both, of <code>rows</code> and <code>cols</code> can be
	 * zero, which means that any number of objects can be placed in a row or in
	 * a column.
	 * 
	 * @param rows
	 *        The number of rows in this layout. Zero means that the layout can
	 *        have any number of rows
	 * @param cols
	 *        The number of columns in this layout. Zero means that the layout
	 *        can have any number of columns
	 */
	public VariableGridLayout(int rows, int cols)
	{
		this(rows, cols, 0, 0);
	}


	/**
	 * Creates an instance with the specified number of rows and columns.
	 * <p>
	 * 
	 * One, but not both, of <code>rows</code> and <code>cols</code> can be
	 * zero, which means that any number of objects can be placed in a row or in
	 * a column.
	 * 
	 * @param rows
	 *        The number of rows in this layout. Zero means that the layout can
	 *        have any number of rows
	 * @param cols
	 *        The number of columns in this layout. Zero means that the layout
	 *        can have any number of columns
	 * @param hgap
	 *        The gap (in pixels) between two columns
	 * @param vgap
	 *        The gap (in pixels) between two rows
	 * 
	 * @exception IllegalArgumentException
	 *            if the value of both <code>rows</code> and <code>cols</code>
	 *            is set to zero
	 */
	public VariableGridLayout(int rows, int cols, int hgap, int vgap)
	{
		if ((rows == 0) && (cols == 0))
		{
			throw new IllegalArgumentException("rows and cols cannot both be zero");
		}
		this.rows = rows;
		this.cols = cols;
		this.hgap = hgap;
		this.vgap = vgap;
	}


	/** Returns the number of rows in this layout */
	public int getRows()
	{
		return rows;
	}


	/** Sets the number of rows in this layout */
	public void setRows(int rows)
	{
		if ((rows == 0) && (this.cols == 0))
		{
			throw new IllegalArgumentException("rows and cols cannot both be zero");
		}
		this.rows = rows;
	}


	/** Returns the number of columns in this layout */
	public int getColumns()
	{
		return cols;
	}


	/** Sets the number of columsn in this layout */
	public void setColumns(int cols)
	{
		if ((cols == 0) && (this.rows == 0))
		{
			throw new IllegalArgumentException("rows and cols cannot both be zero");
		}
		this.cols = cols;
	}


	/** Returns the horizontal gap between columns */
	public int getHgap()
	{
		return hgap;
	}


	/** Sets the horizontal gap between columns */
	public void setHgap(int hgap)
	{
		this.hgap = hgap;
	}


	/** Returns the vertical gap between rows */
	public int getVgap()
	{
		return vgap;
	}


	/** Sets the vertical gap between rows */
	public void setVgap(int vgap)
	{
		this.vgap = vgap;
	}


	/**
	 * Adds the specified component to the layout.
	 * 
	 * @param name
	 *        The name of the component
	 * @param comp
	 *        The component to be added
	 */
	public void addLayoutComponent(String name, Component comp)
	{
		// Does nothing
	}


	/** Removes the specified component from the layout */
	public void removeLayoutComponent(Component comp)
	{
		// Does nothing
	}


	/**
	 * Returns the preferred size of the specified container, using this layout.
	 * <p>
	 * 
	 * This is the sum of all column widths, and the sum of all row heights.
	 * Each column has the width of the widest contained component, and each row
	 * has the height of the highest contained component.
	 * 
	 * @param parent
	 *        The container in which to do the layout
	 * 
	 * @return The preferred dimensions to lay out the subcomponents of the
	 *         specified container
	 */
	public Dimension preferredLayoutSize(Container parent)
	{
		synchronized (parent.getTreeLock())
		{
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			int nrows = rows;
			int ncols = cols;

			if (nrows > 0)
			{
				ncols = (ncomponents + nrows - 1) / nrows;
			}
			else
			{
				nrows = (ncomponents + ncols - 1) / ncols;
			}

			int w = 0;
			int h = 0;

			// Calculate column widths
			int colWidths[] = getColWidths(ncols, nrows, parent);
			for (int i = 0; i < colWidths.length; i++)
				w += colWidths[i];

			// Calculate row heights
			int rowHeights[] = getRowHeights(ncols, nrows, parent);
			for (int i = 0; i < rowHeights.length; i++)
				h += rowHeights[i];

			return new Dimension(insets.left + insets.right + w + (ncols - 1) * hgap, insets.top
						+ insets.bottom + h + (nrows - 1) * vgap);
		}
	}


	/**
	 * Returns the heights of all rows.
	 * 
	 * @param ncols
	 *        Number of columns
	 * @param nrows
	 *        Number of rows
	 * @param parent
	 *        The parent container for which to calculate the row heights.
	 * 
	 * @return An array with the height of each row in the parent container
	 */
	private int[] getRowHeights(int ncols, int nrows, Container parent)
	{
		int result[] = new int[nrows];

		int ncomponents = parent.getComponentCount();

		// Calculate row heights
		for (int y = 0; y < nrows; y++)
		{
			int currHeight = 0;
			for (int x = 0; x < ncols; x++)
			{
				if (x + y * ncols < ncomponents)
				{
					Component c = parent.getComponent(x + y * ncols);
					int h = c.getPreferredSize().height;
					currHeight = Math.max(currHeight, h);
				}
			}

			result[y] = currHeight;
		}

		return result;
	}


	/**
	 * Returns the widths of all columns.
	 * 
	 * @param ncols
	 *        Number of columns
	 * @param nrows
	 *        Number of rows
	 * @param parent
	 *        The parent container for which to calculate the column widths.
	 * 
	 * @return An array with the width of each column in the parent container
	 */
	private int[] getColWidths(int ncols, int nrows, Container parent)
	{
		int result[] = new int[ncols];

		int ncomponents = parent.getComponentCount();

		// Calculate column widths
		for (int x = 0; x < ncols; x++)
		{
			int currWidth = 0;
			for (int y = 0; y < nrows; y++)
			{
				if (x + y * ncols < ncomponents)
					currWidth = Math.max(currWidth, parent.getComponent(x + y * ncols)
								.getPreferredSize().width);
			}

			result[x] = currWidth;
		}

		return result;
	}


	/**
	 * Returns the minimum size of the specified container, using this layout.
	 */
	public Dimension minimumLayoutSize(Container parent)
	{
		return preferredLayoutSize(parent);
	}


	/** Does a layout for the specified parent container */
	public void layoutContainer(Container parent)
	{
		synchronized (parent.getTreeLock())
		{
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			int nrows = rows;
			int ncols = cols;
			boolean ltr = parent.getComponentOrientation().isLeftToRight();

			if (ncomponents == 0)
			{
				return;
			}

			if (nrows > 0)
			{
				ncols = (ncomponents + nrows - 1) / nrows;
			}
			else
			{
				nrows = (ncomponents + ncols - 1) / ncols;
			}

			int colWidths[] = getColWidths(ncols, nrows, parent);
			int rowHeights[] = getRowHeights(ncols, nrows, parent);

			int w = parent.getWidth() - (insets.left + insets.right);
			int h = parent.getHeight() - (insets.top + insets.bottom);
			w = (w - (ncols - 1) * hgap) / ncols;
			h = (h - (nrows - 1) * vgap) / nrows;

			int xp = insets.left;
			for (int x = 0; x < ncols; x++)
			{
				int yp = insets.top;
				for (int y = 0; y < nrows; y++)
				{
					int index = x + y * ncols;
					if (index < ncomponents)
					{
						parent.getComponent(index).setBounds(xp, yp, colWidths[x], rowHeights[y]);
						yp += vgap + rowHeights[y];
					}
				}

				xp += hgap + colWidths[x];
			}
		}
	}


	/** Returns a String representation of this layout. */
	public String toString()
	{
		return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + ",rows=" + rows + ",cols="
					+ cols + "]";
	}
}
