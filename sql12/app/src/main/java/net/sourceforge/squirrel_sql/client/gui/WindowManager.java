package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2003-2006 Colin Bell
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
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.db.*;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.*;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrameWindowState;
import net.sourceforge.squirrel_sql.client.gui.mainframe.WidgetUtils;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.util.ThreadCheckingRepaintManager;
import net.sourceforge.squirrel_sql.client.mainframe.action.*;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.properties.EditWhereColsSheet;
import net.sourceforge.squirrel_sql.client.session.properties.SessionPropertiesSheet;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.SQLFilterSheet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.WindowState;
import net.sourceforge.squirrel_sql.fw.gui.debug.DebugEventListener;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyVetoException;
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
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 * @author <A HREF="mailto:jmheight@users.sourceforge.net">Jason Height</A>
 */
public class WindowManager
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(WindowManager.class);

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(WindowManager.class);

	/**
	 * Key to client property stored in internal frame that udentifies the
	 * internal frame.
	 */
	private static final String MENU = WindowManager.class.getName() + ".menu";

	/** Application API. */
	private final IApplication _app;

	/** Window manager for driver windows. */
	private DriverWindowManager _driverWinMgr;

	/** Window manager for aliases windows. */
	private AliasWindowManager _aliasWinMgr;

	/** Applications main frame. */
	private MainFrame _mainFrame;

	/** Window containing list of database aliases. */
	private AliasesListInternalFrame _aliasesListWindow;

	/** Window containing list of JDBC driver definitions. */
	private DriversListInternalFrame _driversListWindow;

	/** Window Factory for alias maintenace windows. */
//	private final AliasWindowFactory _aliasWinFactory;

	/**
	 * Map of windows(s) that are currently open for a session, keyed by
	 * session ID.
	 */
	private final SessionWindowsHolder _sessionWindows = new SessionWindowsHolder();

	private final SessionWindowListener _windowListener = new SessionWindowListener();

