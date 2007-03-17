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
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.client.session.EditableSqlCheck;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class ResultTab extends JPanel implements IHasIdentifier
{
	/** Uniquely identifies this ResultTab. */
	private IIdentifier _id;

	/** Current session. */
	private ISession _session;

	/** SQL Execution information. */
	private SQLExecutionInfo _exInfo;

	/** Panel displaying the SQL results. */
	private IDataSetViewer _resultSetOutput;

	/** Panel displaying the SQL results meta data. */
	private IDataSetViewer _metaDataOutput;

	/** Scroll pane for <TT>_resultSetOutput</TT>. */
	private JScrollPane _resultSetSp = new JScrollPane();

	/** Scroll pane for <TT>_metaDataOutput</TT>. */
	private JScrollPane _metaDataSp = new JScrollPane();

	/** Tabbed pane containing the SQL results the the results meta data. */
	private JTabbedPane _tp;

	/** <TT>SQLExecuterPanel</TT> that this tab is showing results for. */
	private SQLResultExecuterPanel _sqlPanel;

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
   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(ResultTab.class);
   private ResultTabListener _resultTabListener;

   /**
    * Ctor.
    *
    * @param	session		Current session.
    * @param	sqlPanel	<TT>SQLResultExecuterPanel</TT> that this tab is
    *						showing results for.
    * @param	id			Unique ID for this object.
    *
    * @thrown	IllegalArgumentException
    *			Thrown if a <TT>null</TT> <TT>ISession</TT>,
    *			<<TT>SQLResultExecuterPanel</TT> or <TT>IIdentifier</TT> passed.
    */
   public ResultTab(ISession session, SQLResultExecuterPanel sqlPanel,
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
      if (sqlPanel == null)
      {
         throw new IllegalArgumentException("Null SQLPanel passed");
      }
      if (id == null)
      {
         throw new IllegalArgumentException("Null IIdentifier passed");
      }

      _session = session;
      _sqlPanel = sqlPanel;
      _id = id;
      reInit(creator, exInfo);


      createGUI();
      propertiesHaveChanged(null);
   }

	public void reInit(IDataSetUpdateableTableModel creator, SQLExecutionInfo exInfo)
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
			_resultSetOutput = BaseDataSetViewerDestination.getInstance(props.getSQLResultsOutputClassName(), _creator);

		}
		else
		{
			// sql contains columns from multiple tables,
			// so we cannot use all of the columns in a WHERE clause
			// and it becomes difficult to know which table (or tables!) an
			// edited column belongs to.  Therefore limit the output
			// to be read-only
			_resultSetOutput = BaseDataSetViewerDestination.getInstance(
				props.getReadOnlySQLResultsOutputClassName(), null);
		}


		_resultSetSp.setViewportView(_resultSetOutput.getComponent());
      _resultSetSp.setRowHeader(null);

      if (_session.getProperties().getShowResultsMetaData())
      {
         _metaDataOutput = BaseDataSetViewerDestination.getInstance(props.getMetaDataOutputClassName(), null);
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
	 * Show the results from the passed <TT>IDataSet</TT>.
	 *
	 * @param	rsds	<TT>ResultSetDataSet</TT> to show results for.
	 * @param	mdds	<TT>ResultSetMetaDataDataSet</TT> for rsds.
	 * @param	exInfo	Execution info.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <tt>null</tt> <tt>SQLExecutionInfo</tt> passed.
	 *
	 * @throws	DataSetException
	 * 			Thrown if error occured processing dataset.
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
		if (maxRows > 0 && rowCount >= maxRows)
		{
			String buf = _sql.replaceAll("&", "&amp;");
			buf = buf.replaceAll("<", "&lt;");
			buf = buf.replaceAll("<", "&gt;");
			buf = buf.replaceAll("\"", "&quot;");
            // i18n[ResultTab.limitMessage=Limited to <font color='red'> {0} </font> rows]
            String limitMsg = 
                s_stringMgr.getString("ResultTab.limitMessage", 
                                      new Integer(rowCount));
			_currentSqlLbl.setText("<html><pre>&nbsp;" + limitMsg +
                                   ";&nbsp;&nbsp;" + buf + "</pre></html>");
		}
		else
		{
			_currentSqlLbl.setText(_sql);
		}

		// Display the result set metadata.
		if (mdds != null && _metaDataOutput != null)
		{
			_metaDataOutput.show(mdds, null); // Why null??
		}

		exInfo.resultsProcessingComplete();

		// And the query info.
		_queryInfoPanel.load(rsds, rowCount, exInfo);
	}

	/**
	 * Clear results and current SQL script.
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
	 * Return the current SQL script.
	 *
	 * @return	Current SQL script.
	 */
	public String getSqlString()
	{
		return _exInfo != null ? _exInfo.getSQL() : null;
	}

	/**
	 * Return the current SQL script with control characters removed.
	 *
	 * @return	Current SQL script.
	 */
	public String getViewableSqlString()
	{
		return StringUtilities.cleanString(getSqlString());
	}

	/**
	 * Return the title for this tab.
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
	 * Close this tab.
	 */
	public void closeTab()
	{
		add(_tp, BorderLayout.CENTER);
		_sqlPanel.closeTab(this);
	}

	public void returnToTabbedPane()
	{
		add(_tp, BorderLayout.CENTER);
		_sqlPanel.returnToTabbedPane(this);
	}

	public Component getOutputComponent()
	{
		return _tp;
	}

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
               _resultSetOutput = BaseDataSetViewerDestination.getInstance(SessionProperties.IDataSetDestinations.EDITABLE_TABLE, _creator);
               _resultSetSp.setViewportView(_resultSetOutput.getComponent());
               _resultSetSp.setRowHeader(null);
               _rsds.resetCursor();
               _resultSetOutput.show(_rsds, null);
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

            _resultSetOutput = BaseDataSetViewerDestination.getInstance(readOnlyOutput, _creator);
            _resultSetSp.setViewportView(_resultSetOutput.getComponent());
            _resultSetSp.setRowHeader(null);
            _rsds.resetCursor();
            _resultSetOutput.show(_rsds, null);
         }
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
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
			_sqlPanel.createWindow(ResultTab.this);
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
	 * @see IHasIdentifier#getIdentifier()
	 */
	public IIdentifier getIdentifier()
	{
		return _id;
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

		void load(ResultSetDataSet rsds, int rowCount,
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
