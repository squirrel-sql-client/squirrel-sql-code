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

import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
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
import java.util.HashMap;
import java.util.Vector;


public class AddForeignKeyDialog extends AbstractRefactoringTabbedDialog {

    private static final long serialVersionUID = -1468861452449568878L;
    
	 /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr =
            StringManagerFactory.getStringManager(AddForeignKeyDialog.class);
    /**
     * Logger for this class.
     */
    @SuppressWarnings("unused")
    private final static ILogger log =
            LoggerController.createLogger(AddForeignKeyDialog.class);


    protected interface i18n {
        String DIALOG_TITLE =
                s_stringMgr.getString("ForeignKeyDialog.title");
        String TABBEDPANE_PROPERTIES_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.propertiesTabName");

        String TABBEDPANE_COLUMNS_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.columnsTabName");

        String TABBEDPANE_ACTION_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.actionTabName");

        String PROPERTIES_NAME_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.nameLabel");

        String PROPERTIES_REFERENCES_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.referencesLabel");

        String PROPERTIES_DEFERABLE_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.defereableLabel");

        String PROPERTIES_DEFERRED_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.deferredLabel");

        String PROPERTIES_MATCH_FULL_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.matchFullLabel");

        String PROPERTIES_AUTO_FK_INDEX_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.autoFKIndexLabel");

        String PROPERTIES_FK_INDEX_NAME_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.fkIndexNameLabel");

        String PROPERTIES_COMMENT_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.commentLabel");

        String COLUMNS_LOCAL_COLUMN_HEADER =
                s_stringMgr.getString("ForeignKeyDialog.localTableHeader");

        String COLUMNS_LOCAL_COLUMN_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.localColumnLabel");

        String COLUMNS_REFERENCED_HEADER =
                s_stringMgr.getString("ForeignKeyDialog.referenedTableHeader");

        String COLUMNS_REFERENCING_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.referencingLabel");

        String COLUMNS_ADD_BUTTON_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.addButtonLabel");
        String COLUMNS_REMOVE_BUTTON_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.removeButtonLabel");

        String ACTION_ON_UPDATE_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.onUpdateLabel");

        String ACTION_ON_DELETE_LABEL =
                s_stringMgr.getString("ForeignKeyDialog.onDeleteLabel");

        String ACTION_NO_ACTION_OPTION =
                s_stringMgr.getString("ForeignKeyDialog.noActionOption");

        String ACTION_RESTRICT_OPTION =
                s_stringMgr.getString("ForeignKeyDialog.restrictOption");

        String ACTION_CASCADE_OPTION =
                s_stringMgr.getString("ForeignKeyDialog.cascadeOption");

        String ACTION_SET_NULL_OPTION =
                s_stringMgr.getString("ForeignKeyDialog.setNullOption");

        String ACTION_SET_DEFAULT_OPTION =
                s_stringMgr.getString("ForeignKeyDialog.setDefaultOption");
    }

    private final String _localTableName;
    private final String[] _localTableColumns;
    private String _referencedTable = "";
    private final HashMap<String, TableColumnInfo[]> _tables;

    private ColumnsTab _columnTab;
    private PropertiesTab _propertiesTab;
    private ActionTab _actionTab;


    public AddForeignKeyDialog(String localTable, String[] localTableColumns, HashMap<String, TableColumnInfo[]> tables) {
        super(new Dimension(430, 350));
        _localTableName = localTable;
        _localTableColumns = localTableColumns;
        _tables = tables;
        init();
    }

    private void init() {

        _columnTab = new ColumnsTab();
        _propertiesTab = new PropertiesTab();
        _actionTab = new ActionTab();
        pane.addTab(i18n.TABBEDPANE_PROPERTIES_LABEL, _propertiesTab);
        pane.addTab(i18n.TABBEDPANE_COLUMNS_LABEL, _columnTab);
        pane.addTab(i18n.TABBEDPANE_ACTION_LABEL, _actionTab);
        setAllButtonEnabled(false);
        setTitle(i18n.DIALOG_TITLE);
    }


    private void checkInputCompletion() {
        if (_columnTab._columTable.getRowCount() == 0) {
            setAllButtonEnabled(false);
            return;
        }

        // if the check gets till here we have all the need information
        setAllButtonEnabled(true);
    }

    public String getConstraintName() {
        return _propertiesTab.getNameField();
    }

    public boolean isDeferrable() {
        return _propertiesTab.getDeferrable();
    }

    public boolean isDeferred() {
        return _propertiesTab.getDeferred();
    }

    public boolean isMatchFull() {
        return _propertiesTab.getMatchFull();
    }

    public boolean isAutoFKIndex() {
        return _propertiesTab.getAutoFK();
    }

