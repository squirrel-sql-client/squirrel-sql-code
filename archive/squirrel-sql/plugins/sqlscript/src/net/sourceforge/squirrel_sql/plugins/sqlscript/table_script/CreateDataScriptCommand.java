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
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class CreateDataScriptCommand implements ICommand, InternalFrameListener
{
    protected JInternalFrame _statusFrame;
    protected boolean _bStop = false;

    /** Current session. */
    protected ISession _session;

	/** Current plugin. */
	private final SQLScriptPlugin _plugin;
	
    /**
     * Ctor specifying the current session.
     */
    public CreateDataScriptCommand(ISession session, SQLScriptPlugin plugin)
    {
        super();
        _session = session;
        _plugin = plugin;
    }

    protected void showAbortFrame()
    {
        if(_statusFrame == null)
        {
            JOptionPane optionPane = new JOptionPane("Abort?",JOptionPane.INFORMATION_MESSAGE,JOptionPane.DEFAULT_OPTION);
            _statusFrame = optionPane.createInternalFrame(_session.getSessionSheet(), "Creating data script");
            _statusFrame.addInternalFrameListener(this);
        }
        _bStop = false;
        _statusFrame.setVisible(true);
    }

    protected void hideAbortFrame()
    {
        if(_statusFrame != null)
        {
            _statusFrame.removeInternalFrameListener(this);
            try
            {
                _statusFrame.setClosed(true);
            } catch(Exception e){}
            _statusFrame.setVisible(false);
        }
    }

    /**
     * Execute this command.
     */
    public void execute()
    {
        final StringBuffer sbRows = new StringBuffer(1000);
        _session.getApplication().getThreadPool().addTask(new Runnable()
        {
            public void run()
            {
                SQLConnection conn = _session.getSQLConnection();
                try
                {
                    final Statement stmt = conn.createStatement();
                    try
                    {
//                        IDatabaseObjectInfo[] dbObjs = _session.getSelectedDatabaseObjects();
						IObjectTreeAPI api = _session.getObjectTreeAPI(_plugin);
		                IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();

                        for (int k = 0; k < dbObjs.length; k++)
                        {
                            if (dbObjs[k] instanceof ITableInfo)
                            {
                                if(_bStop) break;
                                ITableInfo ti = (ITableInfo) dbObjs[k];
                                String sTable = ti.getSimpleName();
                                ResultSet srcResult = stmt.executeQuery("select * from " + ti.getQualifiedName());
                                genInserts(srcResult, sTable, sbRows);
                            }
                        }
                    }
                    finally
                    {
                        try
                        {
                            stmt.close();
                        }
                        catch (Exception e)
                        {
                        }
                    }
                }
                catch (Exception e)
                {
                    _session.getMessageHandler().showErrorMessage(e);
                }
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        if(sbRows.length() > 0)
                        {
//                            _session.setEntireSQLScript(sbRows.toString());
                            _session.getSQLPanelAPI(_plugin).appendSQLScript(sbRows.toString(), true);
                            _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
                        }
                        hideAbortFrame();
                    }
                });
            }
        });
        showAbortFrame();
    }

    protected void genInserts(ResultSet srcResult, String sTable, StringBuffer sbRows)
        throws SQLException
    {
        ResultSetMetaData metaData = srcResult.getMetaData();

        int iColumnCount = metaData.getColumnCount();
        String[][] typeAndName = new String[iColumnCount][2];

        for (int i = 1; i <= iColumnCount; i++)
        {
            typeAndName[i-1][0] = metaData.getColumnTypeName(i).toUpperCase();
            typeAndName[i-1][1] = metaData.getColumnName(i);
        }
        while (srcResult.next())
        {
            if(_bStop) break;
            sbRows.append("insert into ");
            StringBuffer sbValues = new StringBuffer();
            sbRows.append(sTable);
            sbRows.append(" (");
            sbValues.append(" values (");
            for (int i = 0; i < iColumnCount; i++)
            {
                String sColumnTypeName = typeAndName[i][0];
                String sName = typeAndName[i][1];
                int iIndexPoint = sName.lastIndexOf('.');
                sName = sName.substring(iIndexPoint + 1);
                sbRows.append(sName);

                if (sColumnTypeName.equals("INTEGER")
                    || sColumnTypeName.equals("COUNTER")
                    || sColumnTypeName.equals("LONG")
                    || sColumnTypeName.equals("DOUBLE")
                    || sColumnTypeName.equals("NUMERIC")
                    || sColumnTypeName.equals("DECIMAL")
                    || sColumnTypeName.equals("NUMBER")
                    || sColumnTypeName.equals("TINY")
                    || sColumnTypeName.equals("SHORT")
                    || sColumnTypeName.equals("FLOAT"))
                {
                    Object value = srcResult.getObject(i+1);
                    sbValues.append(value);
                }
                else if (sColumnTypeName.equals("DATE")
                    || sColumnTypeName.equals("TIME")
                    || sColumnTypeName.equals("TIMESTAMP"))
                {
                    Date date = srcResult.getDate(i+1);
                    if(date == null)
                    {
                        sbValues.append("null");
                    }
                    else
                    {
                        sbValues.append("\'");
                        sbValues.append(date);
                        sbValues.append("\'");
                    }
                }
                else if (sColumnTypeName.equals("BIT"))
                {
                    boolean iBoolean = srcResult.getBoolean(i+1);
                    if (iBoolean)
                    {
                        sbValues.append(1);
                    }
                    else
                    {
                        sbValues.append(0);
                    }
                }
                else
                {
                    String sResult = srcResult.getString(i+1);
                    if (sResult == null)
                    {
                        sbValues.append("null");
                    }
                    else
                    {
                        int iIndex = sResult.indexOf("'");
                        if (iIndex != -1)
                        {
                            int iPrev = 0;
                            StringBuffer sb = new StringBuffer();
                            sb.append(sResult.substring(iPrev, iIndex));
                            sb.append('\\');
                            iPrev = iIndex;
                            iIndex = sResult.indexOf("'", iPrev + 1);
                            while (iIndex != -1)
                            {
                                sb.append(sResult.substring(iPrev, iIndex));
                                sb.append('\\');
                                iPrev = iIndex;
                                iIndex = sResult.indexOf("'", iPrev + 1);
                            }
                            sb.append(sResult.substring(iPrev));
                            sResult = sb.toString();
                        }

                        iIndex = sResult.indexOf('\n');
                        if (iIndex != -1)
                        {
                            int iPrev = 0;
                            StringBuffer sb = new StringBuffer();
                            sb.append(sResult.substring(iPrev, iIndex));
                            sb.append("\\n");
                            iPrev = iIndex+1;
                            iIndex = sResult.indexOf('\n', iPrev + 1);
                            while (iIndex != -1)
                            {
                                sb.append(sResult.substring(iPrev, iIndex));
                                sb.append("\\n");
                                iPrev = iIndex+1;
                                iIndex = sResult.indexOf('\n', iPrev + 1);
                            }
                            sb.append(sResult.substring(iPrev));
                            sResult = sb.toString();
                        }
                        sbValues.append("\'");
                        sbValues.append(sResult);
                        sbValues.append("\'");
                    }
                }
                sbValues.append(",");
                sbRows.append(",");
            }
            // delete last ','
            sbValues.setLength(sbValues.length()-1);
            sbRows.setLength(sbRows.length()-1);

            // close it.
            sbValues.append(");\n");
            sbRows.append(")");
            sbRows.append(sbValues.toString());
        }
    }

    /**
     * @see InternalFrameListener#internalFrameActivated(InternalFrameEvent)
     */
    public void internalFrameActivated(InternalFrameEvent e)
    {
    }

    /**
     * @see InternalFrameListener#internalFrameClosed(InternalFrameEvent)
     */
    public void internalFrameClosed(InternalFrameEvent e)
    {
        _bStop = true;
    }

    /**
     * @see InternalFrameListener#internalFrameClosing(InternalFrameEvent)
     */
    public void internalFrameClosing(InternalFrameEvent e)
    {
    }

    /**
     * @see InternalFrameListener#internalFrameDeactivated(InternalFrameEvent)
     */
    public void internalFrameDeactivated(InternalFrameEvent e)
    {
    }

    /**
     * @see InternalFrameListener#internalFrameDeiconified(InternalFrameEvent)
     */
    public void internalFrameDeiconified(InternalFrameEvent e)
    {
    }

    /**
     * @see InternalFrameListener#internalFrameIconified(InternalFrameEvent)
     */
    public void internalFrameIconified(InternalFrameEvent e)
    {
    }

    /**
     * @see InternalFrameListener#internalFrameOpened(InternalFrameEvent)
     */
    public void internalFrameOpened(InternalFrameEvent e)
    {
    }

}