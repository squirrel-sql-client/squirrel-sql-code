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
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * Base class for tabs to the added to one of the object panels.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public abstract class BaseObjectTab implements IObjectTab
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(BaseObjectTab.class);

	/** Application API. */
	protected IApplication _app;

	/** Current session. */
//	private ISession _session;

	/** ID of the session for this window. */
	private IIdentifier _sessionId;

	/** Defines the object that info is to be displayed for. */
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
		_app = session.getApplication();
		_sessionId = session.getIdentifier();
	}

	/**
	 * Retrieve the current session.
	 *
	 * @return	Current session.
	 */
	public final ISession getSession()
	{
		return _app.getSessionManager().getSession(_sessionId);
	}

	/**
	 * Returns the DialectType of the current session.
	 * 
	 * @return DialectType of the current session.
	 */
	public DialectType getDialectType() {
	   return DialectFactory.getDialectType(getSession().getMetaData());
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
				clear();
				refreshComponent();
			}
			catch (Throwable th)
			{
				th.printStackTrace();
				if (s_log.isDebugEnabled()) {
					s_log.debug("Unexpected exception: "+th.getMessage(), th);
				}
				getSession().showErrorMessage(th);
			}
			_hasBeenDisplayed = true;
		}
	}

	/**
	 * Rebuild the tab. This usually means that some kind of configuration
	 * data has changed (I.E. the output type has changed from text to table).
	 */
	public void rebuild()
	{
		_hasBeenDisplayed = false;
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
}
