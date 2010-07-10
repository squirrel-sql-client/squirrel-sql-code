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
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

public class OutputPropertiesPanel
        implements IGlobalPreferencesPanel, ISessionPropertiesPanel {
    private String _title;
    private String _hint;

    private IApplication _app;
    private SessionProperties _props;

    private MyPanel _myPanel = new MyPanel();

/*
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
    private OutputTypeCombo _sqlCmb = new OutputTypeCombo();
*/

    public OutputPropertiesPanel(String title, String hint/*SessionProperties props*/) {
        super();
//      if (props == null) {
//          throw new IllegalArgumentException("Null SessionProperties passed");
//      }
//      _props = props;
//      createUserInterface();
//      loadData();
        _title = title != null ? title : MyPanel.i18n.OUTPUT;
        _hint = hint != null ? hint : MyPanel.i18n.OUTPUT;
    }

//  public void setSessionProperties(SessionProperties value) {
//      _props = value;
//      loadData();
//  }

    public void initialize(IApplication app)
            throws IllegalArgumentException {
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }

        initialize(app, app.getSquirrelPreferences().getSessionProperties());
    }

    public void initialize(IApplication app, SessionProperties props)
            throws IllegalArgumentException {
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        if (props == null) {
            throw new IllegalArgumentException("Null SessionProperties passed");
        }

        _app = app;
        _props = props;

//      createUserInterface();
        _myPanel.loadData(_props);
//      loadData();
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
/*
        _props.setMetaDataOutputClassName(_metaDataCmb.getSelectedClassName());
        _props.setDataTypesOutputClassName(_dataTypeCmb.getSelectedClassName());
        _props.setTableOutputClassName(_tableCmb.getSelectedClassName());
        _props.setContentsOutputClassName(_contentsCmb.getSelectedClassName());
        _props.setColumnsOutputClassName(_columnsCmb.getSelectedClassName());
        _props.setPrimaryKeyOutputClassName(_primKeysCmb.getSelectedClassName());
        _props.setExportedKeysOutputClassName(_expKeysCmb.getSelectedClassName());
        _props.setImportedKeysOutputClassName(_impKeysCmb.getSelectedClassName());
        _props.setIndexesOutputClassName(_indexesCmb.getSelectedClassName());
        _props.setPriviligesOutputClassName(_priviligesCmb.getSelectedClassName());
        _props.setColumnPriviligesOutputClassName(_columnPriviligesCmb.getSelectedClassName());
        _props.setRowIdOutputClassName(_rowIdPriviligesCmb.getSelectedClassName());
        _props.setVersionsOutputClassName(_versionsCmb.getSelectedClassName());
        _props.setSqlOutputClassName(_sqlCmb.getSelectedClassName());
*/
    }

/*
    private void loadData() {
        _metaDataCmb.selectClassName(_props.getMetaDataOutputClassName());
        _dataTypeCmb.selectClassName(_props.getDataTypesOutputClassName());
        _tableCmb.selectClassName(_props.getTableOutputClassName());
        _contentsCmb.selectClassName(_props.getContentsOutputClassName());
        _columnsCmb.selectClassName(_props.getColumnsOutputClassName());
        _primKeysCmb.selectClassName(_props.getPrimaryKeyOutputClassName());
        _expKeysCmb.selectClassName(_props.getExportedKeysOutputClassName());
        _impKeysCmb.selectClassName(_props.getImportedKeysOutputClassName());
        _indexesCmb.selectClassName(_props.getIndexesOutputClassName());
        _priviligesCmb.selectClassName(_props.getPriviligesOutputClassName());
        _columnPriviligesCmb.selectClassName(_props.getColumnPriviligesOutputClassName());
        _rowIdPriviligesCmb.selectClassName(_props.getRowIdOutputClassName());
        _versionsCmb.selectClassName(_props.getVersionsOutputClassName());
        _sqlCmb.selectClassName(_props.getSqlOutputClassName());
    }
*/

