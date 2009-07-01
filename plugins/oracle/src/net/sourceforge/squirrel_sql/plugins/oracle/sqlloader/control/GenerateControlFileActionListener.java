/*
 Copyright (C) 2009  Jos� David Moreno Ju�rez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.control;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.model.ControlFileGenerator;


/**
 * {@link ActionListener} for the Generate button.</p>
 * It calls the control file generating class.
 * 
 * @author Jos� David Moreno Ju�rez
 *
 */
public class GenerateControlFileActionListener implements ActionListener {
	private final JTextField stringDelimitatorTextField;

	private final JTextField fieldSeparatorTextField;

	private final JRadioButton appendRaddioButton;

	private final JTextField directoryTextfield;

	private static ILogger log;

	private ISession session;

	/**
	 * Creates a new instance of the {@link GenerateControlFileActionListener}
	 * class.</p>
	 * It receives the session and the UI components needed to get the settings
	 * for the control file generation.
	 * 
	 * @param stringDelimitatorTextfield	text box with the string delimitator
	 * @param fieldSeparatorTextfield		text box with the field separator
	 * @param appendRadioButton				radio button for the append setting
	 * @param controlFileTextfield			text box with the directory name
	 * @param session						database session
	 */
	public GenerateControlFileActionListener(JTextField stringDelimitatorTextfield, JTextField fieldSeparatorTextfield, JRadioButton appendRadioButton, JTextField controlFileTextfield, ISession session) {
		this.stringDelimitatorTextField = stringDelimitatorTextfield;
		this.fieldSeparatorTextField = fieldSeparatorTextfield;
		this.appendRaddioButton = appendRadioButton;
		this.directoryTextfield = controlFileTextfield;
		this.session = session;
	}

	/* (sin Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		/* Writes a control file for each table selected */
		for (ITableInfo table : session.getSessionInternalFrame()
				.getObjectTreeAPI().getSelectedTables()) {
			String tableName = table.getSimpleName();
			try {
				/* Generates the control file using the settings specified by
				 * the user */
				ControlFileGenerator.writeControlFile(tableName,
						getColumnNames(table, session), appendRaddioButton.isSelected(), fieldSeparatorTextField.getText(), stringDelimitatorTextField.getText(), directoryTextfield.getText());
	
				/* Shows information dialog to the user */
				JOptionPane.showMessageDialog((Component) e.getSource(), "SQL*Loader control file(s) created in " + directoryTextfield.getText());
			} catch (IOException e1) {
				getLog().error("I/O error while writing control file.", e1);
				e1.printStackTrace();
			} catch (SQLException e2) {
				getLog().error("Error retrieving columns from table "
						+ tableName, e2);
				e2.printStackTrace();
			}
		}
	}

	/**
	 * It returns the column names of the specified table.
	 * 
	 * @param table		table to get the columns from
	 * @param session	database session
	 * 
	 * @return	the column names of the specified table
	 * 
	 * @throws SQLException	throws if an error occurs while retrieving the
	 * 						column names
	 */
	private String[] getColumnNames(ITableInfo table, ISession session) throws SQLException {
		final TableColumnInfo[] columns = session.getMetaData().getColumnInfo(table);
		final int columnCount = columns.length;
		String[] columnNames = new String[columnCount];

		for (int i = 0; i < columnCount; i++) {
			columnNames[i] = columns[i].getColumnName();
		}
		return columnNames;
	}

	/**
	 * Returns the logger.
	 * 
	 * @return	the logger
	 */
	private ILogger getLog() {
		/* If the logger is not yet instantiated, it is created here */ 
		if (log == null) {
			log = LoggerController.createLogger(GenerateControlFileActionListener.class);
		}
		return log;
	}
}

