/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.*;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.time.StopWatch;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to export the result of the current SQL into a File.
 * With this command is the user able to export the result of the current SQL into a file using the {@link ExportDlg}.
 * The command will run on a separate thread and a separate connection to the database. It is monitored with a {@link ProgressAbortDialog} and can be canceled.
 *
 * @author Stefan Willinger
 * @see ResultSetExport
 * @see ProgressAbortCallback
 */
public class CreateFileOfSQLCommand
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CreateFileOfSQLCommand.class);

   private static ILogger s_log = LoggerController.createLogger(CreateFileOfSQLCommand.class);
   private final ISession session;

   /**
    * Command for exporting the data.
    */
   private ResultSetExport _resultSetExport;


   private FileExportProgressManager _fileExportProgressManager;


   /**
    * Ctor specifying the current session.
    */
   public CreateFileOfSQLCommand(ISession session)
   {
      this.session = session;
   }


   public void execute(final JFrame owner)
   {
      final List<String> sqls = getSelectedSelectStatements();
      getSession().getApplication().getThreadPool().addTask(() -> doCreateFileOfCurrentSQL(SelectSQLInfo.of(sqls), owner));
   }

   public void executeForSelectedTables(Window owner, List<ITableInfo> selectedTables, IObjectTreeAPI objectTreeAPI)
   {
      final List<SelectSQLInfo> selectSQLs = ScriptUtil.createSelectSQLs(selectedTables.toArray(new IDatabaseObjectInfo[0]), objectTreeAPI);
      getSession().getApplication().getThreadPool().addTask(() -> doCreateFileOfCurrentSQL(selectSQLs, owner));
   }

   /**
    * Do the work.
    *
    * @param selectSQLInfos
    * @param owner
    */
   private void doCreateFileOfCurrentSQL(List<SelectSQLInfo> selectSQLInfos, Window owner)
   {
      try
      {
         ISQLConnection unmangedConnection = null;
         try
         {
            unmangedConnection = createUnmanagedConnection();

            Connection con;
            if (unmangedConnection != null)
            {
               con = unmangedConnection.getConnection();
            }
            else
            {
               con = getSession().getSQLConnection().getConnection();
            }

            _fileExportProgressManager = new FileExportProgressManager(getSession(), SelectSQLInfo.toJoinedSQLs(getSession(), selectSQLInfos), () -> _resultSetExport.getTargetFile());

            DialectType dialectType = DialectFactory.getDialectType(getSession().getMetaData());

            // Opens the modal export dialog ...
            _resultSetExport = new ResultSetExport(con, selectSQLInfos, dialectType, _fileExportProgressManager, owner);

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // ... called after the  modal export dialog was closed.
            _resultSetExport.export();
            stopWatch.stop();

            if (_fileExportProgressManager.isAborted())
            {
               return;
            }
            else if (_resultSetExport.getWrittenRows() >= 0)
            {
               NumberFormat nf = NumberFormat.getIntegerInstance();

               String rows = nf.format(_resultSetExport.getWrittenRows());
               File targetFile = _resultSetExport.getTargetFile();
               String seconds = nf.format(stopWatch.getTime() / 1000);
               String msg = s_stringMgr.getString("CreateFileOfCurrentSQLCommand.progress.sucessMessage",
                     rows,
                     targetFile,
                     seconds);
               getSession().showMessage(msg);
            }
         }
         finally
         {
            SQLUtilities.closeConnection(unmangedConnection);
         }
      }
      catch (Exception e)
      {
         s_log.error(e);
         if (e.getCause() != null)
         {
            getSession().showErrorMessage(e.getCause());
         }
         getSession().showErrorMessage(e.toString());
      }
      finally
      {
         SwingUtilities.invokeLater(() -> {
            if (null != _fileExportProgressManager)
            {
               _fileExportProgressManager.hideProgressMonitor();
            }
         });
      }
   }


   /**
    * Create a new unmanaged connection, which is not associated with the current session.
    *
    * @return a new unmanaged connection or null, if no connection can be created.
    * @throws SQLException
    */
   private ISQLConnection createUnmanagedConnection() throws SQLException
   {
      if(DialectFactory.isUnityJDBC(getSession().getMetaData()))
      {
         // See bug https://github.com/squirrel-sql-client/squirrel-sql-code/issues/6
         // Creating an unmanaged connection does not work for UnityJDBC.
         // See https://unityjdbc.com and the Multisource-Plugin.
         return null;
      }

      ISQLConnection unmanagedConnection = getSession().createUnmanagedConnection();

      if (unmanagedConnection == null)
      {
         int option = JOptionPane.showConfirmDialog(null, "Unable to open a new connection. The current connection will be used instead.", "Unable to open a new Connection", JOptionPane.OK_CANCEL_OPTION);
         if (option == JOptionPane.CANCEL_OPTION)
         {
            return null;
         }
      }
      else
      {
         // we didn't want autocommit
         unmanagedConnection.setAutoCommit(false);
      }
      return unmanagedConnection;
   }

   /**
    * @return the _session
    */
   public ISession getSession()
   {
      return session;
   }

   /**
    * Looks for the current selected SQL statement in the editor pane.
    * An error occurs when no query is selected.
    * In this case, the user will get a message and <code>null</code> will be returned.
    *
    * @return the selected SELECT statement or null, if not exactly one SELECT statement is selected.
    */
   private List<String> getSelectedSelectStatements()
   {
      ISQLPanelAPI api = FrameWorkAcessor.getSQLPanelAPI(getSession());

      String script = api.getSQLScriptToBeExecuted();

      IQueryTokenizer qt = getSession().getQueryTokenizer();
      qt.setScriptToTokenize(script);

      if (false == qt.hasQuery())
      {
         getSession().showErrorMessage(s_stringMgr.getString("AbstractDataScriptCommand.noQuery"));
         return null;
      }


      List<String> ret = new ArrayList<>();
      while(qt.hasQuery())
      {
         ret.add(qt.nextQuery().getQuery());
      }

      return ret;
   }
}
