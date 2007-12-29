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

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;

/**
 * Defines a rename Table/View dialog.
 */
public class RenameTableDialog extends AbstractRefactoringDialog {


    private static final long serialVersionUID = 1L;

    /**
     * Type for changing a View.
     */
    public static final int DIALOG_TYPE_VIEW = 1;

    /**
     * Type for changing a Table.
     */
    public static final int DIALOG_TYPE_TABLE = 2;

    /**
     * Internationalized strings for this class
     */
    private static final StringManager s_stringMgr =
            StringManagerFactory.getStringManager(RenameTableDialog.class);

    private final IDatabaseObjectInfo[] dbInfo;

    // new Name of the table
    private JTextField tableTF;


    static interface i18n {
        String CATALOG_LABEL =
                s_stringMgr.getString("RenameTableDialog.catalogLabel");

        String SCHEMA_LABEL =
                s_stringMgr.getString("RenameTableDialog.schemaLabel");

        String TABLE_LABEL =
                s_stringMgr.getString("RenameTableDialog.viewLabel");

        String TITLE_VIEW = s_stringMgr.getString("RenameTableDialog.titleView");

        String TITLE_TABLE = s_stringMgr.getString("RenameTableDialog.titleTable");
    }

    /**
     * Constructor of RenameTableDialog.
     *
     * @param dbInfo     InfoObjects of the selected items in the tree.
     * @param dialogType type of the Dialog. (View, Table, ...).
     */
    public RenameTableDialog(IDatabaseObjectInfo[] dbInfo, int dialogType) {
        this.dbInfo = dbInfo;
        if (dialogType == DIALOG_TYPE_TABLE) setTitle(RenameTableDialog.i18n.TITLE_TABLE);
        else if (dialogType == DIALOG_TYPE_VIEW) setTitle(RenameTableDialog.i18n.TITLE_VIEW);
        init();
    }

    /**
     * Creates the UI for this dialog.
     */
    protected void init() {

        setSize(400, 150);
        // Catalog
        JLabel catalogLabel = getBorderedLabel(RenameTableDialog.i18n.CATALOG_LABEL + " ", emptyBorder);
        pane.add(catalogLabel, getLabelConstraints(c));

        JTextField catalogTF = new JTextField();
        catalogTF.setPreferredSize(mediumField);
        catalogTF.setEditable(false);
        catalogTF.setText(dbInfo[0].getCatalogName());
        pane.add(catalogTF, getFieldConstraints(c));

        // Schema
        JLabel schemaLabel = getBorderedLabel(RenameTableDialog.i18n.SCHEMA_LABEL + " ", emptyBorder);
        pane.add(schemaLabel, getLabelConstraints(c));

        JTextField schemaTF = new JTextField();
        schemaTF.setPreferredSize(mediumField);
        schemaTF.setEditable(false);
        schemaTF.setText(dbInfo[0].getSchemaName());
        pane.add(schemaTF, getFieldConstraints(c));

        // view list
        JLabel tableLabel = getBorderedLabel(RenameTableDialog.i18n.TABLE_LABEL + " ", emptyBorder);
        tableLabel.setVerticalAlignment(JLabel.NORTH);
        pane.add(tableLabel, getLabelConstraints(c));

        tableTF = new JTextField();
        tableTF.setToolTipText(s_stringMgr.getString("RenameTableDialog.tableField.ToolTipText", dbInfo[0].getSimpleName()));
        tableTF.setPreferredSize(mediumField);
        tableTF.setEditable(true);
        tableTF.setText(dbInfo[0].getSimpleName());
        pane.add(tableTF, getFieldConstraints(c));


        super.executeButton.setRequestFocusEnabled(true);
    }


    /**
     * Gets the new simple Name, which the user entered.
     *
     * @return new simple name.
     */
    public String getNewSimpleName() {
        return tableTF.getText();
    }

}
