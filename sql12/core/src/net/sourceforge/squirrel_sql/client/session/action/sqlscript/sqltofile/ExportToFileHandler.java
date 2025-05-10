package net.sourceforge.squirrel_sql.client.session.action.sqlscript.sqltofile;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.ExportFileWriter;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.FileExportProgressManager;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.ResultSetExportData;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.TableExportPreferences;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.TableExportPreferencesDAO;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryHolder;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExportToFileHandler
{
   private final static ILogger s_log = LoggerController.createLogger(ExportToFileHandler.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExportToFileHandler.class);


   private ExecutorService _executorService = Executors.newSingleThreadExecutor();

   private final ISession _session;
   private final ISQLPanelAPI _sqlPaneAPI;

   private boolean _abortExecution = false;


   public ExportToFileHandler(ISession session, ISQLPanelAPI sqlPaneAPI)
   {
      _session = session;
      _sqlPaneAPI = sqlPaneAPI;
   }

   public void exportToFile(List<QueryHolder> queryHolders)
   {

      for(QueryHolder query : queryHolders)
      {
         String sqlWithFilePrefix = query.getQuery().trim();

         int fileBeginMarkerPos = sqlWithFilePrefix.indexOf('\'');
         if(-1 == fileBeginMarkerPos)
         {
            Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ExportToFileHandler.noFileBeginMarker"));
            continue;
         }

         int fileEndMarkerPos = sqlWithFilePrefix.indexOf('\'', fileBeginMarkerPos + 1);
         if(-1 == fileEndMarkerPos)
         {
            Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ExportToFileHandler.noFileEndMarker"));
            continue;
         }

         String fileName = sqlWithFilePrefix.substring(fileBeginMarkerPos + 1, fileEndMarkerPos).trim();

         File file = new File(fileName);

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
   }

   public static boolean containsMyMarker(String initialSqlString)
   {
      return null != initialSqlString && StringUtils.containsIgnoreCase(initialSqlString.trim(), "@file");
   }

   /**
    * Used in {@link QueryTokenizer#expandFileIncludes(String)} to
    * prevent conflict with {{@link net.sourceforge.squirrel_sql.plugins.oracle.tokenizer.OracleQueryTokenizer#ORACLE_SCRIPT_INCLUDE_PREFIX}}
    */
   public static boolean willBeHandledByMe(QueryHolder sql)
   {
      return null != sql && StringUtils.startsWithIgnoreCase(sql.getQuery().trim(), "@file");
   }

   private void callResultSetExport(TableExportPreferences prefs, File file, String sqlToWriteToFile)
   {
      try
      {

         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("ExportToFileHandler.writing.file", file.getPath()));
         DialectType dialectType = DialectFactory.getDialectType(_session.getMetaData());
         final Connection con = _session.getSQLConnection().getConnection();
         FileExportProgressManager fileExportProgressManager = new FileExportProgressManager(_session, sqlToWriteToFile, () -> file);

         // Execution will stop at this point displaying a modal progress frame.
         fileExportProgressManager.getOrCreateProgressCallback(() -> onModalProgressDialogIsDisplaying(prefs, file, sqlToWriteToFile, con, dialectType, fileExportProgressManager));

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onModalProgressDialogIsDisplaying(TableExportPreferences prefs, File file, String sqlToWriteToFile, Connection con, DialectType dialectType, FileExportProgressManager fileExportProgressManager)
   {
      _executorService.submit(() -> doWriteFile(prefs, file, sqlToWriteToFile, con, dialectType, fileExportProgressManager));
   }

   private void doWriteFile(TableExportPreferences prefs, File file, String sqlToWriteToFile, Connection con, DialectType dialectType, FileExportProgressManager fileExportProgressManager)
   {
      try(Statement stat = SQLUtilities.createStatementForStreamingResults(con, dialectType))
      {
         ExportFileWriter.writeFile(new ResultSetExportData(stat, sqlToWriteToFile ,dialectType), prefs, fileExportProgressManager.getOrCreateProgressCallback());
         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("ExportToFileHandler.wrote.file", file.getPath()));
      }
      catch (Throwable e)
      {
         s_log.error(e);
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ExportToFileHandler.error.writing.file", file.getPath(), e));

         _abortExecution = _session.getProperties().getAbortOnError();
      }
      finally
      {
         fileExportProgressManager.hideProgressMonitor();
      }
   }

}
