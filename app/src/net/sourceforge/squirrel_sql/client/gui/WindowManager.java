package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2003-2004 Colin Bell
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
import java.awt.Window;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.EventListenerList;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.session.BaseSessionSheet;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.properties.EditWhereColsSheet;
import net.sourceforge.squirrel_sql.client.session.properties.SessionPropertiesSheet;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.SQLFilterSheet;
/**
 * This class manages the windows for the application.
 *
 * TODO: Correct these notes
 * <p>When a session closes the window manager will ensure that
 * all of the windows for that sesion are closed.
 * <p>Similarily when a window is closed the windows manager will ensure that
 * references to the window are removed for the session.
 *
 * JASON: Prior to this patch there was some code movement from this class to
 * Sessionmanager. The idea being that Sessionmanager was the controller.
 * Do we still want to do this? Remember in the future there will probably be
 * an SDI as well as MDI version of the windows.
 *
 * JASON: Rename class to WindowManager as I intend it to manage all windows,
 * not just session ones.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 * @author <A HREF="mailto:jmheight@users.sourceforge.net">Jason Height</A>
 */
public class WindowManager
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(WindowManager.class);

	/** Internationalized strings for this class. */
//	private static final StringManager s_stringMgr =
//		StringManagerFactory.getStringManager(WindowManager.class);

	/** Application API. */
	private final IApplication _app;

	/** Applications main frame. */
	private final MainFrame _mainFrame;

	/**
	 * Map of windows(s) that are currently open for a session, keyed by
	 * session ID.
	 */
	private final Map _sessionWindows = new HashMap();

	private final SessionWindowListener _windowListener = new SessionWindowListener();

	private int _lastSessionIdx = 1;

	// JASON: Mow that multiple object trees exist storing the edit
	// where by objectInfo within session won't work. It needs to be objectinfo
	// within something else.
