package net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script;

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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.insert.InsertGenerator;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.scriptbuilder.StringScriptBuilder;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.ProgressAbortDialog;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDataScriptCommand
{
   private static final ILogger s_log =  LoggerController.createLogger(CreateDataScriptCommand.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CreateDataScriptCommand.class);


   private ISession _session;

   private boolean _templateScriptOnly;

   private final IObjectTreeAPI _objectTreeAPI;
   private ProgressAbortDialog _progressDialog;

   public CreateDataScriptCommand(IObjectTreeAPI objectTreeAPI, boolean templateScriptOnly)
   {
      _objectTreeAPI = objectTreeAPI;
      _templateScriptOnly = templateScriptOnly;

      _session = _objectTreeAPI.getSession();
   }

   public void execute()
   {
      Frame owningFrame = SessionUtils.getOwningFrame(FrameWorkAcessor.getSQLPanelAPI(_session));
      _progressDialog = new ProgressAbortDialog(owningFrame, s_stringMgr.getString("CreateDataScriptCommand.generating.inserts"),() -> onCancel());
      _session.getApplication().getThreadPool().addTask(() -> onCreateScript(_progressDialog));
      _progressDialog.setVisible(true);
   }

   private void onCreateScript(ProgressAbortDialog progressDialog)
   {
      StringScriptBuilder sbRows = new StringScriptBuilder();

      ISQLConnection conn = _session.getSQLConnection();
      try (Statement stmt = conn.createStatement())
      {
         IDatabaseObjectInfo[] dbObjs = _objectTreeAPI.getSelectedDatabaseObjects();

         for (int k = 0; k < dbObjs.length; k++)
         {
            if (dbObjs[k] instanceof ITableInfo)
            {
               if (_progressDialog.isUserCanceled())
               {
                  break;
               }
               ITableInfo ti = (ITableInfo) dbObjs[k];
               String sTable = ScriptUtil.getTableName(ti);

               progressDialog.currentlyLoading(s_stringMgr.getString("CreateDataScriptCommand.executing.select", ti.getSimpleName()));
               ResultSet srcResult = executeDataSelectSQL(stmt, ti);
               progressDialog.currentlyLoading(s_stringMgr.getString("CreateDataScriptCommand.creating.inserts", ti.getSimpleName()));
               new InsertGenerator(_session).genInserts(srcResult, sTable, sbRows, false, _templateScriptOnly, () -> _progressDialog.isUserCanceled());
            }
         }

      }
      catch (Exception e)
      {
         _session.showErrorMessage(e);
         s_log.error(e);
      }
      finally
      {
         GUIUtils.processOnSwingEventThread(() -> onScriptFinished(sbRows.getStringBuilder()));
      }
   }

   private void onScriptFinished(StringBuilder sbRows)
   {
      try
      {
         if (sbRows.length() > 0)
         {
            FrameWorkAcessor.appendScriptToEditor(sbRows.toString(), _objectTreeAPI);
         }
      }
      finally
      {
         _progressDialog.closeProgressDialog();
      }
   }

   private ResultSet executeDataSelectSQL(Statement stmt, ITableInfo ti) throws SQLException
   {
      StringBuilder sql = genDataSelectSQL(ti, ScriptUtil.getTableName(ti));

      ResultSet srcResult;

      try
      {
         srcResult = stmt.executeQuery(sql.toString());
      }
      catch (SQLException e)
      {
         boolean qualifyTableNames = Main.getApplication().getSQLScriptPreferencesManager().getPreferences().isQualifyTableNames();
         if(false == qualifyTableNames)
         {
            try
            {
               sql = genDataSelectSQL(ti, ScriptUtil.getTableName(ti, true, false));
               srcResult = stmt.executeQuery(sql.toString());
            }
            catch (SQLException e1)
            {
               boolean useDoubleQuotes = Main.getApplication().getSQLScriptPreferencesManager().getPreferences().isUseDoubleQuotes();
               if(false == useDoubleQuotes)
               {
                  sql = genDataSelectSQL(ti, ScriptUtil.getTableName(ti, true, false));
                  srcResult = stmt.executeQuery(sql.toString());
               }
               else
               {
                  throw e;
               }
            }
         }
         else
         {
            throw e;
         }
      }
      return srcResult;
   }

   private StringBuilder genDataSelectSQL(ITableInfo ti, String tableName) throws SQLException
   {
      StringBuilder sql = new StringBuilder();
      sql.append("select * from ");
      sql.append(tableName);

      // Some databases cannot order by LONG/LOB columns.
      if (!JDBCTypeMapper.isLongType(getFirstColumnType(ti)))
      {
          sql.append(" order by ");
          sql.append(getFirstColumnName(ti));
          sql.append(" asc ");
      }

      return sql;
   }

   private String getFirstColumnName(ITableInfo ti) throws SQLException
   {
      TableColumnInfo[] infos = _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);
      return ScriptUtil.getColumnName(infos[0]);
   }

   private int getFirstColumnType(ITableInfo ti) throws SQLException
   {
      TableColumnInfo[] infos = _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);
      return infos[0].getDataType();
   }

   private void onCancel()
   {
      Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("CreateDataScriptCommand.user.canceled"));
   }
}
