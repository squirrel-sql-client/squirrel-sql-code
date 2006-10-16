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
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.DatabaseMetaData;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.db.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

/**
 * A dialog that can be used to get column info from the user for adding new 
 * columns or modifying existing ones.
 */
public class ColumnDetailDialog extends JDialog {

    private JLabel tableNameLabel = null;
    private JTextField tableNameTextField = null;
    private JLabel columnNameLabel = null;
    private JTextField columnNameTextField = null;
    private JLabel dialectLabel = null;
    private JComboBox dialectList = null;
    private JLabel typeLabel = null;
    private JComboBox typeList = null;
    private JLabel lengthLabel = null;
    private JSpinner lengthSpinner = null;
    private JLabel precisionLabel = null;
    private JSpinner precisionSpinner = null;
    private JLabel scaleLabel = null;
    private JSpinner scaleSpinner = null;
    private JLabel defaultLabel = null;
    private JTextField defaultTextField = null;
    private JLabel commentLabel = null;
    private JTextArea commentTextArea = null;
    private JLabel nullableLabel = null;
    private JCheckBox nullableCheckBox = null;    
    
    private JButton addButton = null;
    private JButton showSQLButton = null;
    private JButton cancelButton = null;
    
    private interface i18n {
        String ADD_BUTTON_LABEL = "Add Column";
        String CANCEL_BUTTON_LABEL = "Cancel";
        String COMMENT_LABEL = "Comment: ";
        String SHOW_BUTTON_LABEL = "Show SQL";
    }
    
    /**
     * 
     * @param tableName
     */
    public ColumnDetailDialog(String title) { 
        init(title);
    }
    
    /**
     * 
     * @param dbName
     */
    public void setSelectedDialect(String dbName) {
        dialectList.setSelectedItem(dbName);
    }
    
    public String getSelectedDBName() {
        return (String)dialectList.getSelectedItem();
    }
    
    public void setTableName(String tableName) {
        tableNameTextField.setText(tableName);
    }
    
    public String getTableName() {
        return tableNameTextField.getText();
    }
    
    public void setExistingColumnInfo(TableColumnInfo info) {
        columnNameTextField.setText(info.getColumnName());
    }
    
    public String getSelectedTypeName() {
        return (String)typeList.getSelectedItem();
    }
    
    /**
     * Returns a TableColumnInfo representation of the user's settings for the
     * column.
     *  
     * @return 
     */
    public TableColumnInfo getColumnInfo() {
        String tableName = tableNameTextField.getText();
        String columnName = columnNameTextField.getText();
        String typeName = (String)typeList.getSelectedItem();
        int dataType = JDBCTypeMapper.getJdbcType(typeName);
        
        SpinnerNumberModel sizeModel = null;
        if (JDBCTypeMapper.isNumberType(dataType)) {
            sizeModel = (SpinnerNumberModel)lengthSpinner.getModel();
        } else {
            sizeModel = (SpinnerNumberModel)precisionSpinner.getModel();
        }   
        int columnSize = sizeModel.getNumber().intValue();
        SpinnerNumberModel scaleModel = 
            (SpinnerNumberModel)scaleSpinner.getModel();
        int decimalDigits = scaleModel.getNumber().intValue();
        
        int isNullAllowed = 1;
        String isNullable = null;
        if (nullableCheckBox.isSelected()) {
            isNullAllowed = DatabaseMetaData.columnNullable;
            isNullable = "YES";
        } else {
            isNullAllowed = DatabaseMetaData.columnNoNulls;
            isNullable = "NO";
        }
        String remarks = null;
        if (commentTextArea.isEditable()) {
            remarks = commentTextArea.getText();
        }
        String defaultValue = defaultTextField.getText();
        
        // These are not used
        String catalog = null;
        String schema = null;
        int octetLength = 1;
        int ordinalPosition = 1;
        int radix = 1;
        
        TableColumnInfo result = 
            new TableColumnInfo(catalog, 
                                schema,
                                tableName,
                                columnName,
                                dataType,
                                typeName,
                                columnSize,
                                decimalDigits,
                                radix,
                                isNullAllowed,
                                remarks,
                                defaultValue,
                                octetLength,
                                ordinalPosition,
                                isNullable
                                );
        return result;
    }
    
