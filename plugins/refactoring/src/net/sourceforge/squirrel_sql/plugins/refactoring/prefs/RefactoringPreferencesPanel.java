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
package net.sourceforge.squirrel_sql.plugins.refactoring.prefs;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class RefactoringPreferencesPanel extends JPanel  {                              

	private static final long serialVersionUID = -4293776729533111287L;

	private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(RefactoringPreferencesPanel.class);    
	
    /** Logger for this class. */
    @SuppressWarnings("unused")
	private final static ILogger log = 
        LoggerController.createLogger(RefactoringPreferencesPanel.class);    

    RefactoringPreferenceBean _prefs = null;
    
    JCheckBox qualifyTableNamesCheckBox = null;
    JCheckBox quoteIdentifersCheckBox = null;
        
    public RefactoringPreferencesPanel(RefactoringPreferenceBean prefs) {
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
        //i18n[RefactoringPreferencesPanel.borderTitle=Refactoring Preferences]
        String borderTitle = s_stringMgr.getString("RefactoringPreferencesPanel.borderTitle");
        result.setBorder(getTitledBorder(borderTitle));
        addQualifyTableNamesCheckBox(result, 0, 0); 
        addQuoteIdentifiersCheckBox(result, 0, 1);
        return result;
    }
    
    private void addQualifyTableNamesCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.anchor = GridBagConstraints.WEST;
        // i18n[RefactoringPreferencesPanel.qualifyCheckboxLabel=Qualify table names in scripts with schema]
        String cbLabelStr = 
        	s_stringMgr.getString("RefactoringPreferencesPanel.qualifyCheckboxLabel");
        // i18n[RefactoringPreferencesPanel.prefsToolTip=Table names appear in scripts as SCHEMA.TABLE]
        String cbToolTipText = 
        	s_stringMgr.getString("RefactoringPreferencesPanel.qualifyCheckboxToolTip");
        qualifyTableNamesCheckBox = new JCheckBox(cbLabelStr);
        qualifyTableNamesCheckBox.setToolTipText(cbToolTipText);
        panel.add(qualifyTableNamesCheckBox, c);
    }

    private void addQuoteIdentifiersCheckBox(JPanel panel, int col, int row) {
       GridBagConstraints c = new GridBagConstraints();
       c.gridx = col;
       c.gridy = row;  
       c.anchor = GridBagConstraints.WEST;
       // i18n[RefactoringPreferencesPanel.quoteCheckboxLabel=Quote identifiers in scripts]
       String cbLabelStr = 
       	s_stringMgr.getString("RefactoringPreferencesPanel.quoteCheckboxLabel");
       // i18n[RefactoringPreferencesPanel.quoteToolTip=Identifiers appear in quotes]
       String cbToolTipText = 
       	s_stringMgr.getString("RefactoringPreferencesPanel.qualifyCheckboxToolTip");
       quoteIdentifersCheckBox = new JCheckBox(cbLabelStr);
       quoteIdentifersCheckBox.setToolTipText(cbToolTipText);
       panel.add(quoteIdentifersCheckBox, c);
   }    
    
    private Border getTitledBorder(String title) {
        CompoundBorder border = 
            new CompoundBorder(new EmptyBorder(10,10,10,10),
                               new TitledBorder(title));        
        return border;
    }
    
    private void loadData() {
        qualifyTableNamesCheckBox.setSelected(_prefs.isQualifyTableNames());  
        quoteIdentifersCheckBox.setSelected(_prefs.isQuoteIdentifiers());
    }
    
    private void save() {
        _prefs.setQualifyTableNames(qualifyTableNamesCheckBox.isSelected());
        _prefs.setQuoteIdentifiers(quoteIdentifersCheckBox.isSelected());
        RefactoringPreferencesManager.savePrefs();
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
