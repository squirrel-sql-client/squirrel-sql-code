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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
/**
 * This class manages sessions.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionManager
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SessionManager.class);

	/** Linked list of sessions. */
	private final LinkedList _sessionsList = new LinkedList();

	/** Map of sessions keyed by session ID. */
	private final Map _sessionsById = new HashMap();

	/**
	 * Default ctor.
	 */
	public SessionManager()
	{
		super();
	}

	/**
	 * Create a new session.
	 *
	 * @param	app			Application API.
	 * @param	driver		JDBC driver for session.
	 * @param	alias		Defines URL to database.
	 * @param	conn		Connection to database.
	 * @param	user		User name connected with.
	 * @param	password	Password for <TT>user</TT>
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if IApplication, ISQLDriver, ISQLAlias,
	 * 			or SQLConnection is passed as null.
	 */
	public synchronized IClientSession createSession(IApplication app,
									ISQLDriver driver, ISQLAlias alias,
									SQLConnection conn, String user,
									String password)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("null IApplication passed");
		}
		if (driver == null)
		{
			throw new IllegalArgumentException("null ISQLDriver passed");
		}
		if (alias == null)
		{
			throw new IllegalArgumentException("null ISQLAlias passed");
		}
		if (conn == null)
		{
			throw new IllegalArgumentException("null SQLConnection passed");
		}

		IClientSession sess = new Session(app, driver, alias, conn, user, password);
		_sessionsList.addLast(sess);
		_sessionsById.put(sess.getIdentifier(), sess);

		return sess;
	}

	/**
	 * Close all sessions.
	 */
	public synchronized void closeAllSessions()
	{
		final IClientSession[] sessions = getActiveSessions();
		for (int i = 0; i < sessions.length; ++i)
		{
			try
			{
				closeSession(sessions[i]);
			}
			catch (Throwable th)
			{
				s_log.error("Error closing session", th);
			}
		}
	}

	/**
	 * Close a session.
	 *
	 * @param	session		Session to close.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if <TT>null</TT>IClientSession passed.
	 *
	 * @throws	SQLException
	 * 			Thrown if an error closing the SQL connection. The session
	 * 			will still be closed even though the connection may not have
	 *			been.
	 */
	public synchronized void closeSession(IClientSession session)
		throws SQLException
	{
		if (session == null)
		{
			throw new IllegalArgumentException("IClientSession == null");
		}

		if (!session.isClosed())
		{
			if (!_sessionsList.remove(session))
			{
				s_log.error("SessionManager.closeSession()-> Session " +
						session.getIdentifier() +
						" not found in _sessionsList when trying to remove it.");
			}
			if (_sessionsById.remove(session.getIdentifier()) == null)
			{
				s_log.error("SessionManager.closeSession()-> Session " +
						session.getIdentifier() +
						" not found in _sessionsById when trying to remove it.");
			}
		}

		// TODO: Should have session listeners instead of these calls.
		session.getApplication().getPluginManager().sessionEnding(session);

		session.close();
	}

	/**
	 * Retrieve an array of all the sessions currently active.
	 *
	 * @return	array of all active sessions.
	 */
	public synchronized IClientSession[] getActiveSessions()
	{
		IClientSession[] ar = new IClientSession[_sessionsList.size()];
		return (IClientSession[])_sessionsList.toArray(ar);
	}

	/**
	 * Get the next session opened after the passed one.
	 *
	 * @return	The next session or the first one if the passed one is
	 * 			the last session.
	 */
	public synchronized ISession getNextSession(ISession session)
	{
		final int sessionCount = _sessionsList.size();
		int idx = _sessionsList.indexOf(session);
		if (idx != -1)
		{
			++idx;
			if (idx >= sessionCount)
			{
				idx = 0;
			}
			return (ISession)_sessionsList.get(idx);
		}

		s_log.error("SessionManager.getNextSession()-> Session " +
					session.getIdentifier() + " not found in _sessionsList");
		if (sessionCount > 0)
		{
			s_log.error("SessionManager.getNextSession()-> Returning first session");
			return (ISession)_sessionsList.getFirst();
		}
		s_log.error("SessionManager.getNextSession()-> List empty so returning passed session");
		return session;
	}

	/**
	 * Get the next session opened before the passed one.
	 *
	 * @return	The previous session or the last one if the passed one is
	 * 			the first session.
	 */
	public synchronized ISession getPreviousSession(ISession session)
	{
		final int sessionCount = _sessionsList.size();
		int idx = _sessionsList.indexOf(session);
		if (idx != -1)
		{
			--idx;
			if (idx < 0)
			{
				idx = sessionCount - 1;
			}
			return (ISession)_sessionsList.get(idx);
		}

		s_log.error("SessionManager.getPreviousSession()-> Session " +
					session.getIdentifier() + " not found in _sessionsList");
		if (sessionCount > 0)
		{
			s_log.error("SessionManager.getPreviousSession()-> Returning last session");
			return (ISession)_sessionsList.getLast();
		}
		s_log.error("SessionManager.getPreviousSession()-> List empty so returning passed session");
		return session;
	}

	/**
	 * Retrieve the session for the passed identifier.
	 * 
	 * @param	sessionID	ID of session we are trying to retrieve.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>IIdentifier</TT> passed.
	 */
	public ISession getSession(IIdentifier sessionID)
	{
		return (ISession)_sessionsById.get(sessionID);		
	}
}