//	private final Map _editWhereColsSheets = new HashMap();

	private final SessionListener _sessionListener = new SessionListener();

	private EventListenerList _listenerList = new EventListenerList();

	private boolean _sessionClosing = false;

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public WindowManager(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
		_mainFrame = new MainFrame(app);
		_app.getSessionManager().addSessionListener(_sessionListener);
	}

	/**
	 * Retrieve applications main frame.
	 *
	 * @return	Applciations main frame.
	 */
	public MainFrame getMainFrame()
	{
		return _mainFrame;
	}

	/**
	 * Retrieve the main internal frame for the passed session. Can be <TT>null</TT>
	 *
	 * @return	the internal frame for the passed session. Can be <TT>null</TT>.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if ISession is passed as null.
	 */
	public JInternalFrame getMainInternalFrame(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		final List sheets = (List)_sessionWindows.get(session.getIdentifier());
		if (sheets != null)
		{
			for (Iterator it = sheets.iterator(); it.hasNext();)
			{
				final Object sheet = it.next();
				if (sheet instanceof SessionInternalFrame)
				{
					final SessionInternalFrame sif = (SessionInternalFrame)sheet;
					if (sif.getSession().equals(session))
					{
						return sif;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Registers a sheet that is attached to a session. This sheet will
	 * be automatically closed when the session is closing.
	 * <p/><b>There is no need to call this method manually.</b> Any
	 * classes that properly extend BaseSessionSheet will be registered.
	 */
	public synchronized void registerSessionSheet(BaseSessionSheet sheet)
	{
		s_log.debug("Registering " + sheet.getClass().getName() + " in WindowManager");
//		sheet.setTitle(createTitleForChild(sheet));
		final IIdentifier sessionIdentifier = sheet.getSession().getIdentifier();
		List windowList = (List)_sessionWindows.get(sessionIdentifier);
		if (windowList == null)
		{
			windowList = new ArrayList();
			_sessionWindows.put(sessionIdentifier, windowList);
		}

		final int idx = windowList.size();
		if ( idx > 0)
		{
			sheet.setTitle(sheet.getTitle() + " (" + idx + ")");
		}
		windowList.add(sheet);
		sheet.addInternalFrameListener(_windowListener);
	}

	/**
	 * Adds a listener to the sheets attached to this session <p/>When new
	 * sheets are constructed, they are automatically added to the session via
	 * the registerSessionSheet method. <p/>All other listener events fire due
	 * to interaction with the frame. <p/>The
	 * InternalFrameListener.internalFrameOpened is a good location to tailor
	 * the session sheets (ie internal frame) from a plugin. Examples can be
	 * found in the oracle plugin of how to modify how a session sheet.
	 */
	public void addSessionSheetListener(InternalFrameListener listener)
	{
		if (listener == null)
		{
			throw new IllegalArgumentException("InternalFrameListener == null");
		}

		_listenerList.add(InternalFrameListener.class, listener);
	}

	protected void refireSessionSheetOpened(InternalFrameEvent evt)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == InternalFrameListener.class)
			{
				((InternalFrameListener)listeners[i + 1]).internalFrameOpened(evt);
			}
		}
	}

	protected void refireSessionSheetClosing(InternalFrameEvent evt)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == InternalFrameListener.class)
			{
				((InternalFrameListener)listeners[i + 1]).internalFrameClosing(evt);
			}
		}
	}

	protected void refireSessionSheetClosed(InternalFrameEvent evt)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == InternalFrameListener.class)
			{
				((InternalFrameListener)listeners[i + 1]).internalFrameClosed(evt);
			}
		}
	}

	protected void refireSessionSheetIconified(InternalFrameEvent evt)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == InternalFrameListener.class)
			{
				((InternalFrameListener)listeners[i + 1]).internalFrameIconified(evt);
			}
		}
	}

	protected void refireSessionSheetDeiconified(InternalFrameEvent evt)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == InternalFrameListener.class)
			{
				((InternalFrameListener)listeners[i + 1])
						.internalFrameDeiconified(evt);
			}
		}
	}

	protected void refireSessionSheetActivated(InternalFrameEvent evt)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == InternalFrameListener.class)
			{
				((InternalFrameListener)listeners[i + 1]).internalFrameActivated(evt);
			}
		}
	}

	protected void refireSessionSheetDeactivated(InternalFrameEvent evt)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == InternalFrameListener.class)
			{
				((InternalFrameListener)listeners[i + 1]).internalFrameDeactivated(evt);
			}
		}
	}

	/**
	 * Create a new internal frame for the passed session.
	 *
	 * @param	session		Session we are creating internal frame for.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if ISession is passed as null.
	 */
	public synchronized SessionInternalFrame createInternalFrame(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		return new SessionInternalFrame(session);
	}

	/**
	 * Creates a new SQL View internal frame for the passed session.
	 *
	 * @param	session		Session we are creating internal frame for.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if ISession is passed as null.
	 */
	public synchronized SQLInternalFrame createSQLInternalFrame(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		return new SQLInternalFrame(session);
	}

	/**
	 * Get a properties dialog for the passed session. If one already
	 * exists it will be brought to the front. If one doesn't exist it will be
	 * created.
	 *
	 * @param	session		The session that user has request property dialog for.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
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
			_app.getMainFrame().addInternalFrame(propsSheet, true, null);
			positionSheet(propsSheet);
		}
		else
		{
			moveToFront(propsSheet);
		}
	}

	/**
	 * Get an SQL Filter sheet for the passed data. If one already exists it
	 * will be brought to the front. If one doesn't exist it will be created.
	 *
	 * @param	objectTree
	 * @param	objectInfo	An instance of a class containing information about
	 * 						the database metadata.
	 *
	 * @return	The filter dialog.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if <tt>null</tt> <tt>ContentsTab</tt>,
	 *			<tt>IObjectTreeAPI</tt>, or <tt>IDatabaseObjectInfo</tt> passed.
	 */
	public synchronized SQLFilterSheet showSQLFilterDialog(IObjectTreeAPI objectTree,
											IDatabaseObjectInfo objectInfo)
	{
		if (objectTree == null)
		{
			throw new IllegalArgumentException("IObjectTree == null");
		}
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo == null");
		}

