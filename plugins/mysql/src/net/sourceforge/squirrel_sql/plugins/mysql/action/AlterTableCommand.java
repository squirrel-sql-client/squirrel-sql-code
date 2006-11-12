package net.sourceforge.squirrel_sql.plugins.mysql.action;
/*
 * Copyright (C) 2003 Arun Kapilan.P
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
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.WrappedSQLException;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
//import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
//import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;
import net.sourceforge.squirrel_sql.plugins.mysql.gui.AlterTableDialog;
/**
 * AlterTableCommand.java
 *
 * Created on June 10, 2003, 3:08 PM
 *
 * @author  Arun Kapilan.P
 */
public class AlterTableCommand implements ICommand
{
//	private javax.swing.JComboBox cbFieldType;
//	private javax.swing.JCheckBox chAutoIncrement;
//	private javax.swing.JCheckBox chBinary;
//	private javax.swing.JCheckBox chNotNull;
//	private javax.swing.JCheckBox chUnsigned;
//	private javax.swing.JLabel lbAttributes;
//	private javax.swing.JLabel lbDefault;
//	private javax.swing.JLabel lbFieldLength;
//	private javax.swing.JLabel lbFieldName;
//	private javax.swing.JLabel lbFieldType;
//	private javax.swing.JLabel lbFields;
//	private javax.swing.JTextField tfFieldDefault;
//	private javax.swing.JTextField tfFieldLength;
//	private javax.swing.JComboBox cbFieldName;
//	private javax.swing.JButton buttonUpdate;
//	private javax.swing.JButton buttonCancel;
//	private JDialog _dlog;
//	private FieldDetails fd;
//	private int selectedIndex;
//	private DBUtils dbUtils;
//	protected String SQLCommandRoot = "ALTER TABLE ";
//	protected String SQLCommand = "";
	// End of variables declaration

//	/** Logger for this class. */
//	private final static ILogger s_log =
//		LoggerController.createLogger(AlterTableCommand.class);

	/** Current session. */
	private ISession _session;

	/** Current plugin. */
	private final MysqlPlugin _plugin;

	/** Points to the table we want to modify. */
	private final ITableInfo _ti;

	/**
	 * Ctor.
	 *
	 * @param	session		Current session.
	 * @param	plugin		This plugin.
	 * @param	ti			Points to table to be modified.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</T>>,
	 *			<TT>MysqlPlugin</TT> or <<TT>ITableInfo</TT> passed.
	 */
	public AlterTableCommand(ISession session, MysqlPlugin plugin,
								ITableInfo ti)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("MysqlPlugin == null");
		}
		if (ti == null)
		{
			throw new IllegalArgumentException("ITableInfo == null");
		}

		_session = session;
		_plugin = plugin;
		_ti = ti;
//		initComponents();
	}

	public void execute() throws BaseException
	{
		try
		{
			AlterTableDialog dlog = new AlterTableDialog(_session, _plugin, _ti);
			dlog.pack();
			GUIUtils.centerWithinParent(dlog);
			dlog.setVisible(true);
		}
		catch (SQLException ex)
		{
			throw new WrappedSQLException(ex);
		}
	}

