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

public class VacuumTableDialog extends AbstractPostgresDialog {
    /** The tables that where selected */
    protected ITableInfo[] _infos;

    /** Some GUI elements */
    protected JCheckBox _fullCheckBox;
    protected JCheckBox _analyzeCheckBox;

    /** Internationalized strings for this class */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(VacuumTableDialog.class);

    static interface i18n {
        String TITLE = s_stringMgr.getString("VacuumTableDialog.title");
        String CATALOG_LABEL = s_stringMgr.getString("VacuumTableDialog.catalogLabel");
        String SCHEMA_LABEL = s_stringMgr.getString("VacuumTableDialog.schemaLabel");
        String TABLE_LABEL = s_stringMgr.getString("VacuumTableDialog.tableLabel");
        String FULL_LABEL = s_stringMgr.getString("VacuumTableDialog.fullLabel");
        String FULL_TOOLTIP = s_stringMgr.getString("VacuumTableDialog.fullTooltip");
        String ANALYZE_LABEL = s_stringMgr.getString("VacuumTableDialog.analyzeLabel");
        String ANALYZE_TOOLTIP = s_stringMgr.getString("VacuumTableDialog.analyzeTooltip");
    }


    public VacuumTableDialog(ITableInfo[] infos) {
        _infos = infos;
        setTitle(i18n.TITLE);
        init();
    }


    public VacuumTableDialog() {
        _infos = null;
        setTitle(i18n.TITLE);
        init();
    }


    protected void init() {
        defaultInit();

        // Catalog
        JLabel catalogLabel = getBorderedLabel(i18n.CATALOG_LABEL + " ", _emptyBorder);
        _panel.add(catalogLabel, getLabelConstraints(_gbc));

        JTextField catalogTextField = getSizedTextField(_mediumField);
        catalogTextField.setEditable(false);
        if (_infos != null) catalogTextField.setText(_infos[0].getCatalogName());
        _panel.add(catalogTextField, getFieldConstraints(_gbc));

        // Schema
        JLabel schemaLabel = getBorderedLabel(i18n.SCHEMA_LABEL + " ", _emptyBorder);
        _panel.add(schemaLabel, getLabelConstraints(_gbc));

        JTextField schemaTextField = getSizedTextField(_mediumField);
        schemaTextField.setEditable(false);
        if (_infos != null) schemaTextField.setText(_infos[0].getSchemaName());
        _panel.add(schemaTextField, getFieldConstraints(_gbc));

        // table list
        JLabel tableLabel = getBorderedLabel(i18n.TABLE_LABEL + " ", _emptyBorder);
        tableLabel.setVerticalAlignment(JLabel.NORTH);
        _panel.add(tableLabel, getLabelConstraints(_gbc));

        JList tableList;
        if (_infos != null) tableList = new JList(getSimpleNames(_infos));
        else tableList = new JList();
        tableList.setEnabled(false);

        JScrollPane tableScrollPane = new JScrollPane(tableList);
        _gbc = getFieldConstraints(_gbc);
        _gbc.weightx = 1;
        _gbc.weighty = 1;
        _gbc.fill = GridBagConstraints.BOTH;
        _panel.add(tableScrollPane, _gbc);

        // Options:
        // FULL Checkbox
        JLabel fullLabel = new JLabel(i18n.FULL_LABEL);
        fullLabel.setBorder(_emptyBorder);
        _panel.add(fullLabel, getLabelConstraints(_gbc));

        _fullCheckBox = new JCheckBox();
        _fullCheckBox.setToolTipText(i18n.FULL_TOOLTIP);
        _fullCheckBox.setPreferredSize(_mediumField);
        _panel.add(_fullCheckBox, getFieldConstraints(_gbc));

        // ANALYZE Checkbox
        JLabel analyzeLabel = new JLabel(i18n.ANALYZE_LABEL);
        analyzeLabel.setBorder(_emptyBorder);
        _panel.add(analyzeLabel, getLabelConstraints(_gbc));

        _analyzeCheckBox = new JCheckBox();
        _analyzeCheckBox.setToolTipText(i18n.ANALYZE_TOOLTIP);
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


    public void setContent(ITableInfo[] infos) {
        _infos = infos;
    }


    public ITableInfo[] getContent() {
        return _infos;
    }


    public boolean getFullOption() {
        return _fullCheckBox.isSelected();
    }


    public boolean getAnalyzeOption() {
        return _analyzeCheckBox.isSelected();
    }
}

