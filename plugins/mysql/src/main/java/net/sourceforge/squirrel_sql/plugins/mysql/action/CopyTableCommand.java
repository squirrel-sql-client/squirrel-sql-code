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
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;
import net.sourceforge.squirrel_sql.plugins.mysql.util.DBUtils;
/*
 * CopyTableCommand.java
 *
 * Created on June 18, 2003, 5:37 PM
 *
 * @author Arun Kapilan.P
 */
public class CopyTableCommand implements ICommand
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CopyTableCommand.class);


	private javax.swing.JCheckBox chAllFields;
	private javax.swing.JButton buttonOk;
	private javax.swing.JButton buttonCancel;
	private javax.swing.JLabel lbCopyToNewTable;
	private javax.swing.JList listFields;
	private javax.swing.JRadioButton rdStructure;
	private javax.swing.JRadioButton rdStructureData;
	private javax.swing.JTextField tfTableName;
	private javax.swing.JDialog jd;
	private DBUtils dbUtils;
	private String[] colNames;
	//private JCheckBox[] chFields;
	private ITableInfo oldTableName;
	private String newTableName;
	private String SQLCommandRoot = "";
	private String SQLCommand = "";
	private String SQLQuery = "";
	private boolean isStructure = true;
	private DefaultListModel listModel;
//	private Object[] fields;
	private boolean isAllFields = true;

	/** Logger for this class. */
