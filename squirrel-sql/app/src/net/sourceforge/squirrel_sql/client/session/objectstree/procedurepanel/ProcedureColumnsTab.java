package net.sourceforge.squirrel_sql.client.session.objectstree.procedurepanel;
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
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import javax.swing.JLabel;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.objectstree.procedurepanel.BaseProcedurePanelTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel.ResultSetPanel;

/**
 * This tab shows the columns in the currently selected procedure.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ProcedureColumnsTab extends net.sourceforge.squirrel_sql.client.session.objectstree.procedurepanel.BaseProcedurePanelTab {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String TITLE = "Columns";
        String HINT = "Show columns for procedure";
    }

    /** Component to be displayed. */
    private ResultSetPanel _comp;

    /**
     * Return the title for the tab.
     *
     * @return    The title for the tab.
     */
    public String getTitle() {
        return i18n.TITLE;
    }

    /**
     * Return the hint for the tab.
     *
     * @return    The hint for the tab.
     */
    public String getHint() {
        return i18n.HINT;
    }

    /**
     * Return the component to be displayed in the panel.
     *
     * @return    The component to be displayed in the panel.
     */
    public synchronized Component getComponent() {
        if (_comp == null) {
            _comp = new ResultSetPanel();
        }
        return _comp;
    }

    /**
     * Refresh the component displaying the <TT>IProcedureInfo</TT> object.
     */
    public synchronized void refreshComponent() throws IllegalStateException {
        ISession session = getSession();
        if (session == null) {
            throw new IllegalStateException("Null ISession");
        }
        IProcedureInfo pi = getProcedureInfo();
        if ( pi == null) {
            throw new IllegalStateException("Null IProcedureInfo");
        }
        String destClassName = null;//?? session.getProperties().getColumnsOutputClassName();
        try {
            ResultSet rs = session.getSQLConnection().getProcedureColumns(pi);
            ((ResultSetPanel)getComponent()).load(session, rs, null /*new int[] {4,5,6,7,9,10,11,12,13,14,15,16,17,18}*/, destClassName);
        } catch (Exception ex) {
            session.getMessageHandler().showMessage(ex);
        }
    }
}

