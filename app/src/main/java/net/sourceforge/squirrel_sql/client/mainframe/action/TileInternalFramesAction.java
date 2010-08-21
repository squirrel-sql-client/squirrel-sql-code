package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.client.gui.mainframe.IHasJDesktopPane;
import net.sourceforge.squirrel_sql.client.gui.mainframe.WidgetUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IDesktopContainer;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DesktopStyle;
import net.sourceforge.squirrel_sql.client.IApplication;

/**
 * This class will tile all internal frames owned by a
 * <CODE>JDesktopPane</CODE>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public abstract class TileInternalFramesAction extends BaseAction implements IHasJDesktopPane
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TileInternalFramesAction.class);

	/**
	 * The <CODE>JDesktopPane</CODE> that owns the internal frames to be
	 * tiled.
	 */
	private IDesktopContainer _desktop;
   private IApplication _app;

   /**
	 * Default constructor.
    * @param app
    */
	public TileInternalFramesAction(IApplication app)
	{
      super(s_stringMgr.getString("TileInternalFramesAction.title"));
      _app = app;
   }

   /**
	 * Set the <CODE>JDesktopPane</CODE> that owns the internal frames to be
	 * cascaded.
	 *
	 * @param	desktop		the <CODE>JDesktopPane</CODE> that owns the
	 *						internal frames to be cascaded.
	 */
	public void setDesktopContainer(IDesktopContainer value)
	{
		_desktop = value;
	}

	/**
	 * Tile the internal frames.
	 *
	 * @param	evt	 Specifies the event being proceessed.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		if (_desktop != null && _app.getDesktopStyle().isInternalFrameStyle())
		{
			IWidget[] widgets = WidgetUtils.getNonMinimizedNonToolWindows(_desktop.getAllWidgets());
			final int cells = widgets.length;
			if (cells > 0)
			{
				final RowColumnCount rcc = getRowColumnCount(cells);
				final int rows = rcc._rowCount;
				final int cols = rcc._columnCount;
//?? Extract this out into a class like CascadeInternalFramePositioner.

				final Dimension desktopSize = _desktop.getSize();
				final int width = desktopSize.width / cols;
				final int height = desktopSize.height / rows;
				int xPos = 0;
				int yPos = 0;

				for (int y = 0; y < rows; ++y)
				{
					for (int x = 0; x < cols; ++x)
					{
						final int idx = y + (x * rows);
						if (idx >= cells)
						{
							break;
						}
						JInternalFrame frame = widgets[idx].getInternalFrame();
						if (!frame.isClosed())
						{
							if (frame.isIcon())
							{
								try
								{
									frame.setIcon(false);
								} catch (PropertyVetoException ignore)
								{
									// Ignore.
								}
							}
							else if (frame.isMaximum())
							{
								try
								{
									frame.setMaximum(false);
								}
								catch (PropertyVetoException ignore)
								{
									// Ignore.
								}
							}

							frame.reshape(xPos, yPos, width, height);
							xPos += width;
						}
					}
					xPos = 0;
					yPos += height;
				}
			}
		}
	}

	/**
	 * Retrieve the number of rows and columns that the internal frames
	 * should be rearranged into.
	 *
	 * @param	internalFrameCount	Number of internal frames to be rearranged.
	 */
	protected abstract RowColumnCount getRowColumnCount(int internalFrameCount);

   public final static class RowColumnCount
	{
		protected final int _rowCount;
		protected final int _columnCount;

		public RowColumnCount(int rowCount, int columnCount)
		{
			_rowCount = rowCount;
			_columnCount = columnCount;
		}
	}
}
