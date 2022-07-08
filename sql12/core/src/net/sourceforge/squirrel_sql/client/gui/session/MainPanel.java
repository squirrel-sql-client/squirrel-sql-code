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

import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ObjectTreeTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.BaseSQLTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.SQLTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * This tabbed panel is the main panel within the session window.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MainPanel extends JPanel
{
	public interface ITabIndexes
	{
		int OBJECT_TREE_TAB = 0;
		int SQL_TAB = 1;
	}

	private static final String PREFS_KEY_SELECTED_TAB_IX = "squirrelSql_mainPanel_sel_tab_ix";


	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MainPanel.class);

	private static final ILogger s_log = LoggerController.createLogger(MainPanel.class);

	transient private ISession _session;

	/** The tabbed pane for the Session tabs. */
	private final JTabbedPane _tabbedPane = UIFactory.getInstance().createTabbedPane();

	private PropertyChangeListener _propsListener;

	private ChangeListener _tabPnlListener;

	/**
	 * Collection of <TT>IMainPanelTab</TT> objects displayed in
	 * this tabbed panel.
	 */
	private List<IMainPanelTab> _tabs = new ArrayList<>();

	private ArrayList<MainPanelTabSelectionListener> _mainPanelTabSelectionListeners = new ArrayList<>();

	MainPanel(ISession session, TitleFilePathHandler titleFileHandler)
	{
		super(new BorderLayout());

		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		_session = session;

		addMainPanelTab(new ObjectTreeTab(), (int) 'O');
		addMainPanelTab(new SQLTab(_session, titleFileHandler), (int) 'Q');

		add(_tabbedPane, BorderLayout.CENTER);

		propertiesHaveChanged(null);

		// Refresh the currently selected tab.
		(_tabs.get(_tabbedPane.getSelectedIndex())).select();


		GUIUtils.listenToMouseWheelClickOnTab(_tabbedPane, (tabIndex, tabComponent) -> onMouseWheelClickedOnTab(tabIndex));

	}

	private void onMouseWheelClickedOnTab(int tabIndex)
	{
		(_tabs.get(tabIndex)).mouseWheelClickedOnTab();
	}

	/**
	 * Whenever this may cause trouble try to remove the method
	 * by moving the initializations done in the method elsewhere.
	 */
	public void addNotify()
	{
		super.addNotify();

		if (_propsListener == null)
		{
			_propsListener = evt -> propertiesHaveChanged(evt.getPropertyName());
			_session.getProperties().addPropertyChangeListener(_propsListener);
		}

		if(null == _tabPnlListener)
		{
			_tabPnlListener = evt -> performStateChanged();
			_tabbedPane.addChangeListener(_tabPnlListener);
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
			_tabbedPane.removeTabAt(idx);
			_tabs.set(idx, tab);
		}
		else
		{
			idx = _tabbedPane.getTabCount();
			_tabs.add(tab);
		}

      _tabbedPane.insertTab(tab.getTitle(), null, tab.getComponent(), tab.getHint(), idx);
      if(null != tab.getTabComponent())
      {
         _tabbedPane.setTabComponentAt(idx, tab.getTabComponent());
         _tabbedPane.setTitleAt(idx, "");
      }


      int prefIx = Props.getInt(PREFS_KEY_SELECTED_TAB_IX, ITabIndexes.OBJECT_TREE_TAB);
      if(idx == prefIx)
      {
         _tabbedPane.setSelectedIndex(prefIx);
      }


      if(null != mnemonic)
      {
         _tabbedPane.setMnemonicAt(idx, mnemonic);
      }

		performStateChanged();

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
		_tabbedPane.insertTab(tab.getTitle(), null, tab.getComponent(), tab.getHint(), idx);

      if(null != tab.getTabComponent())
      {
         _tabbedPane.setTabComponentAt(idx, tab.getTabComponent());
         _tabbedPane.setTitleAt(idx, "");
      }

      if(selectInsertedTab)
      {
         _tabbedPane.setSelectedIndex(idx);
      }
   }

   public int getTabIndex(IMainPanelTab tab)
   {
      int checkIdx;
      if(null == tab.getTabComponent())
      {
         checkIdx = _tabbedPane.indexOfTab(tab.getTitle());
      }
      else
      {
         checkIdx = _tabbedPane.indexOfTabComponent(tab.getTabComponent());
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
		_tabbedPane.removeTabAt(idx);

		return idx;
	}



	private void updateState()
	{
      int idx = _tabbedPane.getSelectedIndex();
      if (idx != -1)
      {
			IMainPanelTab selectedMainPanelTab = _tabs.get(idx);

			selectedMainPanelTab.select();

			if(null != selectedMainPanelTab.getSqlPanelOrNull())
			{
				selectedMainPanelTab.getSqlPanelOrNull().storeSplitPanePositionOnSessionClose(true);
			}

			for (MainPanelTabSelectionListener mainPanelTabSelectionListener : _mainPanelTabSelectionListeners.toArray(new MainPanelTabSelectionListener[0]))
			{
				mainPanelTabSelectionListener.mainTabSelected(selectedMainPanelTab);
			}

		}
		_session.getApplication().getActionCollection().activationChanged(_session.getSessionInternalFrame());
	}

   public IMainPanelTab getSelectedMainTab()
   {
      int idx = _tabbedPane.getSelectedIndex();
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
		for (SQLPanel sqlPanel : getAllSQLPanels())
		{
			sqlPanel.sessionWorksheetOrTabClosing();
		}

		getObjectTreePanel().sessionWindowClosing();
		int selIx = _tabbedPane.getSelectedIndex();

      if(selIx == ITabIndexes.OBJECT_TREE_TAB || selIx == ITabIndexes.SQL_TAB)
      {
         Props.putInt(PREFS_KEY_SELECTED_TAB_IX, selIx);
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
			_tabbedPane.setTabPlacement(props.getMainTabPlacement());
		}
	}

	void performStateChanged()
	{
		// Needed to guarantee other components a focus lost
		// and to allow to enter the tabs components via tab
		// key in a well defined way (the user can see where the focus is).
		_tabbedPane.requestFocusInWindow();

		updateState();
	}

	ObjectTreePanel getObjectTreePanel()
	{
		ObjectTreeTab tab = (ObjectTreeTab)_tabs.get(ITabIndexes.OBJECT_TREE_TAB);
		return (ObjectTreePanel)tab.getComponent();
	}


	public IMainPanelTab getMainPanelTabAt(int tabIndex)
   {
      return _tabs.get(tabIndex);
   }

	public int getMainTabCount()
	{
		return _tabs.size();
	}

	public int getSelectedMainTabIndex()
	{
		return _tabbedPane.getSelectedIndex();
	}

	public void selectMainTab(int tabIndex)
	{
		_tabbedPane.setSelectedIndex(tabIndex);
	}


	public SQLPanel getMainSQLPanel()
	{
		for (IMainPanelTab tab : _tabs)
		{
			if(tab instanceof SQLTab)
			{
				return ((SQLTab)tab).getSQLPanel();
			}
		}

		throw new IllegalStateException("No SQLTab instance found. This shouldn't happen.");
	}

	/**
	 * @return null if the selected panel is not an SQL panel.
	 */
	public SQLPanel getSelectedSQLPanel()
	{
		IMainPanelTab selectedMainTab = getSelectedMainTab();

		if(selectedMainTab instanceof BaseSQLTab)
		{
			return ((BaseSQLTab)selectedMainTab).getSQLPanel();
		}
		else if(selectedMainTab instanceof AdditionalSQLTab)
		{
			return ((AdditionalSQLTab)selectedMainTab).getSQLPanel();
		}

		return null;
	}

	public SQLPanel getSelectedOrMainSQLPanel()
	{
		SQLPanel ret = getSelectedSQLPanel();

		if(null != ret)
		{
			return ret;
		}

		return getMainSQLPanel();
	}


	List<SQLPanel> getAllSQLPanels()
	{
		ArrayList<SQLPanel> ret = new ArrayList<>();

		for (IMainPanelTab tab : _tabs)
		{
			if(tab instanceof BaseSQLTab)
			{
				ret.add(((BaseSQLTab) tab).getSQLPanel());
			}
		}

		return ret;
	}

	public List<AdditionalSQLTab> getAdditionalSQLTabs()
	{
		ArrayList<AdditionalSQLTab> ret = new ArrayList<>();

		for (IMainPanelTab tab : _tabs)
		{
			if(tab instanceof AdditionalSQLTab)
			{
				ret.add((AdditionalSQLTab) tab);
			}
		}

		return ret;
	}


	public void addMainPanelTabSelectionListener(MainPanelTabSelectionListener mainPanelTabSelectionListener)
	{
		_mainPanelTabSelectionListeners.remove(mainPanelTabSelectionListener);
		_mainPanelTabSelectionListeners.add(mainPanelTabSelectionListener);
	}

	public void removeMainPanelTabSelectionListener(MainPanelTabSelectionListener mainPanelTabSelectionListener)
	{
		_mainPanelTabSelectionListeners.remove(mainPanelTabSelectionListener);
	}
}