//		final ISession session = objectTree.getSession();
		SQLFilterSheet sqlFilterSheet = getSQLFilterSheet(objectTree, objectInfo);
		if (sqlFilterSheet == null)
		{
			sqlFilterSheet = new SQLFilterSheet(objectTree, objectInfo);
			_app.getMainFrame().addInternalFrame(sqlFilterSheet, true, null);
			positionSheet(sqlFilterSheet);
		}
		else
		{
			moveToFront(sqlFilterSheet);
		}

		return sqlFilterSheet;
	}

	/**
	 * Get a EditWhereCols sheet for the passed session. If one already exists it
	 * will be brought to the front. If one doesn't exist it will be created.
	 *
	 * @param	tree		Object tree containing the table.
	 * @param	objectInfo	An instance of a class containing information about
	 * 						the database metadata.
	 *
	 * @return	The maintenance sheet for the passed session.
	 */
	public synchronized EditWhereColsSheet showEditWhereColsDialog(IObjectTreeAPI tree,
											IDatabaseObjectInfo objectInfo)
	{
		if (tree == null)
		{
			throw new IllegalArgumentException("IObjectTreeAPI == null");
		}
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo == null");
		}

		ISession session = tree.getSession();
		EditWhereColsSheet editWhereColsSheet = getEditWhereColsSheet(session, objectInfo);
		if (editWhereColsSheet == null)
		{
//			 JASON: Needs to be done same as the others
			editWhereColsSheet = new EditWhereColsSheet(session, objectInfo);
//			Map map = getAllEditWhereColsSheets(tree);
//			map.put(objectInfo.getQualifiedName(), editWhereColsSheet);
			_app.getMainFrame().addInternalFrame(editWhereColsSheet, true, null);
//			editWhereColsSheet.addInternalFrameListener(_editWhereColsDialogListener);
			positionSheet(editWhereColsSheet);
		}
		else
		{
			moveToFront(editWhereColsSheet);
		}

		return editWhereColsSheet;
	}


	/**
	 * Close all sessions.
	 */
//	public synchronized boolean closeAllSessions()
//	{
//		SessionInternalFrame[] ar = new SessionInternalFrame[_internalFrames.size()];
//		_internalFrames.values().toArray(ar);
//		for (int i = 0; i < ar.length; ++i)
//		{
//			final ISession session = getSession(ar[i]);
//			if (session != null)
//			{
//				if (!closeSession(session))
//				{
//					return false;
//				}
//			}
//		}
//		return true;
//	}

	/**
	 * Close the passed session.
	 *
	 * @param	session		Session to be closed.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if ISession is passed as null.
	 *
	 * @returns <tt>true</tt> if the session was closed.
	 */
//	public synchronized boolean closeSession(ISession session)
//	{
//		if (session == null)
//		{
//			throw new IllegalArgumentException("ISession == null");
//		}
//
//		if (confirmClose(session))
//		{
////			getInternalFrame(session).dispose();
//			privateCloseSession(session);
//			return true;
//		}
//		return false;
//	}

	public void moveToFront(Window win)
	{
		if (win != null)
		{
			win.toFront();
			win.setVisible(true);
		}
	}

	public void moveToFront(JInternalFrame fr)
	{
		if (fr != null)
		{
			fr.moveToFront();
			fr.setVisible(true);
			try
			{
				fr.setSelected(true);
			}
			catch (PropertyVetoException ex)
			{
				s_log.error("Error bringing internal frame to the front", ex);
			}
		}
	}

