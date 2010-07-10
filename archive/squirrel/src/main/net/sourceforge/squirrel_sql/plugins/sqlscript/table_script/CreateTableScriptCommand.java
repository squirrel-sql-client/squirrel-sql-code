package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;
/*
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class CreateTableScriptCommand implements ICommand {
    /** Current session. */
    private ISession _session;

    /**
     * Ctor specifying the current session.
     */
    public CreateTableScriptCommand(ISession session) {
        super();
        _session = session;
    }

    /**
     * Execute this command. Use the database meta data to construct a Create Table
     * SQL script and place it in the SQL entry panel.
     */
    public void execute() {
        SQLConnection conn = _session.getSQLConnection();
        StringBuffer sbScript = new StringBuffer(1000);
        try {
            final Statement stmt = conn.createStatement();
            try {
                IDatabaseObjectInfo[] dbObjs = _session.getSelectedDatabaseObjects();

                for (int k = 0; k < dbObjs.length; k++) {
                    if (dbObjs[k] instanceof ITableInfo) {
                        ITableInfo ti = (ITableInfo) dbObjs[k];

                        String sTable = ti.getSimpleName();
                        sbScript.append("create table ");
                        sbScript.append(sTable);
                        sbScript.append("\n(");

                        Vector pks = new Vector();
                        ResultSet rsPks = conn.getPrimaryKeys(ti);
                        //					String sPkName = "";
                        while (rsPks.next()) {
                            //						sPkName = rsPks.getString(6);
                            int iKeySeq = rsPks.getInt(5) - 1;
                            if (pks.size() <= iKeySeq)
                                pks.setSize(iKeySeq + 1);
                            pks.set(iKeySeq, rsPks.getString(4));
                        }
                        rsPks.close();
                        ResultSet rsColumns = conn.getColumns(ti);
                        while (rsColumns.next()) {
                            String sColumnName = rsColumns.getString(4);
                            sbScript.append("\n\t");
                            sbScript.append(sColumnName);
                            sbScript.append(" ");
                            //						int iType = rsColumns.getInt()
                            String sType = rsColumns.getString(6);
                            sbScript.append(sType);
                            if (sType.toLowerCase().indexOf("char") != -1) {
                                sbScript.append("(");
                                sbScript.append(rsColumns.getString(7));
                                sbScript.append(")");
                            }
                            if (pks.size() == 1 && pks.get(0).equals(sColumnName)) {
                                sbScript.append(" PRIMARY KEY");
                            }
                            if ("NO".equalsIgnoreCase(rsColumns.getString(18))) {
                                sbScript.append(" not null");
                            }
                            sbScript.append(",");

                        }
                        rsColumns.close();

                        if (pks.size() > 1) {
                            sbScript.append("\n\tCONSTRAINT ");
                            sbScript.append(sTable);
                            sbScript.append("_PK PRIMARY KEY (");
                            for (int i = 0; i < pks.size(); i++) {
                                sbScript.append(pks.get(i));
                                sbScript.append(",");
                            }
                            sbScript.setLength(sbScript.length() - 1);
                            sbScript.append("),");
                        }
                        sbScript.setLength(sbScript.length() - 1);
                        sbScript.append("\n);\n");

                    }
                }
            } finally {
                try {
                    stmt.close();
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            _session.getMessageHandler().showMessage(e);
        }
        _session.setSQLScript(sbScript.toString());
        _session.selectMainTab(ISession.IMainTabIndexes.SQL_TAB);
    }
}