//	private int _lastSessionIdx = 1;

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
	public WindowManager(IApplication app, boolean enableUserInterfaceDebug)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		if (s_log.isDebugEnabled())
		{
			RepaintManager.setCurrentManager(new ThreadCheckingRepaintManager());
		}

		_app = app;

		_aliasWinMgr = new AliasWindowManager(_app);
		_driverWinMgr = new DriverWindowManager(_app);

		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				initialize();
			}
		}, true);
		new DebugEventListener().setEnabled(enableUserInterfaceDebug);
	}

	/**
	 * Retrieve applications main frame.
	 *
	 * @return	Applications main frame.
	 */
	public MainFrame getMainFrame()
	{
		return _mainFrame;
	}

	public AliasesListInternalFrame getAliasesListInternalFrame()
	{
		return _aliasesListWindow;
	}

	public DriversListInternalFrame getDriversListInternalFrame()
	{
		return _driversListWindow;
	}

	public WindowState getAliasesWindowState()
	{
		return new WindowState(_aliasesListWindow.getInternalFrame());
	}

	public WindowState getDriversWindowState()
	{
		return new WindowState(_driversListWindow.getInternalFrame());
	}

   /**
    * Get a maintenance sheet for the passed alias. If a maintenance sheet already
    * exists it will be brought to the front. If one doesn't exist it will be
    * created.
    *
    * @param	alias	The alias that user has requested to modify.
    *
    * @throws	IllegalArgumentException
    *			Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> passed.
    */
   public void showModifyAliasInternalFrame(final ISQLAlias alias)
   {
      if (alias == null)
      {
         throw new IllegalArgumentException("ISQLAlias == null");
      }

      _aliasWinMgr.showModifyAliasInternalFrame(alias);
   }

	/**
	 * Create and show a new maintenance window to allow the user to create a
	 * new alias.
	 */
	public void showNewAliasInternalFrame()
	{
		_aliasWinMgr.showNewAliasInternalFrame();
	}

	/**
	 * Create and show a new maintenance sheet that will allow the user to create a
	 * new alias that is a copy of the passed one.
	 *
	 * @return	The new maintenance sheet.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> passed.
	 */
	public void showCopyAliasInternalFrame(final SQLAlias alias)
	{
		if (alias == null)
		{
			throw new IllegalArgumentException("ISQLAlias == null");
		}

		_aliasWinMgr.showCopyAliasInternalFrame(alias);
	}

	/**
	 * Get a maintenance sheet for the passed driver. If a maintenance sheet
	 * already exists it will be brought to the front. If one doesn't exist
	 * it will be created.
	 *
	 * @param	driver	The driver that user has requested to modify.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLDriver</TT> passed.
	 */
	public void showModifyDriverInternalFrame(final ISQLDriver driver)
	{
		if (driver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

		_driverWinMgr.showModifyDriverInternalFrame(driver);
	}

	/**
	 * Create and show a new maintenance window to allow the user to create a
	 * new driver.
	 */
	public void showNewDriverInternalFrame()
	{
		_driverWinMgr.showNewDriverInternalFrame();
	}

	/**
	 * Create and show a new maintenance sheet that will allow the user to
	 * create a new driver that is a copy of the passed one.
	 *
	 * @return	The new maintenance sheet.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLDriver</TT> passed.
	 */
	public void showCopyDriverInternalFrame(final ISQLDriver driver)
	{
		if (driver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

		_driverWinMgr.showCopyDriverInternalFrame(driver);
	}

	/**
	 * Registers a sheet that is attached to a session. This sheet will
	 * be automatically closed when the session is closing.
	 * <p/><b>There is no need to call this method manually.</b> Any
	 * classes that properly extend BaseSessionInternalFrame will be registered.
	 */
	public synchronized void registerSessionSheet(ISessionWidget sheet)
	{
        //i18n[WindowManager.registerSessionSheet=Registering {0} in WindowManager]
        String dbg = 
            s_stringMgr.getString("WindowManager.registerSessionSheet",
                                  sheet.getClass().getName());
		s_log.debug(dbg);
		final IIdentifier sessionIdentifier = sheet.getSession().getIdentifier();

		// Store ptr to newly open window in list of windows per session.
		final int idx = _sessionWindows.addFrame(sessionIdentifier, sheet);

		// For all windows (other than the first one opened) for a session
		// add a number on the end of the title to differentiate them in
		// menus etc.
		if ( idx > 1)
		{
			sheet.setTitle(sheet.getTitle() + " (" + idx + ")");
		}

		sheet.addWidgetListener(_windowListener);
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
	public void addSessionWidgetListener(WidgetAdapter listener)
	{
		if (listener == null)
		{
			throw new IllegalArgumentException("InternalFrameListener == null");
		}

		_listenerList.add(WidgetListener.class, listener);
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

		final SessionInternalFrame sif = new SessionInternalFrame(session);

		session.setSessionInternalFrame(sif);
		_app.getPluginManager().sessionStarted(session);
		_app.getMainFrame().addWidget(sif);

		// If we don't invokeLater here no Short-Cut-Key is sent
		// to the internal frame
		// seen under java version "1.4.1_01" and Linux
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				sif.setVisible(true);
                sif.getObjectTreeAPI().selectRoot();
			}
		});

		return sif;
	}

