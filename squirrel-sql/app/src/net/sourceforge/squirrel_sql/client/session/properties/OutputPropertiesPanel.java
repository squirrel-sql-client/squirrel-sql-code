package net.sourceforge.squirrel_sql.client.session.properties;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetModelJTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class OutputPropertiesPanel implements IGlobalPreferencesPanel, ISessionPropertiesPanel {
	private String _title;
	private String _hint;

	private IApplication _app;
	private SessionProperties _props;

	private MyPanel _myPanel = new MyPanel();

	public OutputPropertiesPanel(String title, String hint) {
		super();
		_title = title != null ? title : MyPanel.i18n.OUTPUT;
		_hint = hint != null ? hint : MyPanel.i18n.OUTPUT;
	}

	public void initialize(IApplication app) throws IllegalArgumentException {
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_props = app.getSquirrelPreferences().getSessionProperties();

		_myPanel.loadData(_props);
	}

	public void initialize(IApplication app, ISession session)
			throws IllegalArgumentException {
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (session == null) {
			throw new IllegalArgumentException("Null ISession passed");
		}

		_app = app;
		_props = session.getProperties();

		_myPanel.loadData(_props);
	}

	public Component getPanelComponent() {
		return _myPanel;
	}

	public String getTitle() {
		return _title;
	}

	public String getHint() {
		return _hint;
	}

	public void applyChanges() {
		_myPanel.applyChanges(_props);
	}

	private static final class MyPanel extends JPanel {
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n {
			String OUTPUT = "Output";
			String COLUMNS = "Columns:";
			String CONTENT = "Content:";
			String DATA_TYPES = "Data Types:";
			String META_DATA = "Meta Data:";
			String PRIVILIGES = "Priviliges:";
			String TABLE_HDG = "Info:";
			String VERSIONS = "Versions:";

			String PROPERTY = "Property";
			String TABLE = "Table";
			String TEXT = "Text";
		}

		private OutputTypeCombo _metaDataCmb = new OutputTypeCombo();
		private OutputTypeCombo _dataTypeCmb = new OutputTypeCombo();
		private OutputTypeCombo _tableCmb = new OutputTypeCombo();
		private OutputTypeCombo _contentsCmb = new OutputTypeCombo();
		private OutputTypeCombo _columnsCmb = new OutputTypeCombo();
		private OutputTypeCombo _primKeysCmb = new OutputTypeCombo();
		private OutputTypeCombo _expKeysCmb = new OutputTypeCombo();
		private OutputTypeCombo _impKeysCmb = new OutputTypeCombo();
		private OutputTypeCombo _indexesCmb = new OutputTypeCombo();
		private OutputTypeCombo _priviligesCmb = new OutputTypeCombo();
		private OutputTypeCombo _columnPriviligesCmb = new OutputTypeCombo();
		private OutputTypeCombo _rowIdPriviligesCmb = new OutputTypeCombo();
		private OutputTypeCombo _versionsCmb = new OutputTypeCombo();
		private OutputTypeCombo _procColumnsCmb = new OutputTypeCombo();
		private OutputConverterCombo _sqlCmb = new OutputConverterCombo();
		private OutputTypeCombo _sqlMetaDataCmb = new OutputTypeCombo();

		MyPanel() {
			super();
			createUserInterface();
		}

		void loadData(SessionProperties props) {
			_metaDataCmb.selectClassName(props.getMetaDataOutputClassName());
			_dataTypeCmb.selectClassName(props.getDataTypesOutputClassName());
			_tableCmb.selectClassName(props.getTableOutputClassName());
			_contentsCmb.selectClassName(props.getContentsOutputClassName());
			_columnsCmb.selectClassName(props.getColumnsOutputClassName());
			_primKeysCmb.selectClassName(props.getPrimaryKeyOutputClassName());
			_expKeysCmb.selectClassName(props.getExportedKeysOutputClassName());
			_impKeysCmb.selectClassName(props.getImportedKeysOutputClassName());
			_indexesCmb.selectClassName(props.getIndexesOutputClassName());
			_priviligesCmb.selectClassName(props.getPriviligesOutputClassName());
			_columnPriviligesCmb.selectClassName(props.getColumnPriviligesOutputClassName());
			_rowIdPriviligesCmb.selectClassName(props.getRowIdOutputClassName());
			_versionsCmb.selectClassName(props.getVersionsOutputClassName());
			_procColumnsCmb.selectClassName(props.getProcedureColumnsOutputClassName());
			_sqlCmb.selectClassName(props.getSqlOutputConverterClassName());
			_sqlMetaDataCmb.selectClassName(props.getSqlOutputMetaDataClassName());
		}

		void applyChanges(SessionProperties props) {
			props.setMetaDataOutputClassName(_metaDataCmb.getSelectedClassName());
			props.setDataTypesOutputClassName(_dataTypeCmb.getSelectedClassName());
			props.setTableOutputClassName(_tableCmb.getSelectedClassName());
			props.setContentsOutputClassName(_contentsCmb.getSelectedClassName());
			props.setColumnsOutputClassName(_columnsCmb.getSelectedClassName());
			props.setPrimaryKeyOutputClassName(_primKeysCmb.getSelectedClassName());
			props.setExportedKeysOutputClassName(_expKeysCmb.getSelectedClassName());
			props.setImportedKeysOutputClassName(_impKeysCmb.getSelectedClassName());
			props.setIndexesOutputClassName(_indexesCmb.getSelectedClassName());
			props.setPriviligesOutputClassName(_priviligesCmb.getSelectedClassName());
			props.setColumnPriviligesOutputClassName(_columnPriviligesCmb.getSelectedClassName());
			props.setRowIdOutputClassName(_rowIdPriviligesCmb.getSelectedClassName());
			props.setVersionsOutputClassName(_versionsCmb.getSelectedClassName());
			props.setProcedureColumnsOutputClassName(_procColumnsCmb.getSelectedClassName());
			props.setSqlOutputConverterClassName(_sqlCmb.getSelectedClassName());
			props.setSqlOutputMetaDataClassName(_sqlMetaDataCmb.getSelectedClassName());
		}

		private void createUserInterface() {
			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = gbc.WEST;
			gbc.fill = gbc.HORIZONTAL;
			//gbc.insets = new Insets(4, 4, 4, 4);
			gbc.gridx = 0;
			gbc.gridy = 0;
			add(createDatabasePanel(), gbc);
			++gbc.gridy;
			add(createTablePanel(), gbc);
			++gbc.gridy;
			add(createProcedurePanel(), gbc);
			++gbc.gridy;
			add(createSQLPanel(), gbc);
		}

		private JPanel createDatabasePanel() {
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("Database"));
			
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(new RightLabel(i18n.META_DATA), gbc);
			++gbc.gridx;
			pnl.add(_metaDataCmb, gbc);
			++gbc.gridx;
			pnl.add(new RightLabel(i18n.DATA_TYPES), gbc);
			++gbc.gridx;
			pnl.add(_dataTypeCmb, gbc);
			
			return pnl;
		}

		private JPanel createTablePanel() {
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("Tables"));
			
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(new RightLabel(i18n.TABLE_HDG), gbc);
			++gbc.gridx;
			pnl.add(_tableCmb, gbc);

			++gbc.gridx;
			pnl.add(new RightLabel(i18n.CONTENT), gbc);
			++gbc.gridx;
			pnl.add(_contentsCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(new RightLabel(i18n.COLUMNS), gbc);
			++gbc.gridx;
			pnl.add(_columnsCmb, gbc);

			++gbc.gridx;
			pnl.add(new RightLabel("Primary Keys:"), gbc);
			++gbc.gridx;
			pnl.add(_primKeysCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(new RightLabel("Exported Keys:"), gbc);
			++gbc.gridx;
			pnl.add(_expKeysCmb, gbc);
			
			++gbc.gridx;
			pnl.add(new RightLabel("Imported Keys:"), gbc);
			++gbc.gridx;
			pnl.add(_impKeysCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(new RightLabel("Indexes:"), gbc);
			++gbc.gridx;
			pnl.add(_indexesCmb, gbc);

			++gbc.gridx;
			pnl.add(new RightLabel(i18n.PRIVILIGES), gbc);
			++gbc.gridx;
			pnl.add(_priviligesCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(new RightLabel("Column Priviliges:"), gbc);
			++gbc.gridx;
			pnl.add(_columnPriviligesCmb, gbc);

			++gbc.gridx;
			pnl.add(new RightLabel("Row ID:"), gbc);
			++gbc.gridx;
			pnl.add(_rowIdPriviligesCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(new RightLabel(i18n.VERSIONS), gbc);
			++gbc.gridx;
			pnl.add(_versionsCmb, gbc);

			return pnl;
		}

		private JPanel createProcedurePanel() {
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("Procedures"));
			
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(new RightLabel("Stored Proc Columns:"), gbc);
			++gbc.gridx;
			pnl.add(_procColumnsCmb, gbc);
			
			return pnl;
		}

		private JPanel createSQLPanel() {
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("SQL"));
			
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(new RightLabel("SQL:"), gbc);
			++gbc.gridx;
			pnl.add(_sqlCmb, gbc);

			++gbc.gridx;
			pnl.add(new RightLabel("SQL Meta Data:"), gbc);
			++gbc.gridx;
			pnl.add(_sqlMetaDataCmb, gbc);
			
			return pnl;
		}

		private final static class OutputType {
			static final OutputType TEXT = new OutputType(i18n.TEXT, DataSetViewerTextPanel.class.getName());
			static final OutputType TABLE = new OutputType(i18n.TABLE, DataSetViewerTablePanel.class.getName());
			static final OutputType TABLE_CONVERTER = new OutputType(i18n.TABLE, DataSetModelJTableModel.class.getName());
			private final String _name;
			private final String _className;

			OutputType(String name, String className) {
				super();
				_name = name;
				_className = className;
			}

			public String toString() {
				return _name;
			}

			String getPanelClassName() {
				return _className;
			}
		}

		private static final class OutputTypeCombo extends JComboBox {
			OutputTypeCombo() {
				super();
				addItem(OutputType.TABLE);
				addItem(OutputType.TEXT);
			}

			void selectClassName(String className) {
				if (className.equals(DataSetViewerTablePanel.class.getName())) {
					setSelectedItem(OutputType.TABLE);
				} else {
					setSelectedItem(OutputType.TEXT);
				}
			}

			String getSelectedClassName() {
				return ((OutputType)getSelectedItem()).getPanelClassName();
			}
		}

		private static final class RightLabel extends JLabel {
			RightLabel(String title) {
				super(title, SwingConstants.RIGHT);
			}
		}

		private static final class OutputConverterCombo extends JComboBox {
			OutputConverterCombo() {
				super();
				addItem(OutputType.TABLE_CONVERTER);
			}

			void selectClassName(String className) {
				if (className.equals(DataSetModelJTableModel.class.getName())) {
					setSelectedItem(OutputType.TABLE_CONVERTER);
				} else {
					//setSelectedItem(OutputType.TEXT);
				}
			}

			String getSelectedClassName() {
				return ((OutputType)getSelectedItem()).getPanelClassName();
			}
		}

	}
}
