package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.util.Logger;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class ResultTab extends JPanel {
	/** Current session. */
	private ISession _session;

	/** Viewer to display the SQL results. */
	private DataSetViewer _resultSetViewer = new DataSetViewer();

	/** Viewer to display the SQL results metadata. */
	private DataSetViewer _metaDataViewer = new DataSetViewer();

	/** Panel displaying the SQL results. */
	private IDataSetViewerDestination _resultSetOutput;

	/** Panel displaying the SQL results meta data. */
	private IDataSetViewerDestination _metaDataOutput;

	/** Scroll pane for <TT>_resultSetOutput</TT>. */
	private JScrollPane _resultSetSp = new JScrollPane();

	/** Scroll pane for <TT>_metaDataOutput</TT>. */
	private JScrollPane _metaDataSp = new JScrollPane();

	/** Tabbed pane containing the SQL results the the results meta data. */
	private JTabbedPane _tp = new JTabbedPane(JTabbedPane.BOTTOM);

	/** <TT>SQLPanel</TT> that this tab is showing results for. */
	private SQLPanel _sqlPanel;

	/** Label shows the current SQL script. */
	private JLabel _currentSqlLbl = new JLabel();

	private MyPropertiesListener _propsListener = new MyPropertiesListener();
//	private MyActionListener _actionListener = new MyActionListener();

	/**
	 * Ctor.
	 *
	 * @param	session		Current session.
	 * @param	sqlPanel	<TT>SQLPanel</TT> that this tab is showing
	 *						results for.
	 *
	 * @thrown	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> or
	 *			<TT>null</TT> <TT>SQLPanel</TT> passed.
	 */
	public ResultTab(ISession session, SQLPanel sqlPanel)
			throws IllegalArgumentException {
		super();
		if (session == null) {
			throw new IllegalArgumentException("Null ISession passed");
		}
		if (sqlPanel == null) {
			throw new IllegalArgumentException("Null SQLPanel passed");
		}

		_session = session;
		_sqlPanel = sqlPanel;

		createUserInterface();

		_session.getProperties().addPropertyChangeListener(_propsListener);
		propertiesHaveChanged(null);
	}

	/**
	 * Show the results from the passed <TT>IDataSet</TT>.
	 *
	 * @param	ds	<TT>IDataSet</TT> to show results for.
	 * @param	sql	SQL script that generated <TT>IDataSet</TT>.
	 */
	public void showResults(ResultSetDataSet rsds, ResultSetMetaDataDataSet mdds, String sql) throws DataSetException {
		_currentSqlLbl.setText(sql);
		_resultSetViewer.show(rsds, null); // Why null??
		_metaDataViewer.show(mdds, null); // Why null??
	}

	/**
	 * Clear results and current SQL script.
	 */
	public void clear() {
		_resultSetOutput.clear();
		_currentSqlLbl.setText("");
	}

	/**
	 * Return the current SQL script.
	 *
	 * @return  Current SQL script.
	 */
	public String getSqlString() {
		return _currentSqlLbl.getText();
	}

	private class MyPropertiesListener implements PropertyChangeListener {
		private boolean _listening = true;

		void stopListening() {
			_listening = false;
		}

		void startListening() {
			_listening = true;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (_listening) {
				propertiesHaveChanged(evt.getPropertyName());
			}
		}
	}

/*	private class MyActionListener implements ActionListener
	{
		private boolean _listening = true;

		void stopListening() {
			_listening = false;
		}

		void startListening() {
			_listening = true;
		}

		public void actionPerformed(ActionEvent evt) {
			if (_listening)
			{
				if(evt.getActionCommand().equals("close"))
				{
					closeTab();
				}
				else
				{
					createWindow();
				}
			}
		}
	}
*/
	/**
	 * Close this tab.
	 */
	public void closeTab() {
		//add(_outputSp, BorderLayout.CENTER);
		add(_tp, BorderLayout.CENTER);
		_sqlPanel.closeTab(this);
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
//	  return _outputSp;
		return _tp;
	}

	private void propertiesHaveChanged(String propertyName) {
		final SessionProperties props = _session.getProperties();
		final Logger logger = _session.getApplication().getLogger();
	
		if (propertyName == null || propertyName.equals(
				SessionProperties.IPropertyNames.SQL_OUTPUT_CLASS_NAME)) {
			final IDataSetViewerDestination previous = _resultSetOutput;
			try {
				Class destClass = Class.forName(props.getSqlOutputClassName());
				if (IDataSetViewerDestination.class.isAssignableFrom(destClass) &&
						Component.class.isAssignableFrom(destClass)) {
					_resultSetOutput = (IDataSetViewerDestination)destClass.newInstance();
				}

			} catch (Exception ex) {
				logger.showMessage(Logger.ILogTypes.ERROR, ex.getMessage());
			}
			if (_resultSetOutput == null) {
				_resultSetOutput = new DataSetViewerTablePanel();
			}
			_resultSetViewer.setDestination(_resultSetOutput);
			_resultSetSp.setRowHeader(null);
			_resultSetSp.setViewportView((Component)_resultSetOutput);
		}

		if (propertyName == null || propertyName.equals(
				SessionProperties.IPropertyNames.SQL_OUTPUT_META_DATA_CLASS_NAME)) {
			final IDataSetViewerDestination previous = _metaDataOutput;
			try {
				Class destClass = Class.forName(props.getSqlOutputMetaDataClassName());
				if (IDataSetViewerDestination.class.isAssignableFrom(destClass) &&
						Component.class.isAssignableFrom(destClass)) {
					_metaDataOutput = (IDataSetViewerDestination)destClass.newInstance();
				}

			} catch (Exception ex) {
				logger.showMessage(Logger.ILogTypes.ERROR, ex.getMessage());
			}
			if (_metaDataOutput == null) {
				_metaDataOutput = new DataSetViewerTablePanel();
			}
			_metaDataViewer.setDestination(_metaDataOutput);
			_metaDataSp.setRowHeader(null);
			_metaDataSp.setViewportView((Component)_metaDataOutput);
		}

	}

	private void createUserInterface() {
//	final Resources rsrc = _session.getApplication().getResources();
		setLayout(new BorderLayout());

		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(1,2,0,0));
		panel2.add(new TabButton(new CreateResultTabFrameAction()));
		panel2.add(new TabButton(new CloseAction()));
		panel1.setLayout(new BorderLayout());
		panel1.add(panel2,BorderLayout.EAST);
		panel1.add(_currentSqlLbl,BorderLayout.CENTER);
		add(panel1,BorderLayout.NORTH);
//	  add(_resultsSp, BorderLayout.CENTER);
		add(_tp, BorderLayout.CENTER);

		_tp.addTab("Results", _resultSetSp);
		_tp.addTab("MetaData", _metaDataSp);
	}

	private final class TabButton extends JButton {
		TabButton(Action action) {
			super(action);
			setMargin(new Insets(0,0,0,0));
			setBorderPainted(false);
			setText("");
		}
	}

	private class CloseAction extends SquirrelAction {
		CloseAction() {
			super(_session.getApplication(), _session.getApplication().getResources());
		}

		public void actionPerformed(ActionEvent evt) {
			closeTab();
		}
	}


	private class CreateResultTabFrameAction extends SquirrelAction {
		CreateResultTabFrameAction() {
			super(_session.getApplication(), _session.getApplication().getResources());
		}

		public void actionPerformed(ActionEvent evt) {
			_sqlPanel.createWindow(ResultTab.this);
		}
	}
}




