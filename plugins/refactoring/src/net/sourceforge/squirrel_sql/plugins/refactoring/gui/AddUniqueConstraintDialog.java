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

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;


public class AddUniqueConstraintDialog extends AbstractRefactoringTabbedDialog {

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr =
            StringManagerFactory.getStringManager(AddUniqueConstraintDialog.class);


    protected interface i18n {
        String DIALOG_TITLE =
                s_stringMgr.getString("AddUniqueConstraintDialog.title");
        String TABBEDPANE_PROPERTIES_LABEL =
                s_stringMgr.getString("AddUniqueConstraintDialog.propertiesTabName");

        String TABBEDPANE_COLUMNS_LABEL =
                s_stringMgr.getString("AddUniqueConstraintDialog.columnsTabName");

        String PROPERTIES_NAME_LABEL =
                s_stringMgr.getString("AddUniqueConstraintDialog.nameLabel");

        String PROPERTIES_TABLE_NAME_LABEL =
                s_stringMgr.getString("AddUniqueConstraintDialog.tableNameLabel");

        String COLUMNS_LOCAL_COLUMN_HEADER =
                s_stringMgr.getString("AddUniqueConstraintDialog.columnsTableHeader");

        String COLUMNS_ADD_BUTTON_LABEL =
                s_stringMgr.getString("AddUniqueConstraintDialog.addButtonLabel");
        String COLUMNS_REMOVE_BUTTON_LABEL =
                s_stringMgr.getString("AddUniqueConstraintDialog.removeButtonLabel");
    }

    private final String _localTableName;
    private final String[] _localTableColumns;

    private ColumnsTab _columnTab;
    private PropertiesTab _propertiesTab;


    public AddUniqueConstraintDialog(String localTable, String[] localTableColumns) {
        super(new Dimension(430, 300));
        _localTableName = localTable;
        _localTableColumns = localTableColumns;
        init();
    }


    private void init() {

        _columnTab = new AddUniqueConstraintDialog.ColumnsTab();
        _propertiesTab = new AddUniqueConstraintDialog.PropertiesTab();
        pane.addTab(AddUniqueConstraintDialog.i18n.TABBEDPANE_PROPERTIES_LABEL, _propertiesTab);
        pane.addTab(AddUniqueConstraintDialog.i18n.TABBEDPANE_COLUMNS_LABEL, _columnTab);
        setAllButtonEnabled(false);
        setTitle(AddUniqueConstraintDialog.i18n.DIALOG_TITLE);
    }


    private void checkInputCompletion() {
        if (_columnTab._columTable.getRowCount() == 0 || getConstraintName().equals("")) {
            setAllButtonEnabled(false);
            return;
        }

        // if the check gets till here we have all the need information
        setAllButtonEnabled(true);
    }


    public String getConstraintName() {
        return _propertiesTab.getNameField();
    }


    public Vector<String> getUniqueColumns() {
        return _columnTab.getUniqueColumns();
    }


    class PropertiesTab extends JPanel {
        private JTextField _nameField;


        public PropertiesTab() {
            init();
        }


        private void init() {
            setLayout(new GridBagLayout());

            JLabel tableLabel = getBorderedLabel(i18n.PROPERTIES_TABLE_NAME_LABEL, emptyBorder);

            JTextField _tableField = new JTextField();
            _tableField.setPreferredSize(mediumField);
            _tableField.setText(_localTableName);
            _tableField.setEnabled(false);

            JLabel nameLabel = getBorderedLabel(i18n.PROPERTIES_NAME_LABEL, emptyBorder);

            _nameField = new JTextField();
            _nameField.setPreferredSize(mediumField);
            _nameField.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    checkInputCompletion();
                }
            });


            GridBagConstraints gbc = c;            // local constraint
            gbc.insets = new Insets(5, 5, 0, 5);

            gbc.gridx = 0;
            gbc.gridy = -1;

            add(tableLabel, getLabelConstraints(gbc));
            add(_tableField, getFieldConstraints(gbc));

            add(nameLabel, getLabelConstraints(gbc));
            add(_nameField, getFieldConstraints(gbc));

            add(new JPanel(), new GridBagConstraints(0, 2, 2, 1, 0, 2, GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, gbc.insets, 0, 0));


        }


        public String getNameField() {
            return _nameField.getText();
        }


    }

    class ColumnsTab extends JPanel {

        private JTable _columTable;

        private final ColumnTableModel _columnTableModel = new ColumnTableModel();

        private JComboBox localColumBox;

        private JButton removeButton, addButton;


        public ColumnsTab() {
            init();
            initData();
        }


        private void init() {

            setLayout(new GridBagLayout());

            _columTable = new JTable();

            _columTable.setModel(_columnTableModel);
            _columTable.setRowSelectionAllowed(true);
            _columTable.setColumnSelectionAllowed(false);
            _columTable.getTableHeader().setReorderingAllowed(false);
            _columTable.setCellSelectionEnabled(false);
            _columTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            _columTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent event) {
                    removeButton.setEnabled(true);
                }
            });

            JScrollPane scrollTablePane = new JScrollPane(_columTable);


            localColumBox = new JComboBox();
            localColumBox.setPreferredSize(mediumField);

            JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 10));
            addButton = new JButton(i18n.COLUMNS_ADD_BUTTON_LABEL);
            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String local = (String) localColumBox.getSelectedItem();

                    localColumBox.removeItem(local);

                    _columnTableModel.addColumn(local);
                    if (localColumBox.getItemCount() == 0)
                        addButton.setEnabled(false);

                    AddUniqueConstraintDialog.this.checkInputCompletion();
                }
            });

            removeButton = new JButton(i18n.COLUMNS_REMOVE_BUTTON_LABEL);
            removeButton.setEnabled(false);

            removeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int index = _columTable.getSelectedRow();
                    if (index != -1) {
                        String removed = _columnTableModel.deleteRow(index);

                        localColumBox.addItem(removed);
                        addButton.setEnabled(true);
                        if (_columnTableModel.getRowCount() == 0) {
                            removeButton.setEnabled(false);
                        } else {
                            int deleteIndex = 0;
                            if (index > 0) deleteIndex = index - 1;
                            _columTable.getSelectionModel().setSelectionInterval(0, deleteIndex);
                        }
                    }
                    AddUniqueConstraintDialog.this.checkInputCompletion();
                }
            });

            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);

            Insets boxesInsets = new Insets(5, 5, 0, 5);

            add(scrollTablePane, new GridBagConstraints(0, 0, 3, 3, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, boxesInsets, 0, 0));

            boxesInsets.bottom = 5;
            add(localColumBox, new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, boxesInsets, 0, 0));

            add(buttonPanel, new GridBagConstraints(1, 3, 2, 0, 0, 0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, boxesInsets, 0, 0));

        }


        private void initData() {
            setLocalColumns(_localTableColumns);

        }


        private void setLocalColumns(String[] localColums) {
            localColumBox.removeAllItems();
            for (String colum : localColums) {
                localColumBox.addItem(colum);
            }
        }


        public Vector<String> getUniqueColumns() {
            return _columnTableModel.getRowData();
        }
    }

    class ColumnTableModel extends AbstractTableModel {
        private final Vector<String> rowData = new Vector<String>();
        private final String[] columnNames = new String[]{i18n.COLUMNS_LOCAL_COLUMN_HEADER};


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
        AddUniqueConstraintDialog dialog = new AddUniqueConstraintDialog("local Table", new String[]{"id", "name", "street", "streetnr", "zip", "city"});
        dialog.setVisible(true);
    }

}
