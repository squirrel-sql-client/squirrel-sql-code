/*
 * SQLFilterSheetFactory.java
 *
 * Created on April 5, 2003, 9:57 AM
 */

package net.sourceforge.squirrel_sql.client.session.sqlfilter;

/*
 * Copyright (C) 2003 Maury Hammel
 * mjhammel@users.sourceforge.net
 *
 * Adapted from SessionPropertiesSheetFactory.java by Colin Bell.
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
import java.util.Map;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionSheet;

/**
 * Factory to handle creation of property sheets for sessions.
 *
 * @author  <A HREF="mailto:mjhammel@users.sourceforge.net">Maury Hammel</A>
 */
public class SQLFilterSheetFactory
{
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(SQLFilterSheetFactory.class);

	/** Application API. */
	private IApplication _app;

	/** 
	 * Collection of <TT>SQLFilterSheet</TT> objects that currently exist for
	 * sessions. Keyed by <TT>ISession.getIdentifier()</TT>.
	 */
	private Map _sheets = new HashMap();

	/** Singleton instance of this class. */
	private static SQLFilterSheetFactory s_instance = new SQLFilterSheetFactory();

	/** Creates a new instance of SQLFilterSheetFactory */
	public SQLFilterSheetFactory()
	{
		super();
	}
	
	/**
	 * Return the single instance of this class.
	 *
	 * @return	the single instance of this class.
	 */
	public static SQLFilterSheetFactory getInstance()
	{
		return s_instance;
	}

	/** Initialize this class. This <EM>must</EM> be called prior to using this class.
   * @param app The instance of the SQuirreL application
   */
	public static void initialize(IApplication app)
	{
		getInstance()._app = app;
	}

	/** Get a SQL Filter sheet for the passed session. If one already exists it will be brought to the front.
   * If one doesn't exist it will be created.
   * @param session The session for which the user has requested the SQL Filter sheet.
   * @return The maintenance sheet for the passed session.
   * @param objectInfo An instance of a class containing information about the database metadata.
   *
   */
	public synchronized SQLFilterSheet showSheet(ISession session, IDatabaseObjectInfo objectInfo)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
    if (objectInfo == null)
    {
      throw new IllegalArgumentException("IDatabaseObjectInfo == null");
    }

		SQLFilterSheet sqlFilterSheet = get(session);
		if (sqlFilterSheet == null)
		{
			sqlFilterSheet = new SQLFilterSheet(session, objectInfo);
			_sheets.put(session.getIdentifier(), sqlFilterSheet);
			_app.getMainFrame().addInternalFrame(sqlFilterSheet, true, null);

			// When properties sheet is closed remove it from the list of property sheets.
			sqlFilterSheet.addInternalFrameListener(new PropertiesSheetListener());

			// When the	session is closed close its properties sheet.
			SessionSheet sessionSheet = session.getSessionSheet();
			sessionSheet.addInternalFrameListener(new SessionSheetListener());

			positionSheet(sqlFilterSheet);
		}

		sqlFilterSheet.moveToFront();
		sqlFilterSheet.setVisible(true);
		return sqlFilterSheet;
	}

  /** Get the unique id associated with the passed session
   * @param session The current SQuirreL session
   * @return Returns a value uniquely identifying the session
   */  
	private SQLFilterSheet get(ISession session)
	{
		return (SQLFilterSheet) _sheets.get(session.getIdentifier());
	}

  /** Position and display the sheet on the desktop.
   * @param sheet The sheet to be displayed.
   */  
	private void positionSheet(SQLFilterSheet sheet)
	{
		GUIUtils.centerWithinDesktop(sheet);
		sheet.setVisible(true);
		sheet.moveToFront();
	}

  /** A listener used to determine when the Session sheet is closed. */  
	private final class SessionSheetListener extends InternalFrameAdapter
	{
    /** Dispose of the Filter sheets when the session is closed.
     * @param evt The event triggered by the closing of the session frame.
     */    
		public void internalFrameClosed(InternalFrameEvent evt)
		{
			synchronized (SQLFilterSheetFactory.getInstance())
			{
				SessionSheet sessionSheet = (SessionSheet) evt.getInternalFrame();
				SQLFilterSheet sqlFilterSheet = (SQLFilterSheet)_sheets.remove(sessionSheet.getSession().getIdentifier());
				if (sqlFilterSheet != null)
				{
					sqlFilterSheet.dispose();
				}
				sessionSheet.removeInternalFrameListener(this);
			}
		}
	}

	private final class PropertiesSheetListener extends InternalFrameAdapter
	{
		public void internalFrameClosed(InternalFrameEvent evt)
		{
			synchronized (SQLFilterSheetFactory.getInstance())
			{
				SQLFilterSheet sqlFilterSheet = (SQLFilterSheet) evt.getInternalFrame();
				if (sqlFilterSheet != null)
				{
					sqlFilterSheet.removeInternalFrameListener(this);
					Object sheet = _sheets.remove(sqlFilterSheet.getSession().getIdentifier());
					if (sheet == null)
					{
						s_log.error("SQLFilterSheet not found for session: " + sqlFilterSheet.getSession().getIdentifier());
					}
				}
			}
		}
	}
}
