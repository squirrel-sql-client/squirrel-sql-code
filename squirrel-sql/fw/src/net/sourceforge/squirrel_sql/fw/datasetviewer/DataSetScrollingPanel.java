package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class DataSetScrollingPanel extends JScrollPane
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(DataSetScrollingPanel.class);

	private boolean _fullyCreated = false;
	private IDataSetViewer _viewer;

	/**
	 * @deprecated
	 */
	public DataSetScrollingPanel()
	{
		super();
	}

	public DataSetScrollingPanel(String destClassName) throws DataSetException
	{
		super();
		createUserInterface(destClassName);
		_fullyCreated = true;
	}

	public void load(IDataSet ds)
	{
		load(ds, null);
	}

	/**
	 * @deprecated
	 */
	public void load(IDataSet ds, String destClassName)
	{
		try
		{
			if (!_fullyCreated)
			{
				createUserInterface(destClassName);
				_fullyCreated = true;
			}
			Runnable run = new UIUpdater(_viewer, ds);
			SwingUtilities.invokeLater(run);
		}
		catch (Exception ex)
		{
			s_log.error("Error", ex);
		}
	}

	public void clear()
	{
		if (_viewer != null)
		{
			_viewer.clear();
		}
	}

	private void createUserInterface(String destClassName)
		throws DataSetException
	{
		setBorder(BorderFactory.createEmptyBorder());
		_viewer = BaseDataSetViewerDestination.getInstance(destClassName);
		Runnable run = new Runnable()
		{
			public void run()
			{
				setViewportView(_viewer.getComponent());
			}
		};
		SwingUtilities.invokeLater(run);
	}

	private final static class UIUpdater implements Runnable
	{
		private final IDataSetViewer _viewer;
		private final IDataSet _ds;

		UIUpdater(IDataSetViewer viewer, IDataSet ds)
		{
			super();
			_viewer = viewer;
			_ds = ds;
		}

		public void run()
		{
			try
			{
				_viewer.show(_ds);
			}
			catch (DataSetException ex)
			{
				//TODO: Handle error.
			}
		}
	}
}
