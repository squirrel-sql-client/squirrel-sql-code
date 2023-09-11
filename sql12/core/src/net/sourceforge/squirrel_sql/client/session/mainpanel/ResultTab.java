package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2001-2004 Johan Compagner
 * jcompagner@j-com.nl
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
 *
 * Modifications copyright (C) 2004 Colin Bell
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

import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.DataModelImplementationDetails;
import net.sourceforge.squirrel_sql.client.session.EditableSqlCheck;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.client.session.mainpanel.findresultcolumn.FindResultColumnUtil;
import net.sourceforge.squirrel_sql.client.session.mainpanel.lazyresulttab.AdditionalResultTabsController;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum.RowColAndSumController;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ReadMoreResultsHandlerListener;
import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates.MarkDuplicatesChooserController;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.DataSetViewerFindHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.action.makeeditable.MakeEditableToolbarCtrl;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ResultTab extends JPanel implements IHasIdentifier, IResultTab
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultTab.class);

   private static ILogger s_log = LoggerController.createLogger(ResultTab.class);


   /** Uniquely identifies this ResultTab. */
	private IIdentifier _id;

	/** Current session. */
	private ISession _session;

	/** SQL Execution information. */
   private SQLExecutionInfo _exInfo;

	/** Panel displaying the SQL results. */
	private DataSetViewerFindHandler _resultDataSetViewerFindHandler;

	private DataSetViewerFindHandler _metaDataDataSetViewerFindHandler;

	/** Tabbed pane containing the SQL results and the results meta data. */
	private JTabbedPane _tabResultTabs;

	/** <TT>SQLExecuterPanel</TT> that this tab is showing results for. */
	private SQLResultExecuterPanelFacade _sqlResultExecuterPanelFacade;

	/** Label shows the current SQL script. */
	private CurrentSqlLabelController _currentSqlLblCtrl = new CurrentSqlLabelController();

	/** The SQL execurtes, cleaned up for display. */
	private String _sql;

	/** Panel showing the query information. */
	private QueryInfoPanel _queryInfoPanel = new QueryInfoPanel();

	/** Listener to the sessions properties. */
	private PropertyChangeListener _propsListener;

   private boolean _allowEditing;

   private IDataSetUpdateableTableModel _dataSetUpdateableTableModel;

   private ResultSetDataSet _rsds;
   

   private ResultTabListener _resultTabListener;
   private ReadMoreResultsHandler _readMoreResultsHandler;
   private RowColAndSumController _rowColAndSumController = new RowColAndSumController();


   private ResultLabelNameSwitcher _resultLabelNameSwitcher;
   private AdditionalResultTabsController _additionalResultTabsController;
   private MarkDuplicatesChooserController _markDuplicatesChooserController;

   /**
    * Ctor.
    *
    * @param	session		Current session.
    * @param	sqlResultExecuterPanelFacade	<TT>SQLResultExecuterPanel</TT> that this tab is
    *						showing results for.
    * @param	id			Unique ID for this object.
    *
    * @thrown	IllegalArgumentException
    *			Thrown if a <TT>null</TT> <TT>ISession</TT>,
    *			<<TT>SQLResultExecuterPanel</TT> or <TT>IIdentifier</TT> passed.
    */
   public ResultTab(ISession session, SQLResultExecuterPanelFacade sqlResultExecuterPanelFacade,
                    IIdentifier id, SQLExecutionInfo exInfo,
                    IDataSetUpdateableTableModel dataSetUpdateableTableModel, ResultTabListener resultTabListener)
      throws IllegalArgumentException
   {
      _resultTabListener = resultTabListener;
      if (session == null)
      {
         throw new IllegalArgumentException("Null ISession passed");
      }
      if (sqlResultExecuterPanelFacade == null)
      {
         throw new IllegalArgumentException("Null SQLPanel passed");
      }
      if (id == null)
      {
         throw new IllegalArgumentException("Null IIdentifier passed");
      }

      _session = session;
      _sqlResultExecuterPanelFacade = sqlResultExecuterPanelFacade;
      _id = id;
      _exInfo = exInfo;
      init(dataSetUpdateableTableModel);



      _readMoreResultsHandler = new ReadMoreResultsHandler(_session);

      createGUI();
      propertiesHaveChanged(null);
   }

   /**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab#reInit(net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel, net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo)
     */
	private void init(IDataSetUpdateableTableModel dataSetUpdateableTableModel)
	{
		_dataSetUpdateableTableModel = dataSetUpdateableTableModel;
		_dataSetUpdateableTableModel.addListener(new DataSetUpdateableTableModelListener()
		{
			public void forceEditMode(boolean mode)
			{
				onForceEditMode(mode);
			}
		});

		_allowEditing = new EditableSqlCheck(_exInfo, _session).allowsEditing();

		final SessionProperties props = _session.getProperties();

      IDataSetViewer resultDataSetViewer;
		if (_allowEditing)
		{
         resultDataSetViewer = BaseDataSetViewerDestination.createInstance(props.getSQLResultsOutputClassName(), _dataSetUpdateableTableModel, new DataModelImplementationDetails(_session, _exInfo), _session);

         _rowColAndSumController.setDataSetViewer(resultDataSetViewer);

		}
		else
		{
			// sql contains columns from multiple tables,
			// so we cannot use all of the columns in a WHERE clause
			// and it becomes difficult to know which table (or tables!) an
			// edited column belongs to.  Therefore limit the output
			// to be read-only
         resultDataSetViewer = BaseDataSetViewerDestination.createInstance(
               props.getReadOnlySQLResultsOutputClassName(), null, new DataModelImplementationDetails(_session, _exInfo), _session);

         _rowColAndSumController.setDataSetViewer(resultDataSetViewer);
		}


      _resultDataSetViewerFindHandler = new DataSetViewerFindHandler(resultDataSetViewer, _session);

      //  SCROLL
		// _resultSetSp.setViewportView(_resultDataSetViewerFindHandler.getComponent());
      // _resultSetSp.setRowHeader(null);

      if (_session.getProperties().getShowResultsMetaData())
      {
         IDataSetViewer metaDataSetViewer = BaseDataSetViewerDestination.createInstance(props.getMetaDataOutputClassName(), null, new DataModelImplementationDetails(_session, _exInfo), _session);
         _metaDataDataSetViewerFindHandler = new DataSetViewerFindHandler(metaDataSetViewer, _session);
      }
	}

	/**
	 * Panel is being added to its parent. Setup any required listeners.
	 */
	public void addNotify()
	{
		super.addNotify();
		if (_propsListener == null)
		{
			_propsListener = new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					propertiesHaveChanged(evt);
				}
			};
			_session.getProperties().addPropertyChangeListener(_propsListener);
		}
	}

	/**
	 * Panel is being removed from its parent. Remove any required listeners.
	 */
	public void removeNotify()
	{
		super.removeNotify();
		if (_propsListener != null)
		{
			_session.getProperties().removePropertyChangeListener(_propsListener);
			_propsListener = null;
		}
	}

	/**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab#showResults(net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet, net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet, net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo)
     */
	public void showResults(ResultSetDataSet rsds, ResultSetMetaDataDataSet mdds)
		throws DataSetException
	{
		_sql = StringUtilities.cleanString(_exInfo.getQueryHolder().getOriginalQuery());


      _rsds = rsds;

		// Display the result set.
      _resultDataSetViewerFindHandler.getDataSetViewer().show(_rsds, null);
      initContinueReadChannel();


		final int rowCount = _rsds.currentRowCount();

      _currentSqlLblCtrl.reInit(_rsds.currentRowCount(), _rsds.isResultLimitedByMaxRowsCount(), _sql, _exInfo.getQueryHolder().getOriginalQuery());

      _additionalResultTabsController.setCurrentResult(_rsds);

      _resultLabelNameSwitcher.setCurrentResult(_rsds, _resultDataSetViewerFindHandler.getDataSetViewer());

		// Display the result set metadata.
		if (mdds != null && _metaDataDataSetViewerFindHandler.getDataSetViewer() != null)
		{
         _metaDataDataSetViewerFindHandler.getDataSetViewer().show(mdds, null); // Why null??
		}

		_queryInfoPanel.load(rowCount, _exInfo);				
	}


   private void initContinueReadChannel()
   {
      final ReadMoreResultsHandlerListener readMoreResultsHandlerListener = () -> onMoreResultsHaveBeenRead();

      _resultDataSetViewerFindHandler.getDataSetViewer().setContinueReadChannel(new ContinueReadChannel()
      {
         @Override
         public void readMoreResults()
         {
            onReadMoreResults(readMoreResultsHandlerListener);
         }

         @Override
         public void closeStatementAndResultSet()
         {
            onCloseStatementAndResultSet();
         }
      });
   }

   private void onCloseStatementAndResultSet()
   {
      _rsds.closeStatementAndResultSet();
   }

   private void onReadMoreResults(ReadMoreResultsHandlerListener readMoreResultsHandlerListener)
   {
      if(_rsds.isAllResultsRead())
      {
         return;
      }

      _readMoreResultsHandler.readMoreResults(_rsds, readMoreResultsHandlerListener);
   }

   private void onMoreResultsHaveBeenRead()
   {
      try
      {

         TableState resultSortableTableState = getTableState(_resultDataSetViewerFindHandler.getDataSetViewer());
         _resultDataSetViewerFindHandler.getDataSetViewer().show(_rsds, null);
         restoreTableState(resultSortableTableState, _resultDataSetViewerFindHandler.getDataSetViewer());
         _resultDataSetViewerFindHandler.resetFind();

         _currentSqlLblCtrl.reInit(_rsds.currentRowCount(), _rsds.isResultLimitedByMaxRowsCount());
         _queryInfoPanel.displayRowCount(_rsds.currentRowCount());


         _additionalResultTabsController.moreResultsHaveBeenRead();

         _resultLabelNameSwitcher.moreResultsHaveBeenRead(_rsds);
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab#getSqlString()
     */
	public String getSqlString()
	{
		return _exInfo != null ? _exInfo.getSQL() : null;
	}

	/**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab#getViewableSqlString()
     */
	public String getViewableSqlString()
	{
		return StringUtilities.cleanString(getSqlString());
	}

	/**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab#getTitle()
     */
	public String getTitle()
	{
		String title = _sql;
		if (title.length() < 20)
		{
			return title;
		}
		return title.substring(0, 15);
	}

   public void disposeTab()
   {
      if (_metaDataDataSetViewerFindHandler != null)
      {
         _metaDataDataSetViewerFindHandler.getDataSetViewer().clear();
      }
      if (_resultDataSetViewerFindHandler != null)
      {
         _resultDataSetViewerFindHandler.getDataSetViewer().clear();
      }
      _exInfo = null;
      _currentSqlLblCtrl.clear();
      _sql = "";

      _rsds.closeStatementAndResultSet();
   }

	/**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab#returnToTabbedPane()
     */
	public void returnToTabbedPane()
	{
		add(_tabResultTabs, BorderLayout.CENTER);
		_sqlResultExecuterPanelFacade.returnToTabbedPane(this);
      _rowColAndSumController.setDataSetViewer(_resultDataSetViewerFindHandler.getDataSetViewer());
      _resultDataSetViewerFindHandler.clearParentWindow();
	}

   @Override
	public JComponent getTabbedPaneOfResultTabs()
	{
		return _tabResultTabs;
	}

   @Override
   public JComponent getCompleteResultTab()
   {
      return this;
   }


   /**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab#reRunSQL()
     */
    public void reRunSQL()
    {
        _resultTabListener.rerunSQL(_exInfo.getSQL(), ResultTab.this);
    }
    
	/**
	 * Session properties have changed so update GUI if required.
	 *
	 * @param	propertyName	Name of property that has changed.
	 */
	private void propertiesHaveChanged(PropertyChangeEvent evt)
	{
      SessionProperties props = _session.getProperties();
      if (evt == null
         || evt.getPropertyName().equals(
            SessionProperties.IPropertyNames.SQL_RESULTS_TAB_PLACEMENT))
      {
         _tabResultTabs.setTabPlacement(props.getSQLResultsTabPlacement());
      }
   }


   private void onForceEditMode(boolean editable)
   {
      try
      {
         if(editable)
         {
            if (_allowEditing)
            {
               TableState resultSortableTableState = getTableState(_resultDataSetViewerFindHandler.getDataSetViewer());

               IDataSetViewer dataSetViewer = BaseDataSetViewerDestination.createInstance(SessionProperties.IDataSetDestinations.EDITABLE_TABLE, _dataSetUpdateableTableModel, new DataModelImplementationDetails(_session, _exInfo), _session);
               // _resultDataSetViewerFindHandler = new DataSetViewerFindHandler(dataSetViewer);
               _resultDataSetViewerFindHandler.replaceDataSetViewer(dataSetViewer);

               _rsds.resetCursor();
               _resultDataSetViewerFindHandler.getDataSetViewer().show(_rsds, null);
               initContinueReadChannel();


               restoreTableState(resultSortableTableState, _resultDataSetViewerFindHandler.getDataSetViewer());
            }
            else
            {
                // i18n[ResultTab.cannotedit=This SQL can not be edited.]
               String msg = s_stringMgr.getString("ResultTab.cannotedit");
               JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
            }
         }
         else
         {
            SessionProperties props = _session.getProperties();

            String readOnlyOutput = props.getReadOnlySQLResultsOutputClassName();

            TableState resultSortableTableState = getTableState(_resultDataSetViewerFindHandler.getDataSetViewer());

            IDataSetViewer dataSetViewer = BaseDataSetViewerDestination.createInstance(readOnlyOutput, _dataSetUpdateableTableModel, new DataModelImplementationDetails(_session, _exInfo), _session);
            IDataSetViewer previousDataSetViewer = _resultDataSetViewerFindHandler.replaceDataSetViewer(dataSetViewer);

            ResultSetDataSetEditsUpdater.updateEdits(previousDataSetViewer, _rsds);
            _rsds.resetCursor();
            _resultDataSetViewerFindHandler.getDataSetViewer().show(_rsds, null);
            initContinueReadChannel();

            restoreTableState(resultSortableTableState, _resultDataSetViewerFindHandler.getDataSetViewer());
         }
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void restoreTableState(TableState resultSortableTableState, IDataSetViewer resultSetOutput)
   {
      if (null != resultSortableTableState)
      {
         resultSetOutput.applyResultSortableTableState(resultSortableTableState);
      }
   }

   private TableState getTableState(IDataSetViewer resultSetOutput)
   {
      TableState resultSortableTableState = null;
      if (null != resultSetOutput)
      {
         resultSortableTableState = resultSetOutput.getResultSortableTableState();
      }
      return resultSortableTableState;
   }


   private void createGUI()
	{
		setLayout(new BorderLayout());

      int sqlResultsTabPlacement = _session.getProperties().getSQLResultsTabPlacement();
      _tabResultTabs = UIFactory.getInstance().createTabbedPane(sqlResultsTabPlacement);

      add(createTopPanel(), BorderLayout.NORTH);
		add(_tabResultTabs, BorderLayout.CENTER);

       //  SCROLL _resultSetSp.setBorder(BorderFactory.createEmptyBorder());
      
      // i18n[ResultTab.resultsTabTitle=Results]
      _tabResultTabs.addTab(null, _resultDataSetViewerFindHandler.getComponent()); //  SCROLL
      _resultLabelNameSwitcher = new ResultLabelNameSwitcher(s_stringMgr.getString("ResultTab.resultsTabTitle"), 0, _session, _tabResultTabs);


      if (_session.getProperties().getShowResultsMetaData())
      {
         // i18n[ResultTab.metadataTabTitle=MetaData]
         String metadataTabTitle =  s_stringMgr.getString("ResultTab.metadataTabTitle");
         _tabResultTabs.addTab(metadataTabTitle, _metaDataDataSetViewerFindHandler.getComponent());
      }

        String infoTabTitle = s_stringMgr.getString("ResultTab.infoTabTitle");
		_tabResultTabs.addTab(infoTabTitle, _queryInfoPanel);

      _additionalResultTabsController = new AdditionalResultTabsController(_session, _tabResultTabs, _resultDataSetViewerFindHandler.getDataSetViewer() instanceof DataSetViewerTablePanel);

   }

   private JPanel createTopPanel()
   {
      JPanel topPanel = new JPanel();
      topPanel.setLayout(new BorderLayout(15, 0));
      topPanel.add(createTopRightPanel(), BorderLayout.EAST);
      topPanel.add(_currentSqlLblCtrl.getLabel(), BorderLayout.CENTER);
      return topPanel;
   }

   private JPanel createTopRightPanel()
   {
      JPanel ret = new JPanel(new BorderLayout());

      ret.add(createTopRightButtonsPanel(), BorderLayout.EAST);

      ret.add(_rowColAndSumController.getPanel(), BorderLayout.CENTER);

      return ret;
   }

   private JPanel createTopRightButtonsPanel()
   {
      JPanel ret = new JPanel();
      ret.setLayout(new GridBagLayout()); // 0 columns means any number of columns

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,5,0,0), 0,0);
      ret.add(_readMoreResultsHandler.getLoadingLabel(),gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,10,0,0), 0,0);
      ret.add(new TabButton(getRerunCurrentSQLResultTabAction()), gbc);

      _markDuplicatesChooserController = new MarkDuplicatesChooserController(this);
      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,0,0), 0,0);
      ret.add(GUIUtils.setPreferredWidth(_markDuplicatesChooserController.getComponent(), 42), gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,0,0), 0,0);
      ret.add(new MakeEditableToolbarCtrl(this, _session).getTabButton(), gbc);

      gbc = new GridBagConstraints(4,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,0,0), 0,0);
      ret.add(new TabButton(new FindResultColumnAction(this)), gbc);

      gbc = new GridBagConstraints(5,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,0,0), 0,0);
      ret.add(new TabButton(new FindInResultAction(this)), gbc);

      gbc = new GridBagConstraints(6,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,0,0), 0,0);
      ret.add(new TabButton(new CreateResultTabFrameAction(this)), gbc);

      gbc = new GridBagConstraints(7,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,0,2), 0,0);
      ret.add(new TabButton(new CloseAction(this)), gbc);

      gbc = new GridBagConstraints(0,1, GridBagConstraints.REMAINDER, 1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);

      //ret.setBorder(BorderFactory.createLineBorder(Color.red, 1));
      return ret;
   }

   private RerunCurrentSQLResultTabAction getRerunCurrentSQLResultTabAction()
   {
	   RerunCurrentSQLResultTabAction rtn = new RerunCurrentSQLResultTabAction(this);
	   
	   rtn.setSQLPanel( _session.getSQLPanelAPIOfActiveSessionWindow() );
 
	   return rtn;
   }


   @Override
   public void toggleShowFindPanel()
   {
      DataSetViewerFindHandler dataSetViewerFindHandlerOfSelectedTab = getDataSetViewerFindHandlerOfSelectedTabOrNull();

      if(null == dataSetViewerFindHandlerOfSelectedTab)
      {
         selectResultTab();
         dataSetViewerFindHandlerOfSelectedTab = _resultDataSetViewerFindHandler;
      }

      if(false == dataSetViewerFindHandlerOfSelectedTab.toggleShowFindPanel())
      {
         _session.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("ResultTab.tableSearchNotSupported"));
         s_log.warn(s_stringMgr.getString("ResultTab.tableSearchNotSupported"));
      }
   }

   @Override
   public void findColumn()
   {
      if(false == _resultDataSetViewerFindHandler.getDataSetViewer() instanceof DataSetViewerTablePanel)
      {
         _session.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("ResultTab.ColumnSearchNotSupported"));
         s_log.warn(s_stringMgr.getString("ResultTab.ColumnSearchNotSupported"));
         return;
      }

      DataSetViewerTablePanel dataSetViewerTablePanel = (DataSetViewerTablePanel) _resultDataSetViewerFindHandler.getDataSetViewer();

      selectResultTab();
      FindResultColumnUtil.findAndShowResultColumns(dataSetViewerTablePanel, GUIUtils.getOwningFrame(_tabResultTabs));
   }


   @Override
   public void markDuplicates(ActionEvent e)
   {
      if(_markDuplicatesChooserController.actionWasFired(e))
      {
         selectResultTab();
      }
   }

   @Override
   public MarkDuplicatesChooserController getMarkDuplicatesChooserController()
   {
      return _markDuplicatesChooserController;

   }

   public void wasReturnedToTabbedPane(MarkDuplicatesChooserController resultFramesMarkDuplicatesChooserController)
   {
      _markDuplicatesChooserController.copyStateFrom(resultFramesMarkDuplicatesChooserController);
   }


   /**
 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab#getIdentifier()
 */
	public IIdentifier getIdentifier()
	{
		return _id;
	}

   @Override
   public TableState getResultSortableTableState()
   {
      return _resultDataSetViewerFindHandler.getDataSetViewer().getResultSortableTableState();
   }

   public void applyResultSortableTableState(TableState sortableTableState)
   {
      _resultDataSetViewerFindHandler.getDataSetViewer().applyResultSortableTableState(sortableTableState);
   }


   public SQLResultExecuterPanelFacade getSQLResultExecuterPanelFacade()
   {
      return _sqlResultExecuterPanelFacade;
   }

   @Override
   public IDataSetViewer getSQLResultDataSetViewer()
   {
      return _resultDataSetViewerFindHandler.getDataSetViewer();
   }

   @Override
   public void setParentWindow(Window parent)
   {
      if(null != getDataSetViewerFindHandlerOfSelectedTabOrNull())
      {
         getDataSetViewerFindHandlerOfSelectedTabOrNull().setParentWindow(parent);
      }
   }

   private DataSetViewerFindHandler getDataSetViewerFindHandlerOfSelectedTabOrNull()
   {
      if(0 == _tabResultTabs.getSelectedIndex())
      {
         return _resultDataSetViewerFindHandler;
      }
      else if(1 == _tabResultTabs.getSelectedIndex() && null != _metaDataDataSetViewerFindHandler)
      {
         return _metaDataDataSetViewerFindHandler;
      }
      else
      {
         return _additionalResultTabsController.getDataSetViewerFindHandlerOfSelectedTabOrNull();
      }
   }

   public boolean allowsEditing()
   {
      return _allowEditing;
   }

   public void selectResultTab()
   {
      _tabResultTabs.setSelectedIndex(0);
   }

}
