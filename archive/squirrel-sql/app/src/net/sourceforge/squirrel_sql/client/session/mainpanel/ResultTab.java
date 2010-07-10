package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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
import java.util.Date;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.datasetviewer.BaseDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.SquirrelTabbedPane;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class ResultTab extends JPanel implements IHasIdentifier
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(ResultTab.class);

	/** Uniquely identifies this ResultTab. */
	private IIdentifier _id;

	/** Current session. */
	private ISession _session;

	/** SQL that generated this tab. */
	private String _sql;

	/** Panel displaying the SQL results. */
	private IDataSetViewer _resultSetOutput;

	/** Panel displaying the SQL results meta data. */
	private IDataSetViewer _metaDataOutput;

	/** Scroll pane for <TT>_resultSetOutput</TT>. */
	private JScrollPane _resultSetSp = new JScrollPane();

	/** Scroll pane for <TT>_metaDataOutput</TT>. */
	private JScrollPane _metaDataSp = new JScrollPane();

	/** Tabbed pane containing the SQL results the the results meta data. */
	private SquirrelTabbedPane _tp;

	/** <TT>SQLPanel</TT> that this tab is showing results for. */
	private SQLPanel _sqlPanel;

	/** Label shows the current SQL script. */
	private JLabel _currentSqlLbl = new JLabel();

	/** Panel showing the query information. */
	private QueryInfoPanel _queryInfoPanel = new QueryInfoPanel();

	/**
	 * Ctor.
	 *
	 * @param	session		Current session.
	 * @param	sqlPanel	<TT>SQLPanel</TT> that this tab is showing
	 *						results for.
	 * @param	id			Unique ID for this object.
	 *
	 * @thrown	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT>,
	 *			<<TT>SQLPanel</TT> or <TT>IIdentifier</TT> passed.
	 */
	public ResultTab(ISession session, SQLPanel sqlPanel, IIdentifier id)
		throws IllegalArgumentException
	{
		super();
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

		createUserInterface();
	}

	/**
	 * Show the results from the passed <TT>IDataSet</TT>.
	 *
	 * @param	ds	<TT>IDataSet</TT> to show results for.
	 * @param	sql	SQL script that generated <TT>IDataSet</TT>.
	 */
	public void showResults(ResultSetDataSet rsds, ResultSetMetaDataDataSet mdds,
								String sql)
		throws DataSetException
	{
		_sql = sql;
		_currentSqlLbl.setText(Utilities.cleanString(sql));

		// Display the result set.
		_resultSetOutput.show(rsds, null);

		// Display the result set metadata.
		if (mdds != null)
		{
			_metaDataOutput.show(mdds, null); // Why null??
		}
		else
		{
			_metaDataOutput.clear();
		}

		// And the query info.
		_queryInfoPanel.load(rsds, sql, _resultSetOutput.getRowCount());
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
		_sql = "";
		_currentSqlLbl.setText("");
	}

	/**
	 * Return the current SQL script.
	 *
	 * @return  Current SQL script.
	 */
	public String getSqlString()
	{
		return _sql;
	}

	/**
	 * Return the current SQL script with control characters removed.
	 *
	 * @return  Current SQL script.
	 */
	public String getViewableSqlString()
	{
		return Utilities.cleanString(_sql);
	}

	/**
	 * Return the title for this tab.
	 */
	public String getTitle()
	{
		String title = _currentSqlLbl.getText();
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

	/**
	 * Insert the method's description here.
	 * Creation date: (20-10-2001 1:23:16)
	 */
	private void createWindow()
	{
		_sqlPanel.createWindow(this);
	}

	public Component getOutputComponent()
	{
		return _tp;
	}

	private void createUserInterface()
	{
		//	final Resources rsrc = _session.getApplication().getResources();
		setLayout(new BorderLayout());

		_tp = new SquirrelTabbedPane(_session.getApplication().getSquirrelPreferences());
		_tp.setTabPlacement(SquirrelTabbedPane.BOTTOM);

		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(1, 2, 0, 0));
		panel2.add(new TabButton(new CreateResultTabFrameAction(_session.getApplication())));
		panel2.add(new TabButton(new CloseAction()));
		panel1.setLayout(new BorderLayout());
		panel1.add(panel2, BorderLayout.EAST);
		panel1.add(_currentSqlLbl, BorderLayout.CENTER);
		add(panel1, BorderLayout.NORTH);
		add(_tp, BorderLayout.CENTER);

		_resultSetSp.setBorder(BorderFactory.createEmptyBorder());
		_metaDataSp.setBorder(BorderFactory.createEmptyBorder());
		_tp.addTab("Results", _resultSetSp);
		_tp.addTab("MetaData", _metaDataSp);

		final JScrollPane sp = new JScrollPane(_queryInfoPanel);
		sp.setBorder(BorderFactory.createEmptyBorder());
		_tp.addTab("Info", sp);

		final SessionProperties props = _session.getProperties();

		_resultSetOutput = BaseDataSetViewerDestination.getInstance(props.getSQLResultsOutputClassName());
		_resultSetSp.setViewportView(_resultSetOutput.getComponent());
		_resultSetSp.setRowHeader(null);

		_metaDataOutput = BaseDataSetViewerDestination.getInstance(props.getMetaDataOutputClassName());
		_metaDataSp.setViewportView(_metaDataOutput.getComponent());
		_metaDataSp.setRowHeader(null);
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

		QueryInfoPanel()
		{
			super();
			createUserInterface();
		}

		void load(ResultSetDataSet rsds, String sql, int rowCount)
		{
			_queryLbl.setText(sql);
			_rowCountLbl.setText("" + rowCount);
			_executedLbl.setText(new Date().toString());
		}

		private void createUserInterface()
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
			add(new JLabel("Executed:", SwingConstants.RIGHT), gbc);

			++gbc.gridy;
			add(new JLabel("Row Count:", SwingConstants.RIGHT), gbc);

			++gbc.gridy;
			add(new JLabel("SQL:", SwingConstants.RIGHT), gbc);

			gbc.gridwidth = gbc.REMAINDER;
			gbc.weightx = 1;
			gbc.gridx = 1;

			gbc.gridy = 0;
			add(_executedLbl, gbc);

			++gbc.gridy;
			add(_rowCountLbl, gbc);

			++gbc.gridy;
			add(_queryLbl, gbc);
		}
	}
}
