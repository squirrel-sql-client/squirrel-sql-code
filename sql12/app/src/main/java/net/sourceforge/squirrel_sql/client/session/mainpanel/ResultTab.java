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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.client.session.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.OverviewCtrl;
import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.DataSetViewerFindDecorator;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ResultTab extends JPanel implements IHasIdentifier, IResultTab
{
    /** Uniquely identifies this ResultTab. */
	private IIdentifier _id;

	/** Current session. */
	private ISession _session;

	/** SQL Execution information. */
   private SQLExecutionInfo _exInfo;

	/** Panel displaying the SQL results. */
	private DataSetViewerFindDecorator _resultSetOutput;

	/** Panel displaying the SQL results meta data. */
	private IDataSetViewer _metaDataOutput;

	/** Scroll pane for <TT>_resultSetOutput</TT>. */
   //  SCROLL
	// private JScrollPane _resultSetSp = new JScrollPane();

	/** Scroll pane for <TT>_metaDataOutput</TT>. */
	private JScrollPane _metaDataSp = new JScrollPane();

	/** Tabbed pane containing the SQL results the the results meta data. */
	private JTabbedPane _tabResultTabs;

	/** <TT>SQLExecuterPanel</TT> that this tab is showing results for. */
	private SQLResultExecuterPanelFacade _sqlResultExecuterPanelFacade;

	/** Label shows the current SQL script. */
	private JLabel _currentSqlLbl = new JLabel();

	/** The SQL execurtes, cleaned up for display. */
	private String _sql;

	/** Panel showing the query information. */
	private QueryInfoPanel _queryInfoPanel = new QueryInfoPanel();

	/** Listener to the sessions properties. */
	private PropertyChangeListener _propsListener;

   private boolean _allowEditing;

   private IDataSetUpdateableTableModel _creator;

   private ResultSetDataSet _rsds;
   
   /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultTab.class);

   private static ILogger s_log = LoggerController.createLogger(ResultTab.class);



   private ResultTabListener _resultTabListener;

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
                    IDataSetUpdateableTableModel creator, ResultTabListener resultTabListener)
      throws IllegalArgumentException
   {
      super();
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
      init(creator, exInfo);


      createGUI();
      propertiesHaveChanged(null);
   }

	/**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab#reInit(net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel, net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo)
     */
	private void init(IDataSetUpdateableTableModel creator, SQLExecutionInfo exInfo)
	{
		_creator = creator;
		_creator.addListener(new DataSetUpdateableTableModelListener()
		{
			public void forceEditMode(boolean mode)
			{
				onForceEditMode(mode);
			}
		});

		_allowEditing = new EditableSqlCheck(exInfo).allowsEditing();

		final SessionProperties props = _session.getProperties();

		if (_allowEditing)
		{
         IDataSetViewer dataSetViewer = BaseDataSetViewerDestination.getInstance(props.getSQLResultsOutputClassName(), _creator, new DefaultDataModelImplementationDetails(_session));
         _resultSetOutput = new DataSetViewerFindDecorator(dataSetViewer, _session.getApplication().getMessageHandler());

		}
		else
		{
			// sql contains columns from multiple tables,
			// so we cannot use all of the columns in a WHERE clause
			// and it becomes difficult to know which table (or tables!) an
			// edited column belongs to.  Therefore limit the output
			// to be read-only
         IDataSetViewer dataSetViewer = BaseDataSetViewerDestination.getInstance(
               props.getReadOnlySQLResultsOutputClassName(), null, new DefaultDataModelImplementationDetails(_session));

         _resultSetOutput = new DataSetViewerFindDecorator(dataSetViewer, _session.getApplication().getMessageHandler());
		}


      //  SCROLL
		// _resultSetSp.setViewportView(_resultSetOutput.getComponent());
      // _resultSetSp.setRowHeader(null);

      if (_session.getProperties().getShowResultsMetaData())
      {
         _metaDataOutput = BaseDataSetViewerDestination.getInstance(props.getMetaDataOutputClassName(), null, new DefaultDataModelImplementationDetails(_session));
         _metaDataSp.setViewportView(_metaDataOutput.getComponent());
         _metaDataSp.setRowHeader(null);
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
	public void showResults(ResultSetDataSet rsds, ResultSetMetaDataDataSet mdds,
								SQLExecutionInfo exInfo)
		throws DataSetException
	{
		_exInfo = exInfo;
		_sql = StringUtilities.cleanString(exInfo.getSQL());

		// Display the result set.
		_resultSetOutput.getDataSetViewer().show(rsds, null);
      _rsds = rsds;

		final int rowCount = _resultSetOutput.getDataSetViewer().getRowCount();

		final int maxRows =_exInfo.getMaxRows(); 
      String escapedSql = Utilities.escapeHtmlChars(_sql);

		if (maxRows > 0 && rowCount >= maxRows)
		{
         // i18n[ResultTab.limitMessage=Limited to <font color='red'> {0} </font> rows]
         String limitMsg = s_stringMgr.getString("ResultTab.limitMessage", Integer.valueOf(rowCount));
         _currentSqlLbl.setText("<html><pre>&nbsp;" + limitMsg + ";&nbsp;&nbsp;" + escapedSql + "</pre></html>");
		}
		else
		{
         // i18n[ResultTab.rowsMessage=Rows {0}]
         String rowsMsg = s_stringMgr.getString("ResultTab.rowsMessage", Integer.valueOf(rowCount));
         _currentSqlLbl.setText("<html><pre>&nbsp;" + rowsMsg + ";&nbsp;&nbsp;" + escapedSql + "</pre></html>");
		}

		// Display the result set metadata.
		if (mdds != null && _metaDataOutput != null)
		{
			_metaDataOutput.show(mdds, null); // Why null??
		}

		_queryInfoPanel.load(rowCount, _exInfo);				
	}

   /**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab#clear()
     */
	public void clear()
	{
		if (_metaDataOutput != null)
		{
			_metaDataOutput.clear();
		}
		if (_resultSetOutput != null)
		{
			_resultSetOutput.getDataSetViewer().clear();
		}
		_exInfo = null;
		_currentSqlLbl.setText("");
		_sql = "";
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

	/**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab#closeTab()
     */
	public void closeTab()
	{
		add(_tabResultTabs, BorderLayout.CENTER);
		_sqlResultExecuterPanelFacade.closeResultTab(this);
	}

	/**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab#returnToTabbedPane()
     */
	public void returnToTabbedPane()
	{
		add(_tabResultTabs, BorderLayout.CENTER);
		_sqlResultExecuterPanelFacade.returnToTabbedPane(this);
	}

	public Component getOutputComponent()
	{
		return _tabResultTabs;
	}

    /**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab#reRunSQL()
     */
    public void reRunSQL() {
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
               TableState resultSortableTableState = getTableState(_resultSetOutput.getDataSetViewer());

               IDataSetViewer dataSetViewer = BaseDataSetViewerDestination.getInstance(SessionProperties.IDataSetDestinations.EDITABLE_TABLE, _creator, new DefaultDataModelImplementationDetails(_session));
               // _resultSetOutput = new DataSetViewerFindDecorator(dataSetViewer);
               _resultSetOutput.replaceDataSetViewer(dataSetViewer);

               //  SCROLL
               // _resultSetSp.setViewportView(_resultSetOutput.getComponent());
               // _resultSetSp.setRowHeader(null);

               _rsds.resetCursor();
               _resultSetOutput.getDataSetViewer().show(_rsds, null);

               restoreTableState(resultSortableTableState, _resultSetOutput.getDataSetViewer());
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

            TableState resultSortableTableState = getTableState(_resultSetOutput.getDataSetViewer());

            IDataSetViewer dataSetViewer = BaseDataSetViewerDestination.getInstance(readOnlyOutput, _creator, new DefaultDataModelImplementationDetails(_session));
            // _resultSetOutput = new DataSetViewerFindDecorator(dataSetViewer);
            _resultSetOutput.replaceDataSetViewer(dataSetViewer);


            //  SCROLL
            // _resultSetSp.setViewportView(_resultSetOutput.getComponent());
            // _resultSetSp.setRowHeader(null);

            _rsds.resetCursor();
            _resultSetOutput.getDataSetViewer().show(_rsds, null);

            restoreTableState(resultSortableTableState, _resultSetOutput.getDataSetViewer());
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
		//	final Resources rsrc = _session.getApplication().getResources();
		setLayout(new BorderLayout());

      int sqlResultsTabPlacement = _session.getProperties().getSQLResultsTabPlacement();
      _tabResultTabs = UIFactory.getInstance().createTabbedPane(sqlResultsTabPlacement);

		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(1, 3, 0, 0));
      panel2.add(new TabButton(new RerunAction(_session.getApplication())));
      panel2.add(new TabButton(new FindInResultAction(_session.getApplication())));
		panel2.add(new TabButton(new CreateResultTabFrameAction(_session.getApplication())));
		panel2.add(new TabButton(new CloseAction()));
		panel1.setLayout(new BorderLayout());
		panel1.add(panel2, BorderLayout.EAST);
		panel1.add(_currentSqlLbl, BorderLayout.CENTER);
		add(panel1, BorderLayout.NORTH);
		add(_tabResultTabs, BorderLayout.CENTER);

       //  SCROLL _resultSetSp.setBorder(BorderFactory.createEmptyBorder());
      
      // i18n[ResultTab.resultsTabTitle=Results]
      String resultsTabTitle = 
          s_stringMgr.getString("ResultTab.resultsTabTitle");
      _tabResultTabs.addTab(resultsTabTitle, _resultSetOutput.getComponent()); //  SCROLL

      if (_session.getProperties().getShowResultsMetaData())
      {
         _metaDataSp.setBorder(BorderFactory.createEmptyBorder());
         
         // i18n[ResultTab.metadataTabTitle=MetaData]
         String metadataTabTitle = 
             s_stringMgr.getString("ResultTab.metadataTabTitle");
         _tabResultTabs.addTab(metadataTabTitle, _metaDataSp);
      }

		final JScrollPane sp = new JScrollPane(_queryInfoPanel);
		sp.setBorder(BorderFactory.createEmptyBorder());
        
        // i18n[ResultTab.infoTabTitle=Info]
        String infoTabTitle = 
            s_stringMgr.getString("ResultTab.infoTabTitle");
		_tabResultTabs.addTab(infoTabTitle, sp);


      final int overViewIx = _tabResultTabs.getTabCount();
      final OverviewCtrl ctrl = new OverviewCtrl(_session);
      _tabResultTabs.addTab(ctrl.getTitle(), ctrl.getPanel());

      _tabResultTabs.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            if (overViewIx == _tabResultTabs.getSelectedIndex())
            {
               ctrl.init(_rsds);
            }
         }
      });

	}

   private class CloseAction extends SquirrelAction
	{
		CloseAction()
		{
			super(
				_session.getApplication(),
				_session.getApplication().getResources());
		}

		public void actionPerformed(ActionEvent evt)
		{
			closeTab();
		}
	}

	private class CreateResultTabFrameAction extends SquirrelAction
	{
		CreateResultTabFrameAction(IApplication app)
		{
			super(app, app.getResources());
		}

		public void actionPerformed(ActionEvent evt)
		{
			_sqlResultExecuterPanelFacade.createSQLResultFrame(ResultTab.this);
		}
	}

   public class RerunAction  extends SquirrelAction
   {
      RerunAction(IApplication app)
      {
         super(app, app.getResources());
      }

      public void actionPerformed(ActionEvent evt)
      {
         reRunSQL();   
      }
   }

   public class FindInResultAction  extends SquirrelAction
   {
      FindInResultAction(IApplication app)
      {
         super(app, app.getResources());
      }

      public void actionPerformed(ActionEvent evt)
      {
         toggleShowFindPanel();
      }
   }

   @Override
   public void toggleShowFindPanel()
   {
      _tabResultTabs.setSelectedIndex(0);
      if(false == _resultSetOutput.toggleShowFindPanel())
      {
         _session.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("ResultTab.tableSearchNotSupported"));
         s_log.warn(s_stringMgr.getString("ResultTab.tableSearchNotSupported"));
      }
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
      return _resultSetOutput.getDataSetViewer().getResultSortableTableState();
   }

   public void applyResultSortableTableState(TableState sortableTableState)
   {
      _resultSetOutput.getDataSetViewer().applyResultSortableTableState(sortableTableState);
   }


}
