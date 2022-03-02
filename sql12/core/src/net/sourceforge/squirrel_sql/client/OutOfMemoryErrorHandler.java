/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.List;

/**
 * Handler, that tries to free some memory. Mostly, if a
 * {@link OutOfMemoryError} occurs,the user not be able to save his work,
 * because the GUI is no longer responsible. To avoid this, we try to free some
 * memory by closing all SQL result tabs. Under normal conditions, the SQL
 * result tabs are components, which uses the most memory. So we have a high
 * chance to free enough memory to keep the GUI responsible, that the user can
 * save his work and restart SQuirrel. The scope is not, to protect the user
 * from getting a {@link OutOfMemoryError}.
 *
 * @author Stefan Willinger
 */
public class OutOfMemoryErrorHandler implements IOutOfMemoryErrorHandler
{

   private static final ILogger log = LoggerController.createLogger(OutOfMemoryErrorHandler.class);

   private static final StringManager stringMgr = StringManagerFactory.getStringManager(OutOfMemoryErrorHandler.class);

   interface i18n
   {
      String message = stringMgr.getString("OutOfMemoryErrorHandler.message");
   }


   /**
    * The application
    */
   private IApplication application;

   public OutOfMemoryErrorHandler()
   {
   }


   /**
    * Constructor setting the {@link #application}
    *
    * @param application
    */
   public OutOfMemoryErrorHandler(IApplication application)
   {
      setApplication(application);
   }


   /**
    * Gets the application
    *
    * @return the application
    */
   public IApplication getApplication()
   {
      return application;
   }

   /**
    * Sets the application.
    *
    * @param application the application to set
    * @throws IllegalArgumentException if application is null;
    */
   public void setApplication(IApplication application)
   {
      if (application == null)
      {
         throw new IllegalArgumentException("application must not be null");
      }
      this.application = application;
   }

   /**
    * To free some memory, close all SQL result-tabs of all current sessions.
    * This may free some memory.
    *
    * @see net.sourceforge.squirrel_sql.client.IOutOfMemoryErrorHandler#handleOutOfMemoryError()
    */
   public synchronized void handleOutOfMemoryError()
   {

      SessionManager sessionManager = application.getSessionManager();

      // All sessions, the user has opened
      List<ISession> sessions = sessionManager.getOpenSessions();

      if (sessions.size() > 0)
      {
         for (ISession session : sessions)
         {
            closeResultTabs(session);
         }
         showMessage(sessionManager);
      }
      else
      {
         log.info("A OutOfMemoryError occurred, but there are no sessions connected - so we can't free memory.");
      }

   }

   /**
    * Inform the user, that a {@link OutOfMemoryError} had occurred.
    *
    * @param sessionManager the sessionManager
    */
   private void showMessage(SessionManager sessionManager)
   {
      ISession activeSession = sessionManager.getActiveSession();
      if (activeSession != null)
      {
         log.info(i18n.message);
         activeSession.showErrorMessage(i18n.message);
         application.showErrorDialog(i18n.message);
      }
      else
      {
         log.info("A OutOfMemoryError occurred, but there are no active session!");
      }
   }

   /**
    * Close all result tabs of the session.
    *
    * @param session the session, where to close the result tabs.
    */
   private void closeResultTabs(ISession session)
   {
      session.getSessionInternalFrame().getMainSQLPanelAPI().closeAllSQLResultTabs(true);
   }

}
