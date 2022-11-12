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

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.ExportDlg;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.ResultSetExport;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;
import org.apache.commons.lang3.time.StopWatch;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
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
public class CreateFileOfCurrentSQLCommand
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CreateFileOfCurrentSQLCommand.class);

   private static ILogger s_log = LoggerController.createLogger(CreateFileOfCurrentSQLCommand.class);
   private final ISession session;
   private final SQLScriptPlugin plugin;


   /**
    * Command for exporting the data.
    */
   private ResultSetExport _resultSetExport;


   private ProgressAbortFactoryCallbackImpl _progressAbortCallback;


   /**
    * Ctor specifying the current session.
    */
   public CreateFileOfCurrentSQLCommand(ISession session, SQLScriptPlugin plugin)
   {
      this.session = session;
      this.plugin = plugin;
   }


   public void execute(final JFrame owner)
   {
      final List<String> sqls = getSelectedSelectStatements();
      getSession().getApplication().getThreadPool().addTask(() -> doCreateFileOfCurrentSQL(sqls, owner));
   }


   /**
    * Do the work.
    *
    * @param sqls
    * @param owner
    */
   private void doCreateFileOfCurrentSQL(List<String> sqls, JFrame owner)
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

            final String sqlsJoined;
            if(1 == session.getProperties().getSQLStatementSeparator().length())
            {
               sqlsJoined = String.join(session.getProperties().getSQLStatementSeparator() + "\n", sqls);
            }
            else
            {
               sqlsJoined = String.join(" " + session.getProperties().getSQLStatementSeparator() + "\n", sqls);
            }
            _progressAbortCallback = new ProgressAbortFactoryCallbackImpl(getSession(), sqlsJoined, () -> _resultSetExport.getTargetFile());


            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            DialectType dialectType = DialectFactory.getDialectType(getSession().getMetaData());

            // Opens the modal export dialog ...
            _resultSetExport = new ResultSetExport(con, sqls, dialectType, _progressAbortCallback, owner);

            // ... called after the  modal export dialog was closed.
            _resultSetExport.export();

            stopWatch.stop();

            if (_progressAbortCallback.isAborted())
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
            if (null != _progressAbortCallback)
            {
               _progressAbortCallback.hideProgressMonitor();
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

   /**
    * @return the _session
    */
   public ISession getSession()
   {
      return session;
   }

   /**
    * @return the _plugin
    */
   public SQLScriptPlugin getPlugin()
   {
      return plugin;
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
