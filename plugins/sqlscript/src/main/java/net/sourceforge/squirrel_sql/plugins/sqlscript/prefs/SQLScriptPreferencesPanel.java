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
package net.sourceforge.squirrel_sql.plugins.sqlscript.prefs;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SQLScriptPreferencesPanel extends JPanel  {                              

    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SQLScriptPreferencesPanel.class);    
	
    /** Logger for this class. */
    private final static ILogger log = 
        LoggerController.createLogger(SQLScriptPreferencesPanel.class);    

    
    SQLScriptPreferenceBean _prefs = null;
    
    JCheckBox qualifyTableNamesCheckBox = null;

    JCheckBox useDoubleQuotesCheckBox = null;

    JCheckBox deleteReferentialActionCheckbox = null;
    
    JCheckBox updateReferentialActionCheckbox = null;
    
    JComboBox deleteActionComboBox = null;
    
    JComboBox updateActionComboBox = null;
    
    JLabel deleteActionLabel = null;
    
    JLabel updateActionLabel = null;
    
    public SQLScriptPreferencesPanel(SQLScriptPreferenceBean prefs) {
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
        c.weighty = .60;
        add(createBottomPanel(), c);
    }
    
    private JPanel createBottomPanel() {
        JPanel result = new JPanel(new GridBagLayout());
        //i18n[SQLScriptPreferencesPanel.borderTitle=SQL Script Preferences]
        String borderTitle = s_stringMgr.getString("SQLScriptPreferencesPanel.borderTitle");
        result.setBorder(getTitledBorder(borderTitle));
        addQualifyTableNamesCheckBox(result, 0, 0); 
        addUseDoubleQuotesCheckBox(result, 0, 1);
        addDeleteRefActionCheckBox(result, 0, 2);
        addDeleteActionComboBox(result, 0, 3);
        addUpdateRefActionCheckBox(result, 0, 4);
        addUpdateActionComboBox(result, 0, 5);
        return result;
    }
    
    private void addQualifyTableNamesCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.anchor = GridBagConstraints.WEST;
        // i18n[SQLScriptPreferencesPanel.qualifyCheckboxLabel=Qualify table names in scripts with schema]
        String cbLabelStr = 
        	s_stringMgr.getString("SQLScriptPreferencesPanel.qualifyCheckboxLabel");
        // i18n[SQLScriptPreferencesPanel.prefsToolTip=Table names appear in scripts as SCHEMA.TABLE]
        String cbToolTipText = 
        	s_stringMgr.getString("SQLScriptPreferencesPanel.qualifyCheckboxToolTip");
        qualifyTableNamesCheckBox = new JCheckBox(cbLabelStr);
        qualifyTableNamesCheckBox.setToolTipText(cbToolTipText);
        panel.add(qualifyTableNamesCheckBox, c);

        qualifyTableNamesCheckBox.addActionListener(new ActionListener()
        {
           @Override
           public void actionPerformed(ActionEvent e)
           {
               onQualifyTableNamesCheckBox();
           }
        });
    }

   private void onQualifyTableNamesCheckBox()
   {
      if(qualifyTableNamesCheckBox.isSelected())
      {
         useDoubleQuotesCheckBox.setEnabled(true);
      }
      else
      {
         useDoubleQuotesCheckBox.setSelected(false);
         useDoubleQuotesCheckBox.setEnabled(false);
      }
   }

   private void addDeleteRefActionCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.anchor = GridBagConstraints.WEST;
        //i18n[SQLScriptPreferencesPanel.deleteRefActionCheckboxLabel=Add delete 
        //referential action to the FK definition]
        String cbLabelStr = 
            s_stringMgr.getString("SQLScriptPreferencesPanel.deleteRefActionCheckboxLabel");
        // i18n[SQLScriptPreferencesPanel.deleteRefActionToolTip=Append ON DELETE ...]
        String cbToolTipText = 
            s_stringMgr.getString("SQLScriptPreferencesPanel.deleteRefActionToolTip");
        deleteReferentialActionCheckbox = new JCheckBox(cbLabelStr);
        deleteReferentialActionCheckbox.setToolTipText(cbToolTipText);

        deleteReferentialActionCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean enabled = deleteReferentialActionCheckbox.isSelected();
                deleteActionLabel.setEnabled(enabled);
                deleteActionComboBox.setEnabled(enabled);
            }
        });

        panel.add(deleteReferentialActionCheckbox, c);
    }

    private void addUpdateRefActionCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.anchor = GridBagConstraints.WEST;
        //i18n[SQLScriptPreferencesPanel.updateRefActionCheckboxLabel=Add update 
        //referential action to the FK definition]
        String cbLabelStr = 
            s_stringMgr.getString("SQLScriptPreferencesPanel.updateRefActionCheckboxLabel");
        // i18n[SQLScriptPreferencesPanel.updateRefActionToolTip=Append ON UPDATE ...]
        String cbToolTipText = 
            s_stringMgr.getString("SQLScriptPreferencesPanel.updateRefActionToolTip");
        updateReferentialActionCheckbox = new JCheckBox(cbLabelStr);
        updateReferentialActionCheckbox.setToolTipText(cbToolTipText);

        updateReferentialActionCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean enabled = updateReferentialActionCheckbox.isSelected();
                updateActionLabel.setEnabled(enabled);
                updateActionComboBox.setEnabled(enabled);
            }
        });

        panel.add(updateReferentialActionCheckbox, c);
    }
    
    private void addDeleteActionComboBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.insets = new Insets(5,25,0,0);
        c.anchor = GridBagConstraints.WEST;
        
        JPanel subpanel = new JPanel();
        
        //i18n[SQLScriptPreferencesPanel.deleteActionLabel=Action to take on delete:]
        String cbLabelStr = 
            s_stringMgr.getString("SQLScriptPreferencesPanel.deleteActionLabel");
        deleteActionLabel = new JLabel(cbLabelStr);
        deleteActionLabel.setHorizontalAlignment(JLabel.LEFT);
                
        deleteActionComboBox = new JComboBox();
        DefaultComboBoxModel model = 
            new DefaultComboBoxModel(new String[] { "NO ACTION", 
                                                    "CASCADE", 
                                                    "SET DEFAULT",
                                                    "SET NULL"});
        deleteActionComboBox.setModel(model);
        subpanel.add(deleteActionLabel);
        subpanel.add(deleteActionComboBox);
        panel.add(subpanel, c);
    }

    private void addUseDoubleQuotesCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.insets = new Insets(5,25,0,0);
        c.anchor = GridBagConstraints.WEST;

        useDoubleQuotesCheckBox = new JCheckBox(s_stringMgr.getString("SQLScriptPreferencesPanel.useDoubleQuotesForQualifying"));

        panel.add(useDoubleQuotesCheckBox, c);
    }

    private void addUpdateActionComboBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.insets = new Insets(5,25,0,0);
        c.anchor = GridBagConstraints.WEST;
        
        JPanel subpanel = new JPanel();
        
        //i18n[SQLScriptPreferencesPanel.updateActionLabel=Action to take on update:]
        String cbLabelStr = 
            s_stringMgr.getString("SQLScriptPreferencesPanel.updateActionLabel");
        updateActionLabel = new JLabel(cbLabelStr);
        updateActionLabel.setHorizontalAlignment(JLabel.LEFT);
                
        updateActionComboBox = new JComboBox();
        DefaultComboBoxModel model = 
            new DefaultComboBoxModel(new String[] { "NO ACTION", 
                                                    "CASCADE", 
                                                    "SET DEFAULT",
                                                    "SET NULL"});
        updateActionComboBox.setModel(model);
        subpanel.add(updateActionLabel);
        subpanel.add(updateActionComboBox);
        panel.add(subpanel, c);
    }

    
    
    private Border getTitledBorder(String title) {
        CompoundBorder border = 
            new CompoundBorder(new EmptyBorder(10,10,10,10),
                               new TitledBorder(title));        
        return border;
    }
    
    private void loadData() {
        qualifyTableNamesCheckBox.setSelected(_prefs.isQualifyTableNames());
        useDoubleQuotesCheckBox.setEnabled(_prefs.isQualifyTableNames());
        useDoubleQuotesCheckBox.setSelected(_prefs.isQualifyTableNames() && _prefs.isUseDoubleQuotes());
        deleteReferentialActionCheckbox.setSelected(_prefs.isDeleteRefAction());
        deleteActionComboBox.setEnabled(deleteReferentialActionCheckbox.isSelected());
        deleteActionComboBox.setSelectedIndex(_prefs.getDeleteAction());
        updateReferentialActionCheckbox.setSelected(_prefs.isUpdateRefAction());
        updateActionComboBox.setEnabled(updateReferentialActionCheckbox.isSelected());
        updateActionComboBox.setSelectedIndex(_prefs.getUpdateAction());
        
    }
    
    private void save() {
        _prefs.setQualifyTableNames(qualifyTableNamesCheckBox.isSelected());
        _prefs.setUseDoubleQuotes(useDoubleQuotesCheckBox.isSelected());
        _prefs.setDeleteRefAction(deleteReferentialActionCheckbox.isSelected());
        _prefs.setUpdateRefAction(updateReferentialActionCheckbox.isSelected());
        int action = deleteActionComboBox.getSelectedIndex();
        _prefs.setDeleteAction(action);
        action = updateActionComboBox.getSelectedIndex();
        _prefs.setUpdateAction(action);
        SQLScriptPreferencesManager.savePrefs();
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
}
