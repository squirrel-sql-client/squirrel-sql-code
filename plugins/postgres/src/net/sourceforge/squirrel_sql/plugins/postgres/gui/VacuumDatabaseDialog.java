package net.sourceforge.squirrel_sql.plugins.postgres.gui;
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

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class VacuumDatabaseDialog extends AbstractPostgresDialog {
    /** Name of the database/catalog. */
    protected String _catalogName;

    /** Some GUI elements */
    protected JCheckBox _fullCheckBox;
    protected JCheckBox _analyzeCheckBox;

    /** Internationalized strings for this class */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(VacuumDatabaseDialog.class);

    static interface i18n {
        String TITLE = s_stringMgr.getString("VacuumDatabaseDialog.title");
        String CATALOG_LABEL = s_stringMgr.getString("VacuumDatabaseDialog.catalogLabel");
        String FULL_LABEL = s_stringMgr.getString("VacuumDatabaseDialog.fullLabel");
        String FULL_TOOLTIP = s_stringMgr.getString("VacuumDatabaseDialog.fullTooltip");
        String ANALYZE_LABEL = s_stringMgr.getString("VacuumDatabaseDialog.analyzeLabel");
        String ANALYZE_TOOLTIP = s_stringMgr.getString("VacuumDatabaseDialog.analyzeTooltip");
    }


    public VacuumDatabaseDialog(String catalogName) {
        _catalogName = catalogName;
        setTitle(VacuumDatabaseDialog.i18n.TITLE);
        init();
    }


    protected void init() {
        defaultInit();

        // Catalog
        JLabel catalogLabel = getBorderedLabel(VacuumDatabaseDialog.i18n.CATALOG_LABEL + " ", _emptyBorder);
        _panel.add(catalogLabel, getLabelConstraints(_gbc));

        JTextField catalogTextField = getSizedTextField(_mediumField);
        catalogTextField.setEditable(false);
        if (_catalogName != null) catalogTextField.setText(_catalogName);
        _panel.add(catalogTextField, getFieldConstraints(_gbc));

        // Options:
        // FULL Checkbox
        JLabel fullLabel = new JLabel(VacuumDatabaseDialog.i18n.FULL_LABEL);
        fullLabel.setBorder(_emptyBorder);
        _panel.add(fullLabel, getLabelConstraints(_gbc));

        _fullCheckBox = new JCheckBox();
        _fullCheckBox.setToolTipText(VacuumDatabaseDialog.i18n.FULL_TOOLTIP);
        _fullCheckBox.setPreferredSize(_mediumField);
        _panel.add(_fullCheckBox, getFieldConstraints(_gbc));

        // ANALYZE Checkbox
        JLabel analyzeLabel = new JLabel(VacuumDatabaseDialog.i18n.ANALYZE_LABEL);
        analyzeLabel.setBorder(_emptyBorder);
        _panel.add(analyzeLabel, getLabelConstraints(_gbc));

        _analyzeCheckBox = new JCheckBox();
        _analyzeCheckBox.setToolTipText(VacuumDatabaseDialog.i18n.ANALYZE_TOOLTIP);
        _analyzeCheckBox.setPreferredSize(_mediumField);
        _panel.add(_analyzeCheckBox, getFieldConstraints(_gbc));
    }


    private String[] getSimpleNames(ITableInfo[] infos) {
        String[] result = new String[infos.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = infos[i].getSimpleName();
        }
        return result;
    }


    public boolean getFullOption() {
        return _fullCheckBox.isSelected();
    }


    public boolean getAnalyzeOption() {
        return _analyzeCheckBox.isSelected();
    }
}
