package net.sourceforge.squirrel_sql.client.session.properties;
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
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionSheet;

/**
 * Factory to handle creation of property sheets for sessions.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionPropertiesSheetFactory
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(SessionPropertiesSheetFactory.class);

	/** Application API. */
	private IApplication _app;

	/**
	 * Collection of <TT>SessionPropertiesSheet</TT> objects that currently exist for
	 * sessions. Keyed by <TT>ISession.getIdentifier()</TT>.
	 */
	private Map _sheets = new HashMap();

	/** Singleton instance of this class. */
	private static SessionPropertiesSheetFactory s_instance =
		new SessionPropertiesSheetFactory();

	/**
	 * ctor. Private as class is a singleton.
	 */
	private SessionPropertiesSheetFactory()
	{
		super();
	}

	/**
	 * Return the single instance of this class.
	 *
	 * @return	the single instance of this class.
	 */
	public static SessionPropertiesSheetFactory getInstance()
	{
		return s_instance;
	}

	/**
	 * Initialize this class. This <EM>must</EM> be called prior to using this class.
	 */
	public static void initialize(IApplication app)
	{
		getInstance()._app = app;
	}

	/**
	 * Get a properties sheet for the passed session. If one already
	 * exists it will be brought to the front. If one doesn't exist it will be
	 * created.
	 *
	 * @param	session		The session that user has request property sheet for.
	 *
	 * @return	The maintenance sheet for the passed session.
	 *
	 * @throws	IllegalArgumentException	if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public synchronized SessionPropertiesSheet showSheet(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		SessionPropertiesSheet propsSheet = get(session);
		if (propsSheet == null)
		{
			propsSheet = new SessionPropertiesSheet(session);
			_sheets.put(session.getIdentifier(), propsSheet);
			_app.getMainFrame().addInternalFrame(propsSheet, true, null);

			// When properties sheet is closed remove it from the list
			// of property sheets.
			propsSheet.addInternalFrameListener(new PropertiesSheetListener());

			// When the	session is closed close its properties sheet.
			SessionSheet sessionSheet = session.getSessionSheet();
			sessionSheet.addInternalFrameListener(new SessionSheetListener());

			positionSheet(propsSheet);
		}

		propsSheet.moveToFront();
		propsSheet.setVisible(true);
		return propsSheet;
	}

	private SessionPropertiesSheet get(ISession session)
	{
		return (SessionPropertiesSheet) _sheets.get(session.getIdentifier());
	}

	private void positionSheet(SessionPropertiesSheet sheet)
	{
		GUIUtils.centerWithinDesktop(sheet);
		sheet.setVisible(true);
		sheet.moveToFront();
	}

	private final class SessionSheetListener extends InternalFrameAdapter
	{
		public void internalFrameClosed(InternalFrameEvent evt)
		{
			synchronized (SessionPropertiesSheetFactory.getInstance())
			{
				SessionSheet sessionSheet = (SessionSheet) evt.getInternalFrame();
				SessionPropertiesSheet propsSheet =
					(SessionPropertiesSheet) _sheets.remove(
						sessionSheet.getSession().getIdentifier());
				if (propsSheet != null)
				{
					propsSheet.dispose();
				}
				sessionSheet.removeInternalFrameListener(this);
			}
		}
	}

	private final class PropertiesSheetListener extends InternalFrameAdapter
	{
		public void internalFrameClosed(InternalFrameEvent evt)
		{
			synchronized (SessionPropertiesSheetFactory.getInstance())
			{
				SessionPropertiesSheet propsSheet =
					(SessionPropertiesSheet) evt.getInternalFrame();
				if (propsSheet != null)
				{
					propsSheet.removeInternalFrameListener(this);
					Object sheet = _sheets.remove(propsSheet.getSession().getIdentifier());
					if (sheet == null)
					{
						s_log.error(
							"SessionPropertiesSheet not found for session: "
								+ propsSheet.getSession().getIdentifier());
					}
				}
			}
		}
	}

}