//	private final static ILogger s_log =
//		LoggerController.createLogger(CopyTableCommand.class);

	/** Current session. */
	private ISession _session;

	/** Current plugin. */
	private final MysqlPlugin _plugin;

	/**
	 * Ctor specifying the current session.
	 */
	public CopyTableCommand(ISession session, MysqlPlugin plugin)
	{
		super();
		_session = session;
		_plugin = plugin;
	}

	public void execute()
	{
		initComponents();
	}

	private void initComponents()
	{
		lbCopyToNewTable = new javax.swing.JLabel();
		tfTableName = new javax.swing.JTextField();
		rdStructure = new javax.swing.JRadioButton();
		rdStructureData = new javax.swing.JRadioButton();
		listFields = new javax.swing.JList(new DefaultListModel());
		chAllFields = new javax.swing.JCheckBox();
		dbUtils = new DBUtils(_session, _plugin);
		colNames = dbUtils.getColumnNames();
		oldTableName = dbUtils.getTableInfo();
		//chFields = new JCheckBox[colNames.length];
		listFields.setSelectionMode(
			ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listModel = (DefaultListModel) listFields.getModel();
		for (int i = 0; i < colNames.length; i++)
		{
			listModel.addElement(colNames[i]);
		}
		buttonOk = new javax.swing.JButton();
		buttonCancel = new javax.swing.JButton();
		jd =
			new JDialog(
				_session.getApplication().getMainFrame(),
				// i18n[mysql.copyTable=Copy Table...]
				s_stringMgr.getString("mysql.copyTable"));
		jd.getContentPane().setLayout(null);

		jd.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				closeDialog(evt);
			}
		});

		lbCopyToNewTable.setFont(new java.awt.Font("Dialog", 0, 12));
		// i18n[mysql.copyToNewTable=Copy to new Table:]
		lbCopyToNewTable.setText(s_stringMgr.getString("mysql.copyToNewTable"));
		jd.getContentPane().add(lbCopyToNewTable);
		lbCopyToNewTable.setBounds(20, 20, 110, 16);

		jd.getContentPane().add(tfTableName);
		tfTableName.setBounds(20, 40, 350, 20);

		rdStructure.setFont(new java.awt.Font("Dialog", 0, 12));
		// i18n[mysql.structure=Structure]
		rdStructure.setText(s_stringMgr.getString("mysql.structure"));
		rdStructure.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				rdStructureActionPerformed(evt);
			}
		});
		jd.getContentPane().add(rdStructure);
		rdStructure.setBounds(220, 90, 74, 24);
		rdStructure.setSelected(true);

		rdStructureData.setFont(new java.awt.Font("Dialog", 0, 12));
		// i18n[mysql.structureAndData=Structure and Data]
		rdStructureData.setText(s_stringMgr.getString("mysql.structureAndData"));
		rdStructureData.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				rdStructureDataActionPerformed(evt);
			}
		});
		jd.getContentPane().add(rdStructureData);
		rdStructureData.setBounds(220, 130, 130, 24);

		ButtonGroup group = new ButtonGroup();
		group.add(rdStructure);
		group.add(rdStructureData);

		jd.getContentPane().add(listFields);
		listFields.setBounds(30, 90, 130, 170);
		listFields
			.addListSelectionListener(
				new javax
				.swing
				.event
				.ListSelectionListener()
		{
			public void valueChanged(javax.swing.event.ListSelectionEvent evt)
			{
				listFieldsValueChanged(evt);
			}
		});
		listFields.setEnabled(false);

		buttonOk.setFont(new java.awt.Font("Dialog", 0, 12));
		// i18n[mysql.copyOk=Ok]
		buttonOk.setText(s_stringMgr.getString("mysql.copyOk"));
		buttonOk.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				buttonOkActionPerformed(evt);
			}
		});

		jd.getContentPane().add(buttonOk);
		buttonOk.setBounds(190, 230, 70, 26);

		buttonCancel.setFont(new java.awt.Font("Dialog", 0, 12));
		// i18n[mysql.copyCancel=Cancel]
		buttonCancel.setText(s_stringMgr.getString("mysql.copyCancel"));
		buttonCancel.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				buttonCancelActionPerformed(evt);
			}
		});

		jd.getContentPane().add(buttonCancel);
		buttonCancel.setBounds(280, 230, 73, 26);
		chAllFields.setFont(new java.awt.Font("Dialog", 0, 12));
		// i18n[mysql.withAllFields=With all Fields]
		chAllFields.setText(s_stringMgr.getString("mysql.withAllFields"));
		chAllFields.setSelected(true);
		chAllFields.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				chAllFieldsActionPerformed(evt);
			}
		});

		jd.getContentPane().add(chAllFields);
		chAllFields.setBounds(20, 60, 110, 24);

		jd.pack();
		jd.setSize(400, 300);
		jd.setLocation(100, 100);
		jd.setVisible(true);
	}
	private void listFieldsValueChanged(
		@SuppressWarnings("unused")
        javax.swing.event.ListSelectionEvent evt)
	{

	}

	//Set the list disabled if allfields of table are to be copied
	private void chAllFieldsActionPerformed(@SuppressWarnings("unused")
    java.awt.event.ActionEvent evt)
	{
		if (chAllFields.isSelected())
		{
			listFields.setEnabled(false);
			isAllFields = true;
		}
		else
		{
			listFields.setEnabled(true);
			isAllFields = false;
		}
	}

	//Set the boolean value when the user selects b/w structure and data
	private void rdStructureDataActionPerformed(@SuppressWarnings("unused")
    java.awt.event.ActionEvent evt)
	{
		if (rdStructureData.isSelected())
			isStructure = false;
		else
			isStructure = true;
	}

	private void rdStructureActionPerformed(@SuppressWarnings("unused")
    java.awt.event.ActionEvent evt)
	{
		if (rdStructure.isSelected())
			isStructure = true;
		else
			isStructure = false;
	}

	private void buttonCancelActionPerformed(@SuppressWarnings("unused")
    java.awt.event.ActionEvent evt)
	{
		jd.setVisible(false);
		jd.dispose();
	}

	private void buttonOkActionPerformed(@SuppressWarnings("unused")
    java.awt.event.ActionEvent evt)
	{
		newTableName = tfTableName.getText();
		String selectedFields = "";
		String fields = "";
		Object[] obj = listFields.getSelectedValues();
		for (int i = 0; i < obj.length; i++)
		{
			selectedFields += obj[i];
			if (i < obj.length - 1)
				selectedFields += ", ";
		}
		if (isAllFields)
			fields = "*";
		else
			fields = selectedFields;

		if (isStructure)
			SQLCommandRoot += "SELECT "
				+ fields
				+ " FROM "
				+ oldTableName
				+ " WHERE 1=0 ;";
		else
			SQLCommandRoot += "SELECT "
				+ fields
				+ " FROM "
				+ oldTableName
				+ " ;";

		SQLQuery = getQuery() + SQLCommandRoot;
		dbUtils.execute(SQLQuery);
		_session.getSessionInternalFrame().getObjectTreeAPI().refreshTree();
		jd.setVisible(false);
		jd.dispose();
		JOptionPane.showMessageDialog(
			null,
			"Table " + newTableName + " created");

	}

	public String getQuery()
	{
		String primaryKeyData = "";
		//DefaultListModel listModel = (DefaultListModel) listFields.getModel();
		SQLCommand = "CREATE TABLE " + newTableName + " ( ";
		primaryKeyData = dbUtils.getPrimaryKeyColumn();
		if (primaryKeyData.length() > 0)
			SQLCommand += "PRIMARY KEY ( " + primaryKeyData + " )";
		SQLCommand += " ) ";
		return (SQLCommand);
	}

	/** Closes the dialog */
	private void closeDialog(@SuppressWarnings("unused")
    java.awt.event.WindowEvent evt)
	{
		jd.setVisible(false);
		jd.dispose();
	}

}
