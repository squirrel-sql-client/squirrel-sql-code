package net.sourceforge.squirrel_sql.plugins.sqlscript;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.ExportFileWriter;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.ResultSetExportData;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.TableExportPreferences;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.TableExportPreferencesDAO;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryHolder;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.ProgressAbortFactoryCallbackImpl;

import java.io.File;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SQLToFileHandler implements ISQLExecutionListener
{
   private final static ILogger s_log = LoggerController.createLogger(SQLToFileHandler.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLToFileHandler.class);
   private ISession _session;
   private ISQLPanelAPI _sqlPaneAPI;

   private boolean _abortExecution = false;


   public SQLToFileHandler(ISession session, ISQLPanelAPI sqlPaneAPI)
   {
      _session = session;
      _sqlPaneAPI = sqlPaneAPI;
   }

   @Override
   public String statementExecuting(String initialSql)
   {
      if(-1 == initialSql.toUpperCase().indexOf("@FILE"))
      {
         return initialSql;
      }


      IQueryTokenizer queryTokenizer = _session.getQueryTokenizer();

      queryTokenizer.setScriptToTokenize(initialSql);

      StringBuilder sqlsNotToWriteToFile = new StringBuilder();


      while(queryTokenizer.hasQuery())
      {
         String query = queryTokenizer.nextQuery().getQuery();

         if(false == startsWithSqlToFileMarker(query))
         {
            sqlsNotToWriteToFile.append(query);

            if(1 == queryTokenizer.getSQLStatementSeparator().length())
            {
               sqlsNotToWriteToFile.append(queryTokenizer.getSQLStatementSeparator()).append("\n");
            }
            else
            {
               sqlsNotToWriteToFile.append(" ").append(queryTokenizer.getSQLStatementSeparator()).append("\n");
            }

            continue;
         }



         String sqlWithFilePrefix = query.trim();

         int fileBeginMarkerPos = sqlWithFilePrefix.indexOf('\'');
         if(-1 == fileBeginMarkerPos)
         {
            Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("SQLToFileHandler.noFileBeginMarker"));
            continue;
         }

         int fileEndMarkerPos = sqlWithFilePrefix.indexOf('\'', fileBeginMarkerPos + 1);
         if(-1 == fileEndMarkerPos)
         {
            Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("SQLToFileHandler.noFileEndMarker"));
            continue;
         }

         String fileName = sqlWithFilePrefix.substring(fileBeginMarkerPos + 1, fileEndMarkerPos).trim();

         File file = new File(fileName);


//         if(false == file.canWrite())
//         {
//            Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("SQLToFileHandler.notAValidFile", fileName));
//         }
//         try
//         {
//            file.getCanonicalPath();
//         }
//         catch (IOException e)
//         {
//            Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("SQLToFileHandler.notAValidFile", fileName));
//         }

         String sqlToWriteToFile = sqlWithFilePrefix.substring(fileEndMarkerPos + 1).trim();


         TableExportPreferences prefs = TableExportPreferencesDAO.createExportPreferencesForFile(fileName);

         callResultSetExport(prefs, file , sqlToWriteToFile);

         if(_abortExecution)
         {
            _abortExecution = false;
            break;
         }

      }


      _sqlPaneAPI.getSQLEntryPanel().requestFocus();

      if (0 == sqlsNotToWriteToFile.length())
      {
         return null;
      }
      else
      {
         return sqlsNotToWriteToFile.toString();
      }
   }

   /**
    * Used in {@link QueryTokenizer#expandFileIncludes(String)} to
    * prevent conflict with {{@link net.sourceforge.squirrel_sql.plugins.oracle.tokenizer.OracleQueryTokenizer#ORACLE_SCRIPT_INCLUDE_PREFIX}}
    */
   public static boolean startsWithSqlToFileMarker(String query)
   {
      return null != query && query.trim().toUpperCase().startsWith("@FILE");
   }

   private void callResultSetExport(TableExportPreferences prefs, File file, String sqlToWriteToFile)
   {
      try
      {

         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SQLToFileHandler.writing.file", file.getPath()));
         Statement stat = _session.getSQLConnection().createStatement();
         DialectType dialectType = DialectFactory.getDialectType(_session.getMetaData());
         ProgressAbortFactoryCallbackImpl progressControllerFactory = new ProgressAbortFactoryCallbackImpl(_session, sqlToWriteToFile, () -> file, stat);

         // Execution will stop at this point displaying a modal progress frame.
         progressControllerFactory.getOrCreate(() -> onModalProgressDialogIsDisplaying(prefs, file, sqlToWriteToFile, stat, dialectType, progressControllerFactory));

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onModalProgressDialogIsDisplaying(TableExportPreferences prefs, File file, String sqlToWriteToFile, Statement stat, DialectType dialectType, ProgressAbortFactoryCallbackImpl progressControllerFactory)
   {
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(() -> doWriteFile(prefs, file, sqlToWriteToFile, stat, dialectType, progressControllerFactory));
   }

   private void doWriteFile(TableExportPreferences prefs, File file, String sqlToWriteToFile, Statement stat, DialectType dialectType, ProgressAbortFactoryCallbackImpl progressControllerFactory)
   {
      try
      {
         ExportFileWriter.writeFile(prefs, new ResultSetExportData(stat.executeQuery(sqlToWriteToFile), dialectType), progressControllerFactory.getOrCreate());
         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SQLToFileHandler.wrote.file", file.getPath()));
      }
      catch (Throwable e)
      {
         s_log.error(e);
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("SQLToFileHandler.error.writing.file", file.getPath(), e));

         _abortExecution = _session.getProperties().getAbortOnError();
      }
      finally
      {
         progressControllerFactory.hideProgressMonitor();
      }
   }


   @Override
   public void statementExecuted(QueryHolder sql) {}

   @Override
   public void executionFinished() {}
}
