package net.sourceforge.squirrel_sql.client.gui.mainframe;
/*
 * Copyright (C) 2001-2004 Colin Bell
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.mainframe.action.CascadeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.GlobalPreferencesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.MaximizeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.NewSessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileHorizontalAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileVerticalAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.action.CommitAction;
import net.sourceforge.squirrel_sql.client.session.action.NewObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.NewSQLWorksheetAction;
import net.sourceforge.squirrel_sql.client.session.action.RollbackAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleAutoCommitAction;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.gui.SortedComboBoxModel;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * Toolbar for <CODE>MainFrame</CODE>.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class MainFrameToolBar extends ToolBar
{
    private static final long serialVersionUID = 1L;

    /** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MainFrameToolBar.class);

	/** Application API. */
	transient private IApplication _app;

	private boolean _dontReactToSessionDropDownAction = false;
   
   /**
    * ctor.
    *
    * @param	app		Application API
    *
    * @throws	IllegalArgumentException
    *			<TT>null</TT> <TT>IApplication</TT> or <TT>MainFrame</TT>
    *			passed.
    */
   MainFrameToolBar(IApplication app)
   {
      super();
      if (app == null)
      {
         throw new IllegalArgumentException("IApplication == null");
      }
      _app = app;
      setUseRolloverButtons(true);
      setFloatable(true);

      ActionCollection actions = _app.getActionCollection();
      JLabel lbl = new JLabel(s_stringMgr.getString("MainFrameToolBar.connectTo"));
      lbl.setAlignmentY(0.5f);
      add(lbl);
      AliasesDropDown drop = new AliasesDropDown(app);
      drop.setAlignmentY(0.5f);
      add(drop);
      addSeparator();
      add(actions.get(GlobalPreferencesAction.class));
      add(actions.get(NewSessionPropertiesAction.class));
      if (_app.getDesktopStyle().isInternalFrameStyle())
      {
         addSeparator();
         add(actions.get(TileAction.class));
         add(actions.get(TileHorizontalAction.class));
         add(actions.get(TileVerticalAction.class));
         add(actions.get(CascadeAction.class));
         add(actions.get(MaximizeAction.class));
         addSeparator();
      }
      JLabel lbl2 = new JLabel(" " + s_stringMgr.getString("MainFrameToolBar.activeSession") + " ");
      lbl.setAlignmentY(0.5f);
      add(lbl2);
      SessionDropDown sessionDropDown = new SessionDropDown(app);
      sessionDropDown.setAlignmentY(0.5f);
      add(sessionDropDown);

      addToggleAction((IToggleAction) actions.get(ToggleAutoCommitAction.class));
      add(actions.get(CommitAction.class));
      add(actions.get(RollbackAction.class));

      addSeparator();

      add(actions.get(NewSQLWorksheetAction.class));
      add(actions.get(NewObjectTreeAction.class));
   }


   /**
	 * Dropdown holding all the current <TT>ISQLAlias</TT> objects. When one is
	 * selected the user will be prompted to connect to it.
	 */
	private static class AliasesDropDown extends JComboBox
											implements ActionListener
	{
	    private static final long serialVersionUID = 1L;
        
        transient final private IApplication _myApp;

		AliasesDropDown(IApplication app)
		{
			super();
			_myApp = app;
			final AliasesDropDownModel model = new AliasesDropDownModel(app, this);
			setModel(model);

			// Under JDK1.4 the first item in a JComboBox
			// is no longer automatically selected.
			if (getModel().getSize() > 0)
			{
				setSelectedIndex(0);
			}

			// Under JDK1.4 an empty JComboBox has an almost zero width.
			else
			{
				final Dimension dm = getPreferredSize();
				dm.width = 100;
				setPreferredSize(dm);
			}
			addActionListener(this);
			setMaximumSize(getPreferredSize());

			app.getDataCache().addAliasesListener(new MyAliasesListener(model, this));
			
			this.setName(this.getClass().getCanonicalName());
		}

		/**
		 * An alias has been selected in the list so attempt to connect to it.
		 *
		 * @param	evt	 Describes the event that has just occured.
		 */
		public void actionPerformed(ActionEvent evt)
		{
			try
			{
				Object obj = getSelectedItem();
				if (obj instanceof SQLAlias && this.isEnabled())
				{
					new ConnectToAliasCommand(_myApp, (SQLAlias)obj).execute();
				}
			}
			finally
			{
				if (getModel().getSize() > 0)
				{
					setSelectedIndex(0);
				}
			}
		}
	}

	/**
	 * Data model for AliasesDropDown.
	 */
	private static class AliasesDropDownModel extends SortedComboBoxModel
	{
        private static final long serialVersionUID = 1L;

        transient private IApplication _myApp;
        private AliasesDropDown _aliasDropDown;
		/**
		 * Default ctor. Listen to the <TT>DataCache</TT> object for additions
		 * and removals of aliases from the cache.
		 */
		public AliasesDropDownModel(IApplication app, AliasesDropDown drop)
		{
			super();
			_myApp = app;
            _aliasDropDown = drop;
			load();
			//_app.getDataCache().addAliasesListener(new MyAliasesListener(this));
		}

		/**
		 * Load from <TT>DataCache</TT>.
		 */
		private void load()
		{
			Iterator<ISQLAlias> it = _myApp.getDataCache().aliases();
			while (it.hasNext())
			{
				addAlias(it.next());
			}
		}

		/**
		 * Add an <TT>ISQLAlias</TT> to this model.
		 *
		 * @param	alias	<TT>ISQLAlias</TT> to be added.
		 */
		private void addAlias(ISQLAlias alias)
		{
            _aliasDropDown.setEnabled(false);
			addElement(alias);
            if (_aliasDropDown.getModel().getSize() > 0) {
                _aliasDropDown.setSelectedIndex(0);
            }
            _aliasDropDown.setEnabled(true);            
		}

		/**
		 * Remove an <TT>ISQLAlias</TT> from this model.
		 *
		 * @param	alias	<TT>ISQLAlias</TT> to be removed.
		 */
		private void removeAlias(ISQLAlias alias)
		{
            _aliasDropDown.setEnabled(false);
			removeElement(alias);
            if (_aliasDropDown.getModel().getSize() > 0) {
                _aliasDropDown.setSelectedIndex(0);
            }
            _aliasDropDown.setEnabled(true);
		}
	}

	/**
	 * Listener to changes in <TT>ObjectCache</TT>. As aliases are
	 * added to/removed from <TT>DataCache</TT> this model is updated.
	 */
	private static class MyAliasesListener implements IObjectCacheChangeListener
	{
		/** Model that is listening. */
		private AliasesDropDownModel _model;

		/** Control for _model. */
		private AliasesDropDown _control;

		/**
		 * Ctor specifying the model and control that is listening.
		 */
		MyAliasesListener(AliasesDropDownModel model, AliasesDropDown control)
		{
			super();
			_model = model;
			_control = control;
		}

		/**
		 * An alias has been added to the cache.
		 *
		 * @param	evt	Describes the event in the cache.
		 */
		public void objectAdded(ObjectCacheChangeEvent evt)
		{
			Object obj = evt.getObject();
			if (obj instanceof ISQLAlias)
			{
				_model.addAlias((ISQLAlias) obj);
			}
			if (_control.getItemCount() == 1)
			{
				_control.setSelectedIndex(0);
			}
		}

		/**
		 * An alias has been removed from the cache.
		 *
		 * @param	evt	Describes the event in the cache.
		 */
		public void objectRemoved(ObjectCacheChangeEvent evt)
		{
			Object obj = evt.getObject();
			if (obj instanceof ISQLAlias)
			{
				_model.removeAlias((ISQLAlias)obj);
			}
		}
	}

	/**
	 * Dropdown holding all the current active <TT>ISession</TT> objects.
	 */
	private class SessionDropDown extends JComboBox
										implements ActionListener
	{
        private static final long serialVersionUID = 1L;
        private IApplication _app;
		private boolean _closing = false;

		SessionDropDown(IApplication app)
		{
			super();
			_app = app;
			final SessionManager sessionManager = _app.getSessionManager();
			final SessionDropDownModel model = new SessionDropDownModel(
															sessionManager);
			setModel(model);

			// Under JDK1.4 the first item in a JComboBox
			// is no longer automatically selected.
			if (getModel().getSize() > 0)
			{
				setSelectedIndex(0);
			}
			else
			{
				// Under JDK1.4 an empty JComboBox has an almost zero width.
				Dimension dm = getPreferredSize();
				dm.width = 200;
				setPreferredSize(dm);
				// Dont enable the session drop down if it is empty
				setEnabled(false);
			}
			addActionListener(this);
			setMaximumSize(getPreferredSize());

			sessionManager.addSessionListener(new MySessionListener(model, this));
		}

		/**
		 * An session has been selected in the list so set it as the active session.
		 *
		 * @param	evt	 Describes the event that has just occured.
		 */
		public void actionPerformed(ActionEvent evt)
		{
			if (!_closing && !_dontReactToSessionDropDownAction)
			{
				final Object obj = getSelectedItem();
				if (obj instanceof ISession)
				{
					_app.getSessionManager().setActiveSession((ISession)obj, false);
				}
			}
		}
	}

	/**
	 * Data model for SessionDropDownModel.
	 */
	private static class SessionDropDownModel extends SortedComboBoxModel
	{
        private static final long serialVersionUID = 1L;
        transient private SessionManager _sessionManager;

		/**
		 * Default ctor. Listen to the <TT>ISessioManager</TT> object for additions
		 * and removals of aliases from the cache.
		 */
		public SessionDropDownModel(SessionManager sessionManager)
		{
			super();
			_sessionManager = sessionManager;
			load();
		}

		/**
		 * Load from <TT>DataCache</TT>.
		 */
		private void load()
		{
			final ISession[] s = _sessionManager.getConnectedSessions();
			if (s != null)
			{
				for (int i = 0; i < s.length; i++)
				{
					addSession(s[i]);
				}
			}
		}

		/**
		 * Add an <TT>ISession</TT> to this model.
		 *
		 * @param	session	<TT>ISession</TT> to be added.
		 */
		private void addSession(ISession session)
		{
			addElement(session);
		}

		/**
		 * Remove an <TT>ISession</TT> from this model.
		 *
		 * @param	session <TT>ISession</TT> to be removed.
		 */
		private void removeSession(ISession session)
		{
			removeElement(session);
		}
	}

	/**
	 * Listener to changes in <TT>SessionManager</TT>. As sessions are
	 * added to/removed from <TT>SessionManager</TT> this model is updated.
	 */
	private class MySessionListener extends SessionAdapter
	{
		/** Model that is listening. */
		private final SessionDropDownModel _model;

		/** Control for _model. */
		private final SessionDropDown _sessionDropDown;

      /**
		 * Ctor specifying the model and control that is listening.
		 */
		MySessionListener(SessionDropDownModel model, SessionDropDown control)
		{
			super();
			_model = model;
			_sessionDropDown = control;
		}

		public void sessionConnected(SessionEvent evt)
		{
			final ISession session = evt.getSession();
         // Needes to be done via event queque because method is not called from the event disptach thread.
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					_model.addSession(session);
					_sessionDropDown.setEnabled(true);
				}
			});
		}

		public void sessionClosing(SessionEvent evt)
		{
			final ISession session = evt.getSession();
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					_sessionDropDown._closing = true;
					_model.removeSession(session);
					if (_model.getSize() == 0)
					{
						_sessionDropDown.setEnabled(false);
					}
					_sessionDropDown._closing = false;
				}
			});

		}

      public void sessionActivated(SessionEvent evt)
      {
         final ISession session = evt.getSession();

         // Needes to be done via event queque because adding the session to the
         // drop down happens via the event queue too.
         GUIUtils.processOnSwingEventThread(new Runnable()
         {
            public void run()
            {
               try
               {
                  _dontReactToSessionDropDownAction = true;
                  _sessionDropDown.setSelectedItem(session);
               }
               finally
               {
                  _dontReactToSessionDropDownAction = false;
               }
            }
         });
      }

   }
}

