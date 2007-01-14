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

package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

public class DropTableDialog extends AbstractRefactoringDialog {

    private JLabel catalogLabel = null;
    private JLabel schemaLabel = null;
    private JTextField catalogTF = null;
    private JTextField schemaTF = null;
    private JList tableList = null;
    private JLabel tableListLabel = null;
    private JLabel cascadeConstraintsLabel = null;
    
    private JCheckBox cascadeCB = null; 
    
    private ITableInfo[] tableInfos = null;
    
    public DropTableDialog(ITableInfo[] tables) {
        super(false);
        // i18n
        setTitle("Drop Table(s)");
        tableInfos = tables;
        init();
    }
    
    public ITableInfo[] getTableInfos() {
        return tableInfos;
    }
        
    public boolean getCascadeConstraints() {
        return cascadeCB.isSelected();
    }
    
    protected void init() {
        // Catalog 
        catalogLabel = getBorderedLabel("Catalog: ", emptyBorder);
        pane.add(catalogLabel, getLabelConstraints(c));
        
        catalogTF = new JTextField();
        catalogTF.setPreferredSize(mediumField);
        catalogTF.setEditable(false);
        catalogTF.setText(tableInfos[0].getCatalogName());
        pane.add(catalogTF, getFieldConstraints(c));
        
        // Schema
        schemaLabel = getBorderedLabel("Schema: ", emptyBorder);
        pane.add(schemaLabel, getLabelConstraints(c));
        
        schemaTF = new JTextField();
        schemaTF.setPreferredSize(mediumField);
        schemaTF.setEditable(false);
        schemaTF.setText(tableInfos[0].getSchemaName());
        pane.add(schemaTF, getFieldConstraints(c));
        
        // table list        
        tableListLabel = getBorderedLabel("Tables: ", emptyBorder);
        tableListLabel.setVerticalAlignment(JLabel.NORTH);
        pane.add(tableListLabel, getLabelConstraints(c));
        
        tableList = new JList(getSimpleNames(tableInfos));
        tableList.setEnabled(false);

        JScrollPane sp = new JScrollPane(tableList);
        c = getFieldConstraints(c);
        c.weightx = 1;
        c.weighty = 1;        
        c.fill=GridBagConstraints.BOTH;
        pane.add(sp, c);
        
        // Cascade Constraints Checkbox
        cascadeConstraintsLabel = new JLabel("Cascade Constraints:");
        cascadeConstraintsLabel.setBorder(emptyBorder);
        pane.add(cascadeConstraintsLabel, getLabelConstraints(c));        
        
        cascadeCB = new JCheckBox();
        cascadeCB.setPreferredSize(mediumField);
        pane.add(cascadeCB, getFieldConstraints(c));
        super.executeButton.setRequestFocusEnabled(true);
    }
    
    private String[] getSimpleNames(ITableInfo[] tableInfos) {
        String[] result = new String[tableInfos.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = tableInfos[i].getSimpleName();
        }
        return result;
    }
    
    
}
