/*
 * Copyright (C) 2002 Christian Sell
 * csell@users.sourceforge.net
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
 *
 * Created on 28. September 2002, 22:32
 */

package net.sourceforge.jcomplete.ui;

import java.util.*;
import java.util.List;
import java.awt.event.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.border.EmptyBorder;
import net.sourceforge.jcomplete.completions.SQLColumn;
import net.sourceforge.jcomplete.SQLSchema;
import net.sourceforge.jcomplete.Completion;


/**
 * a dialog which lets the user choose tables and associated columns and
 */
public class TableColumnChooser extends JDialog
{
    private static final String EMPTY_COL = "";

    private JList columnList;
    private JList tableList;
    private JButton cancelButton;
    private JButton insertButton;
    private JTextField aliasField;

    private CompletionListener  completor;
    private SQLColumn           sqlColumn;
    private boolean             existingCompleted=true, needsSeparator;
    private Map                 assignedTables = new HashMap();


    public TableColumnChooser(JFrame frame, SQLColumn sqlColumn, CompletionListener completor)
    {
        super(frame, true);
        this.completor = completor;
        this.sqlColumn = sqlColumn;

        String existingAlias = sqlColumn.getQualifier();
        String existingColumn = sqlColumn.getName();
        if(existingAlias != null || existingColumn != null)
            existingCompleted = false;

        //remove conflicting aliases
        List tables = sqlColumn.getStatement().getTables(null, null, null);
        if(existingAlias != null) {
            for(Iterator it=tables.iterator(); it.hasNext();) {
                SQLSchema.Table tb = (SQLSchema.Table)it.next();
                if(tb.hasAlias()) it.remove();
            }
        }
        //record predefined aliases
        for(Iterator it=tables.iterator(); it.hasNext();) {
            SQLSchema.Table table = (SQLSchema.Table)it.next();
            if(table.alias != null)
                assignedTables.put(table, table.alias);
        }
        Collections.sort(tables);

        initComponents(tables);
        if(existingAlias != null) {
            aliasField.setText(existingAlias);
            aliasField.setEnabled(false);
        }
        pack();
        tableList.requestFocus();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents(final List tables)
    {
        tableList = new JList();
        columnList = new JList();
        aliasField = new JTextField(10);
        insertButton = new JButton();
        cancelButton = new JButton();

        setTitle("Tables & Columns");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        insertButton.setText("Insert");
        insertButton.setEnabled(false);
        cancelButton.setText("Close");

        JPanel contentPane = new JPanel(new BorderLayout(0, 2), true);
        setContentPane(contentPane);

        JPanel listsPanel = new JPanel();
        listsPanel.setLayout(new GridLayout(1, 0, 2, 0));
        listsPanel.setBorder(new EmptyBorder(new Insets(0, 2, 2, 2)));

        tableList.setModel(new AbstractListModel() {
            public int getSize()
            {
                return tables.size();
            }
            public Object getElementAt(int index)
            {
                return tables.get(index);
            }
        });

        tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroller = new JScrollPane();
        tableScroller.setViewportView(tableList);
        listsPanel.add(tableScroller);

        columnList.setListData(new String[]{EMPTY_COL});
        columnList.setSelectionMode(sqlColumn.isRepeatable() ?
              ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);
        JScrollPane columnScroller = new JScrollPane();
        columnScroller.setViewportView(columnList);
        listsPanel.add(columnScroller);

        contentPane.add(listsPanel, BorderLayout.CENTER);

        JPanel choosePanel = new JPanel();
        choosePanel.setLayout(new GridLayout(2, 0, 0, 5));
        choosePanel.setBorder(new EmptyBorder(new Insets(2, 0, 2, 2)));

        JPanel aliasPanel = new JPanel();
        aliasPanel.setLayout(new BoxLayout(aliasPanel, BoxLayout.X_AXIS));
        aliasPanel.setBorder(new EmptyBorder(new java.awt.Insets(0, 2, 0, 0)));
        JLabel label = new JLabel("Alias: ", SwingConstants.TRAILING);
        label.setAlignmentY(Component.TOP_ALIGNMENT);
        aliasPanel.add(label);

        aliasPanel.add(aliasField);
        choosePanel.add(aliasPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(insertButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(cancelButton);

        choosePanel.add(buttonPanel);
        contentPane.add(choosePanel, BorderLayout.SOUTH);

        tableList.setNextFocusableComponent(columnList);
        columnList.setNextFocusableComponent(aliasField);

        // Event registration
        ////////////////////////////
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                performInsert();
            }
        });
        ActionListener cancelListener = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                closeDialog();
            }
        };
        getRootPane().registerKeyboardAction(
              cancelListener,
              KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true),
              JComponent.WHEN_IN_FOCUSED_WINDOW);
        cancelButton.addActionListener(cancelListener);
        tableList.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e)
            {
                if(existingCompleted && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(tableList.getSelectedValue() != null) {
                        aliasField.setText((String)tableList.getSelectedValue());
                        e.consume();
                    }
                }
            }
        });
        tableList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt)
            {
                tableListValueChanged(evt);
            }
        });
        columnList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt)
            {
                columnListValueChanged(evt);
            }
        });
        columnList.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e)
            {
                columnList.setSelectedIndex(0);
            }
        });
        KeyListener insertKeyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performInsert();
                }
            }
        };
        columnList.addKeyListener(insertKeyListener);
        aliasField.addKeyListener(insertKeyListener);
        insertButton.addKeyListener(insertKeyListener);
    }

    /*
     * Event handlers
     */

    private void performInsert()
    {
        final Object[] cols = columnList.getSelectedValues();
        if(cols.length == 0 || cols[0] == EMPTY_COL)
            return;

        final SQLColumn newCol = new SQLColumn(sqlColumn.getStatement());
        final String alias = aliasField.getText();
        final SQLSchema.Table table = (SQLSchema.Table)tableList.getSelectedValue();

        if(!table.hasAlias() && alias.length() > 0 && assignedTables.get(table) == null) {
            assignedTables.put(table, alias);
            sqlColumn.getStatement().setTable(table.catalog, table.schema, table.name, alias);
        }

        CompletionListener.Event event = new CompletionListener.Event(sqlColumn)
        {
            int index = 0;

            public boolean hasNext()
            {
                return index < cols.length;
            }

            public Completion next()
            {
                SQLColumn col = (!existingCompleted && index == 0) ? sqlColumn : newCol;
                col.setName((String)cols[index++]);
                if(alias.length() > 0) col.setQualifier(alias);
                needsSeparator = true;
                return col;
            }

            public boolean needsSeparator()
            {
                return needsSeparator;
            }
        };
        sqlColumn = (SQLColumn)completor.completionRequested(event);

        setExistingCompleted();

        if(sqlColumn.isRepeatable() == false)
            closeDialog();
    }

    private void columnListValueChanged(ListSelectionEvent evt)
    {
        Object[] cols = columnList.getSelectedValues();
        if(cols.length > 0 && cols[0] != EMPTY_COL)
            insertButton.setEnabled(true);
    }

    private void tableListValueChanged(ListSelectionEvent evt)
    {
        SQLSchema.Table table = (SQLSchema.Table)tableList.getSelectedValue();
        String alias = (String)assignedTables.get(table);
        if(alias != null) {
            aliasField.setText(alias);
            aliasField.setEnabled(false);
        } else if(existingCompleted) {
            aliasField.setText("");
            aliasField.setEnabled(true);
        }
        String[] columns = table.getColumns();
        columnList.setListData(columns);
        columnList.clearSelection();

        insertButton.setEnabled(false);
    }

    /*
     * switch mode after a pre-existing column has been completed
     */
    private void setExistingCompleted()
    {
        existingCompleted = true;
    }

    private void closeDialog()
    {
        setVisible(false);
        dispose();
    }

    public String getTable()
    {
        return (String)tableList.getSelectedValue();
    }
}
