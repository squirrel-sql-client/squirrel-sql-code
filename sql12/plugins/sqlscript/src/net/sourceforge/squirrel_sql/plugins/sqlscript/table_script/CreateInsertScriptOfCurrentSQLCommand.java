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

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.tablenamefind.TableNameFindService;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.insert.InsertGenerator;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.scriptbuilder.ScriptBuilder;

import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.sql.ResultSet;
import java.sql.Statement;

public class CreateInsertScriptOfCurrentSQLCommand
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CreateInsertScriptOfCurrentSQLCommand.class);
   private ISession _session;

   private AbortController _abortController;

   public CreateInsertScriptOfCurrentSQLCommand(ISession session)
   {
      _session = session;
      Frame owningFrame = SessionUtils.getOwningFrame(FrameWorkAcessor.getSQLPanelAPI(_session));
      _abortController = new AbortController(owningFrame);
   }

   public void generateInserts(ScriptBuilder sbRows, InsertScriptFinishedCallBack insertScriptFinishedCallBack)
   {
      _abortController.show();
      _session.getApplication().getThreadPool().addTask(() -> doGenerateInserts(sbRows, insertScriptFinishedCallBack));
   }

   private void doGenerateInserts(ScriptBuilder sbRows, InsertScriptFinishedCallBack insertScriptFinishedCallBack)
   {

      try
      {
          ISQLPanelAPI api = FrameWorkAcessor.getSQLPanelAPI(_session);

          String scripts = api.getSQLScriptToBeExecuted();

         IQueryTokenizer qt = _session.getQueryTokenizer();
         qt.setScriptToTokenize(scripts);

         if(false == qt.hasQuery())
         {
            _session.showErrorMessage(s_stringMgr.getString("CreateTableOfCurrentSQLCommand.noQuery"));
            return;
         }

         ISQLConnection conn = _session.getSQLConnection();

         while (qt.hasQuery())
         {
            final Statement stmt = conn.createStatement();
            try
            {
               String sql = qt.nextQuery().getQuery();

               ResultSet srcResult = stmt.executeQuery(sql);
               String tableName = TableNameFindService.findTableName(sql, srcResult, _session);

               new InsertGenerator(_session).genInserts(srcResult, tableName, sbRows, false, false, () -> _abortController.isStop());
            }
            finally
            {
               SQLUtilities.closeStatement(stmt);
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

   private void onScriptFinished(InsertScriptFinishedCallBack insertScriptFinishedCallBack)
   {
      try
      {
         insertScriptFinishedCallBack.insertScriptFinished();
      }
      finally
      {
         _abortController.close();
      }
   }


}