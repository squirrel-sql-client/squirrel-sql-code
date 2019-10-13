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
package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;

import javax.swing.*;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.gui.action.ResultSetExportCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.TableExportDlg;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

import org.apache.commons.lang.time.StopWatch;

/**
 * Command to export the result of the current SQL into a File.
 * With this command is the user able to export the result of the current SQL into a file using the {@link TableExportDlg}.
 * The command will run on a separate thread and a separate connection to the database. It is monitored with a {@link ProgressAbortDialog} and can be canceled.
 *
 * @author Stefan Willinger
 * @see ResultSetExportCommand
 * @see ProgressAbortCallback
 */
public class CreateFileOfCurrentSQLCommand extends AbstractDataScriptCommand
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CreateFileOfCurrentSQLCommand.class);

   private static ILogger s_log = LoggerController.createLogger(CreateFileOfCurrentSQLCommand.class);


   /**
    * Command for exporting the data.
    */
   private ResultSetExportCommand resultSetExportCommand;

   private Statement stmt = null;

   private ProgressAbortFactoryCallbackImpl _progressAbortCallback;

   /**
    * The current SQL in the SQL editor pane.
    */
   private String currentSQL = null;

   /**
    * Ctor specifying the current session.
    */
   public CreateFileOfCurrentSQLCommand(ISession session, SQLScriptPlugin plugin)
   {
      super(session, plugin);
   }


   public void execute(final JFrame owner)
   {

      this.currentSQL = getSelectedSelectStatement();

      getSession().getApplication().getThreadPool().addTask(() -> doCreateFileOfCurrentSQL(owner));

   }


   /**
    * Do the work.
    *
    * @param owner
    */
   private void doCreateFileOfCurrentSQL(JFrame owner)
   {
      try
      {

         ISQLConnection unmanagedConnection = null;
         try
         {
            unmanagedConnection = createUnmanagedConnection();

            // TODO maybe, we should use a SQLExecutorTask for taking advantage of some ExecutionListeners like the parameter replacement. But how to get the right Listeners?
            if (unmanagedConnection != null)
            {
               stmt = createStatementForStreamingResults(unmanagedConnection.getConnection());
            }
            else
            {
               stmt = createStatementForStreamingResults(getSession().getSQLConnection().getConnection());
            }


            _progressAbortCallback = new ProgressAbortFactoryCallbackImpl(getSession(), currentSQL, () -> resultSetExportCommand.getTargetFile(), stmt);


            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            DialectType dialectType = DialectFactory.getDialectType(getSession().getMetaData());
            resultSetExportCommand = new ResultSetExportCommand(stmt, currentSQL, dialectType, _progressAbortCallback);
            resultSetExportCommand.execute(owner);



            stopWatch.stop();

            if (_progressAbortCallback.isAborted())
            {
               return;
            }
            else if (resultSetExportCommand.getWrittenRows() >= 0)
            {
               NumberFormat nf = NumberFormat.getIntegerInstance();

               String rows = nf.format(resultSetExportCommand.getWrittenRows());
               File targetFile = resultSetExportCommand.getTargetFile();
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
            SQLUtilities.closeStatement(stmt);
            if (unmanagedConnection != null)
            {
               unmanagedConnection.close();
            }
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
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               if (null != _progressAbortCallback)
               {
                  _progressAbortCallback.hideProgressMonitor();
               }
            }
         });
      }
   }

   /**
    * Create a {@link Statement} that will stream the result instead of loading into the memory.
    *
    * @param connection the connection to use
    * @return A Statement, that will stream the result.
    * @throws SQLException
    * @see http://javaquirks.blogspot.com/2007/12/mysql-streaming-result-set.html
    * @see http://dev.mysql.com/doc/refman/5.0/en/connector-j-reference-implementation-notes.html
    */
   private Statement createStatementForStreamingResults(Connection connection) throws SQLException
   {
      Statement stmt;
      DialectType dialectType = DialectFactory.getDialectType(getSession().getMetaData());

      if (DialectType.MYSQL5 == dialectType)
      {
         /*
          * MYSQL will load the whole result into memory. To avoid this, we must use the streaming mode.
          *
          * http://javaquirks.blogspot.com/2007/12/mysql-streaming-result-set.html
          * http://dev.mysql.com/doc/refman/5.0/en/connector-j-reference-implementation-notes.html
          */
         stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
         stmt.setFetchSize(Integer.MIN_VALUE);
      }
      else
      {
         stmt = connection.createStatement();
      }
      return stmt;

   }


   /**
    * Create a new unmanaged connection, , which is not associated with the current session.
    *
    * @return a new unmanaged connection or null, if no connection can be created.
    * @throws SQLException
    */
   private ISQLConnection createUnmanagedConnection() throws SQLException
   {
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
         // we didn't want a autocommit
         unmanagedConnection.setAutoCommit(false);
      }
      return unmanagedConnection;
   }
}
