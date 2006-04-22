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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;

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

      CreateTableOfCurrentSQLCtrl ctrl = new CreateTableOfCurrentSQLCtrl(_session);

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

            doCreateTableOfCurrentSQL(sTable, scriptOnly, dropTable);
         }
      });
      showAbortFrame();
   }

   private void doCreateTableOfCurrentSQL(String sTable, final boolean scriptOnly, boolean dropTable)
   {
      SQLConnection conn = _session.getSQLConnection();

      String selectSQL = FrameWorkAcessor.getSQLPanelAPI(_session, _plugin).getSQLScriptToBeExecuted();

      final StringBuffer sbCreate = new StringBuffer();
      try
      {
         final Statement stmt = conn.createStatement();
         try
         {
            ResultSet srcResult = stmt.executeQuery(selectSQL);

            genCreate(srcResult, sTable, sbCreate);

            StringBuffer sbInsert = new StringBuffer();
            genInserts(srcResult, sTable, sbInsert, true);
            sbInsert.append('\n').append(selectSQL);

            if(false == scriptOnly)
            {
               try
               {
                  if(dropTable)
                  {
                     String sql = "DROP TABLE " + sTable;
                     SQLExecuterTask executer = new SQLExecuterTask(_session, sql, new DefaultSQLExecuterHandler(_session));
                     executer.run();
                  }

                  String statSep = ScriptUtil.getStatementSeparator(_session);
                  sbCreate.append(statSep).append(sbInsert).append(statSep);


                  SQLExecuterTask executer = new SQLExecuterTask(_session, sbCreate.toString(), new DefaultSQLExecuterHandler(_session));
                  executer.run();

                  // i18n[sqlscript.successCreate=Successfully created table {0}]
                  _session.getMessageHandler().showMessage(s_stringMgr.getString("sqlscript.successCreate", sTable));
                  return;
               }
               catch(Exception e)
               {
                  _session.getMessageHandler().showErrorMessage(e);

                  // i18n[sqlscript.storeSqlInTableFailed=An error occured during storing SQL result in table {0}. See messages for details.\nI will create the copy script. You may correct errors and run it again.]
                  String msg = s_stringMgr.getString("sqlscript.storeSqlInTableFailed", sTable);
                  JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
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
         e.printStackTrace();
      }
      finally
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               hideAbortFrame();
               if(scriptOnly)
               {
                  FrameWorkAcessor.getSQLPanelAPI(_session, _plugin).appendSQLScript(sbCreate.toString(), true);
                  _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
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

   private String getTableName()
   {
      return "ygwaTest";
   }

   private String getNextToken(String selectSQL, int startPos)
   {
      int curPos = startPos;
      while(curPos < selectSQL.length() && true == Character.isWhitespace(selectSQL.charAt(curPos)))
      {
         // Move over leading whitespaces
         ++curPos;
      }

      int startPosTrimed = curPos;


      while(curPos < selectSQL.length() && false == Character.isWhitespace(selectSQL.charAt(curPos)))
      {
         ++curPos;
      }

      return selectSQL.substring(startPosTrimed, curPos);
   }

   private int getTokenBeginIndex(String selectSQL, String token)
   {
      String lowerSel = selectSQL.toLowerCase();
      String lowerToken = token.toLowerCase().trim();

      int curPos = 0;
      while(-1 != curPos)
      {
         curPos = lowerSel.indexOf(lowerToken);

         if(
                -1 < curPos
             && (0 == curPos || Character.isWhitespace(lowerSel.charAt(curPos-1)))
             && (lowerSel.length() == curPos + lowerToken.length() || Character.isWhitespace(lowerSel.charAt(curPos + lowerToken.length())))
           )
         {
            return curPos;
         }
      }

      return curPos;
   }
}