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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.client.session.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.OverviewCtrl;
import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
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

public class ResultTab extends JPanel implements IHasIdentifier, IResultTab
{
    private static final long serialVersionUID = 1L;

    /** Uniquely identifies this ResultTab. */
	private IIdentifier _id;

	/** Current session. */
	transient private ISession _session;

	/** SQL Execution information. */
	transient private SQLExecutionInfo _exInfo;

	/** Panel displaying the SQL results. */
	transient private IDataSetViewer _resultSetOutput;

	/** Panel displaying the SQL results meta data. */
	transient private IDataSetViewer _metaDataOutput;

	/** Scroll pane for <TT>_resultSetOutput</TT>. */
	private JScrollPane _resultSetSp = new JScrollPane();

	/** Scroll pane for <TT>_metaDataOutput</TT>. */
	private JScrollPane _metaDataSp = new JScrollPane();

	/** Tabbed pane containing the SQL results the the results meta data. */
	private JTabbedPane _tp;

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
   
    transient private IDataSetUpdateableTableModel _creator;
   
    transient private ResultSetDataSet _rsds;
   
   /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(ResultTab.class);
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
			_resultSetOutput = BaseDataSetViewerDestination.getInstance(props.getSQLResultsOutputClassName(), _creator, new DefaultDataModelImplementationDetails(_session));

		}
		else
		{
			// sql contains columns from multiple tables,
			// so we cannot use all of the columns in a WHERE clause
			// and it becomes difficult to know which table (or tables!) an
			// edited column belongs to.  Therefore limit the output
			// to be read-only
			_resultSetOutput = BaseDataSetViewerDestination.getInstance(
				props.getReadOnlySQLResultsOutputClassName(), null, new DefaultDataModelImplementationDetails(_session));
		}


		_resultSetSp.setViewportView(_resultSetOutput.getComponent());
      _resultSetSp.setRowHeader(null);

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
		_resultSetOutput.show(rsds, null);
      _rsds = rsds;

		final int rowCount = _resultSetOutput.getRowCount();

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
			_resultSetOutput.clear();
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
		add(_tp, BorderLayout.CENTER);
		_sqlResultExecuterPanelFacade.closeResultTab(this);
	}

	/**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab#returnToTabbedPane()
     */
	public void returnToTabbedPane()
	{
		add(_tp, BorderLayout.CENTER);
		_sqlResultExecuterPanelFacade.returnToTabbedPane(this);
	}

	public Component getOutputComponent()
	{
		return _tp;
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
         _tp.setTabPlacement(props.getSQLResultsTabPlacement());
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
               TableState resultSortableTableState = getTableState(_resultSetOutput);

               _resultSetOutput = BaseDataSetViewerDestination.getInstance(SessionProperties.IDataSetDestinations.EDITABLE_TABLE, _creator, new DefaultDataModelImplementationDetails(_session));
               _resultSetSp.setViewportView(_resultSetOutput.getComponent());
               _resultSetSp.setRowHeader(null);
               _rsds.resetCursor();
               _resultSetOutput.show(_rsds, null);

               restoreTableState(resultSortableTableState, _resultSetOutput);
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

            TableState resultSortableTableState = getTableState(_resultSetOutput);

            _resultSetOutput = BaseDataSetViewerDestination.getInstance(readOnlyOutput, _creator, new DefaultDataModelImplementationDetails(_session));
            _resultSetSp.setViewportView(_resultSetOutput.getComponent());
            _resultSetSp.setRowHeader(null);
            _rsds.resetCursor();
            _resultSetOutput.show(_rsds, null);

            restoreTableState(resultSortableTableState, _resultSetOutput);
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
      _tp = UIFactory.getInstance().createTabbedPane(sqlResultsTabPlacement);

		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(1, 3, 0, 0));
      panel2.add(new TabButton(new RerunAction(_session.getApplication())));
		panel2.add(new TabButton(new CreateResultTabFrameAction(_session.getApplication())));
		panel2.add(new TabButton(new CloseAction()));
		panel1.setLayout(new BorderLayout());
		panel1.add(panel2, BorderLayout.EAST);
		panel1.add(_currentSqlLbl, BorderLayout.CENTER);
		add(panel1, BorderLayout.NORTH);
		add(_tp, BorderLayout.CENTER);

      _resultSetSp.setBorder(BorderFactory.createEmptyBorder());
      
      // i18n[ResultTab.resultsTabTitle=Results]
      String resultsTabTitle = 
          s_stringMgr.getString("ResultTab.resultsTabTitle");
      _tp.addTab(resultsTabTitle, _resultSetSp);

      if (_session.getProperties().getShowResultsMetaData())
      {
         _metaDataSp.setBorder(BorderFactory.createEmptyBorder());
         
         // i18n[ResultTab.metadataTabTitle=MetaData]
         String metadataTabTitle = 
             s_stringMgr.getString("ResultTab.metadataTabTitle");
         _tp.addTab(metadataTabTitle, _metaDataSp);
      }

		final JScrollPane sp = new JScrollPane(_queryInfoPanel);
		sp.setBorder(BorderFactory.createEmptyBorder());
        
        // i18n[ResultTab.infoTabTitle=Info]
        String infoTabTitle = 
            s_stringMgr.getString("ResultTab.infoTabTitle");
		_tp.addTab(infoTabTitle, sp);


      final int overViewIx = _tp.getTabCount();
      final OverviewCtrl ctrl = new OverviewCtrl(_session);
      _tp.addTab(ctrl.getTitle(), ctrl.getPanel());

      _tp.addChangeListener(new ChangeListener()
      {
      @Override
         public void stateChanged(ChangeEvent e)
         {
            if (overViewIx == _tp.getSelectedIndex())
            {
               ctrl.init(_rsds);
            }
         }
      });

	}

   private final class TabButton extends JButton
	{
		TabButton(Action action)
		{
			super(action);
			setMargin(new Insets(0, 0, 0, 0));
			setBorderPainted(false);
			setText("");
		}
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
      return _resultSetOutput.getResultSortableTableState();
   }

   public void applyResultSortableTableState(TableState sortableTableState)
   {
      _resultSetOutput.applyResultSortableTableState(sortableTableState);
   }


   private static class QueryInfoPanel extends JPanel
	{

      private MultipleLineLabel _queryLbl = new MultipleLineLabel();
		private JLabel _rowCountLbl = new JLabel();
		private JLabel _executedLbl = new JLabel();
		private JLabel _elapsedLbl = new JLabel();

		QueryInfoPanel()
		{
			super();
			createGUI();
		}

		void load(int rowCount,
					SQLExecutionInfo exInfo)
		{
			_queryLbl.setText(StringUtilities.cleanString(exInfo.getSQL()));
			_rowCountLbl.setText(String.valueOf(rowCount));
			_executedLbl.setText(exInfo.getSQLExecutionStartTime().toString());
			_elapsedLbl.setText(formatElapsedTime(exInfo));
		}

		private String formatElapsedTime(SQLExecutionInfo exInfo)
		{
			final NumberFormat nbrFmt = NumberFormat.getNumberInstance();
			double executionLength = exInfo.getSQLExecutionElapsedMillis() / 1000.0;
			double outputLength = exInfo.getResultsProcessingElapsedMillis() / 1000.0;
            
            String totalTime = nbrFmt.format(executionLength + outputLength);
            String queryTime = nbrFmt.format(executionLength);
            String outputTime = nbrFmt.format(outputLength);
            
            // i18n[ResultTab.elapsedTime=Total: {0}, SQL query: {1}, Building output: {2}]
            String elapsedTime = 
                s_stringMgr.getString("ResultTab.elapsedTime",
                                      new String[] { totalTime, 
                                                     queryTime,
                                                     outputTime});
			return elapsedTime;
		}

		private void createGUI()
		{
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();

			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.gridwidth = 1;
			gbc.weightx = 0;

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.insets = new Insets(5, 10, 5, 10);
			gbc.fill = GridBagConstraints.HORIZONTAL;
            // i18n[ResultTab.executedLabel=Executed:]
            String label = s_stringMgr.getString("ResultTab.executedLabel");
			add(new JLabel(label, SwingConstants.RIGHT), gbc);

			++gbc.gridy;
            // i18n[ResultTab.rowCountLabel=Row Count:]
            label = s_stringMgr.getString("ResultTab.rowCountLabel");
			add(new JLabel(label, SwingConstants.RIGHT), gbc);

			++gbc.gridy;
            // i18n[ResultTab.statementLabel=SQL:]
            label = s_stringMgr.getString("ResultTab.statementLabel");            
			add(new JLabel(label, SwingConstants.RIGHT), gbc);

			++gbc.gridy;
            // i18n[ResultTab.elapsedTimeLabel=Elapsed Time (seconds):]
            label = s_stringMgr.getString("ResultTab.elapsedTimeLabel");            
            add(new JLabel(label, SwingConstants.RIGHT), gbc);

			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.weightx = 1;

			gbc.gridx = 1;
			gbc.gridy = 0;
			add(_executedLbl, gbc);

			++gbc.gridy;
			add(_rowCountLbl, gbc);

			++gbc.gridy;
			add(_queryLbl, gbc);

			++gbc.gridy;
			add(_elapsedLbl, gbc);
		}
	}
}
