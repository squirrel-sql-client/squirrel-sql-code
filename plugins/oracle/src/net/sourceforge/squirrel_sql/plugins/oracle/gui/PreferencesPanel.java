/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.oracle.gui;

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
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.OraclePreferenceBean;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.PreferencesManager;

public class PreferencesPanel extends JPanel  {                              

    OraclePreferenceBean _prefs = null;
    
    JCheckBox useCustomQTCheckBox = null;
    
    JLabel useCustomQTLabel = null;    
    
    JCheckBox removeMultiLineCommentCheckBox = null;
    
    JTextField lineCommentTextField = null;
    
    JLabel lineCommentLabel = null;
    
    JLabel procedureSeparatorLabel = null;
    
    JTextField procedureSeparatorTextField = null;
    
    JLabel statementSeparatorLabel = null;
    
    JTextField statementSeparatorTextField = null;
    
    /** Logger for this class. */
    private final static ILogger log = 
        LoggerController.createLogger(PreferencesPanel.class);    
    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(PreferencesPanel.class);
    
    static interface i18n {
        
        //i18n[PreferencesPanel.useCustomQTLabel=Use Custom Query Tokenizer]
        String USE_CUSTOM_QT_LABEL = 
            s_stringMgr.getString("PreferencesPanel.useCustomQTLabel");

        //i18n[PreferencesPanel.useCustomQTToolTip=Gives enhanced capabilities
        //over the default query tokenizer for handling Oracle scripts]
        String USE_CUSTOM_QT_TOOLTIP = 
            s_stringMgr.getString("PreferencesPanel.useCustomQTToolTip");
        
    }
    
    
    public PreferencesPanel(OraclePreferenceBean prefs) {
        
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
    
    }
    
    private JPanel createTopPanel() {
        JPanel result = new JPanel(new GridBagLayout());
        //i18n[PreferencesPanel.borderLabel=Oracle Script Settings]
        String borderLabel = 
            s_stringMgr.getString("PreferencesPanel.borderLabel");
        result.setBorder(getTitledBorder(borderLabel));
        
        addUseCustomQTCheckBox(result, 0, 0);
        
        addLineCommentLabel(result, 0, 1);
        addLineCommentTextField(result, 1, 1);
        
        addStatementSeparatorLabel(result, 0, 2);
        addStatementSeparatorTextField(result, 1, 2);
        
        addProcedureSeparatorLabel(result, 0, 3);
        addProcedureSeparatorTextField(result, 1, 3);
        
        addRemoveMultiLineCommentCheckBox(result, 0, 4);
        
        return result;
    }    
        