    public void addOKListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }
    
    public void addShowSQLListener(ActionListener listener) {
        showSQLButton.addActionListener(listener);
    }
    
    public void addDialectListListener(ItemListener listener) {
        dialectList.addItemListener(listener);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        columnNameTextField.requestFocus();
        columnNameTextField.select(0, columnNameTextField.getText().length());                
    }
    
    private GridBagConstraints getLabelConstraints(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy++;                
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        return c;
    }
    
    private GridBagConstraints getFieldConstraints(GridBagConstraints c) {
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;   
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
    
    private JTextField getSizedTextField(Dimension mediumField) {
        JTextField result = new JTextField();
        result.setPreferredSize(mediumField);
        return result;
    }
    
    /**
     * Creates the UI for this dialog.
     */
    private void init(String title) {
        super.setModal(true);        
        setTitle(title);
        setSize(350, 400);
        EmptyBorder border = new EmptyBorder(new Insets(5,5,5,5));
        Dimension mediumField = new Dimension(126, 20);
        Dimension largeField = new Dimension(126, 60);
        
        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        pane.setBorder(new EmptyBorder(0,0,0,10));

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
        
        // Column name
        columnNameLabel = getBorderedLabel("Column Name: ", border);
        pane.add(columnNameLabel, getLabelConstraints(c));
        
        columnNameTextField = getSizedTextField(mediumField);
        columnNameTextField.setText("NewColumn");
        columnNameTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (columnNameTextField.getText().length() == 0) {
                    addButton.setEnabled(false);
                    showSQLButton.setEnabled(false);
                } else {
                    addButton.setEnabled(true);
                    showSQLButton.setEnabled(true);
                }
            }
        });
        pane.add(columnNameTextField, getFieldConstraints(c));
        
        // Dialect list
        dialectLabel = getBorderedLabel("Dialect: ", border);
        pane.add(dialectLabel, getLabelConstraints(c));
        
        Object[] dbNames = DialectFactory.getDbNames();
        dialectList = new JComboBox(dbNames);
        dialectList.setPreferredSize(mediumField);
        dialectList.addItemListener(new DialectTypeListListener());
        pane.add(dialectList, getFieldConstraints(c));        
        
        // Type list
        typeLabel = getBorderedLabel("Type: ", border);
        pane.add(typeLabel, getLabelConstraints(c));
        
        String[] jdbcTypes = JDBCTypeMapper.getJdbcTypeList();
        typeList = new JComboBox(jdbcTypes);
        typeList.addItemListener(new ColumnTypeListListener());
        typeList.setPreferredSize(mediumField);
        pane.add(typeList, getFieldConstraints(c));
        
        // Length
        lengthLabel = getBorderedLabel("Length: ", border);
        pane.add(lengthLabel, getLabelConstraints(c));
        
        lengthSpinner = new JSpinner();
        lengthSpinner.setPreferredSize(mediumField);
        double value = 10; 
        double min = 1;
        double max = Long.MAX_VALUE; 
        double step = 1; 
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step); 
        lengthSpinner.setModel(model);
        lengthSpinner.setPreferredSize(mediumField);
        pane.add(lengthSpinner, getFieldConstraints(c));
        
        precisionLabel = new JLabel("Precision: ");
        precisionLabel.setBorder(border);
        pane.add(precisionLabel, getLabelConstraints(c));

        // Precision
        precisionSpinner = new JSpinner();
        precisionSpinner.setPreferredSize(mediumField);
        value = 8; 
        min = 1;
        max = Long.MAX_VALUE; 
        step = 1; 
        SpinnerNumberModel precisionModel = 
            new SpinnerNumberModel(value, min, max, step); 
        precisionSpinner.setModel(precisionModel);
        precisionSpinner.setPreferredSize(mediumField);
        pane.add(precisionSpinner, getFieldConstraints(c));        

        // Scale
        scaleLabel = new JLabel("Scale: ");
        scaleLabel.setBorder(border);
        pane.add(scaleLabel, getLabelConstraints(c));

        scaleSpinner = new JSpinner();
        scaleSpinner.setPreferredSize(mediumField);
        value = 8; 
        min = 1;
        max = Long.MAX_VALUE; 
        step = 1; 
        SpinnerNumberModel scaleModel = 
            new SpinnerNumberModel(value, min, max, step); 
        scaleSpinner.setModel(scaleModel);
        scaleSpinner.setPreferredSize(mediumField);
        pane.add(scaleSpinner, getFieldConstraints(c));        
        
        // Default value
        defaultLabel = new JLabel("Default Value: ");
        defaultLabel.setBorder(border);
        pane.add(defaultLabel, getLabelConstraints(c));
        
        defaultTextField = new JTextField();
        defaultTextField.setPreferredSize(mediumField);
        pane.add(defaultTextField, getFieldConstraints(c));

        // Nullable
        nullableLabel = new JLabel("Nullable: ");
        nullableLabel.setBorder(border);
        pane.add(nullableLabel, getLabelConstraints(c));        
        
        nullableCheckBox = new JCheckBox("");
        nullableCheckBox.setSelected(true);
        pane.add(nullableCheckBox, getFieldConstraints(c));
        
        // Comment
        commentLabel = new JLabel(i18n.COMMENT_LABEL);
        commentLabel.setBorder(border);
        pane.add(commentLabel, getLabelConstraints(c));                
        
        commentTextArea = new JTextArea();
        commentTextArea.setBorder(new LineBorder(Color.DARK_GRAY, 1));
        commentTextArea.setLineWrap(true);
        c = getFieldConstraints(c);
        c.weightx = 2;
        c.weighty = 2;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(); 
        scrollPane.getViewport().add(commentTextArea);
        scrollPane.setPreferredSize(largeField);
        pane.add(scrollPane, c);        
        
        Container contentPane = super.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(pane, BorderLayout.CENTER);
        
        contentPane.add(getButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel getButtonPanel() {
        JPanel result = new JPanel();
        addButton = new JButton(i18n.ADD_BUTTON_LABEL);
        showSQLButton = new JButton(i18n.SHOW_BUTTON_LABEL);
        cancelButton = new JButton(i18n.CANCEL_BUTTON_LABEL);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        result.add(addButton);
        result.add(showSQLButton);
        result.add(cancelButton);
        return result;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationArguments.initialize(new String[] {});
        final ColumnDetailDialog c = new ColumnDetailDialog("Add New Column");
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
    private class ColumnTypeListListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            if (precisionSpinner == null) {
                return;
            }
            String columnType = (String)typeList.getSelectedItem();
            int jdbcType = JDBCTypeMapper.getJdbcType(columnType);
            if (JDBCTypeMapper.isNumberType(jdbcType)) {
                precisionSpinner.setEnabled(true);
                scaleSpinner.setEnabled(true);
                lengthSpinner.setEnabled(false);
            } else {
                precisionSpinner.setEnabled(false);
                scaleSpinner.setEnabled(false);
                lengthSpinner.setEnabled(true);                
            }
        }
        
    }
    
    private class DialectTypeListListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            String dbName = (String)dialectList.getSelectedItem();
            HibernateDialect dialect = DialectFactory.getDialect(dbName);
            if (!dialect.supportsColumnComment()) {
                commentTextArea.setEditable(false);
                commentTextArea.setToolTipText(dbName+" does not support column comments");
            } else {
                commentTextArea.setEditable(true);
                commentTextArea.setToolTipText("");
            }
        }
    }
}
