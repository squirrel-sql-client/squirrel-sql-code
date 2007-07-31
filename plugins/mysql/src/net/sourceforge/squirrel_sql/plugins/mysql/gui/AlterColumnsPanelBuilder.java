package net.sourceforge.squirrel_sql.plugins.mysql.gui;
/*
 * Copyright (C) 2003 Arun Kapilan.P
 *
 * Modifications Copyright (C) 2003 Colin Bell
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.FormLayout;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.sql.DataTypeInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
//import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
//import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.gui.builders.DefaultFormBuilder;
import net.sourceforge.squirrel_sql.client.gui.controls.ColumnsComboBox;
import net.sourceforge.squirrel_sql.client.gui.controls.DataTypesComboBox;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This builder creates the component that allows the user to alter the
 * structure of a column, add and remove columns.
 *
 * @author Arun Kapilan.P
 */
class AlterColumnsPanelBuilder
{
	/** Logger for this class. */
//	private final static ILogger s_log =
//		LoggerController.createLogger(AlterColumnsPanelBuilder.class);

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AlterTablePanelBuilder.class);

	/**
	 * Stores <TT>DataTypeInfo</TT> objects represeniting the different
	 * data types that MySQL supports keyed by an uppercase version of the
	 * type name. We uppercase the type name as MySQL and/or its JDBC
	 * drivers are inconsistent in the case of the data type names.
	 */
	private Map<String, DataTypeInfo> _dataTypesByTypeName;

	/**
	 * Update the status of the GUI controls as the user makes changes.
	 */
	private ControlMediator _mediator;

	/** Combobox of the <TT>TableColumnInfo</TT> objects in the database. */
	private ColumnsComboBox _columnsCmb;

	/** Combobox of the <TT>DataTypeInfo</TT> objects in the database. */
	private DataTypesComboBox _dataTypesCmb;

	/** Column length. */
	private IntegerField _columnLengthField;

	/** Default value. */
	private JTextField _defaultvalue;

	/** Are nulls allowed for this column? */
	private JCheckBox _allowNullChk;

	/** Is column an auto-increment column? */
	private JCheckBox _autoIncChk;

	/** Is column unsigned? */
	private JCheckBox _unsignedChk;

	/** Is column binary? */
	private JCheckBox _binaryChk;

	/** Is column zero fill? */
	private JCheckBox _zeroFillChk;

	AlterColumnsPanelBuilder()
	{
		super();
	}

	public JPanel buildPanel(ISession session, ITableInfo ti)
		throws SQLException
	{
		initComponents(session, ti);

		final FormLayout layout = new FormLayout(
				"12dlu, left:max(40dlu;pref), 3dlu, 75dlu:grow(0.50), 7dlu, 75dlu:grow(0.50), 3dlu",
				"");
		final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.setLeadingColumnOffset(1);

		builder.appendSeparator(getString("AlterColumnsPanelBuilder.selectcolumn"));
		builder.append(getString("AlterColumnsPanelBuilder.columnname"), _columnsCmb, 3);

		builder.appendSeparator(getString("AlterColumnsPanelBuilder.attributes"));
		builder.append(getString("AlterColumnsPanelBuilder.datatype"), _dataTypesCmb, 3);

		builder.nextLine();
		builder.append(getString("AlterColumnsPanelBuilder.length"), _columnLengthField, 3);

		builder.nextLine();
		builder.append(getString("AlterColumnsPanelBuilder.default"), _defaultvalue, 3);

		builder.nextLine();
		builder.setLeadingColumnOffset(3);
		builder.append(_unsignedChk);
		builder.append(_autoIncChk);

		builder.nextLine();
		builder.append(_binaryChk);
		builder.append(_zeroFillChk);

		builder.nextLine();
		builder.append(_allowNullChk);
		builder.setLeadingColumnOffset(1);

		return builder.getPanel();
	}

	private static String getString(String stringMgrKey)
	{
		return s_stringMgr.getString(stringMgrKey);
	}

	private void updateControlStatus()
	{
		final TableColumnInfo tci = _columnsCmb.getSelectedColumn();
		_dataTypesCmb.setSelectedItem(_dataTypesByTypeName.get(tci.getTypeName().toUpperCase()));



		_columnLengthField.setInt(tci.getColumnSize());
		_defaultvalue.setText(tci.getDefaultValue());



//		selectedIndex = cbFieldName.getSelectedIndex();
//		DefaultComboBoxModel comboModel =
//			(DefaultComboBoxModel) cbFieldName.getModel();
//		FieldDetails fd = (FieldDetails) comboModel.getElementAt(selectedIndex);
//
//		cbFieldName.setSelectedItem(fd.getFieldName());
//		cbFieldType.setSelectedItem(fd.getFieldType());
//		tfFieldDefault.setText(fd.getDefault());
//		chAutoIncrement.setSelected(fd.IsAutoIncrement());
//		chNotNull.setSelected(fd.IsNotNull());





//		boolean isSelected = _exportPrefsChk.isSelected();
//		_exportPrefsText.setEditable(isSelected);
//		_exportPrefsBtn.setEnabled(isSelected);
//
//		isSelected = _exportDriversChk.isSelected();
//		_exportDriversText.setEditable(isSelected);
//		_exportDriversBtn.setEnabled(isSelected);
//
//		isSelected = _exportAliasesChk.isSelected();
//		_exportAliasesText.setEditable(isSelected);
//		_exportAliasesBtn.setEnabled(isSelected);
//		_includeUserNamesChk.setEnabled(isSelected);
//		_includePasswordsChk.setEnabled(isSelected);
	}

	private void initComponents(ISession session, ITableInfo ti)
		throws SQLException
	{
		_dataTypesByTypeName = new HashMap<String, DataTypeInfo>();
		_mediator = new ControlMediator();

		final ISQLConnection conn = session.getSQLConnection();

		_columnsCmb = new ColumnsComboBox(conn, ti);

		_dataTypesCmb = new DataTypesComboBox(conn);
		for (int i = 0, limit = _dataTypesCmb.getItemCount(); i < limit; ++i)
		{
			DataTypeInfo dti = _dataTypesCmb.getDataTypeAt(i);
			_dataTypesByTypeName.put(dti.getSimpleName().toUpperCase(), dti);
		}

		_columnLengthField = new IntegerField();
		_defaultvalue = new JTextField();
		_allowNullChk = new JCheckBox(getString("AlterColumnsPanelBuilder.allownull"));
		_unsignedChk = new JCheckBox(getString("AlterColumnsPanelBuilder.unsigned"));
		_autoIncChk = new JCheckBox(getString("AlterColumnsPanelBuilder.autoinc"));
		_binaryChk = new JCheckBox(getString("AlterColumnsPanelBuilder.binary"));
		_zeroFillChk = new JCheckBox(getString("AlterColumnsPanelBuilder.zerofill"));

//		final File here = new File(".");
//
//		final String export = getString("ExportPanel.export");
//		_exportPrefsChk = new JCheckBox(export);
//		_exportDriversChk = new JCheckBox(export);
//		_exportAliasesChk = new JCheckBox(export);
//
//		_exportPrefsText = new JTextField();
//		_exportDriversText = new JTextField();
//		_exportAliasesText = new JTextField();
//
//		final String btnTitle = getString("ExportPanel.browse");
//		_exportPrefsBtn = new JButton(btnTitle);
//		_exportDriversBtn = new JButton(btnTitle);
//		_exportAliasesBtn = new JButton(btnTitle);
//
////		final ApplicationFiles appFiles = new ApplicationFiles();
////		_exportPrefsText.setText(getFileName(here, appFiles.getUserPreferencesFile().getName()));
////		_exportDriversText.setText(getFileName(here, appFiles.getDatabaseDriversFile().getName()));
////		_exportAliasesText.setText(getFileName(here, appFiles.getDatabaseAliasesFile().getName()));
//
//		_includeUserNamesChk = new JCheckBox(getString("ExportPanel.includeusers"));
//		_includePasswordsChk = new JCheckBox(getString("ExportPanel.includepasswords"));

		_columnsCmb.addActionListener(_mediator);
//		_exportDriversChk.addActionListener(_mediator);
//		_exportAliasesChk.addActionListener(_mediator);
//
//		_exportPrefsBtn.addActionListener(new BrowseButtonListener(_exportPrefsText));
//		_exportDriversBtn.addActionListener(new BrowseButtonListener( _exportDriversText));
//		_exportAliasesBtn.addActionListener(new BrowseButtonListener(_exportAliasesText));
//
//		_exportPrefsChk.setSelected(prefs.getExportPreferences());
//		_exportDriversChk.setSelected(prefs.getExportDrivers());
//		_exportAliasesChk.setSelected(prefs.getExportAliases());
//
//		_includeUserNamesChk.setSelected(prefs.getIncludeUserNames());
//		_includePasswordsChk.setSelected(prefs.getIncludePasswords());
//
//		_exportPrefsText.setText(prefs.getPreferencesFileName());
//		_exportDriversText.setText(prefs.getDriversFileName());
//		_exportAliasesText.setText(prefs.getAliasesFileName());

		updateControlStatus();
	}

	/**
	 * This class will update the status of the GUI controls as the user
	 * makes changes.
	 */
	private final class ControlMediator implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			updateControlStatus();
		}
	}

}