    private void addUseCustomQTCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.insets = new Insets(5,5,0,0);
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 2;  // Span across two columns
        useCustomQTCheckBox = new JCheckBox(i18n.USE_CUSTOM_QT_LABEL);        
        useCustomQTCheckBox.setToolTipText(i18n.USE_CUSTOM_QT_TOOLTIP);
        useCustomQTCheckBox.addActionListener(new UseQTHandler());
        panel.add(useCustomQTCheckBox, c);        
    }    
    
    private void addRemoveMultiLineCommentCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2;  // Span across two columns
        String cbLabel = 
            s_stringMgr.getString("PreferencesPanel.removeMultiLineCommentLabel");
        removeMultiLineCommentCheckBox = new JCheckBox(cbLabel);        
        String cbToolTipText = 
            s_stringMgr.getString("PreferencesPanel.removeMultiLineCommentLabelTipText");
        removeMultiLineCommentCheckBox.setToolTipText(cbToolTipText);
        panel.add(removeMultiLineCommentCheckBox, c);        
    }
        
    private void addStatementSeparatorLabel(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.insets = new Insets(5,5,0,0);
        String bsLabel = 
            s_stringMgr.getString("PreferencesPanel.statementSeparatorLabel");
        statementSeparatorLabel = new JLabel(bsLabel);
        statementSeparatorLabel.setHorizontalAlignment(JLabel.LEFT);
        String labelToolTipText = 
            s_stringMgr.getString("PreferencesPanel.statementSeparatorToolTip");
        statementSeparatorLabel.setToolTipText(labelToolTipText);
        panel.add(statementSeparatorLabel, c);        
    }    
    
    private void addStatementSeparatorTextField(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;   
        c.ipadx = 40;    // Increases component width by 40 pixels
        c.insets = new Insets(5,5,0,0);
        c.anchor = GridBagConstraints.WEST;
        statementSeparatorTextField = new JTextField(10);
        
        statementSeparatorTextField.setHorizontalAlignment(JTextField.RIGHT);
        String toolTip = 
            s_stringMgr.getString("PreferencesPanel.statementSeparatorToolTip");
        statementSeparatorTextField.setToolTipText(toolTip);
        panel.add(statementSeparatorTextField, c);        
    }    
        
    private void addLineCommentLabel(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.insets = new Insets(5,5,0,0);
        String bsLabel = 
            s_stringMgr.getString("PreferencesPanel.lineCommentLabel");
        lineCommentLabel = new JLabel(bsLabel);
        lineCommentLabel.setHorizontalAlignment(JLabel.LEFT);
        String labelToolTipText = 
            s_stringMgr.getString("PreferencesPanel.lineCommentToolTip");
        lineCommentLabel.setToolTipText(labelToolTipText);
        panel.add(lineCommentLabel, c);        
    }
    
    private void addLineCommentTextField(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;   
        c.ipadx = 40;    // Increases component width by 40 pixels
        c.insets = new Insets(5,5,0,0);
        c.anchor = GridBagConstraints.WEST;
        lineCommentTextField = new JTextField(10);
        lineCommentTextField.setHorizontalAlignment(JTextField.RIGHT);
        String toolTip = 
            s_stringMgr.getString("PreferencesPanel.lineCommentToolTip");
        lineCommentTextField.setToolTipText(toolTip);
        panel.add(lineCommentTextField, c);        
    }
    
    
    private void addProcedureSeparatorLabel(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.insets = new Insets(5,5,0,0);
        String commitLabel = 
            s_stringMgr.getString("PreferencesPanel.procedureSeparatorLabel");
        procedureSeparatorLabel = new JLabel(commitLabel);
        procedureSeparatorLabel.setHorizontalAlignment(JLabel.RIGHT);
        String commitlabelToolTipText = 
            s_stringMgr.getString("PreferencesPanel.procedureSeparatorToolTip");
        procedureSeparatorLabel.setToolTipText(commitlabelToolTipText);
        panel.add(procedureSeparatorLabel, c);                
    }
    
    private void addProcedureSeparatorTextField(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.ipadx = 40;      // Increases component width by 20 pixels
        c.insets = new Insets(5,5,0,0);
        c.anchor = GridBagConstraints.WEST;
        procedureSeparatorTextField = new JTextField(10);
        procedureSeparatorTextField.setHorizontalAlignment(JTextField.RIGHT);
        String commitlabelToolTipText = 
            s_stringMgr.getString("PreferencesPanel.procedureSeparatorToolTip");
        procedureSeparatorTextField.setToolTipText(commitlabelToolTipText);
        panel.add(procedureSeparatorTextField, c);                
    }

    private Border getTitledBorder(String title) {
        CompoundBorder border = 
            new CompoundBorder(new EmptyBorder(10,10,10,10),
                               new TitledBorder(title));        
        return border;
    }
    
    private void loadData() {        
        removeMultiLineCommentCheckBox.setSelected(_prefs.isRemoveMultiLineComments());
        lineCommentTextField.setText(_prefs.getLineComment());
        statementSeparatorTextField.setText(_prefs.getStatementSeparator());
        procedureSeparatorTextField.setText(_prefs.getProcedureSeparator());
        useCustomQTCheckBox.setSelected(_prefs.isInstallCustomQueryTokenizer());
        updatePreferenceState();
    }
    
    private void save() {
        _prefs.setRemoveMultiLineComments(removeMultiLineCommentCheckBox.isSelected());
        _prefs.setLineComment(lineCommentTextField.getText());
        _prefs.setStatementSeparator(statementSeparatorTextField.getText());
        _prefs.setProcedureSeparator(procedureSeparatorTextField.getText());
        _prefs.setInstallCustomQueryTokenizer(useCustomQTCheckBox.isSelected());
        PreferencesManager.savePrefs();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#applyChanges()
     */
    public void applyChanges() {
        save();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getPanelComponent()
     */
    public Component getPanelComponent() {
        return this;
    }
    
    private void updatePreferenceState() {
        if (useCustomQTCheckBox.isSelected()) {
            removeMultiLineCommentCheckBox.setEnabled(true);
            lineCommentTextField.setEnabled(true);
            lineCommentLabel.setEnabled(true);
            statementSeparatorTextField.setEnabled(true);
            statementSeparatorLabel.setEnabled(true);
            procedureSeparatorLabel.setEnabled(true);
            procedureSeparatorTextField.setEnabled(true);            
        } else {
            removeMultiLineCommentCheckBox.setEnabled(false);
            lineCommentTextField.setEnabled(false);
            lineCommentLabel.setEnabled(false);
            statementSeparatorTextField.setEnabled(false);
            statementSeparatorLabel.setEnabled(false);                
            procedureSeparatorLabel.setEnabled(false);
            procedureSeparatorTextField.setEnabled(false);
        }        
    }    
    
    private class UseQTHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            updatePreferenceState();
        }
    }
    
}
