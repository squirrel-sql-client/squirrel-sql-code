package net.sourceforge.squirrel_sql.client.session.properties;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.util.HashMap;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionSheet;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

/**
 * Factory to handle creation of property sheets for sessions.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionPropertiesSheetFactory {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(SessionPropertiesSheetFactory.class);

	/** Application API. */
	private IApplication _app;

	/**
	 * Collection of <TT>SessionPropertiesSheet</TT> objects that currently exist for
	 * sessions. Keyed by <TT>ISession.getIdentifier()</TT>.
	 */
	private Map _sheets = new HashMap();

	/** Singleton instance of this class. */
	private static SessionPropertiesSheetFactory s_instance = new SessionPropertiesSheetFactory();

	/**
	 * ctor. Private as class is a singleton.
	 */
	private SessionPropertiesSheetFactory() {
		super();
	}

	/**
	 * Return the single instance of this class.
	 * 
	 * @return	the single instance of this class.
	 */
	public static SessionPropertiesSheetFactory getInstance() {
		return s_instance;
	}

	/**
	 * Initialize this class. This <EM>must</EM> be called prior to using this class.
	 */
	public static void initialize(IApplication app) {
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
	public synchronized SessionPropertiesSheet showSheet(ISession session) {
		if (session == null) {
			throw new IllegalArgumentException("ISession == null");
		}

		SessionPropertiesSheet propsSheet = get(session);
		if (propsSheet == null) {
			 propsSheet = new SessionPropertiesSheet(session);
			_sheets.put(session.getIdentifier(), propsSheet);
			_app.getMainFrame().addInternalFrame(propsSheet, true, null);

			session.getSessionSheet().addInternalFrameListener(new SessionSheetListener());

			// When the	session is closed close its properties sheet.
			JInternalFrame sessionSheet = session.getSessionSheet();
			sessionSheet.addInternalFrameListener(new InternalFrameAdapter() {
				public void internalFrameClosed(InternalFrameEvent evt) {
					synchronized(SessionPropertiesSheetFactory.getInstance()) {
						SessionSheet sessionSheet = (SessionSheet)evt.getInternalFrame();
						SessionPropertiesSheet propsSheet = (SessionPropertiesSheet)_sheets.remove(sessionSheet.getSession().getIdentifier());
						if (propsSheet != null) {
							propsSheet.dispose();
						}
						sessionSheet.removeInternalFrameListener(this);
					}
				}
			});

			positionSheet(propsSheet);
		}

		propsSheet.moveToFront();
		propsSheet.setVisible(true);
		return propsSheet;
	}

	private SessionPropertiesSheet get(ISession session) {
		return (SessionPropertiesSheet)_sheets.get(session.getIdentifier());
	}

	private void positionSheet(SessionPropertiesSheet sheet) {
		GUIUtils.centerWithinDesktop(sheet);
		sheet.setVisible(true);
		sheet.moveToFront();
	}

//	private final class SessionInfo {
//		private ISession _session;
//		private SessionPropertiesSheet _propsSheet;
//		private PropertiesSheetListener _propsSheetListener;
//		private SessionSheetListener _sessionSheetListener;

//		SessionInfo(ISession session) {
//			super();
//			_session = session;
//			_propsSheet = new SessionPropertiesSheet(session);
//			_propsSheetListener = new PropertiesSheetListener();
//			_propsSheet.addInternalFrameListener(_propsSheetListener);
//			session.getSessionSheet().addInternalFrameListener(_sessionSheetListener);
//		}

//		void cleanup() {
//			_propsSheet.removeInternalFrameListener(_propsSheetListener);
//			_propsSheet.dispose();
//			_propsSheet = null;
//			_propsSheetListener = null;
//			_session.getSessionSheet().removeInternalFrameListener(_sessionSheetListener);
//			_sessionSheetListener = null;
//			_session = null;
//		}
//	}

//	private final class PropertiesSheetListener extends InternalFrameAdapter {
//		public void internalFrameClosing(InternalFrameEvent evt) {
//			synchronized(SessionPropertiesSheetFactory.getInstance()) {
//				s_log.debug("Removing object from _sheets");
//			}
//		}
//	}

	private final class SessionSheetListener extends InternalFrameAdapter {
		public void internalFrameClosed(InternalFrameEvent evt) {
			synchronized(SessionPropertiesSheetFactory.getInstance()) {
				SessionSheet sessionSheet = (SessionSheet)evt.getInternalFrame();
				SessionPropertiesSheet propsSheet = (SessionPropertiesSheet)_sheets.remove(sessionSheet.getSession().getIdentifier());
				if (propsSheet != null) {
					propsSheet.dispose();
				}
				sessionSheet.removeInternalFrameListener(this);
			}
		}
	}
}
