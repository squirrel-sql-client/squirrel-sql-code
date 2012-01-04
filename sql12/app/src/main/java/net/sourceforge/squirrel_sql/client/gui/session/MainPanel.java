package net.sourceforge.squirrel_sql.client.gui.session;
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
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ObjectTreeTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This tabbed panel is the main panel within the session window.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MainPanel extends JPanel
{
   /**
	 * IDs of tabs.
	 */
	public interface ITabIndexes
	{
		int OBJECT_TREE_TAB = 0;
		int SQL_TAB = 1;
	}

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MainPanel.class);

	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(MainPanel.class);

	/** Current session. */
	transient private ISession _session;

	/** The tabbed pane. */
	private final JTabbedPane _tabPnl = UIFactory.getInstance().createTabbedPane();

	/** Listener to the sessions properties. */
	transient private PropertyChangeListener _propsListener;

	/** Listener for changes to the tabbed panel. */
	transient private ChangeListener _tabPnlListener;

	/**
	 * Collection of <TT>IMainPanelTab</TT> objects displayed in
	 * this tabbed panel.
	 */
	private List<IMainPanelTab> _tabs = new ArrayList<IMainPanelTab>();

   private static final String PREFS_KEY_SELECTED_TAB_IX = "squirrelSql_mainPanel_sel_tab_ix";

	/**
	 * ctor specifying the current session.
	 *
	 * @param	session		Current session.
	 *
	 * @throws	IllegalArgumentException
	 *			If a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	MainPanel(ISession session)
	{
		super(new BorderLayout());

		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		_session = session;

		addMainPanelTab(new ObjectTreeTab(), Integer.valueOf('O'));
		addMainPanelTab(new SQLTab(_session), Integer.valueOf('Q'));

		add(_tabPnl, BorderLayout.CENTER);

		propertiesHaveChanged(null);

		// Refresh the currently selected tab.
		(_tabs.get(getTabbedPane().getSelectedIndex())).select();
	}

	public void addNotify()
	{
		super.addNotify();

		if (_propsListener == null)
		{
			_propsListener = new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					propertiesHaveChanged(evt.getPropertyName());
				}
			};
			_session.getProperties().addPropertyChangeListener(_propsListener);
		}

		_tabPnlListener = new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt)
			{
				performStateChanged();
			}
		};
		_tabPnl.addChangeListener(_tabPnlListener);
	}

	public void removeNotify()
	{
		super.removeNotify();

		if (_propsListener != null)
		{
			_session.getProperties().removePropertyChangeListener(_propsListener);
			_propsListener = null;
		}

		if (_tabPnlListener != null)
		{
			_tabPnl.removeChangeListener(_tabPnlListener);
			_tabPnlListener = null;
		}
	}

	/**
	 * Add a tab to this panel. If a tab with this title already exists it is
	 * removed from the tabbed pane and the passed tab inserted in its
	 * place. New tabs are inserted at the end.
	 *
	 * @param	tab	 The tab to be added.
    *
    * @return The index of th added tab
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ITablePanelTab</TT> passed.
	 */
   public int addMainPanelTab(IMainPanelTab tab)
   {
      return addMainPanelTab(tab, null);
   }

	public int addMainPanelTab(IMainPanelTab tab, Integer mnemonic)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IMainPanelTab passed");
		}
		tab.setSession(_session);

      int idx = getTabIndex(tab);


      if (idx != -1)
		{
			_tabPnl.removeTabAt(idx);
			_tabs.set(idx, tab);
		}
		else
		{
			idx = _tabPnl.getTabCount();
			_tabs.add(tab);
		}

      _tabPnl.insertTab(tab.getTitle(), null, tab.getComponent(), tab.getHint(), idx);
      if(null != tab.getTabComponent())
      {
         _tabPnl.setTabComponentAt(idx, tab.getTabComponent());
      }


      int prefIx = Preferences.userRoot().getInt(PREFS_KEY_SELECTED_TAB_IX, ITabIndexes.OBJECT_TREE_TAB);
      if(idx == prefIx)
      {
         _tabPnl.setSelectedIndex(prefIx);
      }

      if(null != mnemonic)
      {
         _tabPnl.setMnemonicAt(idx, mnemonic.intValue());

      }

      return idx;
	}

	/**
	 * Add a tab to this panel at the specified index.
	 *
	 * @param	tab		The tab to be added.
	 * @param	idx		The index to add the tab at.
	 *
	 * @param selectInsertedTab
    * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ITablePanelTab</TT> passed.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a tab already exists with the same title as the one
	 *			passed in.
	 */
	public void insertMainPanelTab(IMainPanelTab tab, int idx, boolean selectInsertedTab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IMainPanelTab passed");
		}

		tab.setSession(_session);

      int checkIdx = getTabIndex(tab);


      if (checkIdx != -1)
		{
			throw new IllegalArgumentException("A tab with the same title already exists at index " + checkIdx);
		}

		_tabs.add(idx, tab);
		_tabPnl.insertTab(tab.getTitle(), null, tab.getComponent(), tab.getHint(), idx);

      if(null != tab.getTabComponent())
      {
         _tabPnl.setTabComponentAt(idx, tab.getTabComponent());
      }

      if(selectInsertedTab)
      {
         _tabPnl.setSelectedIndex(idx);
      }
   }

   public int getTabIndex(IMainPanelTab tab)
   {
      int checkIdx;
      if(null == tab.getTabComponent())
      {
         checkIdx = _tabPnl.indexOfTab(tab.getTitle());
      }
      else
      {
         checkIdx = _tabPnl.indexOfTabComponent(tab.getTabComponent());
      }
      return checkIdx;
   }

   public int removeMainPanelTab(IMainPanelTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IMainPanelTab passed");
		}

		int idx = getTabIndex(tab);
		if (idx == -1)
		{
			return idx;
		}

      _tabs.remove(idx);
		_tabPnl.removeTabAt(idx);

		return idx;
	}



	private void updateState()
	{
      int idx = _tabPnl.getSelectedIndex();
      if (idx != -1)
      {
         (_tabs.get(idx)).select();
      }
		_session.getApplication().getActionCollection().activationChanged(_session.getSessionInternalFrame());
	}

   public IMainPanelTab getSelectedMainTab()
   {
      int idx = _tabPnl.getSelectedIndex();
      if (idx == -1)
      {
         return null;
      }

      return _tabs.get(idx);
   }


	/**
	 * The passed session is closing so tell each tab.
	 *
	 * @param	session		Session being closed.
	 */
	void sessionClosing(ISession session)
	{
		for (Iterator<IMainPanelTab> it = _tabs.iterator(); it.hasNext();)
		{
			try
			{
				(it.next()).sessionClosing(session);
			}
			catch (Throwable th)
			{
				String msg = s_stringMgr.getString("MainPanel.error.sessionclose");
				_session.getApplication().showErrorDialog(msg, th);
				s_log.error(msg, th);
			}
		}
	}


   public void sessionWindowClosing()
   {
      getSQLPanel().sessionWindowClosing();
		getObjectTreePanel().sessionWindowClosing();
		int selIx = _tabPnl.getSelectedIndex();

      if(selIx == ITabIndexes.OBJECT_TREE_TAB || selIx == ITabIndexes.SQL_TAB)
      {
         Preferences.userRoot().putInt(PREFS_KEY_SELECTED_TAB_IX, selIx);
      }
   }


	/**
	 * Session properties have changed so update GUI if required.
	 *
	 * @param	propertyName	Name of property that has changed.
	 */
	private void propertiesHaveChanged(String propertyName)
	{
		SessionProperties props = _session.getProperties();
		if (propertyName == null
			|| propertyName.equals(SessionProperties.IPropertyNames.MAIN_TAB_PLACEMENT))
		{
			_tabPnl.setTabPlacement(props.getMainTabPlacement());
		}
	}

	private void performStateChanged()
	{
		// Needed to guarantee other components a focus lost
		// and to allow to enter the tabs components via tab
		// key in a well defined way (the user can see where the focus is).
		_tabPnl.requestFocusInWindow();

		updateState();
	}

	ObjectTreePanel getObjectTreePanel()
	{
		ObjectTreeTab tab = (ObjectTreeTab)_tabs.get(ITabIndexes.OBJECT_TREE_TAB);
		return (ObjectTreePanel)tab.getComponent();
	}

	SQLPanel getSQLPanel()
	{
		return ((SQLTab)_tabs.get(ITabIndexes.SQL_TAB)).getSQLPanel();
	}

	/**
	 * Retrieve the tabbed pane for this component.
	 *
	 * @return	The tabbed pane.
	 */
	JTabbedPane getTabbedPane()
	{
		return _tabPnl;
	}
}
