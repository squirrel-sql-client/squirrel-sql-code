package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2003 Colin Bell
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.properties.SessionPropertiesSheet;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.SQLFilterSheet;
/**
 * This class manages the windows for sessions.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionWindowManager
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SessionManager.class);

	/** Application API. */
	private final IApplication _app;

	/** Map of internal frames keyed by session ID. */
	private final Map _internalFrames = new HashMap();

	/**
	 * Collection of <TT>SessionPropertiesSheet</TT> objects that currently exist for
	 * sessions. Keyed by <TT>ISession.getIdentifier()</TT>.
	 */
	private final Map _sessionPropertySheets = new HashMap();

	private final Map _sheets = new HashMap();

	/** Listens to sessions waiting for them to close. */
	private final MySessionListener _sessionListener = new MySessionListener();

	/** Listens to session properties dialogs waiting for them to close. */
	private final PropertiesSheetListener _sessionPropertiesDialogListener = new PropertiesSheetListener();

	/** Listens to SQL filter dialogs waiting for them to close. */
	private final SQLFilterDialogListener _sqlFilterDialogListener = new SQLFilterDialogListener();

	/**
	 * Ctor.
	 * 
	 * @param	app		Application API.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public SessionWindowManager(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null"); 
		}

		_app = app;
	}

	/**
	 * Create a new internal frame for the passed session.
	 *
	 * @param	session		Session we are creating internal frame for.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if ISession is passed as null.
	 */
	public synchronized SessionInternalFrame createInternalFrame(IClientSession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("null ISession passed");
		}

		SessionInternalFrame sif = new SessionInternalFrame(session);
		_internalFrames.put(session.getIdentifier(), sif);
		session.addSessionListener(_sessionListener);

		return sif;
	}

	/**
	 * Retrieve the internal frame for the passed session. Can be <TT>null</TT>
	 * 
	 * @return	the internal frame for the passed session. Can be <TT>null</TT>.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if ISession is passed as null.
	 */
	public JInternalFrame getInternalFrame(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("null ISession passed");
		}

		return (JInternalFrame)_internalFrames.get(session.getIdentifier());
	}

	/**
	 * Get a properties dialog for the passed session. If one already
	 * exists it will be brought to the front. If one doesn't exist it will be
	 * created.
	 *
	 * @param	session		The session that user has request property dialog for.
	 *
	 * @throws	IllegalArgumentException	if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public synchronized void showSessionPropertiesDialog(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		SessionPropertiesSheet propsSheet = getSessionPropertiesDialog(session);
		if (propsSheet == null)
		{
			propsSheet = new SessionPropertiesSheet(session);
			_sessionPropertySheets.put(session.getIdentifier(), propsSheet);
			_app.getMainFrame().addInternalFrame(propsSheet, true, null);
			propsSheet.addInternalFrameListener(_sessionPropertiesDialogListener);
			positionSheet(propsSheet);
		}

		propsSheet.moveToFront();
		propsSheet.setVisible(true);
	}

	/**
	 * Get a SQL Filter sheet for the passed session. If one already exists it
	 * will be brought to the front. If one doesn't exist it will be created.
	 *
	 * @param	session		The session for which the user has requested the SQL
	 * 						Filter sheet.
	 * @param	objectInfo	An instance of a class containing information about
	 * 						the database metadata.
	 *
	 * @return	The maintenance sheet for the passed session.
	 */
	public synchronized SQLFilterSheet showSQLFilterDialog(ISession session,
											IDatabaseObjectInfo objectInfo)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo == null");
		}

		SQLFilterSheet sqlFilterSheet = getSQLFilterSheet(session, objectInfo);
		if (sqlFilterSheet == null)
		{
			sqlFilterSheet = new SQLFilterSheet(session, objectInfo);
			Map map = getAllSQLFilterSheets(session);
			map.put(objectInfo.getQualifiedName(), sqlFilterSheet);
			_app.getMainFrame().addInternalFrame(sqlFilterSheet, true, null);
			sqlFilterSheet.addInternalFrameListener(_sqlFilterDialogListener);
			positionSheet(sqlFilterSheet);
		}

		sqlFilterSheet.moveToFront();
		sqlFilterSheet.setVisible(true);
		return sqlFilterSheet;
	}

	private SessionPropertiesSheet getSessionPropertiesDialog(ISession session)
	{
		return (SessionPropertiesSheet)_sessionPropertySheets.get(session.getIdentifier());
	}

	private SQLFilterSheet getSQLFilterSheet(ISession session, IDatabaseObjectInfo objectInfo)
	{
		Map map = getAllSQLFilterSheets(session);
		return (SQLFilterSheet)map.get(objectInfo.getQualifiedName());
	}

	private Map getAllSQLFilterSheets(ISession session)
	{
		Map map = (Map)_sheets.get(session.getIdentifier());
		if (map == null)
		{
			map = new HashMap();
			_sheets.put(session.getIdentifier(), map);
		}
		return map;
	}

	private void positionSheet(JInternalFrame jif)
	{
		GUIUtils.centerWithinDesktop(jif);
		jif.setVisible(true);
		jif.moveToFront();
	}

	private synchronized void sqlFilterDialogClosed(SQLFilterSheet sfs)
	{
		if (sfs != null)
		{
			sfs.removeInternalFrameListener(_sqlFilterDialogListener);
			Map map = getAllSQLFilterSheets(sfs.getSession());
			String key = sfs.getDatabaseObjectInfo().getQualifiedName();
			if (map.remove(key) == null)
			{
				s_log.error("Unable to find SQLFilterSheete for " + key);
			}
		}
	}

	private synchronized void sessionPropertiesDialogClosed(SessionPropertiesSheet sps)
	{
		if (sps != null)
		{
			sps.removeInternalFrameListener(_sessionPropertiesDialogListener);
			_sessionPropertySheets.remove(sps.getSession().getIdentifier());
		}
	}

	private synchronized void sessionHasClosed(ISession session)
	{
		JInternalFrame jif = getSessionPropertiesDialog(session);
		if (jif != null)
		{
			jif.dispose();
		}

		Map map = getAllSQLFilterSheets(session);
		for (Iterator it = map.values().iterator(); it.hasNext();)
		{
			((JInternalFrame)it.next()).dispose();
		}
		map.clear(); //TODO: This map should be removed from its containing map.

		session.removeSessionListener(_sessionListener);
		jif = (JInternalFrame)_internalFrames.remove(session.getIdentifier());
		if (jif != null)
		{
			jif.dispose();
		}
	}

	private final class MySessionListener extends SessionAdapter
	{
		/**
		 * The session has been closed.
		 *
		 * @param	evt		The event that has just occured.
		 */
		public void sessionClosed(SessionEvent evt)
		{
			SessionWindowManager.this.sessionHasClosed(evt.getSession());
		}
	}

	private final class PropertiesSheetListener extends InternalFrameAdapter
	{
		public void internalFrameClosed(InternalFrameEvent evt)
		{
			SessionPropertiesSheet sps = (SessionPropertiesSheet)evt.getInternalFrame();
			SessionWindowManager.this.sessionPropertiesDialogClosed(sps);
		}
	}

	private final class SQLFilterDialogListener extends InternalFrameAdapter
	{
		public void internalFrameClosed(InternalFrameEvent evt)
		{
			SQLFilterSheet sfs = (SQLFilterSheet)evt.getInternalFrame();
			SessionWindowManager.this.sqlFilterDialogClosed(sfs);
		}
	}
}