//	private void initComponents()
//	{
//		lbFields = new javax.swing.JLabel();
//		lbFieldName = new javax.swing.JLabel();
//		lbFieldLength = new javax.swing.JLabel();
//		lbFieldType = new javax.swing.JLabel();
//		lbDefault = new javax.swing.JLabel();
//		lbAttributes = new javax.swing.JLabel();
//		chBinary = new javax.swing.JCheckBox();
//		chNotNull = new javax.swing.JCheckBox();
//		chUnsigned = new javax.swing.JCheckBox();
//		chAutoIncrement = new javax.swing.JCheckBox();
//		cbFieldName = new javax.swing.JComboBox();
//		cbFieldType = new javax.swing.JComboBox();
//		tfFieldLength = new javax.swing.JTextField();
//		tfFieldDefault = new javax.swing.JTextField();
//		buttonUpdate = new javax.swing.JButton();
//		buttonCancel = new javax.swing.JButton();
//		dbUtils = new DBUtils(_session, _plugin);
//		SQLCommandRoot += dbUtils.getTableInfo() + " CHANGE ";
//		_dlog = new JDialog(_session.getApplication().getMainFrame(),
//										"Alter Table...");
//		_dlog.getContentPane().setLayout(null);
//
//		_dlog.addWindowListener(new java.awt.event.WindowAdapter()
//		{
//			public void windowClosing(java.awt.event.WindowEvent evt)
//			{
//				closeDialog(evt);
//			}
//		});
//
//		lbFields.setText("Fields");
//		lbFields.setBorder(
//			new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
//		_dlog.getContentPane().add(lbFields);
//		lbFields.setBounds(20, 20, 40, 18);
//
//		lbFieldName.setFont(new java.awt.Font("Dialog", 0, 12));
//		lbFieldName.setText("Name:");
//		_dlog.getContentPane().add(lbFieldName);
//		lbFieldName.setBounds(20, 60, 40, 16);
//
//		lbFieldLength.setFont(new java.awt.Font("Dialog", 0, 12));
//		lbFieldLength.setText("Length/Set:");
//		_dlog.getContentPane().add(lbFieldLength);
//		lbFieldLength.setBounds(20, 120, 62, 16);
//
//		lbFieldType.setFont(new java.awt.Font("Dialog", 0, 12));
//		lbFieldType.setText("Type:");
//		_dlog.getContentPane().add(lbFieldType);
//		lbFieldType.setBounds(20, 90, 29, 16);
//
//		lbDefault.setFont(new java.awt.Font("Dialog", 0, 12));
//		lbDefault.setText("Default:");
//		_dlog.getContentPane().add(lbDefault);
//		lbDefault.setBounds(20, 150, 50, 16);
//
//		lbAttributes.setText("Attributes");
//		lbAttributes.setBorder(
//			new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
//		_dlog.getContentPane().add(lbAttributes);
//		lbAttributes.setBounds(20, 190, 60, 18);
//
//		chBinary.setFont(new java.awt.Font("Dialog", 0, 12));
//		chBinary.setText("Binary");
//		_dlog.getContentPane().add(chBinary);
//		chBinary.setBounds(20, 230, 70, 24);
//
//		chNotNull.setFont(new java.awt.Font("Dialog", 0, 12));
//		chNotNull.setText("Not Null");
//		_dlog.getContentPane().add(chNotNull);
//		chNotNull.setBounds(130, 230, 70, 24);
//
//		chUnsigned.setFont(new java.awt.Font("Dialog", 0, 12));
//		chUnsigned.setText("Unsigned");
//		_dlog.getContentPane().add(chUnsigned);
//		chUnsigned.setBounds(20, 270, 79, 24);
//
//		chAutoIncrement.setFont(new java.awt.Font("Dialog", 0, 12));
//		chAutoIncrement.setText("AutoIncrement");
//		_dlog.getContentPane().add(chAutoIncrement);
//		chAutoIncrement.setBounds(130, 270, 110, 24);
//
//		cbFieldName.setEditable(false);
//		cbFieldName.addActionListener(new java.awt.event.ActionListener()
//		{
//			public void actionPerformed(java.awt.event.ActionEvent evt)
//			{
//				cbFieldNameActionPerformed(evt);
//			}
//		});
//
//		_dlog.getContentPane().add(cbFieldName);
//		cbFieldName.setBounds(110, 60, 120, 20);
//
//		cbFieldType.setFont(new java.awt.Font("Dialog", 0, 12));
//		cbFieldType.addActionListener(new java.awt.event.ActionListener()
//		{
//			public void actionPerformed(java.awt.event.ActionEvent evt)
//			{
//				cbFieldTypeActionPerformed(evt);
//			}
//		});
//		cbFieldType.setModel(
//			new javax.swing.DefaultComboBoxModel(
//				new String[] {
//					"TINYINT",
//					"SMALLINT",
//					"MEDIUMINT",
//					"INT",
//					"BIGINT",
//					"FLOAT",
//					"DOUBLE",
//					"DECIMAL",
//					"DATE",
//					"DATETIME",
//					"TIMESTAMP",
//					"TIME",
//					"YEAR",
//					"CHAR",
//					"VARCHAR",
//					"TINYBLOB",
//					"TINYTEXT",
//					"TEXT",
//					"BLOB",
//					"MEDIUMBLOB",
//					"MEDIUMTEXT",
//					"LONGBLOB",
//					"LONGTEXT",
//					"ENUM",
//					"SET" }));
//		cbFieldType.addActionListener(new java.awt.event.ActionListener()
//		{
//			public void actionPerformed(java.awt.event.ActionEvent evt)
//			{
//				cbFieldTypeActionPerformed(evt);
//			}
//		});
//
//		_dlog.getContentPane().add(cbFieldType);
//		cbFieldType.setBounds(110, 90, 120, 20);
//
//		_dlog.getContentPane().add(tfFieldLength);
//		tfFieldLength.setBounds(110, 120, 120, 20);
//
//		_dlog.getContentPane().add(tfFieldDefault);
//		tfFieldDefault.setBounds(110, 150, 120, 20);
//
//		buttonUpdate.setFont(new java.awt.Font("Dialog", 0, 12));
//		buttonUpdate.setText("Update..");
//		buttonUpdate.addActionListener(new java.awt.event.ActionListener()
//		{
//			public void actionPerformed(java.awt.event.ActionEvent evt)
//			{
//				buttonUpdateActionPerformed(evt);
//			}
//		});
//
//		_dlog.getContentPane().add(buttonUpdate);
//		buttonUpdate.setBounds(130, 310, 81, 26);
//
//		buttonCancel.setFont(new java.awt.Font("Dialog", 0, 12));
//		buttonCancel.setText("Cancel");
//		buttonCancel.addActionListener(new java.awt.event.ActionListener()
//		{
//			public void actionPerformed(java.awt.event.ActionEvent evt)
//			{
//				buttonCancelActionPerformed(evt);
//			}
//		});
//
//		_dlog.getContentPane().add(buttonCancel);
//		buttonCancel.setBounds(220, 310, 81, 26);
//
//		_dlog.pack();
//	}

	//Action performed for the cancel button