//	private boolean confirmClose(ISession session)
//	{
//		if (!_app.getSquirrelPreferences().getConfirmSessionClose())
//		{
//			return true;
//		}
//
//		final String msg = s_stringMgr.getString("WindowManager.confirmClose",
//							session.getTitle());
//		return Dialogs.showYesNo(_app.getMainFrame(), msg);
//	}

	private SessionPropertiesSheet getSessionPropertiesDialog(ISession session)
	{
		List sheets = (List)_sessionWindows.get(session.getIdentifier());
		if (sheets != null)
		{
			for (Iterator sheetItr = sheets.iterator(); sheetItr.hasNext();)
			{
				Object sheet = sheetItr.next();
				if (sheet instanceof SessionPropertiesSheet)
					return (SessionPropertiesSheet)sheet;
			}
		}
		return null;
	}

	private SQLFilterSheet getSQLFilterSheet(IObjectTreeAPI tree,
												IDatabaseObjectInfo objectInfo)
	{
		final ISession session = tree.getSession();
		final List sheets = (List)_sessionWindows.get(session.getIdentifier());
		if (sheets != null)
		{
			for (Iterator it = sheets.iterator(); it.hasNext();)
			{
				final Object sheet = it.next();
				if (sheet instanceof SQLFilterSheet)
				{
					final SQLFilterSheet sfs = (SQLFilterSheet)sheet;
					if (sfs.getObjectTree() == tree &&
							objectInfo.equals(sfs.getDatabaseObjectInfo()))
					{
						return sfs;
					}
				}
			}
		}
		return null;
	}

	private EditWhereColsSheet getEditWhereColsSheet(ISession session,
											IDatabaseObjectInfo objectInfo)
	{
//		final Map map = getAllEditWhereColsSheets(tree);
//		return (EditWhereColsSheet)map.get(objectInfo.getQualifiedName());
		final List sheets = (List)_sessionWindows.get(session.getIdentifier());
		if (sheets != null)
		{
			for (Iterator it = sheets.iterator(); it.hasNext();)
			{
				final Object sheet = it.next();
				if (sheet instanceof EditWhereColsSheet)
				{
					final EditWhereColsSheet sfs = (EditWhereColsSheet)sheet;
//					if (sfs.getObjectTree() == tree &&
//							objectInfo.equals(sfs.getDatabaseObjectInfo()))
					if (objectInfo.equals(sfs.getDatabaseObjectInfo()))
					{
						return sfs;
					}
				}
			}
		}
		return null;
	}

	// JASON: FIX THIS
//	private Map getAllEditWhereColsSheets(IObjectTreeAPI tree)
//	{
//		Map map = (Map)_editWhereColsSheets.get(tree.getIdentifier());
//		if (map == null)
//		{
//			map = new HashMap();
//			_editWhereColsSheets.put(session.getIdentifier(), map);
//		}
//		return map;
//	}

//	private String createTitleForChild(BaseSessionSheet child)
//	{
//		// TODO: Remove instanceof
//		if (child instanceof SessionInternalFrame)
//		{
//			return child.getTitle();// + " (" + _lastSessionIdx++ + ")";
//		}
//
//		if (child instanceof SQLInternalFrame)
//		{
//			return child.getTitle();// + " (" + _lastSessionIdx++ + ")";
//		}
//
//		if (child instanceof ObjectTreeInternalFrame)
//		{
//			return child.getTitle();// + " (" + _lastSessionIdx++ + ")";
//		}
//
//		return "???????????????";// TODO:
//	}

	private void positionSheet(JInternalFrame jif)
	{
		GUIUtils.centerWithinDesktop(jif);
		moveToFront(jif);
	}

	private void selectFrontWindow()
	{
		final JDesktopPane desktop = _app.getMainFrame().getDesktopPane();
		if (desktop != null)
		{
			final JInternalFrame[] jifs = desktop.getAllFrames();
			if (jifs != null && jifs.length > 0)
			{
				moveToFront(jifs[0]);
			}
		}
	}

	// JASON: Needs to be done elsewhere
//	private synchronized void editWhereColsDialogClosed(EditWhereColsSheet sfs)
//	{
//		if (sfs != null)
//		{
//			sfs.removeInternalFrameListener(_editWhereColsDialogListener);
//			Map map = getAllEditWhereColsSheets(sfs.getSession());
//			String key = sfs.getDatabaseObjectInfo().getQualifiedName();
//			if (map.remove(key) == null)
//			{
//				s_log.error("Unable to find EditWhereColsSheet for " + key);
//			}
//		}
//	}

	// JASON: Do this elsewhere