    public String getFKIndexName() {
        return _propertiesTab.getFkIndex();
    }

    public String getReferencedTable() {
        return _propertiesTab.getReferencesField();
    }

    public Vector<String[]> getReferencedColumns() {
        return _columnTab.getReferencedColumns();
    }

    public String getOnUpdateAction() {
        return _actionTab.getSelectedUpdateAction();
    }

    public String getOnDeleteAction() {
        return _actionTab.getSelectedDeleteAction();
    }


    class PropertiesTab extends JPanel {
        private static final long serialVersionUID = -8311422583709806081L;
        private JComboBox _referencesField;
        private JTextField _nameField;

        private JCheckBox _deferrableBox;

        private JCheckBox _deferredBox;

        private JCheckBox _matchFullBox;

        private JCheckBox _autoFKBox;

        private JTextField _fkIndexBox;

        public PropertiesTab() {
            init();
            initData();
        }


        private void init() {
            setLayout(new GridBagLayout());

            JLabel nameLabel = getBorderedLabel(i18n.PROPERTIES_NAME_LABEL, emptyBorder);

            _nameField = new JTextField();
            _nameField.setPreferredSize(mediumField);

            JLabel referencesLabel = getBorderedLabel(i18n.PROPERTIES_REFERENCES_LABEL, emptyBorder);
            _referencesField = new JComboBox();
            _referencesField.setPreferredSize(mediumField);

            _referencesField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    _referencedTable = (String) _referencesField.getSelectedItem();
                    _columnTab.setReferencedColumns(_referencedTable);
                }
            });

            JLabel deferrableLabel = getBorderedLabel(i18n.PROPERTIES_DEFERABLE_LABEL, emptyBorder);
            _deferrableBox = new JCheckBox();
            _deferrableBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if (!_deferrableBox.isSelected()) {
                        _deferredBox.setSelected(false);
                    }
                }
            });

            JLabel deferredLabel = getBorderedLabel(i18n.PROPERTIES_DEFERRED_LABEL, emptyBorder);
            _deferredBox = new JCheckBox();

            JLabel matchFullLabel = getBorderedLabel(i18n.PROPERTIES_MATCH_FULL_LABEL, emptyBorder);
            _matchFullBox = new JCheckBox();

            JLabel autoFKLabel = getBorderedLabel(i18n.PROPERTIES_AUTO_FK_INDEX_LABEL, emptyBorder);
            _autoFKBox = new JCheckBox();
            _autoFKBox.setSelected(true);
            _autoFKBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if (_autoFKBox.isSelected()) {
                        _fkIndexBox.setEnabled(true);
                    } else {
                        _fkIndexBox.setEnabled(false);
                    }
                }
            });

            JLabel fkIndexLabel = getBorderedLabel(i18n.PROPERTIES_FK_INDEX_NAME_LABEL, emptyBorder);
            _fkIndexBox = new JTextField("fki_");     // "fki_" is default startname
            _fkIndexBox.setPreferredSize(mediumField);

            GridBagConstraints gbc = c;            // local constraint

            gbc.gridx = 0;
            gbc.gridy = -1;

            add(nameLabel, getLabelConstraints(gbc));
            add(_nameField, getFieldConstraints(gbc));

            add(referencesLabel, getLabelConstraints(gbc));
            add(_referencesField, getFieldConstraints(gbc));

            add(deferrableLabel, getLabelConstraints(gbc));
            add(_deferrableBox, getFieldConstraints(gbc));

            add(deferredLabel, getLabelConstraints(gbc));
            add(_deferredBox, getFieldConstraints(gbc));

            add(matchFullLabel, getLabelConstraints(gbc));
            add(_matchFullBox, getFieldConstraints(gbc));

            add(autoFKLabel, getLabelConstraints(gbc));
            add(_autoFKBox, getFieldConstraints(gbc));

            add(fkIndexLabel, getLabelConstraints(gbc));
            add(_fkIndexBox, getFieldConstraints(gbc));

        }

        public void setReferencesTable(String[] tableList) {
            for (String table : tableList) {
                _referencesField.addItem(table);
            }
        }

        public void enableReferencedComboBox(boolean enable) {
            _referencesField.setEnabled(enable);
        }

        public String getNameField() {
            return _nameField.getText();
        }


        public String getReferencesField() {
            return _referencesField.getSelectedItem().toString();
        }

        public boolean getDeferrable() {
            return _deferrableBox.isSelected();
        }

        public boolean getDeferred() {
            return _deferredBox.isSelected();
        }

        public boolean getMatchFull() {
            return _matchFullBox.isSelected();
        }

        public boolean getAutoFK() {
            return _autoFKBox.isSelected();
        }

        public String getFkIndex() {
            return _fkIndexBox.getText();
        }


        private void initData() {
            String[] tables = _tables.keySet().toArray(new String[]{});
            setReferencesTable(tables);

        }


    }

    class ColumnsTab extends JPanel {
        private static final long serialVersionUID = 932908227634023574L;

        private JTable _columTable;

        private final ColumnTableModel _columnTableModel = new ColumnTableModel();

        private JComboBox localColumBox;

        private JComboBox referencingBox;

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

            JLabel localColumLabel = getBorderedLabel(i18n.COLUMNS_LOCAL_COLUMN_LABEL, emptyBorder);
            JLabel referencingLabel = getBorderedLabel(i18n.COLUMNS_REFERENCING_LABEL, emptyBorder);

            localColumBox = new JComboBox();
            localColumBox.setPreferredSize(mediumField);
            referencingBox = new JComboBox();
            localColumBox.setPreferredSize(mediumField);

            JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 10));
            addButton = new JButton(i18n.COLUMNS_ADD_BUTTON_LABEL);
            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String local = (String) localColumBox.getSelectedItem();
                    String ref = (String) referencingBox.getSelectedItem();

                    localColumBox.removeItem(local);
                    referencingBox.removeItem(ref);

                    _columnTableModel.addColumn(new String[]{local, ref});
                    _propertiesTab.enableReferencedComboBox(false);
                    if (localColumBox.getItemCount() == 0 || referencingBox.getItemCount() == 0)
                        addButton.setEnabled(false);

                    AddForeignKeyDialog.this.checkInputCompletion();
                }
            });

            removeButton = new JButton(i18n.COLUMNS_REMOVE_BUTTON_LABEL);
            removeButton.setEnabled(false);

            removeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int index = _columTable.getSelectedRow();
                    if (index != -1) {
                        String[] removed = _columnTableModel.deleteRow(index);

                        localColumBox.addItem(removed[0]);
                        referencingBox.addItem(removed[1]);
                        addButton.setEnabled(true);
                        if (_columnTableModel.getRowCount() == 0) {
                            removeButton.setEnabled(false);
                            _propertiesTab.enableReferencedComboBox(true);
                        } else {
                            int deleteIndex = 0;
                            if (index > 0) deleteIndex = index - 1;
                            _columTable.getSelectionModel().setSelectionInterval(0, deleteIndex);
                        }
                    }
                    AddForeignKeyDialog.this.checkInputCompletion();
                }
            });

            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);

            //adding all Colums together
            add(scrollTablePane, new GridBagConstraints(0, 0, 2, 3, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            add(localColumLabel, new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));
            add(localColumBox, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));

            add(referencingLabel, new GridBagConstraints(0, 4, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));
            add(referencingBox, new GridBagConstraints(1, 4, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));

            add(buttonPanel, new GridBagConstraints(0, 5, 2, 0, 1, 0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        }

        private void initData() {
            setLocalColumns(_localTableColumns);

            setReferencedColumns(_localTableName);
        }

        public void setReferencedColumns(String tableName) {
            TableColumnInfo[] tableColumns = _tables.get(tableName);
            Vector<String> columns = new Vector<String>();
            for (TableColumnInfo columnInfo : tableColumns) {
                columns.add(columnInfo.getColumnName());
            }
            setReferencedColumns(columns.toArray(new String[]{}));
        }

        private void setLocalColumns(String[] localColums) {
            localColumBox.removeAllItems();
            for (String colum : localColums) {
                localColumBox.addItem(colum);
            }
        }

        private void setReferencedColumns(String[] refColums) {
            referencingBox.removeAllItems();
            for (String ref : refColums) {
                referencingBox.addItem(ref);
            }
        }

        public Vector<String[]> getReferencedColumns() {
            return _columnTableModel.getRowData();
        }
    }

    class ColumnTableModel extends AbstractTableModel {
        private static final long serialVersionUID = -1809722908124102411L;
        private final Vector<String[]> rowData = new Vector<String[]>();
        private final String[] columnNames = new String[]{i18n.COLUMNS_LOCAL_COLUMN_HEADER, i18n.COLUMNS_REFERENCED_HEADER};

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
            return rowData.get(row)[col];
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void addColumn(String[] column) {
            rowData.add(column);
            fireTableDataChanged();
        }

        public String[] deleteRow(int row) {
            String[] removedRow = rowData.remove(row);
            fireTableDataChanged();
            return removedRow;
        }

        public Vector<String[]> getRowData() {
            return rowData;
        }
    }

    class ActionTab extends JPanel {
        private static final long serialVersionUID = -6693305068890653755L;
        private static final String NO_ACTION_COMMAND = "NO ACTION";
        private static final String RESTRICT_COMMAND = "RESTRICT";
        private static final String CASCADE_COMMAND = "CASCADE";
        private static final String SET_NULL_COMMAND = "SET NULL";
        private static final String SET_DEFAULT_COMMAND = "SET DEFAULT";

        private final ButtonGroup _onUpdateRadioButton = new ButtonGroup();
        private final ButtonGroup _onDeleteRadioButton = new ButtonGroup();

        public ActionTab() {
            init();
        }

        private void init() {
            setLayout(new GridBagLayout());

            JPanel onUpdatePanel = new JPanel(new GridLayout(0, 1));
            JPanel onDeletePanel = new JPanel(new GridLayout(0, 1));
            onUpdatePanel.setBorder(BorderFactory.createTitledBorder(i18n.ACTION_ON_UPDATE_LABEL));
            onDeletePanel.setBorder(BorderFactory.createTitledBorder(i18n.ACTION_ON_DELETE_LABEL));


            JRadioButton upNoActionButton = new JRadioButton(i18n.ACTION_NO_ACTION_OPTION);
            upNoActionButton.setActionCommand(NO_ACTION_COMMAND);
            upNoActionButton.setSelected(true);       // pre-selection
            JRadioButton deNoActionButton = new JRadioButton(i18n.ACTION_NO_ACTION_OPTION);
            deNoActionButton.setActionCommand(NO_ACTION_COMMAND);
            deNoActionButton.setSelected(true);       // pre-selection

            JRadioButton upRestrictButton = new JRadioButton(i18n.ACTION_RESTRICT_OPTION);
            upRestrictButton.setActionCommand(RESTRICT_COMMAND);
            JRadioButton deRestrictButton = new JRadioButton(i18n.ACTION_RESTRICT_OPTION);
            deRestrictButton.setActionCommand(RESTRICT_COMMAND);

            JRadioButton upCascadeButton = new JRadioButton(i18n.ACTION_CASCADE_OPTION);
            upCascadeButton.setActionCommand(CASCADE_COMMAND);
            JRadioButton deCascadeButton = new JRadioButton(i18n.ACTION_CASCADE_OPTION);
            deCascadeButton.setActionCommand(CASCADE_COMMAND);

            JRadioButton upSetNullButton = new JRadioButton(i18n.ACTION_SET_NULL_OPTION);
            upSetNullButton.setActionCommand(SET_NULL_COMMAND);
            JRadioButton deSetNullButton = new JRadioButton(i18n.ACTION_SET_NULL_OPTION);
            deSetNullButton.setActionCommand(SET_NULL_COMMAND);

            JRadioButton upSetDefaultButton = new JRadioButton(i18n.ACTION_SET_DEFAULT_OPTION);
            upSetDefaultButton.setActionCommand(SET_DEFAULT_COMMAND);
            JRadioButton deSetDefaultButton = new JRadioButton(i18n.ACTION_SET_DEFAULT_OPTION);
            deSetDefaultButton.setActionCommand(SET_DEFAULT_COMMAND);

            // add to the button group
            _onUpdateRadioButton.add(upNoActionButton);
            _onUpdateRadioButton.add(upRestrictButton);
            _onUpdateRadioButton.add(upCascadeButton);
            _onUpdateRadioButton.add(upSetNullButton);
            _onUpdateRadioButton.add(upSetDefaultButton);

            // add to the button group
            _onDeleteRadioButton.add(deNoActionButton);
            _onDeleteRadioButton.add(deRestrictButton);
            _onDeleteRadioButton.add(deCascadeButton);
            _onDeleteRadioButton.add(deSetNullButton);
            _onDeleteRadioButton.add(deSetDefaultButton);

            // add to the panel  - Update

            onUpdatePanel.add(upNoActionButton);
            onUpdatePanel.add(upRestrictButton);
            onUpdatePanel.add(upCascadeButton);
            onUpdatePanel.add(upSetNullButton);
            onUpdatePanel.add(upSetDefaultButton);

            // add to the panel  - Update
            onDeletePanel.add(deNoActionButton);
            onDeletePanel.add(deRestrictButton);
            onDeletePanel.add(deCascadeButton);
            onDeletePanel.add(deSetNullButton);
            onDeletePanel.add(deSetDefaultButton);

            // add to jpanel pane
            add(onUpdatePanel, new GridBagConstraints(0, 0, 1, 0, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
            add(onDeletePanel, new GridBagConstraints(1, 0, 1, 0, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));

        }

        public String getSelectedUpdateAction() {
            return _onUpdateRadioButton.getSelection().getActionCommand();
        }

        public String getSelectedDeleteAction() {
            return _onDeleteRadioButton.getSelection().getActionCommand();
        }

    }
}
