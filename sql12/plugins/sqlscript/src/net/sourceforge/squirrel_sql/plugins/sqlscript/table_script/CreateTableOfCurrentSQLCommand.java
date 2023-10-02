package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

/*
 * Copyright (C) 2005 Gerd Wagner
 * gerdwagner@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.client.session.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.BaseSQLTab;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.ProgressAbortDialog;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.insert.InsertGenerator;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.scriptbuilder.StringScriptBuilder;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTableOfCurrentSQLCommand
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CreateTableOfCurrentSQLCommand.class);

   private static final ILogger s_log =  LoggerController.createLogger(CreateTableOfCurrentSQLCommand.class);



   private ISession _session;
   private ProgressAbortDialog _progressDialog;

   /**
    * Ctor specifying the current session.
    */
   public CreateTableOfCurrentSQLCommand(ISession session)
   {
      _session = session;
   }

   /**
    * Execute this command.
    */
   public void execute()
   {
      Frame owningFrame = SessionUtils.getOwningFrame(FrameWorkAcessor.getSQLPanelAPI(_session));

      CreateTableOfCurrentSQLCtrl ctrl = new CreateTableOfCurrentSQLCtrl(owningFrame);

      if(false == ctrl.isOK())
      {
         return;
      }

      final String sTable = ctrl.getTableName();
      final boolean scriptOnly = ctrl.isScriptOnly();
      final boolean dropTable = ctrl.isDropTable();

      ISQLPanelAPI api =  FrameWorkAcessor.getSQLPanelAPI(_session);
      String script = api.getSQLScriptToBeExecuted();

      _progressDialog = new ProgressAbortDialog(owningFrame, s_stringMgr.getString("CreateTableOfCurrentSQLCommand.generating.insert.table.script", sTable), () -> onCancel());
      _session.getApplication().getThreadPool().addTask(() -> doCreateTableOfCurrentSQL(script, sTable, scriptOnly, dropTable, _progressDialog));
      _progressDialog.setVisible(true);
   }

   private void doCreateTableOfCurrentSQL(String script, String sTable, boolean scriptOnly, boolean dropTable, ProgressAbortDialog progressDialog)
   {

      StringBuilder sbScript = new StringBuilder();
      try
      {

         IQueryTokenizer qt = _session.getQueryTokenizer();
         qt.setScriptToTokenize(script);
         
         if(false == qt.hasQuery())
         {
            _session.showErrorMessage(s_stringMgr.getString("CreateTableOfCurrentSQLCommand.noQuery"));
            return;
         }

         ISQLConnection conn = _session.getSQLConnection();
         Statement stmt = null;
         try
         {
            StringBuilder sbCreate = new StringBuilder();
            StringScriptBuilder ssbInsert = new StringScriptBuilder();
            StringBuilder sbDrop = new StringBuilder();
            String statSep = ScriptUtil.getStatementSeparator(_session);

            stmt = conn.createStatement();
            stmt.setMaxRows(1);

            progressDialog.currentlyLoading(s_stringMgr.getString("CreateTableOfCurrentSQLCommand.executing.select.for.meta.data"));
            String sql = qt.nextQuery().getQuery();
            progressDialog.setSql(sql);
            ResultSet srcResult = stmt.executeQuery(sql);

            if(_progressDialog.isUserCanceled())
            {
               return;
            }


            progressDialog.currentlyLoading(s_stringMgr.getString("CreateTableOfCurrentSQLCommand.generating.script"));
            genCreate(srcResult, sTable, sbCreate);

            new InsertGenerator( _session).genInserts(srcResult, sTable, ssbInsert, true, false, () -> _progressDialog.isUserCanceled());
            StringBuilder sbInsert = ssbInsert.getStringBuilderClone();
            sbInsert.append('\n').append(sql);

            sbDrop.append("DROP TABLE " + sTable);

            if(dropTable && _session.getSchemaInfo().isTable(sTable))
            {
               sbScript.append(sbDrop).append(statSep);
            }

            sbScript.append(sbCreate).append(statSep);
            sbScript.append(sbInsert).append(statSep);
         }
         finally
         {
            SQLUtilities.closeStatement(stmt);
         }

         if (false == scriptOnly)
         {
            try
            {
               SQLExecuterTask executer = new SQLExecuterTask(_session, sbScript.toString(), new DefaultSQLExecuterHandler(_session));
               executer.run();

               _session.showMessage(s_stringMgr.getString("CreateTableOfCurrentSQLCommand.successCreate", sTable));
            }
            catch (Exception e)
            {
               _session.showErrorMessage(e);
               s_log.error(e);
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
         SwingUtilities.invokeLater(() -> onScriptFinished(scriptOnly, sbScript));
      }
   }

   private void onScriptFinished(boolean scriptOnly, StringBuilder sbScript)
   {
      try
      {
         if (scriptOnly && 0 < sbScript.toString().trim().length())
         {
            FrameWorkAcessor.getSQLPanelAPI(_session).appendSQLScript(sbScript.toString(), true);

            if (false == _session.getSelectedMainTab() instanceof BaseSQLTab)
            {
               _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
            }
         }
      }
      finally
      {
         _progressDialog.closeProgressDialog();
      }
   }

   private void genCreate(ResultSet srcResult, String sTable, StringBuilder sbCreate)
   {
      try
      {
         ResultSetMetaData metaData = srcResult.getMetaData();

         sbCreate.append("\n\nCREATE TABLE ").append(sTable).append('\n');
         sbCreate.append("(\n");

         String sColName = metaData.getColumnName(1);
         String sColType = metaData.getColumnTypeName(1);
         int colSize = metaData.getColumnDisplaySize(1);
         int decimalDigits =  metaData.getScale(1);
         sbCreate.append("   ").append(ScriptUtil.getColumnDef(sColName, sColType, colSize, decimalDigits));

         for(int i=2; i <= metaData.getColumnCount(); ++i)
         {
            sbCreate.append(",\n");

            sColName = metaData.getColumnName(i);
            sColType = metaData.getColumnTypeName(i);
            colSize = metaData.getColumnDisplaySize(i);
            decimalDigits =  metaData.getScale(i);
            sbCreate.append("   ").append(ScriptUtil.getColumnDef(sColName, sColType, colSize, decimalDigits));
         }

         sbCreate.append("\n)");

      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onCancel()
   {
      Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("CreateTableOfCurrentSQLCommand.user.canceled"));
   }
}