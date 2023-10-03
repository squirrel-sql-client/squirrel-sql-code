package net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script;

/*
 * Copyright (C) 2006 Rob Manning
 * manningr@sourceforge.net
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
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class DropTableScriptCommand implements ICommand
{
   private static ILogger s_log = LoggerController.createLogger(DropTableScriptCommand.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DropTableScriptCommand.class);

   private IObjectTreeAPI _objectTreeAPI;


   public DropTableScriptCommand(IObjectTreeAPI objectTreeAPI)
   {
      _objectTreeAPI = objectTreeAPI;
   }

   /**
    * Execute this command. Use the database meta data to construct a Create
    * Table SQL script and place it in the SQL entry panel.
    */
   public void execute()
   {
      IDatabaseObjectInfo[] dbObjs = _objectTreeAPI.getSelectedDatabaseObjects();
      scriptTablesToSQLEntryArea(dbObjs);
   }

   public void scriptTablesToSQLEntryArea(final IDatabaseObjectInfo[] dbObjs)
   {
      Main.getApplication().getThreadPool().addTask(new Runnable()
      {
         public void run()
         {
            final String script = dropTableScriptString(dbObjs);
            if (script != null)
            {
               GUIUtils.processOnSwingEventThread(() -> FrameWorkAcessor.appendScriptToEditor(script, _objectTreeAPI));
            }
         }
      });
   }

   public String dropTableScriptString(IDatabaseObjectInfo[] dbObjs)
   {
      StringBuffer sbScript = new StringBuffer(1000);
      try
      {
         for (int k = 0; k < dbObjs.length; k++)
         {
            if (false == dbObjs[k] instanceof ITableInfo)
            {
               continue;

            }
            ITableInfo ti = (ITableInfo) dbObjs[k];

            String sTable = ScriptUtil.getTableName(ti);
            sbScript.append("DROP TABLE ");
            sbScript.append(sTable);
            sbScript.append(ScriptUtil.getStatementSeparator(_objectTreeAPI.getSession()));
            sbScript.append("\n");
         }
      }
      catch (Exception e)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(e);
         s_log.error(e);
      }
      return sbScript.toString();
   }
}