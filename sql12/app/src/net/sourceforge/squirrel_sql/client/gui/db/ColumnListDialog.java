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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;

/**
 * A dialog that can be used to get columns to drop from the user 
 */
public class ColumnListDialog extends JDialog {

    private JLabel tableNameLabel = null;
    private JTextField tableNameTextField = null;
    private JLabel columnListLabel = null;
    private JList columnList = null;
    
    private JButton dropButton = null;
    private JButton cancelButton = null;
    
    private interface i18n {
        String DROP_BUTTON_LABEL = "Drop Column(s)";
        String CANCEL_BUTTON_LABEL = "Cancel";
        String TITLE = "Select Column(s) To Drop";
    }
    
    /**
     * 
     * @param tableName
     */
    public ColumnListDialog(String[] columnNames) { 
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
        
    public Object[] getColumnsToDropList() {
        return columnList.getSelectedValues();
    }
    
    public void addDropListener(ActionListener listener) {
        dropButton.addActionListener(listener);
    }
    
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        //columnNameTextField.requestFocus();                
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
        c.weightx = 1;
        c.weighty = 1;
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
        setTitle(i18n.TITLE);
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
        tableNameLabel = getBorderedLabel("Table Name: ", border);
        pane.add(tableNameLabel, getLabelConstraints(c));
        
        tableNameTextField = new JTextField();
        tableNameTextField.setPreferredSize(mediumField);
        tableNameTextField.setEditable(false);
        pane.add(tableNameTextField, getFieldConstraints(c));
                
        // Dialect list
        columnListLabel = getBorderedLabel("Column: ", border);
        columnListLabel.setVerticalAlignment(JLabel.NORTH);
        pane.add(columnListLabel, getLabelConstraints(c));
        
        columnList = new JList(columnNames);
        columnList.setPreferredSize(mediumField);
        columnList.addListSelectionListener(new ColumnListSelectionListener());
        JScrollPane sp = new JScrollPane();
        sp.getViewport().add(columnList);
        pane.add(sp, getFieldConstraints(c));        
                
        Container contentPane = super.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(pane, BorderLayout.CENTER);
        
        contentPane.add(getButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel getButtonPanel() {
        JPanel result = new JPanel();
        dropButton = new JButton(i18n.DROP_BUTTON_LABEL);
        dropButton.setEnabled(false);
        cancelButton = new JButton(i18n.CANCEL_BUTTON_LABEL);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        result.add(dropButton);
        result.add(cancelButton);
        return result;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationArguments.initialize(new String[] {});
        final ColumnListDialog c = new ColumnListDialog(new String [] {"A_Really_Long_Nasty_Column_Called_ColumnA", "ColumnB"});
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
                    && selected.length == columnList.getModel().getSize())
            {
                JOptionPane.showMessageDialog(ColumnListDialog.this, 
                                "Can't select all columns - a table must have at least one column", 
                                "Too Many Columns Selected", 
                                JOptionPane.ERROR_MESSAGE);
                columnList.clearSelection();
                dropButton.setEnabled(false);
            } else {
                dropButton.setEnabled(true);
            }
            
        }
        
    }
}
