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

import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;

import net.sourceforge.squirrel_sql.client.session.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.BaseSQLTab;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

public class CreateTableOfCurrentSQLCommand extends CreateDataScriptCommand
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(CreateTableOfCurrentSQLCommand.class);


   /**
    * Current plugin.
    */
   private final SQLScriptPlugin _plugin;

   /**
    * Ctor specifying the current session.
    */
   public CreateTableOfCurrentSQLCommand(ISession session, SQLScriptPlugin plugin)
   {
      super(session, plugin, true);
      _plugin = plugin;
   }

   /**
    * Execute this command.
    */
   public void execute()
   {
      Frame owningFrame = SessionUtils.getOwningFrame(FrameWorkAcessor.getSQLPanelAPI(_session, _plugin));

      CreateTableOfCurrentSQLCtrl ctrl = new CreateTableOfCurrentSQLCtrl(owningFrame);

      if(false == ctrl.isOK())
      {
         return;
      }

      final String sTable = ctrl.getTableName();
      final boolean scriptOnly = ctrl.isScriptOnly();
      final boolean dropTable = ctrl.isDropTable();


	
      _session.getApplication().getThreadPool().addTask(new Runnable()
      {
         public void run()
         {
            /*
             * Ok, this sleep is a hack. (Stefan)
             * On my system (Ubuntu 10.10 Java 1.6.0_24), I get the cancel dialog in a unusable state.
             * On my Windows system, this problem doesn't occurs.
             * This small sleep does the tick, where synchronization fails :-(
             *
             */
            try
            {
               Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
               // nothing to do
            }
            doCreateTableOfCurrentSQL(sTable, scriptOnly, dropTable);
         }
      });
      showAbortFrame();
   }

   private void doCreateTableOfCurrentSQL(final String sTable, final boolean scriptOnly, final boolean dropTable)
   {

      final StringBuffer sbScript = new StringBuffer();
      try
      {
//    	  Thread.sleep(100);
          ISQLPanelAPI api = 
              FrameWorkAcessor.getSQLPanelAPI(_session, _plugin);
          
          String script = api.getSQLScriptToBeExecuted();
          
         IQueryTokenizer qt = _session.getQueryTokenizer();
         qt.setScriptToTokenize(script);
         
         if(false == qt.hasQuery())
         {
            // i18n[CreateTableOfCurrentSQLCommand.noQuery=No query found to create the script from.]
            _session.showErrorMessage(s_stringMgr.getString("CreateTableOfCurrentSQLCommand.noQuery"));
            return;
         }

         ISQLConnection conn = _session.getSQLConnection();
         Statement stmt = null;
         try
         {
            StringBuffer sbCreate = new StringBuffer();
            StringBuffer sbInsert = new StringBuffer();
            StringBuffer sbDrop = new StringBuffer();
            String statSep = ScriptUtil.getStatementSeparator(_session);



            stmt = conn.createStatement();
            stmt.setMaxRows(1);
            String sql = qt.nextQuery().getQuery();
            ResultSet srcResult = stmt.executeQuery(sql);

            if(isAborted())
            {
               return;
            }


            genCreate(srcResult, sTable, sbCreate);

            genInserts(srcResult, sTable, sbInsert, true);
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

               // i18n[sqlscript.successCreate=Successfully created table {0}]
               _session.showMessage(s_stringMgr.getString("sqlscript.successCreate", sTable));
            }
            catch (Exception e)
            {
               _session.showErrorMessage(e);

               // i18n[sqlscript.storeSqlInTableFailed=An error occurred during storing SQL result in table {0}. See messages for details.\nI will create the copy script. You may correct errors and run it again.]
               String msg = s_stringMgr.getString("sqlscript.storeSqlInTableFailed", sTable);
               JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
            }

         }
      }
      catch (Exception e)
      {
         _session.showErrorMessage(e);
         e.printStackTrace();
      }
      finally
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               hideAbortFrame();
               if (scriptOnly && 0 < sbScript.toString().trim().length())
               {
                  FrameWorkAcessor.getSQLPanelAPI(_session, _plugin).appendSQLScript(sbScript.toString(), true);

                  if (false == _session.getSelectedMainTab() instanceof BaseSQLTab)
                  {
                     _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
                  }
               }
            }
         });
      }
   }

   private void genCreate(ResultSet srcResult, String sTable, StringBuffer sbCreate)
   {
      try
      {
         ResultSetMetaData metaData = srcResult.getMetaData();

         sbCreate.append("\n\nCREATE TABLE ").append(sTable).append('\n');
         sbCreate.append("(\n");

         ScriptUtil su = new ScriptUtil();

         String sColName = metaData.getColumnName(1);
         String sColType = metaData.getColumnTypeName(1);
         int colSize = metaData.getColumnDisplaySize(1);
         int decimalDigits =  metaData.getScale(1);
         sbCreate.append("   ").append(su.getColumnDef(sColName, sColType, colSize, decimalDigits));

         for(int i=2; i <= metaData.getColumnCount(); ++i)
         {
            sbCreate.append(",\n");

            sColName = metaData.getColumnName(i);
            sColType = metaData.getColumnTypeName(i);
            colSize = metaData.getColumnDisplaySize(i);
            decimalDigits =  metaData.getScale(i);
            sbCreate.append("   ").append(su.getColumnDef(sColName, sColType, colSize, decimalDigits));
         }

         sbCreate.append("\n)");

      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

}