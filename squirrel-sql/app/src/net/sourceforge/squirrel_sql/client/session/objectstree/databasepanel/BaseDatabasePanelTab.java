package net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel;
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

import net.sourceforge.squirrel_sql.fw.datasetviewer.BaseDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel.BaseObjectPanelTab;
/**
 * Base class for tabs to the added to <TT>DatabasePanel</TT>. If you are
 * writing a class for a tab to be added to <TT>DatabasePanel</TT> you don't
 * have to inherit from this class (only implement <TT>IDatabasePanelTab</TT>)
 * but it has convenience methods.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public abstract class BaseDatabasePanelTab extends BaseObjectPanelTab
												implements IDatabasePanelTab
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(BaseDatabasePanelTab.class);

	/** Destination to display data in. */
	private IDataSetViewer _viewer;

	/** Component to be displayed. */
	private DatabasePanelTabComponent _comp;

	/**
	 * @see BaseObjectPanelTab#refreshComponent()
	 */
	protected void refreshComponent() throws DataSetException
	{
		((DatabasePanelTabComponent)getComponent()).load(createDataSet(getSession()));
	}

	protected abstract IDataSet createDataSet(ISession session)
		throws DataSetException;

	/**
	 * @see IObjectPanelTab#getComponent()
	 */
	public synchronized Component getComponent()
	
	{
		if (_comp == null)
		{
			ISession session = getSession();
			_comp = new DatabasePanelTabComponent(session, createViewer(session));
		}
		return _comp;
	}

	/**
	 * @see IObjectPanelTab#clear()
	 */
	public void clear()
	{
		if (_comp != null)
		{
			_comp.clear();
		}
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

	protected IDataSetViewer createViewer(ISession session)
	{
		String destClassName = session.getProperties().getMetaDataOutputClassName();
		return BaseDataSetViewerDestination.getInstance(destClassName);
	}
}
