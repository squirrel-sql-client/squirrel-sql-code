package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.awt.Component;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetScrollingPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;

public abstract class BaseDataSetTab extends BaseObjectTab
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(BaseDataSetTab.class);

	/** Component to display in tab. */
	private DataSetScrollingPanel _comp;

	public BaseDataSetTab()
	{
		super();
	}

	/**
	 * Return the component to be displayed in the panel.
	 *
	 * @return	The component to be displayed in the panel.
	 */
	public synchronized Component getComponent()
	{
		if (_comp == null)
		{
			try
			{
				_comp = new DataSetScrollingPanel(getDestinationClassName());
			}
			catch (Exception ex)
			{
				s_log.error("Error", ex);
			}
		}
		return _comp;
	}

	/**
	 * Rebuild the tab. This usually means that some kind of configuration
	 * data has changed (I.E. the output type has changed from text to table).
	 */
	public void rebuild()
	{
		super.rebuild();
		_comp = null;
	}

	/**
	 * @see BaseObjectPanelTab#clear()
	 */
	public void clear()
	{
		((DataSetScrollingPanel)getComponent()).clear();
	}

	/**
	 * Refresh the component displaying the database object.
	 */
	public synchronized void refreshComponent() throws DataSetException
	{
		ISession session = getSession();
		if (session == null)
		{
			throw new IllegalStateException("Null ISession");
		}
		((DataSetScrollingPanel)getComponent()).load(createDataSet());
	}

	protected abstract IDataSet createDataSet() throws DataSetException;

	protected String getDestinationClassName()
	{
		return getSession().getProperties().getMetaDataOutputClassName();
	}
}