//	private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt)
//	{
//		_dlog.setVisible(false);
//		_dlog.dispose();
//	}

	//Action performed for the Update button
//	private void buttonUpdateActionPerformed(java.awt.event.ActionEvent evt)
//	{
//		String query = getQuery();
//		dbUtils.execute(query);
//		_dlog.setVisible(false);
//		_dlog.dispose();
//		JOptionPane.showMessageDialog(null, "Field updated");
//	}

	//Action performed for the combo box
//	private void cbFieldNameActionPerformed(java.awt.event.ActionEvent evt)
//	{
//
//		selectedIndex = cbFieldName.getSelectedIndex();
//		DefaultComboBoxModel comboModel =
//			(DefaultComboBoxModel) cbFieldName.getModel();
//		FieldDetails fd = (FieldDetails) comboModel.getElementAt(selectedIndex);
//
//		cbFieldName.setSelectedItem(fd.getFieldName());
//		cbFieldType.setSelectedItem(fd.getFieldType());
//		tfFieldLength.setText(fd.getFieldLength());
//		tfFieldDefault.setText(fd.getDefault());
//		chAutoIncrement.setSelected(fd.IsAutoIncrement());
//		chNotNull.setSelected(fd.IsNotNull());
//
//	}

	//Action performed on selection of field type
//	private void cbFieldTypeActionPerformed(java.awt.event.ActionEvent evt)
//	{
//		if (cbFieldType.getSelectedItem().equals("VARCHAR"))
//		{
//			chBinary.setEnabled(true);
//			chUnsigned.setEnabled(false);
//		}
//		else
//		{
//			chUnsigned.setEnabled(false);
//		}
//		if (cbFieldType.getSelectedItem().equals("INT"))
//		{
//			chBinary.setEnabled(false);
//			chUnsigned.setEnabled(true);
//		}
//	}

	/** Closes the dialog */
