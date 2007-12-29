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
import java.awt.*;

/**
 * Defines a drop View dialog for many Drop Operations.
 */
public class DefaultDropDialog extends AbstractRefactoringDialog {


    private static final long serialVersionUID = 1L;
    private JCheckBox _cascadeCB;
    private JLabel _cascadeConstraintsLabel;


    /**
     * Internationalized strings for this class
     */
    private static final StringManager s_stringMgr =
            StringManagerFactory.getStringManager(DefaultDropDialog.class);

    private String _dropItem = "";

    private final IDatabaseObjectInfo[] _objectInfo;
    /**
     * If the dialog is used to drop views.
     */
    public static final int DIALOG_TYPE_VIEW = 1;
    /**
     * If the dialog is used to drop tables.
     */
    public static final int DIALOG_TYPE_TABLE = 2;
    /**
     * If the dialog is used to drop indexes.
     */
    public static final int DIALOG_TYPE_INDEX = 3;

    /**
     * If the dialog is used to drop sequences.
     */
    public static final int DIALOG_TYPE_SEQUENCE = 4;
    /**
     * If the dialog is used to drop foreign keys.
     */
    public static final int DIALOG_TYPE_FOREIGN_KEY = 5;

    /**
     * If the dialog is used to drop foreign keys.
     */
    public static final int DIALOG_TYPE_UNIQUE_CONSTRAINT_KEY = 6;

    static interface i18n {
        String CATALOG_LABEL =
                s_stringMgr.getString("DefaultDropDialog.catalogLabel");

        String SCHEMA_LABEL =
                s_stringMgr.getString("DefaultDropDialog.schemaLabel");

        String TABLE_LABEL =
                s_stringMgr.getString("DefaultDropDialog.tableLabel");

        String VIEW_LABEL =
                s_stringMgr.getString("DefaultDropDialog.viewLabel");

        String INDEX_LABEL =
                s_stringMgr.getString("DefaultDropDialog.indexLabel");

        String SEQUENCE_LABEL =
                s_stringMgr.getString("DefaultDropDialog.sequenceLabel");

        String FOREIGN_KEY_LABEL =
                s_stringMgr.getString("DefaultDropDialog.foreignKeyLabel");

        String UNIQUE_CONSTRAINT_LABEL =
                s_stringMgr.getString("DefaultDropDialog.uniqueConstraintLabel");


        String CASCADE_LABEL = s_stringMgr.getString("DefaultDropDialog.cascadeLabel");
    }

    /**
     * Constructor of DefaultDropDialog.
     *
     * @param objectInfo InfoObjects of the selected items in the tree. (Views)
     * @param dialogType dialog constant type.
     */
    public DefaultDropDialog(IDatabaseObjectInfo[] objectInfo, int dialogType) {
        this._objectInfo = objectInfo;

        setTypeByID(dialogType);
        init();
    }

    /**
     * Finds and sets the correct title for the specific type dialog.
     *
     * @param dialogType dialog type.
     */
    private void setTypeByID(int dialogType) {
        String object = "";
        switch (dialogType) {
            case DIALOG_TYPE_VIEW:
                object = i18n.VIEW_LABEL;
                break;
            case DIALOG_TYPE_TABLE:
                object = i18n.TABLE_LABEL;
                break;
            case DIALOG_TYPE_INDEX:
                object = i18n.INDEX_LABEL;
                break;
            case DIALOG_TYPE_SEQUENCE:

                object = i18n.SEQUENCE_LABEL;
                break;
            case DIALOG_TYPE_FOREIGN_KEY:
                object = i18n.FOREIGN_KEY_LABEL;
                break;
            case DIALOG_TYPE_UNIQUE_CONSTRAINT_KEY:
                object = i18n.UNIQUE_CONSTRAINT_LABEL;
                break;
            default:
        }
        _dropItem = object;
        setTitle(s_stringMgr.getString("DefaultDropDialog.title", object));
    }

    /**
     * Creates the UI for this dialog.
     */
    protected void init() {

        // Catalog
        JLabel catalogLabel = getBorderedLabel(DefaultDropDialog.i18n.CATALOG_LABEL + " ", emptyBorder);
        pane.add(catalogLabel, getLabelConstraints(c));

        JTextField catalogTF = new JTextField();
        catalogTF.setPreferredSize(mediumField);
        catalogTF.setEditable(false);
        catalogTF.setText(_objectInfo[0].getCatalogName());
        pane.add(catalogTF, getFieldConstraints(c));

        // Schema
        JLabel schemaLabel = getBorderedLabel(DefaultDropDialog.i18n.SCHEMA_LABEL + " ", emptyBorder);
        pane.add(schemaLabel, getLabelConstraints(c));

        JTextField schemaTF = new JTextField();
        schemaTF.setPreferredSize(mediumField);
        schemaTF.setEditable(false);
        schemaTF.setText(_objectInfo[0].getSchemaName());
        pane.add(schemaTF, getFieldConstraints(c));

        // view list
        JLabel viewListLabel = getBorderedLabel(_dropItem, emptyBorder);
        viewListLabel.setVerticalAlignment(JLabel.NORTH);
        pane.add(viewListLabel, getLabelConstraints(c));

        JList viewList = new JList(getSimpleNames(_objectInfo));
        viewList.setEnabled(false);

        JScrollPane sp = new JScrollPane(viewList);
        c = getFieldConstraints(c);
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        pane.add(sp, c);

        // Cascade Constraints Checkbox
        _cascadeConstraintsLabel = new JLabel(DefaultDropDialog.i18n.CASCADE_LABEL + " ");
        _cascadeConstraintsLabel.setBorder(emptyBorder);
        pane.add(_cascadeConstraintsLabel, getLabelConstraints(c));

        _cascadeCB = new JCheckBox();
        _cascadeCB.setPreferredSize(mediumField);
        pane.add(_cascadeCB, getFieldConstraints(c));
        super.executeButton.setRequestFocusEnabled(true);
    }

    /**
     * Gets the simple view names of the selected DatabaseObjectInfos.
     *
     * @param dbInfo InfoObjects of the selected items in the tree.
     * @return the simple names of the database item.
     */
    private String[] getSimpleNames(IDatabaseObjectInfo[] dbInfo) {
        String[] result = new String[dbInfo.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = dbInfo[i].getSimpleName();
        }
        return result;
    }

    /**
     * Checks if the user selected the CASCADE option in the dialog.
     *
     * @return true if selected, otherwise false.
     */
    public boolean isCascadeSelected() {
        return _cascadeCB.isSelected();
    }

    /**
     * Makes the cascade option (checkbox) visible or invisible
     *
     * @param visible true to make the component visible; false to make it invisible.
     */
    public void setCascadeVisible(boolean visible) {
        _cascadeConstraintsLabel.setVisible(visible);
        _cascadeCB.setVisible(visible);
    }
}