/*
    private void createUserInterface() {
        setSingleColumn(false);
        add(new JLabel(i18n.META_DATA, SwingConstants.RIGHT), _metaDataCmb);
        add(new JLabel(i18n.DATA_TYPES, SwingConstants.RIGHT), _dataTypeCmb);
        add(new JLabel(i18n.TABLE_HDG, SwingConstants.RIGHT), _tableCmb);
        add(new JLabel(i18n.CONTENT, SwingConstants.RIGHT), _contentsCmb);
        add(new JLabel(i18n.COLUMNS, SwingConstants.RIGHT), _columnsCmb);
        add(new JLabel("Primary Keys:", SwingConstants.RIGHT), _primKeysCmb);
        add(new JLabel("Exported Keys:", SwingConstants.RIGHT), _expKeysCmb);
        add(new JLabel("Imported Keys:", SwingConstants.RIGHT), _impKeysCmb);
        add(new JLabel("Indexes:", SwingConstants.RIGHT), _indexesCmb);
        add(new JLabel(i18n.PRIVILIGES, SwingConstants.RIGHT), _priviligesCmb);
        add(new JLabel("Column Priviliges:", SwingConstants.RIGHT), _columnPriviligesCmb);
        add(new JLabel("Row ID:", SwingConstants.RIGHT), _rowIdPriviligesCmb);
        add(new JLabel(i18n.VERSIONS, SwingConstants.RIGHT), _versionsCmb);
        add(new JLabel("SQL:", SwingConstants.RIGHT), _sqlCmb);
    }
*/

    private static final class MyPanel extends PropertyPanel {
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
        private OutputTypeCombo _sqlCmb = new OutputTypeCombo();

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
            _sqlCmb.selectClassName(props.getSqlOutputClassName());
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
            props.setSqlOutputClassName(_sqlCmb.getSelectedClassName());
        }

        private void createUserInterface() {
            setSingleColumn(false);
            add(new JLabel(i18n.META_DATA, SwingConstants.RIGHT), _metaDataCmb);
            add(new JLabel(i18n.DATA_TYPES, SwingConstants.RIGHT), _dataTypeCmb);
            add(new JLabel(i18n.TABLE_HDG, SwingConstants.RIGHT), _tableCmb);
            add(new JLabel(i18n.CONTENT, SwingConstants.RIGHT), _contentsCmb);
            add(new JLabel(i18n.COLUMNS, SwingConstants.RIGHT), _columnsCmb);
            add(new JLabel("Primary Keys:", SwingConstants.RIGHT), _primKeysCmb);
            add(new JLabel("Exported Keys:", SwingConstants.RIGHT), _expKeysCmb);
            add(new JLabel("Imported Keys:", SwingConstants.RIGHT), _impKeysCmb);
            add(new JLabel("Indexes:", SwingConstants.RIGHT), _indexesCmb);
            add(new JLabel(i18n.PRIVILIGES, SwingConstants.RIGHT), _priviligesCmb);
            add(new JLabel("Column Priviliges:", SwingConstants.RIGHT), _columnPriviligesCmb);
            add(new JLabel("Row ID:", SwingConstants.RIGHT), _rowIdPriviligesCmb);
            add(new JLabel(i18n.VERSIONS, SwingConstants.RIGHT), _versionsCmb);
            add(new JLabel("SQL:", SwingConstants.RIGHT), _sqlCmb);
        }

        private final static class OutputType {
            static final OutputType TEXT = new OutputType(i18n.TEXT, net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel.class.getName());
            static final OutputType TABLE = new OutputType(i18n.TABLE, net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel.class.getName());
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
                if (className.equals(net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel.class.getName())) {
                    setSelectedItem(OutputType.TABLE);
                } else {
                    setSelectedItem(OutputType.TEXT);
                }
            }

            String getSelectedClassName() {
                return ((OutputType)getSelectedItem()).getPanelClassName();
            }
        }

    }
}
