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

import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.Logger;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class ResultTab extends JPanel {
	private ISession _session;
	private JScrollPane _outputSp;
	private IDataSetViewerDestination _output;
	private DataSetViewer _viewer;

	/** <TT>SQLPanel</TT> that this tab is showing results for. */
	private SQLPanel _sqlPanel;

	/** Label shows the current SQL script. */
	private JLabel _currentSqlLbl;

	private MyPropertiesListener _propsListener = new MyPropertiesListener();

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
	 * @param   ds	  <TT>IDataSet</TT> to show results for.
	 * @param   sql	 SQL script that generated <TT>IDataSet</TT>.
	 */
	public void show(IDataSet ds, String sql) throws DataSetException {
		_currentSqlLbl.setText(sql);
		_viewer.show(ds, _session.getMessageHandler());
	}

	/**
	 * Clear results and current SQL script.
	 */
	public void clear() {
		_output.clear();
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
		public void propertyChange(PropertyChangeEvent evt) {
			propertiesHaveChanged(evt.getPropertyName());
		}
	}

	public void closeTab() {
		add(_outputSp, BorderLayout.CENTER);
		_sqlPanel.closeTab(this);
	}

	public Component getOutputComponent() {
		return _outputSp;
	}

	private void propertiesHaveChanged(String propertyName) {
		final SessionProperties props = _session.getProperties();
		if (propertyName == null || propertyName.equals(
			SessionProperties.IPropertyNames.SQL_OUTPUT_CLASS_NAME)) {
			final IDataSetViewerDestination previous = _output;
			try {
				Class destClass = Class.forName(props.getSqlOutputClassName());
				if (IDataSetViewerDestination.class.isAssignableFrom(destClass) &&
					Component.class.isAssignableFrom(destClass)) {
					_output = (IDataSetViewerDestination)destClass.newInstance();
				}
			} catch (Exception ex) {
				_session.getApplication().getLogger().showMessage(Logger.ILogTypes.ERROR, ex.getMessage());
			}
			if (_output == null) {
				_output = new DataSetViewerTextPanel();
			}
			_viewer.setDestination(_output);
			_outputSp.setRowHeader(null);
			_outputSp.setViewportView((Component)_output);
		}
	}

	private void createUserInterface() {
		setLayout(new BorderLayout());
		_viewer = new DataSetViewer();
		_currentSqlLbl = new JLabel();

		JButton closeBtn = new TabButton(new CloseAction());
		JButton createButton = new TabButton(new CreateResultTabFrameAction());

		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(1,2,0,0));
		panel2.add(createButton);
		panel2.add(closeBtn);

		panel1.setLayout(new BorderLayout());
		panel1.add(panel2,BorderLayout.EAST);
		panel1.add(_currentSqlLbl,BorderLayout.CENTER);
		add(panel1, BorderLayout.NORTH);
		_outputSp = new JScrollPane();
		add(_outputSp, BorderLayout.CENTER);
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
			add(_outputSp, BorderLayout.CENTER);
			_sqlPanel.closeTab(ResultTab.this);
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




