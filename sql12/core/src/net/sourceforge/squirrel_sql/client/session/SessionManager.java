package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.session.event.ISessionListener;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.gui.DontShowAgainDialog;
import net.sourceforge.squirrel_sql.fw.gui.DontShowAgainResult;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLSchemaUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class manages sessions.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionManager
{
   private static final ILogger s_log = LoggerController.createLogger(SessionManager.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionManager.class);

   private ISession _activeSession;

   private final ArrayList<ISession> _sessionsList = new ArrayList<>();

   /** Map of sessions keyed by session ID. */
   private final Map<IIdentifier, ISession> _sessionsById = new HashMap<>();

   private EventListenerList listenerList = new EventListenerList();

   /** Factory used to generate session IDs. */
   private final IntegerIdentifierFactory _idFactory = new IntegerIdentifierFactory(1);
   private ArrayList<IAllowedSchemaChecker> _allowedSchemaCheckers = new ArrayList<>();
   private Hashtable<IIdentifier, String[]> _allowedSchemasBySessionID = new Hashtable<>();
   private Hashtable<IIdentifier, String[]> _allSchemasBySessionID = new Hashtable<>();
   private HashSet<IIdentifier> _inCloseSession = new HashSet<>();
   private Set<IIdentifier> _inCreateSession = Collections.synchronizedSet(new HashSet());

   public SessionManager()
   {
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
    *			Thrown if IApplication, ISQLDriver, SQLAlias,
    * 			or SQLConnection is passed as null.
    */
   public synchronized ISession createSession(IApplication app,
                                              ISQLDriver driver, SQLAlias alias,
                                              SQLConnection conn, String user,
                                              String password)
   {
      final IIdentifier sessionId = _idFactory.createIdentifier();
      try
      {
         _inCreateSession.add(sessionId);

         final Session sess = new Session(app, driver, alias, conn, user, password, sessionId);
         _sessionsList.add(sess);
         _sessionsById.put(sess.getIdentifier(), sess);

         fireSessionAdded(sess);
         setActiveSession(sess, false);

         return sess;
      }
      finally
      {
         _inCreateSession.remove(sessionId);

         // Needed instead to be in ConnectionInternalFrameHandler.createSessionFrame
         // Main.getApplication().getMainFrame().getMainFrameTitleHandler().updateMainFrameTitle();
      }
   }

   public boolean isInCreateSession()
   {
      return 0 < _inCreateSession.size();
   }



   public void setActiveSession(ISession session, boolean force)
   {
      if (session != _activeSession || force)
      {
         _activeSession = session;
         fireSessionActivated(session);
         Main.getApplication().getMainFrame().getMainFrameTitleHandler().updateMainFrameTitle();
      }
   }

   /**
    * Retrieve an array of all the sessions currently connected.
    *
    * @return array of all connected sessions.
    */
   public List<ISession> getOpenSessions()
   {
      return new ArrayList<>(_sessionsList);
   }

   /**
    * Retrieve the session that is currently activated within the
    * session manager. Any new sql worksheets etc will be created
    * against this session
    */
   public ISession getActiveSession()
   {
      return _activeSession;
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
      return _sessionsById.get(sessionID);
   }

   /**
    * Close a session.
    *
    * @param	session		Session to close.
    *
    * @return	<tt>true</tt> if session was closed else <tt>false</tt>.
    *
    * @throws	IllegalArgumentException
    *			Thrown if <TT>null</TT>ISession passed.
    */
   public boolean closeSession(ISession session)
   {
      return closeSession(session, false);
   }

   public boolean closeSession(ISession session, boolean doNothingAndReturnTrueWhenInClosing)
   {

      if(isInCloseSession(session))
      {
         if (doNothingAndReturnTrueWhenInClosing)
         {
            return true;
         }
         else
         {
            return false;
         }
      }

      try
      {
         _inCloseSession.add(session.getIdentifier());
         if (confirmClose(session))
         {
            // TODO: Should have session listeners instead of these calls.
            session.getApplication().getPluginManager().sessionEnding(session);

            fireSessionClosing(session);
            try
            {
               session.close();
            }
            catch (SQLException sqle)
            {
               s_log.error("Error closing Session", sqle);
               session.showErrorMessage(s_stringMgr.getString("SessionManager.ErrorClosingSession", sqle));
            }
            fireSessionClosed(session);

            final IIdentifier sessionId = session.getIdentifier();
            if (!_sessionsList.remove(session))
            {
               s_log.error("SessionManager.closeSession()-> Session " +
                     sessionId +
                     " not found in _sessionsList when trying to remove it.");
            }
            if (_sessionsById.remove(sessionId) == null)
            {
               s_log.error("SessionManager.closeSession()-> Session " +
                     sessionId +
                     " not found in _sessionsById when trying to remove it.");
            }

            if (_sessionsList.isEmpty())
            {
               fireAllSessionsClosed();
            }

            // Activate another session since the current
            // active session has closed.
            if (session == _activeSession)
            {
               if (!_sessionsList.isEmpty())
               {
                  setActiveSession(_sessionsList.get(_sessionsList.size() - 1), false);
               }
               else
               {
                  _activeSession = null;
               }
            }
            else
            {
               setActiveSession(_activeSession, true);
            }

            _allowedSchemasBySessionID.remove(session.getIdentifier());
            _allSchemasBySessionID.remove(session.getIdentifier());

            return true;
         }
      }
      catch (Throwable ex)
      {
         s_log.error("Error closing Session", ex);
         session.showErrorMessage(s_stringMgr.getString("SessionManager.ErrorClosingSession", ex));
      }
      finally
      {
         _inCloseSession.remove(session.getIdentifier());
      }

      return false;
   }

   public boolean isInCloseSession(ISession session)
   {
      return _inCloseSession.contains(session.getIdentifier());
   }

   /**
    * Closes all currently open sessions.
    *
    * @return	<tt>true</tt> if all sessions closed else <tt>false</tt>.
    *
    * @throws	SQLException
    * 			Thrown if an error closing the SQL connection. The session
    * 			will still be closed even though the connection may not have
    *			been.
    */
   public boolean closeAllSessions()
   {
      // Get an array since we dont want trouble with the sessionsList when
      // we remove the sessions from it.
      List<ISession> sessions = getOpenSessions();

      for (ISession session : sessions)
      {
         if (!closeSession(session))
         {
            return false;
         }
      }
      return true;
   }

   public boolean closeAllButCurrentSessions()
   {
      ISession activeSession = getActiveSession();

      // Get an array since we dont want trouble with the sessionsList when
      // we remove the sessions from it.
      List<ISession> sessions = getOpenSessions();

      for (int i = sessions.size() - 1; i >= 0; i--)
      {
         if(sessions.get(i) == activeSession)
         {
            continue; 
         }

         if (!closeSession(sessions.get(i)))
         {
            return false;
         }
      }
      return true;
   }


   /**
    * Adds a session listener
    *
    * @param	lis		The listener to add.
    */
   public void addSessionListener(ISessionListener lis)
   {
      if (lis != null)
      {
         listenerList.add(ISessionListener.class, lis);
      }
      else
      {
         s_log.error("Attempted to add null listener: SessionManager.addSessionListener");
      }
   }

   /**
    * Removes a session listener
    *
    * @param	lis		The listener to remove.
    */
   public void removeSessionListener(ISessionListener lis)
   {
      if (lis != null)
      {
         listenerList.remove(ISessionListener.class, lis);
      }
      else
      {
         s_log.error("Attempted to remove null listener: SessionManager.addSessionListener");
      }
   }

   /**
    * Fired when a session is connected (added) to the session
    * manager
    */
   private void fireSessionAdded(ISession session)
   {
      Object[] listeners = listenerList.getListenerList();
      SessionEvent evt = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            // Lazily create the event:
            if (evt == null)
               evt = new SessionEvent(session);
            ((ISessionListener)listeners[i + 1]).sessionConnected(evt);
         }
      }
   }

   /**
    * Fired when a session is closed (removed) from the session manager
    */
   private void fireSessionClosed(ISession session)
   {
      Object[] listeners = listenerList.getListenerList();
      SessionEvent evt = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            // Lazily create the event:
            if (evt == null)
               evt = new SessionEvent(session);
            ((ISessionListener)listeners[i + 1]).sessionClosed(evt);
         }
      }
   }

   /**
    * Fired when a session is about to close from the session manager
    */
   private void fireSessionClosing(ISession session)
   {
      Object[] listeners = listenerList.getListenerList();
      SessionEvent evt = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            // Lazily create the event:
            if (evt == null)
            {
               evt = new SessionEvent(session);
            }
            ((ISessionListener)listeners[i + 1]).sessionClosing(evt);
         }
      }
   }

   /**
    * Fired when all the session have been closed (removed) from the
    * session manager
    */
   private void fireAllSessionsClosed()
   {
      Object[] listeners = listenerList.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            ((ISessionListener)listeners[i + 1]).allSessionsClosed();
         }
      }
   }

   /**
    * Fired when the active session changed
    */
   private void fireSessionActivated(ISession session)
   {
      Object[] listeners = listenerList.getListenerList();
      SessionEvent evt = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            // Lazily create the event:
            if (evt == null)
            {
               evt = new SessionEvent(session);
            }

            ((ISessionListener)listeners[i + 1]).sessionActivated(evt);
         }
      }
   }

   /**
    * Confirm whether session is to be closed.
    *
    * @param	session		Session being closed.
    *
    * @return	<tt>true</tt> if confirmed to close session.
    */
   private boolean confirmClose(ISession session)
   {
      if (!Main.getApplication().getSquirrelPreferences().getConfirmSessionClose())
      {
         return session.confirmClose();
      }

      String msg = s_stringMgr.getString("SessionManager.confirmClose", session.getTitle());
      String switchOnHowTo = s_stringMgr.getString("SessionManager.confirmClose.switchOnHowTo", session.getTitle());

      final DontShowAgainDialog confirmCloseDialog = new DontShowAgainDialog(SessionUtils.getOwningFrame(session), msg, switchOnHowTo);
      confirmCloseDialog.setTitle(s_stringMgr.getString("SessionManager.confirmClose.title"));
      DontShowAgainResult result = confirmCloseDialog.showAndGetResult("SessionManagerDontShowAgainId", 380, 150);

      if(result.isDontShowAgain())
      {
         Main.getApplication().getSquirrelPreferences().setConfirmSessionClose(false);
      }

      if (false == result.isYes())
      {
         return false;
      }
      else
      {
         return session.confirmClose();
      }
   }

   void fireConnectionClosedForReconnect(Session session)
   {
      Object[] listeners = listenerList.getListenerList();
      SessionEvent evt = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            // Lazily create the event:
            if (evt == null)
            {
               evt = new SessionEvent(session);
            }
            ((ISessionListener) listeners[i + 1]).connectionClosedForReconnect(evt);
         }
      }
   }

   void fireReconnected(Session session)
   {
      Object[] listeners = listenerList.getListenerList();
      SessionEvent evt = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            // Lazily create the event:
            if (evt == null)
            {
               evt = new SessionEvent(session);
            }
            ((ISessionListener) listeners[i + 1]).reconnected(evt);
         }
      }
   }

   void fireReconnectFailed(Session session)
   {
      Object[] listeners = listenerList.getListenerList();
      SessionEvent evt = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            // Lazily create the event:
            if (evt == null)
            {
               evt = new SessionEvent(session);
            }
            ((ISessionListener) listeners[i + 1]).reconnectFailed(evt);
         }
      }
   }


   void fireSessionFinalized(final IIdentifier sessionIdentifier)
   {
      // invokeLater to make the call synchronto the event queue
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2)
            {
               if (listeners[i] == ISessionListener.class)
               {
                  ((ISessionListener)listeners[i + 1]).sessionFinalized(sessionIdentifier);
               }
            }
         }
      });
   }

   public void fireResultTabOfOpenSessionFinalized(IIdentifier sessionIdentifier)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2)
            {
               if (listeners[i] == ISessionListener.class)
               {
                  ((ISessionListener)listeners[i + 1]).resultTabOfOpenSessionFinalized(sessionIdentifier);
               }
            }
         }
      });
   }


   public void addAllowedSchemaChecker(IAllowedSchemaChecker allowedSchemaChecker)
   {
      _allowedSchemaCheckers.add(allowedSchemaChecker);
   }


   public boolean areAllSchemasAllowed(ISession session)
   {
      try
      {
         String[] allowedSchemas = getAllowedSchemas(session);
         String[] schemas = getAllSchemas(session.getSQLConnection(), session.getIdentifier());

         return allowedSchemas.length == schemas.length;
      }
      catch (SQLException e)
      {
         s_log.error("Failed to check allowed Schemas", e);
         return true;
      }
   }

   public String[] getAllowedSchemas(ISession session)
   {
      String[] allowedSchemas = _allowedSchemasBySessionID.get(session.getIdentifier());
      if(null == allowedSchemas)
      {
         allowedSchemas = getAllowedSchemas(session.getSQLConnection(), session.getAlias(), session.getIdentifier());
         _allowedSchemasBySessionID.put(session.getIdentifier(), allowedSchemas);
      }

      return allowedSchemas;

   }


   /**
    * Note: This Method does not cache allowed Schemas.
    * It is preferable to use getAllowedSchemas(ISession) if a Session is available.
    */
   public String[] getAllowedSchemas(ISQLConnection con, SQLAlias alias, IIdentifier sessionIdentifierOrNull)
   {
      try
      {
         // Do not do new HashMap() here.
         HashMap<String, Object> uniqueAllowedSchemas = null;

         for (int i = 0; i < _allowedSchemaCheckers.size(); i++)
         {
            String[] allowedSchemas = null;
            try
            {
               allowedSchemas = (_allowedSchemaCheckers.get(i)).getAllowedSchemas(con, alias);
            }
            catch (Exception e)
            {
               s_log.error("Failed to get allowed Schemas from Plugin", e);
            }

            if(null != allowedSchemas)
            {
               if(null == uniqueAllowedSchemas)
               {
                  uniqueAllowedSchemas = new HashMap<>();
               }

               for (int j = 0; j < allowedSchemas.length; j++)
               {
                  uniqueAllowedSchemas.put(allowedSchemas[j], null);
               }
            }
         }

         if(null == uniqueAllowedSchemas)
         {
            return getAllSchemas(con, sessionIdentifierOrNull);
         }
         else
         {
            ArrayList<String> list = new ArrayList<>(uniqueAllowedSchemas.keySet());
            Collections.sort(list);
            return list.toArray(new String[list.size()]);
         }
      }
      catch (Exception e)
      {
         s_log.error("Failed to get allowed Schemas", e);
         return new String[0];
      }
   }

   public String[] getAllSchemas(ISession session) throws SQLException
   {
      return getAllSchemas(session.getSQLConnection(), session.getIdentifier());
   }

   private String[] getAllSchemas(ISQLConnection con, IIdentifier sessionIdentifierOrNull) throws SQLException
   {
      if(null == sessionIdentifierOrNull)
      {
         return SQLSchemaUtil.toSchemaNameArray(con.getSQLMetaData().getSchemas());
      }


      String[] ret = _allSchemasBySessionID.get(sessionIdentifierOrNull);
      if(null == ret)
      {
         ret = SQLSchemaUtil.toSchemaNameArray(con.getSQLMetaData().getSchemas());
         _allSchemasBySessionID.put(sessionIdentifierOrNull, ret);
      }
      return ret;
   }

   public void clearSchemaCache(ISession session)
   {
      _allowedSchemasBySessionID.remove(session.getIdentifier());
      _allSchemasBySessionID.remove(session.getIdentifier());
   }

   public List<ISessionWidget> getSessionWindowsForSession(Session session)
   {
      return List.of(Main.getApplication().getWindowManager().getAllFramesOfSession(session.getIdentifier()));
   }
}
