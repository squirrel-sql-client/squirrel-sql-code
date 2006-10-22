/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
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

package net.sourceforge.squirrel_sql.client.gui.db;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * A dialog that can be used to get column(s) selected by the user 
 */
public class ColumnListDialog extends JDialog {

    private JLabel tableNameLabel = null;
    private JTextField tableNameTextField = null;
    private JLabel columnListLabel = null;
    private JList columnList = null;
    
    private JButton selectColumnButton = null;
    private JButton cancelButton = null;
    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ColumnListDialog.class);
    
    private interface i18n {
        //i18n[ColumnListDialog.cancelButtonLabel=Cancel]
        String CANCEL_BUTTON_LABEL = 
            s_stringMgr.getString("ColumnListDialog.cancelButtonLabel");
        //i18n[ColumnListDialog.columnNameLabel=Column: ]
        String COLUMN_NAME_LABEL = 
            s_stringMgr.getString("ColumnListDialog.columnNameLabel");
        //i18n[ColumnListDialog.dropButtonLabel=Drop Column(s)]
        String DROP_BUTTON_LABEL = 
            s_stringMgr.getString("ColumnListDialog.dropButtonLabel");
        //i18n[ColumnListDialog.dropErrorMessage=Can't drop all columns - a 
        //table must have at least one column
        String DROP_ERROR_MESSAGE = 
            s_stringMgr.getString("ColumnListDialog.dropErrorMessage");
        //i18n[ColumnListDialog.dropErrorTitle=Too Many Columns Selected]
        String DROP_ERROR_TITLE = 
            s_stringMgr.getString("Too Many Columns Selected");
        //i18n[ColumnListDialog.dropTitle=Select Column(s) To Drop]
        String DROP_TITLE = 
            s_stringMgr.getString("ColumnListDialog.dropTitle");
        //i18n[ColumnListDialog.modifyButtonLabel=Modify Column]
        String MODIFY_BUTTON_LABEL = 
            s_stringMgr.getString("ColumnListDialog.modifyButtonLabel");
        //i18n[ColumnListDialog.modifyTitle=Select Column To Modify]
        String MODIFY_TITLE = 
            s_stringMgr.getString("ColumnListDialog.modifyTitle");
        //i18n[ColumnListDialog.tableNameLabel=Table Name: ]
        String TABLE_NAME_LABEL = 
            s_stringMgr.getString("ColumnListDialog.tableNameLabel"); 
    }
    
    public static final int DROP_COLUMN_MODE = 0;
    public static final int MODIFY_COLUMN_MODE = 1;
    
    private int _mode = DROP_COLUMN_MODE;
    
    /**
     * 
     * @param mode TODO
     * @param tableName
     */
    public ColumnListDialog(String[] columnNames, int mode) { 
        _mode = mode;
        init(columnNames);
    }
            
    public void setColumnList(String[] columnNames) {
        if (columnList != null) {
            columnList.setListData(columnNames);
        } else {
            init(columnNames);
        }
    }
    
    public void setTableName(String tableName) {
        tableNameTextField.setText(tableName);
    }
    
    public String getTableName() {
        return tableNameTextField.getText();
    }
        
    public Object[] getSelectedColumnList() {
        return columnList.getSelectedValues();
    }
    
    public void addColumnSelectionListener(ActionListener listener) {
        selectColumnButton.addActionListener(listener);
    }
    
    public void setMultiSelection() {
        columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }
    
    public void setSingleSelection() {
        columnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private GridBagConstraints getLabelConstraints(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy++;                
        c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        return c;
    }
    
    private GridBagConstraints getFieldConstraints(GridBagConstraints c) {
        c.gridx++;
        c.anchor = GridBagConstraints.NORTHWEST;   
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }

    private JLabel getBorderedLabel(String text, Border border) {
        JLabel result = new JLabel(text);
        result.setBorder(border);
        result.setPreferredSize(new Dimension(115, 20));
        result.setHorizontalAlignment(SwingConstants.RIGHT);
        return result;
    }
        
    /**
     * Creates the UI for this dialog.
     */
    private void init(String[] columnNames) {
        super.setModal(true);        
        if (_mode == DROP_COLUMN_MODE) {
            setTitle(i18n.DROP_TITLE);
        } else {
            setTitle(i18n.MODIFY_TITLE);
        }
        setSize(300, 250);
        EmptyBorder border = new EmptyBorder(new Insets(5,5,5,5));
        Dimension mediumField = new Dimension(126, 20);
        
        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        pane.setBorder(new EmptyBorder(10,0,0,30));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = -1;

        // Table name
        tableNameLabel = getBorderedLabel(i18n.TABLE_NAME_LABEL, border);
        pane.add(tableNameLabel, getLabelConstraints(c));
        
        tableNameTextField = new JTextField();
        tableNameTextField.setPreferredSize(mediumField);
        tableNameTextField.setEditable(false);
        pane.add(tableNameTextField, getFieldConstraints(c));
                
        // Dialect list        
        columnListLabel = getBorderedLabel(i18n.COLUMN_NAME_LABEL, border);
        columnListLabel.setVerticalAlignment(JLabel.NORTH);
        pane.add(columnListLabel, getLabelConstraints(c));
        
        columnList = new JList(columnNames);
        columnList.setPreferredSize(mediumField);
        columnList.addListSelectionListener(new ColumnListSelectionListener());

        JScrollPane sp = new JScrollPane(columnList);
        c = getFieldConstraints(c);
        c.weightx = 1;
        c.weighty = 1;        
        c.fill=GridBagConstraints.BOTH;
        pane.add(sp, c);
                
        Container contentPane = super.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(pane, BorderLayout.CENTER);
        
        contentPane.add(getButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel getButtonPanel() {
        JPanel result = new JPanel();
        if (_mode == DROP_COLUMN_MODE) {
            selectColumnButton = new JButton(i18n.DROP_BUTTON_LABEL);    
        } else {
            selectColumnButton = new JButton(i18n.MODIFY_BUTTON_LABEL);
        }
        
        selectColumnButton.setEnabled(false);
        cancelButton = new JButton(i18n.CANCEL_BUTTON_LABEL);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        result.add(selectColumnButton);
        result.add(cancelButton);
        return result;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationArguments.initialize(new String[] {});
        String[] data = 
            new String [] {"A_Really_Long_Nasty_Column_Called_ColumnA", 
                           "ColumnB","ColumnC","ColumnD","ColumnE","ColumnF",
                           "ColumnG","ColumnH","ColumnI","ColumnJ","ColumnK",
                           "ColumnL","ColumnM","ColumnN","ColumnO","ColumnP",
                           "ColumnP","ColumnQ","ColumnR","ColumnS","ColumnT"};
        final ColumnListDialog c = new ColumnListDialog(data, 0);
        c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c.setTableName("FooTable");
        c.addComponentListener(new ComponentListener() {
            public void componentHidden(ComponentEvent e) {}
            public void componentMoved(ComponentEvent e) {}
            public void componentResized(ComponentEvent e) {
                System.out.println("Current size = "+c.getSize());
            }
            public void componentShown(ComponentEvent e) {}            
        });
        c.cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
        c.setVisible(true);
        
    }
    
    private class ColumnListSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            int[] selected = columnList.getSelectedIndices();
            if (selected != null
                    && _mode == DROP_COLUMN_MODE
                    && selected.length == columnList.getModel().getSize())
            {
                JOptionPane.showMessageDialog(ColumnListDialog.this, 
                                i18n.DROP_ERROR_MESSAGE, 
                                i18n.DROP_ERROR_TITLE, 
                                JOptionPane.ERROR_MESSAGE);
                columnList.clearSelection();
                selectColumnButton.setEnabled(false);
            } else {
                selectColumnButton.setEnabled(true);
            }
            
        }
        
    }
}
