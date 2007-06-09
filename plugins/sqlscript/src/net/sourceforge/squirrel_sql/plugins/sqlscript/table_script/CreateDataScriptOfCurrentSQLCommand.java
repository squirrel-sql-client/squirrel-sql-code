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

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

public class CreateDataScriptOfCurrentSQLCommand extends CreateDataScriptCommand
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(CreateDataScriptOfCurrentSQLCommand.class);


   /**
    * Current plugin.
    */
   private final SQLScriptPlugin _plugin;

   /**
    * Ctor specifying the current session.
    */
   public CreateDataScriptOfCurrentSQLCommand(ISession session, SQLScriptPlugin plugin)
   {
      super(session, plugin, false);
      _plugin = plugin;
   }

   /**
    * Execute this command.
    */
   public void execute()
   {
      _session.getApplication().getThreadPool().addTask(new Runnable()
      {
         public void run()
         {

            final StringBuffer sbRows = new StringBuffer(1000);

            try
            {
                ISQLPanelAPI api = 
                    FrameWorkAcessor.getSQLPanelAPI(_session, _plugin);
                
                String script = api.getSQLScriptToBeExecuted();
                
               IQueryTokenizer qt = _session.getQueryTokenizer();
               qt.setScriptToTokenize(script);
                
               if(false == qt.hasQuery())
               {
                  // i18n[CreateDataScriptOfCurrentSQLCommand.noQuery=No query found to create the script from.]
                  _session.showErrorMessage(s_stringMgr.getString("CreateTableOfCurrentSQLCommand.noQuery"));
                  return;
               }



               ISQLConnection conn = _session.getSQLConnection();


               final Statement stmt = conn.createStatement();
               try
               {
                  String sql = qt.nextQuery();

                  ResultSet srcResult = stmt.executeQuery(sql);
                  ResultSetMetaData metaData = srcResult.getMetaData();
                  String sTable = metaData.getTableName(1);
                  if (sTable == null || sTable.equals(""))
                  {
                     int iFromIndex = 
                         StringUtilities.getTokenBeginIndex(sql, "from");
                     sTable = getNextToken(sql, iFromIndex + "from".length());
                  }
                  genInserts(srcResult, sTable, sbRows, false);
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
               _session.showErrorMessage(e);
            }
            finally
            {
               SwingUtilities.invokeLater(new Runnable()
               {
                  public void run()
                  {
                     if (sbRows.length() > 0)
                     {
                        FrameWorkAcessor.getSQLPanelAPI(_session, _plugin).appendSQLScript(sbRows.toString(), true);

                        _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
                     }
                     hideAbortFrame();
                  }
               });
            }
         }
      });
      showAbortFrame();
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

}