//	private final class EditWhereColsDialogListener extends InternalFrameAdapter
//	{
//		public void internalFrameClosed(InternalFrameEvent evt)
//		{
//			EditWhereColsSheet sfs = (EditWhereColsSheet)evt.getInternalFrame();
//			WindowManager.this.editWhereColsDialogClosed(sfs);
//		}
//	}

	private final class SessionWindowListener implements InternalFrameListener
	{
		public void internalFrameOpened(InternalFrameEvent e)
		{
			refireSessionSheetOpened(e);
		}

		public void internalFrameClosing(InternalFrameEvent e)
		{
			refireSessionSheetClosing(e);
		}

		public void internalFrameClosed(InternalFrameEvent e)
		{
			//Only remove the frame if the entire session is not closing
			if (!_sessionClosing)
			{
				// Find the internal Frame in the list of internal frames
				// and remove it.
				BaseSessionSheet sessionSheet = (BaseSessionSheet)e.getInternalFrame();
				List sessionSheets = (List)_sessionWindows.get(sessionSheet.getSession().getIdentifier());
				if (sessionSheets != null)
				{
					for (Iterator sheetItr = sessionSheets.iterator();
							sheetItr.hasNext();)
					{
						Object sheet = sheetItr.next();
						if (sheet == sessionSheet)
						{
							sheetItr.remove();
							WindowManager.this.selectFrontWindow();
							break;
						}
					}
				}
			}
			refireSessionSheetClosed(e);
		}

		public void internalFrameIconified(InternalFrameEvent e)
		{
			refireSessionSheetIconified(e);
		}

		public void internalFrameDeiconified(InternalFrameEvent e)
		{
			refireSessionSheetDeiconified(e);
		}

		public void internalFrameActivated(InternalFrameEvent e)
		{
			refireSessionSheetActivated(e);
		}

		public void internalFrameDeactivated(InternalFrameEvent e)
		{
			refireSessionSheetDeactivated(e);
		}
	}

	/**
	 * Used to update the UI depending on various session events.
	 */
	private final class SessionListener extends SessionAdapter
	{
		/**
		 * Session has been connected to a database.
		 */
		public void sessionConnected(SessionEvent evt)
		{
			// Add the message handler to the session
			evt.getSession().setMessageHandler(_app.getMessageHandler());
		}

		/**
		 * A session has been activated.
		 */
		public void sessionActivated(SessionEvent evt)
		{
			final ISession newSession = evt.getSession();

			// Allocate the current session to the actions.
			for (Iterator it = _app.getActionCollection().actions();
					it.hasNext();)
			{
				final Action act = (Action)it.next();
				if (act instanceof ISessionAction)
				{
					((ISessionAction)act).setSession(newSession);
				}
			}

			// If the active window isn't for the currently selected session
			// then select the main window for the session.
			ISession currSession = null;
			JInternalFrame sif = getMainFrame().getDesktopPane().getSelectedFrame();
			if (sif instanceof BaseSessionSheet)
			{
				currSession = ((BaseSessionSheet)sif).getSession();
			}
			if (currSession != newSession)
			{
				sif = getMainInternalFrame(newSession);
				if (sif != null)
				{
					moveToFront(sif);
				}
			}
		}

		/**
		 * A session is being closed.
		 *
		 * @param	evt		Current event.
		 */
		public void sessionClosing(SessionEvent evt)
		{
			// Clear session info from all actions.
			for (Iterator it = _app.getActionCollection().actions();
					it.hasNext();)
			{
				final Action act = (Action)it.next();
				if (act instanceof ISessionAction)
				{
					((ISessionAction)act).setSession(null);
				}
			}

			// Close all sheets for the session.
			_sessionClosing = true;
			IIdentifier sessionId = evt.getSession().getIdentifier();
			List sessionSheets = (List)_sessionWindows.get(sessionId);
			if (sessionSheets != null)
			{
				for (Iterator sheetItr = sessionSheets.iterator();
						sheetItr.hasNext();)
				{
					((BaseSessionSheet)sheetItr.next()).dispose();
				}
			}
			_sessionWindows.remove(sessionId);

			selectFrontWindow();

			_sessionClosing = false;
		}

		public void sessionClosed(SessionEvent evt)
		{
			// Check that we have no sheets left open (coding error).
			// TODO: Better error msg
			final IIdentifier sessionId = evt.getSession().getIdentifier();
			final List sessionSheets = (List)_sessionWindows.get(sessionId);
			if ((sessionSheets != null) && (sessionSheets.size() > 0))
			{
				throw new RuntimeException("Coding error");
			}

			// Then remove the list.
			_sessionWindows.remove(sessionId);
		}
	}

}
