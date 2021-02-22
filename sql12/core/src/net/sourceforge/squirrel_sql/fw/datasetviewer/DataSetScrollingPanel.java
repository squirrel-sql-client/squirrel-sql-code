package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2002-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.client.session.DataModelImplementationDetails;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class DataSetScrollingPanel extends JScrollPane
{
	private static final ILogger s_log = LoggerController.createLogger(DataSetScrollingPanel.class);

	private boolean _fullyCreated = false;
	private IDataSetViewer _viewer;
	private ISession _session;

	public DataSetScrollingPanel(String destClassName, IDataSetUpdateableModel updateableModel, ISession session)
		throws DataSetException
	{
      this(destClassName, updateableModel, null, session);
	}

	public DataSetScrollingPanel(String destClassName, IDataSetUpdateableModel updateableModel, DataModelImplementationDetails dataModelImplementationDetails, ISession session)
		throws DataSetException
	{
		_session = session;
		createUserInterface(destClassName, updateableModel, dataModelImplementationDetails);
		_fullyCreated = true;
	}

	public void load(IDataSet ds)
	{
		load(ds, null);
	}

	public void load(IDataSet ds, DataModelImplementationDetails dataModelImplementationDetails)
	{
		load(ds, null, dataModelImplementationDetails);
	}

	private void load(IDataSet ds, String destClassName, DataModelImplementationDetails dataModelImplementationDetails)
	{
		try
		{
			if (!_fullyCreated)
			{
				createUserInterface(destClassName, null, dataModelImplementationDetails);
				_fullyCreated = true;
			}
			Runnable run = new DataSetScrollingPanelUpdater(_viewer, ds);
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

	private void createUserInterface(String destClassName, IDataSetUpdateableModel updateableModel, DataModelImplementationDetails dataModelImplementationDetails)
		throws DataSetException
	{
		setBorder(BorderFactory.createEmptyBorder());
		_viewer = BaseDataSetViewerDestination.createInstance(destClassName, updateableModel, dataModelImplementationDetails, _session);
		Runnable run = new Runnable()
		{
			public void run()
			{
				setViewportView(_viewer.getComponent());
			}
		};
		SwingUtilities.invokeLater(run);
	}

	/**
	 * Get the viewer being used in this panel.
	 */
	public IDataSetViewer getViewer()
	{
		return _viewer;
	}
}
