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
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class CreateDataScriptCommand implements ICommand {
    /** Current session. */
    private ISession _session;

    /**
     * Ctor specifying the current session.
     */
    public CreateDataScriptCommand(ISession session) {
        super();
        _session = session;
    }

    /**
     * Execute this command.
     */
    public void execute() {
        SQLConnection conn = _session.getSQLConnection();
        StringBuffer sbRows = new StringBuffer(1000);
        try {
            final Statement stmt = conn.createStatement();
            try {
                IDatabaseObjectInfo[] dbObjs = _session.getSelectedDatabaseObjects();

                for (int k = 0; k < dbObjs.length; k++) {
                    if (dbObjs[k] instanceof ITableInfo) {
                        ITableInfo ti = (ITableInfo) dbObjs[k];
                        String sTable = ti.getSimpleName();
                        ResultSet srcResult = stmt.executeQuery("select * from " + sTable);
                        ResultSetMetaData metaData = srcResult.getMetaData();

                        while (srcResult.next()) {
                            sbRows.append("insert into ");
                            StringBuffer sbValues = new StringBuffer();
                            sbRows.append(sTable);
                            sbRows.append(" (");
                            sbValues.append(" values (");
                            for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                                if (i > 1) {
                                    sbValues.append(",");
                                    sbRows.append(",");
                                }
                                String sColumnTypeName = metaData.getColumnTypeName(i);
                                String sName = metaData.getColumnName(i);
                                int iIndexPoint = sName.lastIndexOf('.');
                                sName = sName.substring(iIndexPoint + 1);
                                sbRows.append(sName);

                                if (sColumnTypeName.equalsIgnoreCase("INTEGER")
                                    || sColumnTypeName.equalsIgnoreCase("COUNTER")
                                    || sColumnTypeName.equalsIgnoreCase("LONG")
                                    || sColumnTypeName.equalsIgnoreCase("DOUBLE")
                                    || sColumnTypeName.equalsIgnoreCase("NUMERIC")
                                    || sColumnTypeName.equalsIgnoreCase("DECIMAL")
                                    || sColumnTypeName.equalsIgnoreCase("TINY")
                                    || sColumnTypeName.equalsIgnoreCase("SHORT")
                                    || sColumnTypeName.equalsIgnoreCase("FLOAT")) {
                                    Object value = srcResult.getObject(i);
                                    sbValues.append(value);
                                } else if (sColumnTypeName.equalsIgnoreCase("DATE")) {
                                    sbValues.append("\'");
                                    sbValues.append(srcResult.getDate(i));
                                    sbValues.append("\'");
                                } else if (sColumnTypeName.equalsIgnoreCase("BIT")) {
                                    boolean iBoolean = srcResult.getBoolean(i);
                                    if (iBoolean) {
                                        sbValues.append(1);
                                    } else {
                                        sbValues.append(0);
                                    }
                                } else {
                                    String sResult = srcResult.getString(i);
                                    if (sResult == null) {
                                        sbValues.append("null");
                                    } else {
                                        int iIndex = sResult.indexOf("'");
                                        if (iIndex != -1) {
                                            int iPrev = 0;
                                            StringBuffer sb = new StringBuffer();
                                            sb.append(sResult.substring(iPrev, iIndex));
                                            sb.append('\\');
                                            iPrev = iIndex;
                                            iIndex = sResult.indexOf("'", iPrev + 1);
                                            while (iIndex != -1) {
                                                sb.append(sResult.substring(iPrev, iIndex));
                                                sb.append('\\');
                                                iPrev = iIndex;
                                                iIndex = sResult.indexOf("'", iPrev + 1);
                                            }
                                            sb.append(sResult.substring(iPrev));
                                            sResult = sb.toString();
                                        }
                                        sbValues.append("\'");
                                        sbValues.append(sResult);
                                        sbValues.append("\'");
                                    }
                                }
                            }
                            sbValues.append(");\n");
                            sbRows.append(")");
                            sbRows.append(sbValues.toString());
                        }
                    }
                }
            } finally {
                try {
                    stmt.close();
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            _session.getMessageHandler().showMessage(e);
        }
        _session.setSQLScript(sbRows.toString());
        _session.selectMainTab(ISession.IMainTabIndexes.SQL_TAB);
    }
}