//    /**
//     * A callback method to allow the session we are creating to tell us it has
//     * finished it's initialization. It's important that the plugins are only
//     * notified that a session has been started, after the session window and
//     * it's associated toolbar have been created, and populated with the core
//     * toolbar menu-items.
//     *
//     * @param session the ISession whose SessionPanel has finished it's
//     *                initialization.
//     */
//    public void sessionInitComplete(ISession session) {
//        _app.getPluginManager().sessionStarted(session);
//    }
    
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
		final SQLInternalFrame sif = new SQLInternalFrame(session);
		getMainFrame().addWidget(sif);

		// If we don't invokeLater here no Short-Cut-Key is sent
		// to the internal frame
		// seen under java version "1.4.1_01" and Linux
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				sif.setVisible(true);
            sif.requestFocus();
			}
		});

		return sif;
	}

	/**
	 * Creates a new Object Tree internal frame for the passed session.
	 *
	 * @param	session		Session we are creating internal frame for.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if ISession is passed as null.
	 */
	public synchronized ObjectTreeInternalFrame createObjectTreeInternalFrame(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		final ObjectTreeInternalFrame oif = new ObjectTreeInternalFrame(session);
		getMainFrame().addWidget(oif);

		// If we don't invokeLater here no Short-Cut-Key is sent
		// to the internal frame
		// seen under java version "1.4.1_01" and Linux
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				oif.setVisible(true);
                oif.getObjectTreeAPI().selectRoot();
			}
		});

		return oif;
	}

	/**
	 * Get a properties dialog for the passed session. If one already
	 * exists it will be brought to the front. If one doesn't exist it will be
	 * created.
	 *
	 * @param	session		The session that user has request property dialog for.
    * @param tabNameToSelect The name (title) of the Tab to select. First Tab will be selected
    * if tabNameToSelect is null or doesnt match any tab.
    *
    * @param tabNameToSelect
    * @throws	IllegalArgumentException
    *			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public synchronized void showSessionPropertiesDialog(ISession session, int tabIndexToSelect)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		SessionPropertiesSheet propsSheet = getSessionPropertiesDialog(session);
		if (propsSheet == null)
		{
			propsSheet = new SessionPropertiesSheet(session);
			_app.getMainFrame().addWidget(propsSheet);
			positionSheet(propsSheet);
		}
		else
		{
			propsSheet.moveToFront();
		}

      propsSheet.selectTabIndex(tabIndexToSelect);
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

		SQLFilterSheet sqlFilterSheet = getSQLFilterSheet(objectTree, objectInfo);
		if (sqlFilterSheet == null)
		{
			sqlFilterSheet = new SQLFilterSheet(objectTree, objectInfo);
			_app.getMainFrame().addWidget(sqlFilterSheet);
			positionSheet(sqlFilterSheet);
		}
		else
		{
			sqlFilterSheet.moveToFront();
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
			editWhereColsSheet = new EditWhereColsSheet(session, objectInfo);
			_app.getMainFrame().addWidget(editWhereColsSheet);
			positionSheet(editWhereColsSheet);
		}
		else
		{
			editWhereColsSheet.moveToFront();
		}

		return editWhereColsSheet;
	}

	public void moveToFront(final Window win)
	{
		if (win != null)
		{
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					win.toFront();
					win.setVisible(true);
				}
			});
		}
	}

	public void moveToFront(final JInternalFrame fr)
	{
		if (fr != null)
		{
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					fr.moveToFront();
					fr.setVisible(true);
					try
					{
						fr.setSelected(true);
					}
					catch (PropertyVetoException ex)
					{
                        // i18n[WindowManager.error.bringtofront=Error bringing internal frame to the front]
						s_log.error(s_stringMgr.getString("WindowManager.error.bringtofront"), ex);
					}
				}
			});
		}
	}

	public void activateNextSessionWindow()
	{
		final SessionManager sessMgr = _app.getSessionManager();
		final ISession sess = sessMgr.getActiveSession();

		if (sess == null)
		{
         return;
		}

      ISessionWidget activeSessionWindow = sess.getActiveSessionWindow();

      if(null == activeSessionWindow)
      {
         throw new IllegalStateException("Active Session with no active window ???");
      }


      ISessionWidget nextSessionWindow = _sessionWindows.getNextSessionWindow(activeSessionWindow);

		if (false == activeSessionWindow.equals(nextSessionWindow))
		{
			new SelectWidgetCommand(nextSessionWindow).execute();
		}
	}

	public void activatePreviousSessionWindow()
	{
      final SessionManager sessMgr = _app.getSessionManager();
      final ISession sess = sessMgr.getActiveSession();

      if (sess == null)
      {
         return;
      }

      ISessionWidget activeSessionWindow = sess.getActiveSessionWindow();

      if(null == activeSessionWindow)
      {
         throw new IllegalStateException("Active Session with no active window ???");
      }

      ISessionWidget previousSessionWindow = _sessionWindows.getPreviousSessionWindow(activeSessionWindow);

      if (false == activeSessionWindow.equals(previousSessionWindow))
      {
         new SelectWidgetCommand(previousSessionWindow).execute();
      }
	}

	protected void refireSessionSheetOpened(WidgetEvent evt)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == WidgetListener.class)
			{
				((WidgetListener)listeners[i + 1]).widgetOpened(evt);
			}
		}
	}

	protected void refireSessionSheetClosing(WidgetEvent evt)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == WidgetListener.class)
         {
            ((WidgetListener)listeners[i + 1]).widgetClosing(evt);
         }
      }
	}

	protected void refireSessionSheetClosed(WidgetEvent evt)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == WidgetListener.class)
         {
            ((WidgetListener)listeners[i + 1]).widgetClosed(evt);
         }
      }
	}

	protected void refireSessionSheetIconified(WidgetEvent evt)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == WidgetListener.class)
			{
				((WidgetListener)listeners[i + 1]).widgetIconified(evt);
			}
		}
	}

	protected void refireSessionSheetDeiconified(WidgetEvent evt)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == WidgetListener.class)
			{
				((WidgetListener)listeners[i + 1]).widgetDeiconified(evt);
			}
		}
	}

	protected void refireSessionSheetActivated(WidgetEvent evt)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == WidgetListener.class)
			{
				((WidgetListener)listeners[i + 1]).widgetActivated(evt);
			}
		}
	}

	protected void refireSessionSheetDeactivated(WidgetEvent evt)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == WidgetListener.class)
			{
				((WidgetListener)listeners[i + 1]).widgetDeactivated(evt);
			}
		}
	}

	private SessionPropertiesSheet getSessionPropertiesDialog(ISession session)
	{

      ISessionWidget[] framesOfSession = _sessionWindows.getFramesOfSession(session.getIdentifier());

      for (int i = 0; i < framesOfSession.length; i++)
      {
         if (framesOfSession[i] instanceof SessionPropertiesSheet)
         {
            return (SessionPropertiesSheet)framesOfSession[i];
         }
      }
		return null;
	}

	private SQLFilterSheet getSQLFilterSheet(IObjectTreeAPI tree,
												IDatabaseObjectInfo objectInfo)
	{
		final ISession session = tree.getSession();

      ISessionWidget[] framesOfSession = _sessionWindows.getFramesOfSession(session.getIdentifier());

      for (int i = 0; i < framesOfSession.length; i++)
      {
         if (framesOfSession[i] instanceof SQLFilterSheet)
         {
            final SQLFilterSheet sfs = (SQLFilterSheet)framesOfSession[i];
            if (sfs.getObjectTree() == tree &&
                  objectInfo.equals(sfs.getDatabaseObjectInfo()))
            {
               return sfs;
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

      ISessionWidget[] framesOfSession = _sessionWindows.getFramesOfSession(session.getIdentifier());

      for (int i = 0; i < framesOfSession.length; i++)
      {
         if (framesOfSession[i] instanceof EditWhereColsSheet)
         {
            final EditWhereColsSheet sfs = (EditWhereColsSheet)framesOfSession[i];
//					if (sfs.getObjectTree() == tree &&
//							objectInfo.equals(sfs.getDatabaseObjectInfo()))
            if (objectInfo.equals(sfs.getDatabaseObjectInfo()))
            {
               return sfs;
            }
         }
      }
		return null;
	}


	private void positionSheet(SessionDialogWidget sfs)
	{
		DialogWidget.centerWithinDesktop(sfs);
		sfs.moveToFront();
	}

	private void selectFrontWindow()
	{
      if(false == _app.getDesktopStyle().isInternalFrameStyle())
      {
         // This is a funny functionality anyway and
         // leads to problems with the DockTabStyle.
         // E.g. when SessionProperties is closed first tab gets selected.
         return;
      }


		final IDesktopContainer desktop = _app.getMainFrame().getDesktopContainer();
		if (desktop != null)
		{
			final IWidget[] jifs = desktop.getAllWidgets();
			if (jifs != null && jifs.length > 0)
			{
				jifs[0].moveToFront();
			}
		}
	}

	private void initialize()
	{
		createAliasesListUI();
		createDriversListUI();
		preLoadActions();
		_app.getSessionManager().addSessionListener(_sessionListener);
		createMainFrame();
		setupFromPreferences();
	}

	private void createMainFrame()
	{
		_mainFrame = new MainFrame(_app);
      GUIUtils.setMainFrame(_mainFrame);
   }

	private void createAliasesListUI()
	{
		final IToogleableAliasesList al = new AliasesList(_app);

		final ActionCollection actions = _app.getActionCollection();
		actions.add(new ModifyAliasAction(_app, al));
		actions.add(new DeleteAliasAction(_app, al));
		actions.add(new CopyAliasAction(_app, al));
		actions.add(new ConnectToAliasAction(_app, al));
		actions.add(new CreateAliasAction(_app));
		actions.add(new SortAliasesAction(_app, al));
		actions.add(new AliasPropertiesAction(_app, al));
		actions.add(new ToggleTreeViewAction(_app, al));
		actions.add(new NewAliasFolderAction(_app, al));
      actions.add(new CopyToPasteAliasFolderAction(_app, al));
		actions.add(new CutAliasFolderAction(_app, al));
		actions.add(new PasteAliasFolderAction(_app, al));
		actions.add(new CollapseAllAliasFolderAction(_app, al));
		actions.add(new ExpandAllAliasFolderAction(_app, al));

      _aliasesListWindow = new AliasesListInternalFrame(_app, al);

   }

	private void createDriversListUI()
	{
		final DriversList dl = new DriversList(_app);

		final ActionCollection actions = _app.getActionCollection();
		actions.add(new ModifyDriverAction(_app, dl));
		actions.add(new DeleteDriverAction(_app, dl));
		actions.add(new CopyDriverAction(_app, dl));
		actions.add(new CreateDriverAction(_app));
        actions.add(new ShowDriverWebsiteAction(_app, dl));

		_driversListWindow = new DriversListInternalFrame(_app, dl);
	}

	private void preLoadActions()
	{
		final ActionCollection actions = _app.getActionCollection();
		if (actions == null)
		{
			throw new IllegalStateException("ActionCollection hasn't been created.");
		}

		actions.add(new ViewAliasesAction(_app, getAliasesListInternalFrame()));
		actions.add(new ViewDriversAction(_app, getDriversListInternalFrame()));

//		IAliasesList al = getAliasesListInternalFrame().getAliasesList();
	}

	private void setupFromPreferences()
	{
		final SquirrelPreferences prefs = _app.getSquirrelPreferences();
		final MainFrameWindowState ws = prefs.getMainFrameWindowState();

      prepareAliasWindow(ws);
      prepareDriversWindow(ws);
		prefs.setMainFrameWindowState(new MainFrameWindowState(this));
	}

   private void prepareDriversWindow(MainFrameWindowState ws)
   {
      _mainFrame.addWidget(_driversListWindow);
      WindowState toolWs = ws.getDriversWindowState();
      _driversListWindow.setBounds(toolWs.getBounds().createRectangle());

      if (toolWs.isVisible() && _app.getDesktopStyle().isInternalFrameStyle())
      {
         _driversListWindow.setVisible(true);

         // Has to be done directly on the main frame because of racing condition at start up.
         _mainFrame.setEnabledDriversMenu(true);
         //_driversListWindow.nowVisible(true);

         try
         {
            _driversListWindow.setSelected(true);
         }
         catch (PropertyVetoException ex)
         {
            // i18n[WindowManager.errorselectingwindow=Error selecting window]
            s_log.error(s_stringMgr.getString("WindowManager.errorselectingwindow"), ex);
         }
      }
      else
      {
         _driversListWindow.setVisible(false);

         // Has to be done directly on the main frame because of racing condition at start up.
         _mainFrame.setEnabledDriversMenu(false);
         //_driversListWindow.nowVisible(false);
      }
   }

   private void prepareAliasWindow(MainFrameWindowState ws)
   {
      WindowState toolWs;
      _mainFrame.addWidget(_aliasesListWindow);
      toolWs = ws.getAliasesWindowState();
      _aliasesListWindow.setBounds(toolWs.getBounds().createRectangle());
      if (
              (toolWs.isVisible() && _app.getDesktopStyle().isInternalFrameStyle())
           || (false == _app.getDesktopStyle().isInternalFrameStyle() && false == _aliasesListWindow.isEmpty())
         )
      {
         _aliasesListWindow.setVisible(true);

         // Has to be done directly on the main frame because of racing condition at start up.
         //_aliasesListWindow.nowVisible(true);
         _mainFrame.setEnabledAliasesMenu(true);

         try
         {
            _aliasesListWindow.setSelected(true);
         }
         catch (PropertyVetoException ex)
         {
            // i18n[WindowManager.errorselectingwindow=Error selecting window]
            s_log.error(s_stringMgr.getString("WindowManager.errorselectingwindow"), ex);
         }
      }
      else if(false == _app.getDesktopStyle().isInternalFrameStyle())
      {
         _aliasesListWindow.setVisible(false);

         // Has to be done directly on the main frame because of racing condition at start up.
         //_aliasesListWindow.nowVisible(false);
         _mainFrame.setEnabledAliasesMenu(false);
      }
   }

   /**
	 * Retrieve an internal frame for the passed session. Can be <TT>null</TT>
	 *
	 * @return	an internal frame for the passed session. Can be <TT>null</TT>.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if ISession is passed as null.
	 */
	private IWidget getWidgetForSession(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		IWidget firstWindow = null;

      ISessionWidget[] framesOfSession = _sessionWindows.getFramesOfSession(session.getIdentifier());
      for (int i = 0; i < framesOfSession.length; i++)
      {
         if (framesOfSession[i] instanceof ISessionWidget)
         {
            firstWindow = framesOfSession[i];
         }
         if (framesOfSession[i] instanceof SessionInternalFrame)
         {
            final SessionInternalFrame sif = (SessionInternalFrame)framesOfSession[i];
            if (sif.getSession().equals(session))
            {
               return sif;
            }
         }
      }
		return firstWindow;
	}

   public ISessionWidget[] getAllFramesOfSession(IIdentifier sessionIdentifier)
   {
      return _sessionWindows.getFramesOfSession(sessionIdentifier);
   }

   public void disableSessionMenu()
   {
      getMainFrame().getSessionMenu().setEnabled(false);
   }


   private final class SessionWindowListener implements WidgetListener
	{
		public void widgetOpened(WidgetEvent evt)
		{
			final IWidget widget = evt.getWidget();

			// JASON: Make menu smarter. When second window for the same
			// session is added create a hierarchical menu for all windows
			// for the session.

			// Add an item to the Windows menu for this window and
			// store the menu item back in the internal frame.
			final JMenu menu = getMainFrame().getWindowsMenu();

			final Action action = new SelectWidgetAction(widget);

         final JMenuItem menuItem = menu.add(action);
			widget.putClientProperty(MENU, menuItem);

			// Enable/Disable actions that require open session frames.
			IWidget[] frames = WidgetUtils.getOpenNonToolWindows(getMainFrame().getDesktopContainer().getAllWidgets());
			_app.getActionCollection().internalFrameOpenedOrClosed(frames.length);

			refireSessionSheetOpened(evt);
		}

		public void widgetClosing(WidgetEvent evt)
		{
			refireSessionSheetClosing(evt);
		}

		public void widgetClosed(WidgetEvent evt)
		{
			final IWidget widget = evt.getWidget();

			// Only remove the frame if the entire session is not closing
			if (!_sessionClosing)
			{
				// Find the internal Frame in the list of internal frames
				// and remove it.
				if (widget instanceof ISessionWidget)
				{
					final ISessionWidget sessionWidget = (ISessionWidget)widget;
					final IIdentifier sessionID = sessionWidget.getSession().getIdentifier();
               ISessionWidget[] sessionSheets = _sessionWindows.getFramesOfSession(sessionID);

               for (int i = 0; i < sessionSheets.length; i++)
               {
                  if (sessionSheets[i] == sessionWidget)
                  {
                     _sessionWindows.removeWindow(sessionSheets[i]);
                     WindowManager.this.selectFrontWindow();
                     break;
                  }
               }
				}
			}

			// Remove menu item from Windows menu that relates to this
			// internal frame.
			final JMenuItem menuItem = (JMenuItem)widget.getClientProperty(MENU);
			if (menuItem != null)
			{
				final JMenu menu = getMainFrame().getWindowsMenu();
				if (menu != null)
				{
					menu.remove(menuItem);
				}
			}

			// Enable/Disable actions that require open session frames.
			IWidget[] frames = WidgetUtils.getOpenNonToolWindows(getMainFrame().getDesktopContainer().getAllWidgets());

			_app.getActionCollection().internalFrameOpenedOrClosed(frames.length);

			refireSessionSheetClosed(evt);
		}

		public void widgetIconified(WidgetEvent e)
		{
			refireSessionSheetIconified(e);
		}

		public void widgetDeiconified(WidgetEvent e)
		{
			refireSessionSheetDeiconified(e);
		}

		public void widgetActivated(WidgetEvent e)
		{
			refireSessionSheetActivated(e);
		}

		public void widgetDeactivated(WidgetEvent e)
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
			_app.getActionCollection().setCurrentSession(newSession);

			// If the active window isn't for the currently selected session
			// then select the main window for the session.
			ISession currSession = null;
			IWidget sif = getMainFrame().getDesktopContainer().getSelectedWidget();
			if (sif instanceof ISessionWidget)
			{
				currSession = ((ISessionWidget)sif).getSession();
			}
			if (currSession != newSession)
			{
				sif = getWidgetForSession(newSession);
				if (sif != null)
				{
					sif.moveToFront();
				}
			}

			// Make sure that the session menu is enabled.
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					getMainFrame().getSessionMenu().setEnabled(true);
				}
			});
		}

		/**
		 * A session is being closed.
		 *
		 * @param	evt		Current event.
		 */
		public void sessionClosing(SessionEvent evt)
		{
			getMainFrame().getSessionMenu().setEnabled(false);

			// Clear session info from all actions.
			_app.getActionCollection().setCurrentSession(null);

			try
			{
				if(_sessionClosing)
				{
					return;
				}

				_sessionClosing = true;
				IIdentifier sessionId = evt.getSession().getIdentifier();

				ISessionWidget[] framesOfSession = _sessionWindows.getFramesOfSession(sessionId);
				for (int i = 0; i < framesOfSession.length; i++)
				{
					if(framesOfSession[i] instanceof SessionTabWidget)
					{
						// We are in the closing event of the Session main window.
						// We don't want to send this event again therefore
						// we pass withEvents = false.
						framesOfSession[i].closeFrame(false);
					}
					else
					{
						framesOfSession[i].closeFrame(true);
					}
				}

				_sessionWindows.removeAllWindows(sessionId);

				selectFrontWindow();
			}
			finally
			{
				_sessionClosing = false;
			}
		}
	}
}
