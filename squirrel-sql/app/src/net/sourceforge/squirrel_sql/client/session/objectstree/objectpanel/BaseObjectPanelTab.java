package net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel;
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
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * Base class for tabs to the added to one of the object panels.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public abstract class BaseObjectPanelTab implements IObjectPanelTab
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(BaseObjectPanelTab.class);

	/** Current session. */
	private ISession _session;

	/** Defines the table that info is to be displayed for. */
	private IDatabaseObjectInfo _dbObjInfo;

	/**
	 * Set to <TT>true</TT> if the current <TT>IDatabaseObjectInfo</TT> object
	 * has been displayed.
	 */
	private boolean _hasBeenDisplayed;

	/**
	 * Set the current session.
	 *
	 * @param	session	Current session.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> ISession</TT> passed.
	 */
	public void setSession(ISession session) throws IllegalArgumentException
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_session = session;
	}

	/**
	 * Retrieve the current session.
	 *
	 * @return	Current session.
	 */
	public final ISession getSession()
	{
		return _session;
	}

	/**
	 * This tab has been selected. This will call <TT>refreshComponent()</TT>
	 * only if it hasn't been called for the current MTT>IDatabaseObjectInfo</TT> object.
	 *
	 * @throws	IllegalStateException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> or
	 *			<TT>IDatabaseObjectInfo</TT> object is stored here.
	 */
	public synchronized void select()
	{
		if (!_hasBeenDisplayed)
		{
			s_log.debug("Refreshing " + getTitle() + " table tab.");
			try
			{
				refreshComponent();
			}
			catch (DataSetException ex)
			{
				getSession().getMessageHandler().showMessage(ex);
			}
			_hasBeenDisplayed = true;
		}
	}

	/**
	 * Refresh the component displaying the <TT>IDatabaseObjectInfo</TT> object.
	 */
	protected abstract void refreshComponent() throws DataSetException;

	/**
	 * Set the <TT>IDatabaseObjectInfo</TT> object that specifies the object that
	 * is to have its information displayed.
	 *
	 * @param	value	<TT>IDatabaseObjectInfo</TT> object that specifies the currently
	 *					selected object. This can be <TT>null</TT>.
	 */
	public void setDatabaseObjectInfo(IDatabaseObjectInfo value)
	
	{
		_dbObjInfo = value;
		_hasBeenDisplayed = false;
	}

	/**
	 * Retrieve the current <TT>IDatabaseObjectInfo</TT> object.
	 *
	 * @return	Current <TT>IDatabaseObjectInfo</TT> object.
	 */
	protected final IDatabaseObjectInfo getDatabaseObjectInfo()
	{
		return _dbObjInfo;
	}

	/**
	 * Create a viewer panel for an <T>IDataSet</TT>. If the passed class
	 * name is invalid return a <TT>import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel</TT>.
	 *
	 * @param	destClassName	Class Name of panel to be created. This class
	 *							must have a default constructor.
	 *
	 * @return  The newly created panel.
	 */
	/*
		protected IDataSetViewerDestination createDestination(String destClassName) {
			IDataSetViewerDestination dest = null;
			try {
				Class destClass = Class.forName(destClassName);
				if (IDataSetViewerDestination.class.isAssignableFrom(destClass) &&
						Component.class.isAssignableFrom(destClass)) {
					dest = (IDataSetViewerDestination)destClass.newInstance();
				}
	
			} catch (Exception ignore) {
			}
			if (dest == null) {
				dest = new DataSetViewerTablePanel();
			}
			return dest;
		}
	*/
}
