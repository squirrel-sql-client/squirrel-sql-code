package net.sourceforge.squirrel_sql.client.action;
/*
 * Copyright (C) 2001-2006 Colin Bell
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
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.mainframe.action.*;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.*;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * This class represents a collection of <TT>Action</CODE> objects for the
 * application.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ActionCollection
{
	/** Logger for this class. */
	private static ILogger s_log;

	/** Application API. */
	private IApplication _app;

	/** Collection of all Actions keyed by class name. */
	private final Map<String, Action> _actionColl = new HashMap<String, Action>();

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ActionCollection.class);
    
	/**
	 * Ctor. Disable all actions that are not valid when the
	 * application is first initialised.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public ActionCollection(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (s_log == null)
		{
			s_log = LoggerController.createLogger(getClass());
		}
		_app = app;
		preloadActions();
		enableInternalFrameOptions(false);
	}

	/**
	 * Add an <TT>Action</TT> to this collection. Normally <TT>get</TT> will
	 * do this &quot;on demand&quot; but this function can be used when
	 * there is no default ctor for the <TT>Action</TT>.
	 *
	 * @param	action	<TT>Action</TT> to be added.
	 *
	 * @throws	IllegalArgumentException
	 *			If a <TT>null</TT> <TT>Action</TT> passed.
	 */
	public void add(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}
		_actionColl.put(action.getClass().getName(), action);
	}

	/**
	 * Returns the instance of the passed <TT>Action</TT> class that is stored
	 * in this collection.
	 *
	 * @param	actionClass	The <TT>Class</TT> of the <TT>Action</TT>
	 *						required. Because the instance is created
	 *						using <TT>newInstance()</TT> this <TT>Class</TT>
	 *						must have a default ctor.
	 *
	 * @throws	IllegalArgumentException	Thrown if a null action class passed.
	 */
	public synchronized Action get(Class<? extends Action> actionClass)
	{
		if (actionClass == null)
		{
			throw new IllegalArgumentException("null Action Class passed.");
		}

		return get(actionClass.getName());
	}

	/**
	 * Returns the instance of the passed <TT>Action</TT> class name that is
	 * stored in this collection.
	 *
	 * @param	actionClass	The <TT>Class</TT> of the <TT>Action</TT>
	 *						required. Because the instance is created
	 *						using <TT>newInstance()</TT> this <TT>Class</TT>
	 *						must have a default ctor.
	 *
	 * @throws	IllegalArgumentException	Thrown if a null action class passed.
	 */
	public synchronized Action get(String actionClassName)
	{
		if (actionClassName == null)
		{
			throw new IllegalArgumentException("null Action Class Name passed.");
		}

		Action action = _actionColl.get(actionClassName);
		if (action == null)
		{
            // i18n[ActionCollection.actionNotFound=Action {0} not found in ActionCollection.]
            String errMsg = 
                s_stringMgr.getString("ActionCollection.actionNotFound", 
                                      actionClassName);
            s_log.error(errMsg);
			action = createAction(actionClassName);
		}
		return action;
	}

	/**
	 * Emable/Disable the instance of the passed <TT>Action</TT> class that is
	 * stored in this collection. If one isn't in this collection then an instance
	 * of <TT>actionClass</TT> will be created and stored.
	 *
	 * @param	actionClass	The <TT>Class</TT> of the <TT>Action</TT>
	 *						to be enabled/disabled. Because the instance
	 *						is created using <TT>newInstance()</TT> this
	 *						<TT>Class</TT> must have a default ctor.
	 * @param	enable		If <TT>true</TT> then enable else disable
	 *						the action.
	 *
	 * @throws	IllegalArgumentException	Thrown if a null action class passed.
	 */
    @SuppressWarnings("unchecked")
	public void enableAction(Class actionClass, boolean enable)
		throws IllegalArgumentException
	{
		if (actionClass == null)
		{
			throw new IllegalArgumentException("null Action Class passed.");
		}

		final Action action = get(actionClass);
		if (action != null)
		{
			action.setEnabled(enable);
		}
	}

	/**
	 * This function should be called whenever an internal frame is
	 * opened or closed. It enables/disabled actions that are only
	 * applicable to an internal frame.
	 *
	 * @param	nbrInternalFramesOpen	The count of the internal frames open.
	 */
	public void internalFrameOpenedOrClosed(int nbrInternalFramesOpen)
	{
		enableInternalFrameOptions(nbrInternalFramesOpen > 0);
	}

	/**
	 * This function should be called whenever an internal frame is
	 * deactivated.
	 *
	 * JASON: Should this be in Sessionmanager or SessionWindowmanager?
	 *
	 * @param	frame	The <TT>JInternalFrame</TT> deactivated.
	 */
	public void deactivationChanged(IWidget frame)
	{
		final boolean isSQLFrame = (frame instanceof SQLInternalFrame);
		final boolean isTreeFrame = (frame instanceof ObjectTreeInternalFrame);
		final boolean isSessionInternalFrame = (frame instanceof SessionInternalFrame);

		for (Iterator<Action> it = actions(); it.hasNext();)
		{
			final Action act = it.next();

			if (act instanceof ISessionAction)
			{
				((ISessionAction)act).setSession(null);
			}

			if (isSQLFrame && (act instanceof ISQLPanelAction))
			{
				((ISQLPanelAction)act).setSQLPanel(null);
			}
			if (isTreeFrame && (act instanceof IObjectTreeAction))
			{
				((IObjectTreeAction)act).setObjectTree(null);
			}
			if ((isSessionInternalFrame) && (act instanceof ISQLPanelAction))
			{
				((ISQLPanelAction)act).setSQLPanel(null);
			}
			if ((isSessionInternalFrame) && (act instanceof IObjectTreeAction))
			{
				((IObjectTreeAction)act).setObjectTree(null);
			}
		}
	}

	/**
	 * This function should be called whenever an internal frame is
	 * activated.
	 *
	 * @param	frame	The <TT>JInternalFrame</TT> activated.
	 */
	public synchronized void activationChanged(IWidget frame)
	{
		final boolean isSQLFrame = (frame instanceof SQLInternalFrame);
		final boolean isTreeFrame = (frame instanceof ObjectTreeInternalFrame);
		final boolean isSessionInternalFrame = (frame instanceof SessionInternalFrame);

		ISession session = null;
		if (frame instanceof ISessionWidget)
		{
			session = ((ISessionWidget)frame).getSession();
		}

		for (Iterator<Action> it = actions(); it.hasNext();)
		{
			final Action act = it.next();
			if (act instanceof ISessionAction)
			{
				((ISessionAction)act).setSession(session);
			}
			if (isSQLFrame && (act instanceof ISQLPanelAction))
			{
				((ISQLPanelAction)act).setSQLPanel(((SQLInternalFrame)frame).getSQLPanel().getSQLPanelAPI());
			}
			if (isTreeFrame && (act instanceof IObjectTreeAction))
			{
				((IObjectTreeAction)act).setObjectTree(((ObjectTreeInternalFrame)frame).getObjectTreePanel());
			}
         
         if(isSessionInternalFrame && act instanceof IMainPanelTabAction)
         {
            ((IMainPanelTabAction)act).setSelectedMainPanelTab(session.getSelectedMainTab());
         }
         

			if ((isSessionInternalFrame) && (act instanceof ISQLPanelAction))
			{
            SessionInternalFrame sif = (SessionInternalFrame) frame;
            if(sif.getSessionPanel().isSQLTabSelected())
            {
   				((ISQLPanelAction)act).setSQLPanel(sif.getSessionPanel().getSQLPaneAPI());
            }
            else
            {
               ((ISQLPanelAction)act).setSQLPanel(null);
            }
			}
			if ((isSessionInternalFrame) && (act instanceof IObjectTreeAction))
			{
            SessionInternalFrame sif = (SessionInternalFrame) frame;
            if(sif.getSessionPanel().isObjectTreeTabSelected())
            {
               ((IObjectTreeAction)act).setObjectTree(((SessionInternalFrame)frame).getSessionPanel().getObjectTreePanel());
            }
            else
            {
               ((IObjectTreeAction)act).setObjectTree(null);
            }
			}
		}
	}

	/**
	 * Apply these action keys to the actions currently loaded.
	 *
	 * actionkeys	Action keys to load.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ActionKeys[]</TT> passed.
	 */
	public synchronized void loadActionKeys(ActionKeys[] actionKeys)
	{
		if (actionKeys == null)
		{
			throw new IllegalArgumentException("null ActionKeys[] passed");
		}

		for (int i = 0; i < actionKeys.length; ++i)
		{
			final ActionKeys ak = actionKeys[i];
			final Action action = get(ak.getActionClassName());
			if (action != null)
			{
				final String accel = ak.getAccelerator();
				if (accel != null && accel.length() > 0)
				{
					action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accel));
				}

				final int mnemonic = ak.getMnemonic();
				if (mnemonic != KeyEvent.VK_UNDEFINED)
				{
					action.putValue(Action.MNEMONIC_KEY, Integer.valueOf(mnemonic));
				}
			}
		}
	}

	/**
	 * Return an <TT>Iterator</TT> over this collection.
	 */
	public Iterator<Action> actions()
	{
		return _actionColl.values().iterator();
	}

	/**
	 * Specify the current session for actions.
	 *
	 * @param	session		The current session. Can be <tt>null</tt>.
	 */
	public synchronized void setCurrentSession(ISession session)
	{
		for (Iterator<Action> it = actions(); it.hasNext();)
		{
			final Action act = it.next();
			if (act instanceof ISessionAction)
			{
				((ISessionAction)act).setSession(session);
			}
		}
	}

	/**
	 * Create a new instance of <TT>actionCassName</TT> and store in this
	 * collection.
	 *
	 * @param	actionClass	The name of the <TT>Class</TT> of the <TT>Action</TT>
	 *						required. Because the instance is created
	 *						using <TT>newInstance()</TT> this <TT>Class</TT>
	 *						must have a default ctor.
	 */
	private Action createAction(String actionClassName)
	{
		Action action = null;
		try
		{
            // i18n[ActionCollection.createActionInfo=Attempting to load action class - {0}]
            String msg = 
                s_stringMgr.getString("ActionCollection.createActionInfo", 
                                      actionClassName);
		    s_log.info(msg);
            action = (Action)Class.forName(actionClassName).newInstance();
			_actionColl.put(actionClassName, action);
		}
		catch (Exception ex)
		{
            // i18n[ActionCollection.createActionError=Error occured creating Action: {0}]
            String msg = 
                s_stringMgr.getString("ActionCollection.createActionError",
                                      actionClassName);
			s_log.error(msg, ex);
		}
		return action;
	}

	/**
	 * Enable/disable actions that are valid only if an internal frame
	 * is open.
	 */
	private void enableInternalFrameOptions(boolean enable)
	{
		enableAction(CascadeAction.class, enable);
		enableAction(MaximizeAction.class, enable);
		enableAction(TileAction.class, enable);
		enableAction(TileHorizontalAction.class, enable);
		enableAction(TileVerticalAction.class, enable);
		enableAction(CloseAllSessionsAction.class, enable);
		enableAction(CloseAllButCurrentSessionsAction.class, enable);
	}

	/**
	 * Load actions.
	 */
	private void preloadActions()
	{
		add(new AboutAction(_app));
		add(new CascadeAction(_app));
		add(new ToolsPopupAction(_app));
		add(new CloseAllSessionsAction(_app));
		add(new CloseAllButCurrentSessionsAction(_app));
		add(new CloseAllSQLResultTabsAction(_app));
		add(new CloseAllSQLResultTabsButCurrentAction(_app));
		add(new CloseCurrentSQLResultTabAction(_app));
		add(new ToggleCurrentSQLResultTabStickyAction(_app));
		add(new CloseAllSQLResultWindowsAction(_app));
		add(new ViewObjectAtCursorInObjectTreeAction(_app));
		add(new CloseSessionAction(_app));
		add(new CloseSessionWindowAction(_app));
		add(new CommitAction(_app));
		add(new CopyQualifiedObjectNameAction(_app));
		add(new CopySimpleObjectNameAction(_app));
		add(new DisplayPluginSummaryAction(_app));
		//add(new DropSelectedTablesAction(_app));
		add(new DeleteSelectedTablesAction(_app));
		add(new DumpApplicationAction(_app));
        add(new SavePreferencesAction(_app));
		add(new DumpSessionAction(_app));
		add(new ExecuteSqlAction(_app));
		add(new ExitAction(_app));
		add(new FileNewAction(_app));
		add(new FileOpenAction(_app));
		add(new FileAppendAction(_app));
		add(new FileSaveAction(_app));
		add(new FileSaveAsAction(_app));
        add(new FilePrintAction(_app));
		add(new FileCloseAction(_app));
		add(new GlobalPreferencesAction(_app));
		add(new GotoNextResultsTabAction(_app));
		add(new GotoPreviousResultsTabAction(_app));
		add(new InstallDefaultDriversAction(_app));
		add(new MaximizeAction(_app));
		add(new NewObjectTreeAction(_app));
		add(new NewSQLWorksheetAction(_app));
		add(new NewSessionPropertiesAction(_app));
		add(new NextSessionAction(_app));
		add(new PreviousSessionAction(_app));
		add(new ReconnectAction(_app));
		add(new RefreshSchemaInfoAction(_app));
		add(new RefreshObjectTreeItemAction(_app));
		add(new RollbackAction(_app));
		add(new SessionPropertiesAction(_app));
		add(new FilterObjectsAction(_app));
		add(new SetDefaultCatalogAction(_app));
		add(new ShowLoadedDriversOnlyAction(_app));
		add(new ShowNativeSQLAction(_app));
		add(new SQLFilterAction(_app));
		add(new EditWhereColsAction(_app));
		add(new TileAction(_app));
		add(new TileHorizontalAction(_app));
		add(new TileVerticalAction(_app));
		add(new ToggleAutoCommitAction(_app));
		add(new UpdateAction(_app));
		add(new ViewHelpAction(_app));
		add(new ViewLogsAction(_app));
		add(new PreviousSqlAction(_app));
		add(new NextSqlAction(_app));
		add(new SelectSqlAction(_app));
		add(new OpenSqlHistoryAction(_app));
      add(new FormatSQLAction(_app));

      add(new RenameSessionAction(_app));
	}

}
