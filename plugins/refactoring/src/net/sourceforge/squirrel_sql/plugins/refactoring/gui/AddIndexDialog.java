package net.sourceforge.squirrel_sql.plugins.refactoring.gui;
/*
* Copyright (C) 2007 Daniel Regli & Yannick Winiger
* http://sourceforge.net/projects/squirrel-sql
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

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;


public class AddIndexDialog extends AbstractRefactoringTabbedDialog {

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr =
            StringManagerFactory.getStringManager(AddIndexDialog.class);
    /**
     * Logger for this class.
     */
    private final static ILogger log =
            LoggerController.createLogger(AddIndexDialog.class);


    protected interface i18n {

        String DIALOG_TITLE =
                s_stringMgr.getString("AddIndexDialog.title");

        String TABBEDPANE_PROPERTIES_LABEL =
                s_stringMgr.getString("AddIndexDialog.propertiesTabname");

        String TABBEDPANE_COLUMNS_LABEL =
                s_stringMgr.getString("AddIndexDialog.columnsTabName");

        String PROPERTIES_NAME_LABEL =
                s_stringMgr.getString("AddIndexDialog.propertiesNameLabel");

        String PROPERTIES_TABLESPACE_LABEL =
                s_stringMgr.getString("AddIndexDialog.tableSpaceLabel");

        String PROPERTIES_ACCESS_METHOD_LABEL =
                s_stringMgr.getString("AddIndexDialog.accessMethodLabel");

        String PROPERTIES_UNIQUE_LABEL =
                s_stringMgr.getString("AddIndexDialog.uniqueLabel");

        String PROPERTIES_CLUSTERED_LABEL =
                s_stringMgr.getString("AddIndexDialog.clusteredLabel");

        String PROPERTIES_CONSTRAINT_LABEL =
                s_stringMgr.getString("AddIndexDialog.constraintLabel");

        String COLUMNS_COLUMN_HEADER =
                s_stringMgr.getString("AddIndexDialog.columnsTableHeader");

        String COLUMNS_ADD_BUTTON_LABEL =
                s_stringMgr.getString("AddIndexDialog.addButtonLabel");
        String COLUMNS_REMOVE_BUTTON_LABEL =
                s_stringMgr.getString("AddIndexDialog.removeButtonLabel");

    }

    private ColumnsTab _columnTab;
    private PropertiesTab _propertiesTab;
    private final String[] _tableColumns;


    public AddIndexDialog(String[] tableColumns) {
        super(new Dimension(400, 340));

        this._tableColumns = tableColumns;
        init();
    }


    private void init() {

        _columnTab = new AddIndexDialog.ColumnsTab();
        _propertiesTab = new PropertiesTab();
        pane.addTab(i18n.TABBEDPANE_PROPERTIES_LABEL, _propertiesTab);
        pane.addTab(i18n.TABBEDPANE_COLUMNS_LABEL, _columnTab);
        setAllButtonEnabled(false);
        setTitle(i18n.DIALOG_TITLE);
    }


    private void checkInputCompletion() {
        if (_columnTab._columnTable.getRowCount() == 0) {
            setAllButtonEnabled(false);
            return;
        }

        // if the check gets till here we have all the need information
        setAllButtonEnabled(true);
    }


    public void enableTablespaceField(boolean enable) {
        _propertiesTab.enableTableSpaceField(enable);
    }


    public void setAccessMethods(boolean enable, String[] accessMethods) {
        _propertiesTab.setAccessMethods(enable, accessMethods);
    }


    public String getIndexName() {
        return _propertiesTab.getIndexName();
    }


    public String getTablespaceText() {
        return _propertiesTab.getTablespaceText();
    }


    public String getAccessMethod() {
        return _propertiesTab.getAccessMethod();
    }


    public boolean isUniqueSet() {
        return _propertiesTab.isUniqueSet();
    }


    public String getConstraints() {
        return _propertiesTab.getConstraints();
    }


    public String[] getIndexColumns() {
        return _columnTab.getIndexColumns();
    }


    class PropertiesTab extends JPanel {


        private JTextField _indexNameField;
        private JTextField _tableSpaceField;
        private JComboBox _accessMethodBox;
        private JCheckBox _uniqueBox;
        private JTextArea _constraintArea;


        public PropertiesTab() {
            init();
        }


        private void init() {
            setLayout(new GridBagLayout());

            JLabel nameLabel = getBorderedLabel(i18n.PROPERTIES_NAME_LABEL, emptyBorder);

            _indexNameField = new JTextField();
            _indexNameField.setPreferredSize(mediumField);

            JLabel tableSpaceLabel = getBorderedLabel(i18n.PROPERTIES_TABLESPACE_LABEL, emptyBorder);
            _tableSpaceField = new JTextField();
            _tableSpaceField.setPreferredSize(mediumField);
            _tableSpaceField.setEnabled(false);

            JLabel accessMethodLabel = getBorderedLabel(i18n.PROPERTIES_ACCESS_METHOD_LABEL, emptyBorder);
            _accessMethodBox = new JComboBox();
            _accessMethodBox.setPreferredSize(mediumField);
            _accessMethodBox.setEditable(false);
            _accessMethodBox.setEnabled(false);

            JLabel uniqueLabel = getBorderedLabel(i18n.PROPERTIES_UNIQUE_LABEL, emptyBorder);
            _uniqueBox = new JCheckBox();


            JLabel constraintLabel = getBorderedLabel(i18n.PROPERTIES_CONSTRAINT_LABEL, emptyBorder);
            _constraintArea = new JTextArea();
            _constraintArea.setBorder(BorderFactory.createLineBorder(Color.black));
            _constraintArea.setPreferredSize(mediumField);
            _constraintArea.setLineWrap(true);
            _constraintArea.setWrapStyleWord(true);

            JScrollPane scrollAreaConstraint = new JScrollPane(_constraintArea);
            scrollAreaConstraint.setVerticalScrollBarPolicy(
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollAreaConstraint.setAutoscrolls(true);
            scrollAreaConstraint.setPreferredSize(mediumField);


            Insets boxesInsets = new Insets(5, 5, 0, 5);

            add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, boxesInsets, 0, 0));
            add(_indexNameField, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, boxesInsets, 0, 0));


            add(tableSpaceLabel, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, boxesInsets, 0, 0));
            add(_tableSpaceField, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, boxesInsets, 0, 0));


            add(accessMethodLabel, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, boxesInsets, 0, 0));
            add(_accessMethodBox, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, boxesInsets, 0, 0));


            add(uniqueLabel, new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, boxesInsets, 0, 0));
            add(_uniqueBox, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, boxesInsets, 0, 0));

            add(constraintLabel, new GridBagConstraints(0, 4, 1, 1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, boxesInsets, 0, 0));

            boxesInsets.bottom = 5;
            add(scrollAreaConstraint, new GridBagConstraints(1, 4, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, boxesInsets, 0, 0));


        }


        public void enableTableSpaceField(boolean enable) {
            _tableSpaceField.setEnabled(enable);
        }


        public void setAccessMethods(boolean enable, String[] accessMethods) {
            _accessMethodBox.setEnabled(enable);
            if (accessMethods != null) {
                for (String method : accessMethods) {
                    _accessMethodBox.addItem(method);
                }
            }
        }


        public String getIndexName() {
            return _indexNameField.getText();
        }


        public String getTablespaceText() {
            return _tableSpaceField.getText();
        }


        public String getAccessMethod() {
            return _accessMethodBox.getSelectedItem().toString();
        }


        public boolean isUniqueSet() {
            return _uniqueBox.isSelected();
        }


        public String getConstraints() {
            return _constraintArea.getText();
        }
    }

    class ColumnsTab extends JPanel {

        private JTable _columnTable;

        private final ColumnTableModel _columnTableModel = new ColumnTableModel();

        private JComboBox _columnBox;

        private JButton _removeButton, _addButton;


        public ColumnsTab() {
            init();
            initData();
        }


        private void init() {

            setLayout(new GridBagLayout());

            _columnTable = new JTable();

            _columnTable.setModel(_columnTableModel);
            _columnTable.setDragEnabled(false);
            _columnTable.setRowSelectionAllowed(true);
            _columnTable.setColumnSelectionAllowed(false);
            _columnTable.getTableHeader().setReorderingAllowed(false);
            _columnTable.setCellSelectionEnabled(false);
            _columnTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            _columnTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent event) {
                    _removeButton.setEnabled(true);
                }
            });

            JScrollPane scrollTablePane = new JScrollPane(_columnTable);

            _columnBox = new JComboBox();


            JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 10));
            _addButton = new JButton(AddIndexDialog.i18n.COLUMNS_ADD_BUTTON_LABEL);
            _addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String local = (String) _columnBox.getSelectedItem();

                    _columnBox.removeItem(local);

                    _columnTableModel.addColumn(local);
                    if (_columnBox.getItemCount() == 0)
                        _addButton.setEnabled(false);

                    AddIndexDialog.this.checkInputCompletion();
                }
            });

            _removeButton = new JButton(AddIndexDialog.i18n.COLUMNS_REMOVE_BUTTON_LABEL);
            _removeButton.setEnabled(false);

            _removeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int index = _columnTable.getSelectedRow();
                    if (index != -1) {
                        String removed = _columnTableModel.deleteRow(index);

                        _columnBox.addItem(removed);
                        _addButton.setEnabled(true);
                        if (_columnTableModel.getRowCount() == 0) {
                            _removeButton.setEnabled(false);
                        } else {
                            int deleteIndex = 0;
                            if (index > 0) deleteIndex = index - 1;
                            _columnTable.getSelectionModel().setSelectionInterval(0, deleteIndex);
                        }
                    }
                    AddIndexDialog.this.checkInputCompletion();
                }
            });

            buttonPanel.add(_addButton);
            buttonPanel.add(_removeButton);

            //adding all Colums together
            add(scrollTablePane, new GridBagConstraints(0, 0, 4, 3, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

            add(_columnBox, new GridBagConstraints(0, 3, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));

            add(buttonPanel, new GridBagConstraints(2, 3, 2, 0, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));

        }


        private void initData() {
            for (String column : _tableColumns) {
                _columnBox.addItem(column);
            }
        }


        public String[] getIndexColumns() {
            return _columnTableModel.getRowData().toArray(new String[]{});
        }

    }

    class ColumnTableModel extends AbstractTableModel {
        private final Vector<String> rowData = new Vector<String>();
        private final String[] columnNames = new String[]{i18n.COLUMNS_COLUMN_HEADER};


        public String getColumnName(int col) {
            return columnNames[col];
        }


        public int getRowCount() {
            return rowData.size();
        }


        public int getColumnCount() {
            return columnNames.length;
        }


        public Object getValueAt(int row, int col) {
            return rowData.get(row);
        }


        public boolean isCellEditable(int row, int col) {
            return false;
        }


        public void addColumn(String column) {
            rowData.add(column);
            fireTableDataChanged();
        }


        public String deleteRow(int row) {
            String removedRow = rowData.remove(row);
            fireTableDataChanged();
            return removedRow;
        }


        public Vector<String> getRowData() {
            return rowData;
        }
    }


    public static void main(String[] args) {
        // just plain data
        String[] tableColumns = {"id", "name", "street", "number"};
        String[] accessMethods = {"b-tree", "hash", "gin", "gi"};

        AddIndexDialog dialog = new AddIndexDialog(tableColumns);
        dialog.enableTablespaceField(true);
        dialog.setAccessMethods(true, accessMethods);
        dialog.setVisible(true);
    }

}
