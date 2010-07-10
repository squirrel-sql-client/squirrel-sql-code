package net.sourceforge.squirrel_sql.client.session.objectstree;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;

import net.sourceforge.squirrel_sql.client.session.ISession;

class ProcedurePanel extends PropertyPanel {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String NAME = "Procedure Name:";
        String CATALOG = "Catalogue:";
        String SCHEMA = "Schema:";
        String TYPE = "Type:";
        String REMARKS = "Remarks:";
    }

    private ISession _session;

    private IProcedureInfo _procInfo;

    private JLabel _nameLbl = new JLabel();
    private JLabel _catalogLbl = new JLabel();
    private JLabel _schemaLbl = new JLabel();
    private JLabel _typeLbl = new JLabel();
    private JLabel _remarksLbl = new JLabel();

    ProcedurePanel(ISession session) {
        super();
        _session = session;
        createUserInterface();
    }

    void setProcedureInfo(IProcedureInfo value) {
        _procInfo = value;
        _nameLbl.setText(value != null ? getString(value.getSimpleName()) : "" );
        _catalogLbl.setText(value != null ? getString(value.getCatalogName()) : "" );
        _schemaLbl.setText(value != null ? getString(value.getSchemaName()) : "" );
        _typeLbl.setText(value != null ? getString(value.getTypeDescription()) : "" );
        _remarksLbl.setText(value != null ? getString(value.getRemarks()) : "" );
    }

    private void createUserInterface() {
        add(new JLabel(i18n.NAME, SwingConstants.RIGHT), _nameLbl);
        add(new JLabel(i18n.CATALOG, SwingConstants.RIGHT), _catalogLbl);
        add(new JLabel(i18n.SCHEMA, SwingConstants.RIGHT), _schemaLbl);
        add(new JLabel(i18n.TYPE, SwingConstants.RIGHT), _typeLbl);
        add(new JLabel(i18n.REMARKS, SwingConstants.RIGHT), _remarksLbl);
    }

    private String getString(String str) {
        return str != null ? str : "";
    }
}