//	private void closeDialog(java.awt.event.WindowEvent evt)
//	{
//		_dlog.setVisible(false);
//		_dlog.dispose();
//	}

	//Populate the dialog with the field properties when its loaded
//	public void execute() throws BaseException
//	{
//		JDialog jd =
//			new JDialog(
//				_session.getApplication().getMainFrame(),
//				"Properties",
//				true);
//		final SQLConnection conn = _session.getSQLConnection();
//		ResultSetMetaData rsmd;
//		int colCount = 0;
//
//		try
//		{
//			SQLDatabaseMetaData dmd = conn.getSQLMetaData();
//			IObjectTreeAPI treeAPI = _session.getObjectTreeAPI(_plugin);
//			IDatabaseObjectInfo[] dbInfo = treeAPI.getSelectedDatabaseObjects();
//			ITableInfo tableInfo = null;
//
//			// can't work with multiple selected objects
//			if (dbInfo.length != 1)
//			{
//				return;
//			}
//
//			// get the table info for the selected table
//			if (dbInfo[0] instanceof ITableInfo)
//			{
//				tableInfo = (ITableInfo) dbInfo[0];
//			}
//
//			final ResultSet rs = dmd.getColumns(tableInfo);
//			rsmd = rs.getMetaData();
//			colCount = rsmd.getColumnCount();
//			DefaultComboBoxModel comboModel =
//				(DefaultComboBoxModel) cbFieldName.getModel();
//
//			//Store the column info in a object (Field details) and add to the combo
//			while (rs.next())
//			{
//				fd = new FieldDetails();
//				fd.setFieldName(rs.getString(4));
//				fd.setFieldType(rs.getString(6).toUpperCase());
//				fd.setFieldLength(rs.getString(7));
//				fd.setDefault(rs.getString(13));
//				if ("auto_increment".equals(rs.getString(12)))
//					fd.setAutoIncrement(true);
//				else
//					fd.setAutoIncrement(false);
//				if ("0".equals(rs.getString(11)))
//					fd.setNotNull(true);
//				else
//					fd.setNotNull(false);
//
//				comboModel.addElement(fd);
//
//			}
//		}
//		catch (SQLException ex)
//		{
//			_session.getMessageHandler().showErrorMessage(ex);
//		}
//
//		JButton buttonUpdate = new JButton("Update");
//		buttonUpdate.addActionListener(new java.awt.event.ActionListener()
//		{
//			public void actionPerformed(java.awt.event.ActionEvent evt)
//			{
//				buttonUpdateActionPerformed(evt);
//			}
//		});
//		_dlog.setLocation(100, 100);
//		_dlog.setSize(325, 400);
//		_dlog.setVisible(true);
//
//	}

	//Get the query to alter the table
//	public String getQuery()
//	{
//		DefaultComboBoxModel comboModel =
//			(DefaultComboBoxModel) cbFieldName.getModel();
//		FieldDetails fd = (FieldDetails) comboModel.getElementAt(selectedIndex);
//		SQLCommand += fd.getFieldName();
//		SQLCommand += " ";
//		SQLCommand += cbFieldName.getSelectedItem();
//		SQLCommand += " ";
//		SQLCommand += cbFieldType.getSelectedItem();
//		SQLCommand += "(" + tfFieldLength.getText() + ")";
//		if (chAutoIncrement.isSelected())
//			SQLCommand += " AUTO_INCREMENT ";
//		if (chNotNull.isSelected())
//			SQLCommand += " NOT NULL ";
//		if (chUnsigned.isSelected())
//			SQLCommand += " UNSIGNED ";
//		SQLCommand = SQLCommandRoot + SQLCommand;
//		return (SQLCommand);
//	}

}
