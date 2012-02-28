/*
 * Copyright (C) 2005 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.dbcopy.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.DBCopyPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;

public class PreferencesPanel extends JPanel  {                              

	private static final long serialVersionUID = 1L;

	DBCopyPreferenceBean _prefs = null;
    
    JCheckBox truncateCheckBox = null;
    
    JCheckBox fileCachingCheckBox = null;
    
    JTextField bufferSizeTextField = null;
    
    JLabel bufferSizeLabel = null;
    
    JCheckBox autoCommitCheckBox = null;
    
    JCheckBox commitAfterCreateTableCheckBox = null;
    
    JLabel commitRecordCountLabel = null;
    
    JTextField commitRecordCountTextField = null;
    
    JCheckBox saveScriptCheckBox = null;
    
    JCheckBox copyTableRecords = null;
    
    JCheckBox copyIndexDefs = null;
    
    JCheckBox copyForeignKeys = null;
    
    JCheckBox copyPrimaryKeys = null;
    
    JCheckBox pruneDuplicateIndexDefs = null;
    
    JCheckBox writeScriptCheckBox = null;

    JCheckBox appendRecordsToExistingCheckBox = null;

    JCheckBox promptForHibernateCheckBox = null;
    
    JCheckBox checkKeywordsCheckBox = null;
    
    JCheckBox testColumnNamesCheckBox = null;
    
    JLabel selectFetchSizeLabel = null;
    
    JTextField selectFetchSizeTextField = null;
    
    JCheckBox delayBetweenObjects = null;
    
    JLabel delayTablesLabel = null;
    
    JTextField delayTablesTextField = null;
    
    JLabel delayRecordsLabel = null;
    
    JTextField delayRecordsTextField = null;
        
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(PreferencesPanel.class);
    
    
    public PreferencesPanel(DBCopyPreferenceBean prefs) {
        
        super();
        _prefs = prefs;
        createGUI();
        loadData();
    }
    
    private void createGUI() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;   // Column 0
        c.gridy = 0;   // Row 0
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = .40;
        add(createTopPanel(), c);
    
        c = new GridBagConstraints();
        c.gridx = 0;   // Column 0
        c.gridy = 1;   // Row 1
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = .60;
        add(createBottomPanel(), c);
    }
    
    private JPanel createTopPanel() {
        JPanel result = new JPanel(new GridBagLayout());
        //i18n[PreferencesPanel.transferOptionsBorderLabel=Transfer Options]
        String tranferOptionsBorderLabel = 
            s_stringMgr.getString("PreferencesPanel.transferOptionsBorderLabel");
        result.setBorder(getTitledBorder(tranferOptionsBorderLabel));
        String cbLabel = 
            s_stringMgr.getString("PreferencesPanel.truncateLabel");
        truncateCheckBox = new JCheckBox(cbLabel);
        
        addUseTruncateCheckBox(result, 0, 0);
        addCopyTableRecordsCheckBox(result, 0, 1);
        
        addFetchSizeLabel(result, 0, 2);
        addFetchSizeTextField(result, 1, 2);
        
        addCopyPrimaryKeysCheckBox(result, 0, 3);
        addCopyForeignKeysCheckBox(result, 0, 4);
        addCopyIndexDefsCheckBox(result, 0, 5);        
        addPruneDuplicateIndexDefsCheckBox(result, 0, 6);
        addFileCacheCheckBox(result, 0, 7);        
        
        addBufferSizeLabel(result, 0, 8);
        addBufferSizeTextField(result, 1, 8);
        
        addAutoCommitCheckcBox(result, 0, 9);    
        addCommitAfterCreateTableCheckBox(result, 0, 10);
        
        addRecordCountLabel(result, 0, 11);
        addCommitRecordCountTextField(result, 1, 11);
        
        addDelayCheckBox(result, 0, 12);
        
        addDelayTablesLabel(result, 0, 13);
        addDelayTablesTextField(result, 1, 13);

        addDelayRecordsLabel(result, 0, 14);
        addDelayRecordsTextField(result, 1, 14);        
        
        addWriteScriptCheckBox(result, 0, 15);
        addAppendRecordsToExistingCheckBox(result, 0, 16);

        return result;
    }    
        
    private void addPruneDuplicateIndexDefsCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2;  // Span across two columns
        //c.ipadx = 40;
        c.insets = new Insets(10,25,0,0);
        c.anchor = GridBagConstraints.WEST;
        String cbLabelStr =         
            s_stringMgr.getString("PreferencesPanel.pruneDuplicateIndexDefs");
        String toolTipText = 
            s_stringMgr.getString("PreferencesPanel.pruneDuplicateIndexDefsToolTip");
        pruneDuplicateIndexDefs = new JCheckBox(cbLabelStr);
        pruneDuplicateIndexDefs.setToolTipText(toolTipText);
        panel.add(pruneDuplicateIndexDefs, c);        
    }
    
    private void addWriteScriptCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2;  // Span across two columns
        c.insets = new Insets(10,0,0,0);
        c.anchor = GridBagConstraints.WEST;
        String cbLabelStr =         
            s_stringMgr.getString("PreferencesPanel.writeScript");
        String toolTipText = 
            s_stringMgr.getString("PreferencesPanel.writeScriptToolTip");
        writeScriptCheckBox = new JCheckBox(cbLabelStr);
        writeScriptCheckBox.setToolTipText(toolTipText);
        panel.add(writeScriptCheckBox, c);                
    }

    private void addAppendRecordsToExistingCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2;  // Span across two columns
        c.insets = new Insets(10,0,0,0);
        c.anchor = GridBagConstraints.WEST;
        String cbLabelStr =
            s_stringMgr.getString("PreferencesPanel.appendRecordsToExistingCheckBox");
        String toolTipText =
            s_stringMgr.getString("PreferencesPanel.appendRecordsToExistingCheckBoxToolTip");
        appendRecordsToExistingCheckBox = new JCheckBox(cbLabelStr);
        appendRecordsToExistingCheckBox.setToolTipText(toolTipText);
        panel.add(appendRecordsToExistingCheckBox, c);
    }

    private void addUseTruncateCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2;  // Span across two columns
        c.insets = new Insets(10,0,0,0);
        c.anchor = GridBagConstraints.WEST;
        String cbToolTipText =
            s_stringMgr.getString("PreferencesPanel.truncateLabelTipText");
        truncateCheckBox.setToolTipText(cbToolTipText);
        panel.add(truncateCheckBox, c);        
    }
    
    private void addCopyTableRecordsCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2;  // Span across two columns
        c.insets = new Insets(10,0,0,0);
        c.anchor = GridBagConstraints.WEST;
        String cbLabelStr =         
            s_stringMgr.getString("PreferencesPanel.copyTableRecords");
        String toolTipText = 
            s_stringMgr.getString("PreferencesPanel.copyTableRecordsToolTip");
        copyTableRecords = new JCheckBox(cbLabelStr);
        copyTableRecords.setToolTipText(toolTipText);
        copyTableRecords.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectFetchSizeLabel.setEnabled(copyTableRecords.isSelected());
                selectFetchSizeTextField.setEnabled(copyTableRecords.isSelected());
            }
        });
        panel.add(copyTableRecords, c);        
    }
    
    private void addFetchSizeLabel(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.insets = new Insets(5,25,0,0);
        String bsLabel = 
            s_stringMgr.getString("PreferencesPanel.fetchSizeLabel");
        selectFetchSizeLabel = new JLabel(bsLabel);
        selectFetchSizeLabel.setHorizontalAlignment(JLabel.LEFT);
        String labelToolTipText = 
            s_stringMgr.getString("PreferencesPanel.fetchSizeToolTip");
        selectFetchSizeLabel.setToolTipText(labelToolTipText);
        panel.add(selectFetchSizeLabel, c);        
    }    
    
    private void addFetchSizeTextField(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;   
        c.ipadx = 40;    // Increases component width by 40 pixels
        c.insets = new Insets(5,5,0,0);
        c.anchor = GridBagConstraints.WEST;
        selectFetchSizeTextField = new JTextField(10);
        
        selectFetchSizeTextField.setHorizontalAlignment(JTextField.RIGHT);
        String toolTip = 
            s_stringMgr.getString("PreferencesPanel.fetchSizeTextFieldToolTip");
        selectFetchSizeTextField.setToolTipText(toolTip);
        panel.add(selectFetchSizeTextField, c);        
    }    
    
    private void addCopyPrimaryKeysCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2;  // Span across two columns
        c.insets = new Insets(10,0,0,0);
        c.anchor = GridBagConstraints.WEST;
        String cbLabelStr =         
            s_stringMgr.getString("PreferencesPanel.copyPrimaryKeys");
        String toolTipText = 
            s_stringMgr.getString("PreferencesPanel.copyPrimaryKeysToolTip");
        copyPrimaryKeys = new JCheckBox(cbLabelStr);
        copyPrimaryKeys.setToolTipText(toolTipText);
        panel.add(copyPrimaryKeys, c);                        
    }
    
    private void addCopyForeignKeysCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2;  // Span across two columns
        c.insets = new Insets(10,0,0,0);
        c.anchor = GridBagConstraints.WEST;
        String cbLabelStr =         
            s_stringMgr.getString("PreferencesPanel.copyForeignKeys");
        String toolTipText = 
            s_stringMgr.getString("PreferencesPanel.copyForeignKeysToolTip");
        copyForeignKeys = new JCheckBox(cbLabelStr);
        copyForeignKeys.setToolTipText(toolTipText);
        panel.add(copyForeignKeys, c);                
    }
    
    private void addCopyIndexDefsCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2;  // Span across two columns
        c.insets = new Insets(10,0,0,0);
        c.anchor = GridBagConstraints.WEST;
        String cbLabelStr =         
            s_stringMgr.getString("PreferencesPanel.copyIndexDefs");
        String toolTipText = 
            s_stringMgr.getString("PreferencesPanel.copyIndexDefsToolTip");
        copyIndexDefs = new JCheckBox(cbLabelStr);
        copyIndexDefs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (copyIndexDefs.isSelected()) {
                    pruneDuplicateIndexDefs.setEnabled(true);
                } else {
                    pruneDuplicateIndexDefs.setEnabled(false);
                }
            }
        });
        copyIndexDefs.setToolTipText(toolTipText);
        panel.add(copyIndexDefs, c);
    }
    
    private void addFileCacheCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2; // Span across two columns
        c.insets = new Insets(10,0,0,0);
        c.anchor = GridBagConstraints.WEST;
        String cbLabelStr = 
            s_stringMgr.getString("PreferencesPanel.useFileCachingLabel");
        fileCachingCheckBox = new JCheckBox(cbLabelStr);
        fileCachingCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (fileCachingCheckBox.isSelected()) {
                    bufferSizeTextField.setEnabled(true);
                    bufferSizeLabel.setEnabled(true);
                } else {
                    bufferSizeTextField.setEnabled(false);
                    bufferSizeLabel.setEnabled(false);
                }
            }
        });
        String toolTipText = 
            s_stringMgr.getString("PreferencesPanel.useFileCachingToolTip");
        fileCachingCheckBox.setToolTipText(toolTipText);
        panel.add(fileCachingCheckBox, c);
    }
    
    private void addBufferSizeLabel(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.insets = new Insets(5,25,0,0);
        String bsLabel = 
            s_stringMgr.getString("PreferencesPanel.copyBufferSizeLabel");
        bufferSizeLabel = new JLabel(bsLabel);
        bufferSizeLabel.setHorizontalAlignment(JLabel.LEFT);
        String labelToolTipText = 
            s_stringMgr.getString("PreferencesPanel.copyBufferSizeToolTip");
        bufferSizeLabel.setToolTipText(labelToolTipText);
        panel.add(bufferSizeLabel, c);        
    }
    
    private void addBufferSizeTextField(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;   
        c.ipadx = 40;    // Increases component width by 40 pixels
        c.insets = new Insets(5,5,0,0);
        c.anchor = GridBagConstraints.WEST;
        bufferSizeTextField = new JTextField(10);
        bufferSizeTextField.setHorizontalAlignment(JTextField.RIGHT);
        String toolTip = 
            s_stringMgr.getString("PreferencesPanel.bufferSizeTextFieldToolTip");
        bufferSizeTextField.setToolTipText(toolTip);
        panel.add(bufferSizeTextField, c);        
    }
    
    private void addAutoCommitCheckcBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2;   // Span across two columns
        c.insets = new Insets(10,0,0,0);
        c.anchor = GridBagConstraints.WEST;
        String cbLabelStr = 
            s_stringMgr.getString("PreferencesPanel.autoCommitLabel");
        autoCommitCheckBox = new JCheckBox(cbLabelStr);
        autoCommitCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (autoCommitCheckBox.isSelected()) {
                    commitRecordCountLabel.setEnabled(false);
                    commitRecordCountTextField.setEnabled(false);
                    commitAfterCreateTableCheckBox.setEnabled(false);
                } else {
                    commitRecordCountLabel.setEnabled(true);
                    commitRecordCountTextField.setEnabled(true);
                    commitAfterCreateTableCheckBox.setEnabled(true);
                }
            }
        });
        panel.add(autoCommitCheckBox, c);        
    }
    
    private void addCommitAfterCreateTableCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2;  // Span across two columns
        //c.ipadx = 40;
        c.insets = new Insets(10,25,0,0);
        c.anchor = GridBagConstraints.WEST;
        String cbLabelStr =         
            s_stringMgr.getString("PreferencesPanel.commitAfterCreateTable");
        String toolTipText = 
            s_stringMgr.getString("PreferencesPanel.commitAfterCreateTableToolTip");
        commitAfterCreateTableCheckBox = new JCheckBox(cbLabelStr);
        commitAfterCreateTableCheckBox.setToolTipText(toolTipText);
        panel.add(commitAfterCreateTableCheckBox, c);        
    }    
    
    private void addRecordCountLabel(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.insets = new Insets(5,25,0,0);
        String commitLabel = 
            s_stringMgr.getString("PreferencesPanel.commitRecordCountLabel");
        commitRecordCountLabel = new JLabel(commitLabel);
        commitRecordCountLabel.setHorizontalAlignment(JLabel.RIGHT);
        String commitlabelToolTipText = 
            s_stringMgr.getString("PreferencesPanel.commitRecordCountToolTip");
        commitRecordCountLabel.setToolTipText(commitlabelToolTipText);
        panel.add(commitRecordCountLabel, c);                
    }
    
    private void addCommitRecordCountTextField(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.ipadx = 40;      // Increases component width by 20 pixels
        c.insets = new Insets(5,5,0,0);
        c.anchor = GridBagConstraints.WEST;
        commitRecordCountTextField = new JTextField(10);
        commitRecordCountTextField.setHorizontalAlignment(JTextField.RIGHT);
        String commitlabelToolTipText = 
            s_stringMgr.getString("PreferencesPanel.commitRecordCountToolTip");
        commitRecordCountTextField.setToolTipText(commitlabelToolTipText);
        panel.add(commitRecordCountTextField, c);                
    }
    
    private void addDelayCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.gridwidth = 2;  // Span across two columns
        c.insets = new Insets(10,0,0,0);
        c.anchor = GridBagConstraints.WEST;        
        String label = 
            s_stringMgr.getString("PreferencesPanel.delayLabel");
        delayBetweenObjects = new JCheckBox(label);
        //selectFetchSizeLabel.setHorizontalAlignment(JLabel.LEFT);
        String delayToolTip = 
            s_stringMgr.getString("PreferencesPanel.delayToolTip");
        delayBetweenObjects.setToolTipText(delayToolTip);
        delayBetweenObjects.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                delayRecordsLabel.setEnabled(delayBetweenObjects.isSelected());
                delayRecordsTextField.setEnabled(delayBetweenObjects.isSelected());
                delayTablesLabel.setEnabled(delayBetweenObjects.isSelected());
                delayTablesTextField.setEnabled(delayBetweenObjects.isSelected());
            }
        });
        panel.add(delayBetweenObjects, c);        
    }    
    
    private void addDelayTablesLabel(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.insets = new Insets(5,25,0,0);
        String label = 
            s_stringMgr.getString("PreferencesPanel.delayTablesLabel");
        delayTablesLabel = new JLabel(label);
        delayTablesLabel.setHorizontalAlignment(JLabel.LEFT);
        panel.add(delayTablesLabel, c);        
    }    
    
    private void addDelayTablesTextField(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;   
        c.ipadx = 40;    // Increases component width by 40 pixels
        c.insets = new Insets(5,5,0,0);
        c.anchor = GridBagConstraints.WEST;
        delayTablesTextField = new JTextField(10);
        
        delayTablesTextField.setHorizontalAlignment(JTextField.RIGHT);
        panel.add(delayTablesTextField, c);        
    }    
    
    private void addDelayRecordsLabel(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.insets = new Insets(5,25,0,0);
        String label = 
            s_stringMgr.getString("PreferencesPanel.delayRecordsLabel");
        delayRecordsLabel = new JLabel(label);
        delayRecordsLabel.setHorizontalAlignment(JLabel.LEFT);
        panel.add(delayRecordsLabel, c);        
    }    
    
    private void addDelayRecordsTextField(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;   
        c.ipadx = 40;    // Increases component width by 40 pixels
        c.insets = new Insets(5,5,0,0);
        c.anchor = GridBagConstraints.WEST;
        delayRecordsTextField = new JTextField(10);
        
        delayRecordsTextField.setHorizontalAlignment(JTextField.RIGHT);
        panel.add(delayRecordsTextField, c);        
    }    
    
    private JPanel createBottomPanel() {
        JPanel result = new JPanel(new GridBagLayout());

        //i18n[PreferencesPanel.colTypeMappingBorderLabel=Column Type Mapping]
        String colTypeMappingBorderLabel = 
            s_stringMgr.getString("PreferencesPanel.colTypeMappingBorderLabel");
        
        result.setBorder(getTitledBorder(colTypeMappingBorderLabel));        
        
        addPromptForHibernateCheckBox(result, 0, 0);
        
        addCheckKeywordsCheckBox(result, 0, 1);
        
        addTestColumnNamesCheckBox(result, 0, 2);
        
        return result;
    }
    
    private void addPromptForHibernateCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.anchor = GridBagConstraints.WEST;
        String cbLabelStr = 
            s_stringMgr.getString("PreferencesPanel.promptForHibernate");
        String cbToolTipText =
            s_stringMgr.getString("PreferencesPanel.promptForHibernateToolTip");
        promptForHibernateCheckBox = new JCheckBox(cbLabelStr);
        promptForHibernateCheckBox.setToolTipText(cbToolTipText);
        panel.add(promptForHibernateCheckBox, c);
    }
    
    private void addCheckKeywordsCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.anchor = GridBagConstraints.WEST;
        String cbLabelStr =
            s_stringMgr.getString("PreferencesPanel.checkKeywords");
        String cbToolTipText =
            s_stringMgr.getString("PreferencesPanel.checkKeywordsToolTip");
        
        checkKeywordsCheckBox = new JCheckBox(cbLabelStr);
        checkKeywordsCheckBox.setToolTipText(cbToolTipText);
        panel.add(checkKeywordsCheckBox, c);        
    }
    
    private void addTestColumnNamesCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.anchor = GridBagConstraints.WEST;
        String cbLabelStr =
            s_stringMgr.getString("PreferencesPanel.testColumnNames");
        String cbToolTipText =
            s_stringMgr.getString("PreferencesPanel.testColumnNamesToolTip");
        testColumnNamesCheckBox = new JCheckBox(cbLabelStr);
        testColumnNamesCheckBox.setToolTipText(cbToolTipText);
        panel.add(testColumnNamesCheckBox, c);        
    }
    
    private Border getTitledBorder(String title) {
        CompoundBorder border = 
            new CompoundBorder(new EmptyBorder(10,10,10,10),
                               new TitledBorder(title));        
        return border;
    }
    
    private void loadData() {
        fileCachingCheckBox.setSelected(_prefs.isUseFileCaching());
        bufferSizeTextField.setText(""+_prefs.getFileCacheBufferSize());
        commitRecordCountTextField.setText(""+_prefs.getCommitCount());
        autoCommitCheckBox.setSelected(_prefs.isAutoCommitEnabled());
        if (_prefs.isUseFileCaching()) {
            bufferSizeLabel.setEnabled(true);
            bufferSizeTextField.setEnabled(true);
        } else {
            bufferSizeLabel.setEnabled(false);
            bufferSizeTextField.setEnabled(false);
        }
        if (_prefs.isAutoCommitEnabled()) {
            commitRecordCountLabel.setEnabled(false);
            commitRecordCountTextField.setEnabled(false);
            commitAfterCreateTableCheckBox.setEnabled(false);
        } else {
            commitRecordCountLabel.setEnabled(true);
            commitRecordCountTextField.setEnabled(true);     
            commitAfterCreateTableCheckBox.setEnabled(true);
        }
        truncateCheckBox.setSelected(_prefs.isUseTruncate());
        copyTableRecords.setSelected(_prefs.isCopyData());
        selectFetchSizeLabel.setEnabled(_prefs.isCopyData());
        selectFetchSizeTextField.setEnabled(_prefs.isCopyData());
        selectFetchSizeTextField.setText(""+_prefs.getSelectFetchSize());
        copyIndexDefs.setSelected(_prefs.isCopyIndexDefs());
        copyForeignKeys.setSelected(_prefs.isCopyForeignKeys());
        copyPrimaryKeys.setSelected(_prefs.isCopyPrimaryKeys());
        writeScriptCheckBox.setSelected(_prefs.isWriteScript());
        appendRecordsToExistingCheckBox.setSelected(_prefs.isAppendRecordsToExisting());
        pruneDuplicateIndexDefs.setSelected(_prefs.isPruneDuplicateIndexDefs());
        commitAfterCreateTableCheckBox.setSelected(_prefs.isCommitAfterTableDefs());
        promptForHibernateCheckBox.setSelected(_prefs.isPromptForDialect());
        checkKeywordsCheckBox.setSelected(_prefs.isCheckKeywords());
        testColumnNamesCheckBox.setSelected(_prefs.isTestColumnNames());
        delayTablesTextField.setText(""+_prefs.getTableDelayMillis());
        delayRecordsTextField.setText(""+_prefs.getRecordDelayMillis());
        delayTablesTextField.setEnabled(_prefs.isDelayBetweenObjects());
        delayRecordsTextField.setEnabled(_prefs.isDelayBetweenObjects());
        delayBetweenObjects.setSelected(_prefs.isDelayBetweenObjects());
        delayTablesLabel.setEnabled(_prefs.isDelayBetweenObjects());
        delayRecordsLabel.setEnabled(_prefs.isDelayBetweenObjects());
    }
    
    private void save() {
        _prefs.setUseFileCaching(fileCachingCheckBox.isSelected());
        _prefs.setUseTruncate(truncateCheckBox.isSelected());
        _prefs.setCopyData(copyTableRecords.isSelected());
        _prefs.setCopyIndexDefs(copyIndexDefs.isSelected());
        _prefs.setAutoCommitEnabled(autoCommitCheckBox.isSelected());
        _prefs.setCopyForeignKeys(copyForeignKeys.isSelected());
        _prefs.setCopyPrimaryKeys(copyPrimaryKeys.isSelected());
        _prefs.setWriteScript(writeScriptCheckBox.isSelected());
        _prefs.setAppendRecordsToExisting(appendRecordsToExistingCheckBox.isSelected());
        _prefs.setPruneDuplicateIndexDefs(pruneDuplicateIndexDefs.isSelected());
        _prefs.setCommitAfterTableDefs(commitAfterCreateTableCheckBox.isSelected());
        _prefs.setPromptForDialect(promptForHibernateCheckBox.isSelected());
        _prefs.setCheckKeywords(checkKeywordsCheckBox.isSelected());
        _prefs.setTestColumnNames(testColumnNamesCheckBox.isSelected());
        _prefs.setDelayBetweenObjects(delayBetweenObjects.isSelected());
        try {
            String value = bufferSizeTextField.getText();
            _prefs.setFileCacheBufferSize(Integer.parseInt(value));
        } catch (Exception e) {
            // Do nothing.
        }
        try {
            String value = commitRecordCountTextField.getText();
            _prefs.setCommitCount(Integer.parseInt(value));
        } catch (Exception e) {
            // Do nothing.
        }        
        try {
            String value = selectFetchSizeTextField.getText();
            _prefs.setSelectFetchSize(Integer.parseInt(value));
        } catch (Exception e) {
            // Do nothing.
        }
        try {
            String value = delayRecordsTextField.getText();
            _prefs.setRecordDelayMillis(Long.parseLong(value));
        } catch (Exception e) {
            // Do nothing.
        }
        try {
            String value = delayTablesTextField.getText();
            _prefs.setTableDelayMillis(Long.parseLong(value));
        } catch (Exception e) {
            // Do nothing.
        }
        PreferencesManager.savePrefs();
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#applyChanges()
     */
    public void applyChanges() {
        save();
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getPanelComponent()
     */
    public Component getPanelComponent() {
        return this;
    }
}
