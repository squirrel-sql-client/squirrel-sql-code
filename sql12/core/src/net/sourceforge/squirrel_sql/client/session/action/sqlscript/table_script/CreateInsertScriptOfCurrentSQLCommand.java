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
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.insert.InsertGenerator;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.scriptbuilder.FileScriptBuilder;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.scriptbuilder.ScriptBuilder;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.ProgressAbortDialog;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.tablenamefind.TableNameFindService;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.Statement;

public class CreateInsertScriptOfCurrentSQLCommand
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CreateInsertScriptOfCurrentSQLCommand.class);
   private ISession _session;
   private ProgressAbortDialog _progressDialog;

   public CreateInsertScriptOfCurrentSQLCommand(ISession session)
   {
      _session = session;
   }

   public void generateInserts(ScriptBuilder sbRows, InsertScriptFinishedCallBack insertScriptFinishedCallBack)
   {
      String script = FrameWorkAcessor.getSQLPanelAPI(_session).getSQLScriptToBeExecuted();
      generateInserts(script, sbRows, insertScriptFinishedCallBack);
   }

   public void generateInserts(String script, ScriptBuilder scriptBuilder, InsertScriptFinishedCallBack insertScriptFinishedCallBack)
   {

      String fileName = null;

      if(scriptBuilder instanceof FileScriptBuilder)
      {
         fileName = ((FileScriptBuilder)scriptBuilder).getFileName();
      }

      Frame owningFrame = SessionUtils.getOwningFrame(FrameWorkAcessor.getSQLPanelAPI(_session));
      _progressDialog = new ProgressAbortDialog(owningFrame, s_stringMgr.getString("CreateInsertScriptOfCurrentSQLCommand.generating.inserts"), fileName, script, 0, () -> onCancel(), null);

      Main.getApplication().getThreadPool().addTask(() -> doGenerateInserts(script, scriptBuilder, insertScriptFinishedCallBack, _progressDialog));
      _progressDialog.setVisible(true);
   }

   private void doGenerateInserts(String script, ScriptBuilder sbRows, InsertScriptFinishedCallBack insertScriptFinishedCallBack, ProgressAbortDialog progressDialog)
   {
      try
      {
         IQueryTokenizer qt = _session.getQueryTokenizer();
         qt.setScriptToTokenize(script);

         if(false == qt.hasQuery())
         {
            _session.showErrorMessage(s_stringMgr.getString("CreateInsertScriptOfCurrentSQLCommand.noQuery"));
            return;
         }

         ISQLConnection conn = _session.getSQLConnection();

         while (qt.hasQuery())
         {
            try(Statement stmt = conn.createStatement())
            {
               String sql = qt.nextQuery().getQuery();
               progressDialog.setSql(sql);
               progressDialog.currentlyLoading(StringUtils.replace(sql, "\n", " "));

               ResultSet srcResult = stmt.executeQuery(sql);
               String tableName = TableNameFindService.findTableNameBySqlOrResultMetaData(sql, srcResult, _session);

               new InsertGenerator(_session).genInserts(srcResult, tableName, sbRows, false, false, () -> _progressDialog.isUserCanceled());
            }
         }  // end while
      }
      catch (Exception e)
      {
         _session.showErrorMessage(e);
      }
      finally
      {
         SwingUtilities.invokeLater(() -> onScriptFinished(insertScriptFinishedCallBack));
      }
   }

   private void onCancel()
   {
      Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("CreateInsertScriptOfCurrentSQLCommand.user.canceled"));
   }


   private void onScriptFinished(InsertScriptFinishedCallBack insertScriptFinishedCallBack)
   {
      try
      {
         insertScriptFinishedCallBack.insertScriptFinished();
      }
      finally
      {
         closeProgress();
      }
   }

   private void closeProgress()
   {
      if (null != _progressDialog)
      {
         _progressDialog.closeProgressDialog();
      }
   }
}