package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.*;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.SQLExecutionException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.ResultSet;
import java.sql.SQLWarning;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * This class is the handler for the execution of sql against the SQLExecuterPanel
 */
class SQLExecutionHandler implements ISQLExecuterHandler
{
   private static final ILogger s_log =
        LoggerController.createLogger(SQLExecutionHandler.class);

   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(SQLExecutionHandler.class);


   private CancelPanelCtrl _cancelPanelCtrl;
   private SQLExecuterTask _executer;
   private ISession _session;
   private ISQLExecutionHandlerListener _executionHandlerListener;

   private static enum SQLType
   {
      INSERT, SELECT, UPDATE, DELETE, UNKNOWN
   }



   /**
    * Hold onto the current ResultDataSet so if the execution is
    * cancelled then this can be cancelled.
    */
   private ResultSetDataSet rsds = null;

   private String sqlToBeExecuted = null;
   private SQLType sqlType = null;
   private IResultTab _resultTabToReplace;
   private boolean _largeScript = false;
   private double _scriptTotalTime = 0;
   private double _scriptQueryTime = 0;
   private double _scriptOutptutTime = 0;
   private int _scriptRowsInserted = 0;
   private int _scriptRowsSelected = 0;
   private int _scriptRowsUpdated = 0;
   private int _scriptRowsDeleted = 0;

   public SQLExecutionHandler(IResultTab resultTabToReplace,
                              ISession session,
                              String sql,
                              ISQLExecutionHandlerListener executionHandlerListener,
                              ISQLExecutionListener[] executionListeners)
   {
      _session = session;
      _executionHandlerListener = executionHandlerListener;


      _executer = new SQLExecuterTask(_session, sql, this, executionListeners);
      SquirrelPreferences prefs = _session.getApplication().getSquirrelPreferences();

      if (prefs.getLargeScriptStmtCount() > 0
            && _executer.getQueryCount() > prefs.getLargeScriptStmtCount())
      {
         _executer.setExecutionListeners(new ISQLExecutionListener[0]);
         setLargeScript(true);
      }



      _resultTabToReplace = resultTabToReplace;
      CancelPanelListener listener = new CancelPanelListener()
      {
         @Override
         public void cancelRequested()
         {
            onCancelRequested();
         }

         @Override
         public void closeRquested()
         {
            _executionHandlerListener.removeCancelPanel(_cancelPanelCtrl, null);
         }
      };

      _cancelPanelCtrl = new CancelPanelCtrl(listener, session);
      _executionHandlerListener.setCancelPanel(_cancelPanelCtrl);




      _session.getApplication().getThreadPool().addTask(_executer);
   }

   private void onCancelRequested()
   {
      try
      {
         if (_executer != null)
         {
            _executer.cancel();
         }
      }
      catch (Throwable th)
      {
         s_log.error("Error occured cancelling SQL", th);
      }
   }

   /**
    * Set whether or not the script is large.  If the script is large, then
    * do some performance optimizations with the GUI so that it remains
    * responsive.  If the UI is not responsive, then the user is not able
    * to see what is happening, nor are they able to control it (cancelling
    * becomes ineffective)
    *
    * @param aBoolean whether or not the script is large.
    */
   public void setLargeScript(boolean aBoolean)
   {
      _largeScript = aBoolean;
   }

   /**
    * Determines whether or not the current statement SQL should be rendered.
    * Since too many statements can cause the UI to stop rendering the
    * statements, we back off rendering after many statements so that the UI
    * can continue to provide feedback to the user.
    *
    * @param current
    * @param total
    * @return
    */
   private boolean shouldRenderSQL(int current, int total)
   {
      if (!_largeScript)
      {
         return true;
      }
      boolean result = true;
      // Back-off a bit after a hundred updates to allow the UI to update
      if (total > 200 && current > 100 && current % 10 != 0)
      {
         result = false;
      }
      if (total > 1000 && current > 500 && current % 50 != 0)
      {
         result = false;
      }
      if (total > 2000 && current > 1000 && current % 100 != 0)
      {
         result = false;
      }
      return result;
   }

   public void sqlToBeExecuted(final String sql)
   {
      _cancelPanelCtrl.incCurrentQueryIndex();
      int currentStmtCount = _cancelPanelCtrl.getCurrentQueryIndex();
      if (!shouldRenderSQL(currentStmtCount, _cancelPanelCtrl.getTotalCount()))
      {
         return;
      }
      final String cleanSQL = StringUtilities.cleanString(sql);
      sqlToBeExecuted = cleanSQL;
      sqlType = getSQLType(cleanSQL);

      _cancelPanelCtrl.setSQL(sqlToBeExecuted);

      // i18n[SQLResultExecuterPanel.execStatus=Executing SQL...]
      String status = s_stringMgr.getString("SQLResultExecuterPanel.execStatus");
      _cancelPanelCtrl.setStatusLabel(status);
   }

   private SQLType getSQLType(String sql)
   {
      SQLType result = SQLType.UNKNOWN;
      if (sql.toLowerCase().startsWith("insert"))
      {
         result = SQLType.INSERT;
      }
      if (sql.toLowerCase().startsWith("update"))
      {
         result = SQLType.UPDATE;
      }
      if (sql.toLowerCase().startsWith("select"))
      {
         result = SQLType.SELECT;
      }
      if (sql.toLowerCase().startsWith("delete"))
      {
         result = SQLType.DELETE;
      }
      return result;
   }


   /**
    * This will - depending on the size of the script - print a message
    * indicating the time that it took to execute one or more queries.
    * When executing a large script (as defined by the user, but default is
    * > 200 statements) we don't want to keep sending messages to the
    * message panel, otherwise the UI will get behind and slow the execution
    * of the script and prevent the user from cancelling the operation.  So
    * this method will track the total time when executing a large script,
    * otherwise for small scripts it puts out a message for every statemselect * from employeeent.
    */
   public void sqlExecutionComplete(final SQLExecutionInfo exInfo,
                                    final int processedStatementCount,
                                    final int statementCount)
   {
      final Integer numberResultRowsRead = exInfo.getNumberResultRowsRead();

      if (_largeScript)
      {
         double executionLength = ((double) exInfo.getSQLExecutionElapsedMillis()) / 1000;
         double outputLength = ((double) exInfo.getResultsProcessingElapsedMillis()) / 1000;
         double totalLength = executionLength + outputLength;

         // Track the time in aggregate for the script.
         _scriptQueryTime += executionLength;
         _scriptOutptutTime += outputLength;
         _scriptTotalTime += totalLength;

         // When we get to the last statement, if the script is large,
         // show the user the total execution time.
         if (statementCount == processedStatementCount)
         {
            printScriptExecDetails(statementCount,
                  _scriptQueryTime,
                  _scriptOutptutTime,
                  _scriptTotalTime);
         }
      }
      else
      {
         allProcessingComplete(exInfo, processedStatementCount, statementCount, numberResultRowsRead);
      }
   }

   private void allProcessingComplete(SQLExecutionInfo exInfo, int processedStatementCount, int statementCount, Integer numberResultRowsRead)
   {
      double executionLength = ((double) exInfo.getSQLExecutionElapsedMillis()) / 1000;
      double outputLength = ((double) exInfo.getResultsProcessingElapsedMillis()) / 1000;
      double totalLength = executionLength + outputLength;

      final NumberFormat nbrFmt = NumberFormat.getNumberInstance();

      Object[] args = new Object[]{
            Integer.valueOf(processedStatementCount),
            Integer.valueOf(statementCount),
            numberResultRowsRead == null ? 0 : numberResultRowsRead,
            nbrFmt.format(totalLength),
            nbrFmt.format(executionLength),
            nbrFmt.format(outputLength)
      };

      //i18n[SQLResultExecuterPanel.queryStatistics=Query {0} of {1}
      //elapsed time (seconds) - Total: {2}, SQL query: {3},
      //Building output: {4}]
      String stats = s_stringMgr.getString("SQLResultExecuterPanel.queryStatistics", args);

      _session.showMessage(stats);
   }

   private void printScriptExecDetails(int statementCount,
                                       double executionLength,
                                       double outputLength,
                                       double totalLength)
   {
      final NumberFormat nbrFmt = NumberFormat.getNumberInstance();

      Object[] args = new Object[]{
            Integer.valueOf(statementCount),
            nbrFmt.format(totalLength),
            nbrFmt.format(executionLength),
            nbrFmt.format(outputLength)
      };

      //i18n[SQLResultExecuterPanel.scriptQueryStatistics=Executed {0}
      //queries; elapsed time (seconds) - Total: {1}, SQL query: {2},
      //Building output: {3}]
      String stats = s_stringMgr.getString("SQLResultExecuterPanel.scriptQueryStatistics", args);

      String[] counts =
            new String[]{Integer.toString(_scriptRowsInserted),
                  Integer.toString(_scriptRowsSelected),
                  Integer.toString(_scriptRowsUpdated),
                  Integer.toString(_scriptRowsDeleted)};

      //i18n[SQLResultExecuterPanel.scriptStmtCounts=Row update
      //counts: {0} Inserts, {1} Selects, {2} Updates, {3} Deletes
      String msg =
            s_stringMgr.getString("SQLResultExecuterPanel.scriptStmtCounts", counts);

      _session.showMessage(msg);
      _session.showMessage(stats);
   }

   public void sqlExecutionCancelled()
   {
      if (rsds != null)
      {
         rsds.cancelProcessing();
      }
      // i18n[SQLResultExecuterPanel.canceleRequested=Query execution cancel requested by user.]
//          String canc =
//          s_stringMgr.getString("SQLResultExecuterPanel.canceleRequested");
//          getSession().getMessageHandler().showMessage(canc);
   }

   public void sqlDataUpdated(int updateCount)
   {

      Integer count = Integer.valueOf(updateCount);
      String msg = "";

      switch (sqlType)
      {
         case INSERT:
            if (_largeScript)
            {
               _scriptRowsInserted++;
            }
            else
            {
               // i18n[SQLResultExecuterPanel.rowsUpdated={0} Row(s) Inserted]
               msg = s_stringMgr.getString("SQLResultExecuterPanel.rowsInserted", count);
            }
            break;
         case SELECT:
            if (_largeScript)
            {
               _scriptRowsSelected++;
            }
            else
            {
               // i18n[SQLResultExecuterPanel.rowsSelected={0} Row(s) Selected]
               msg = s_stringMgr.getString("SQLResultExecuterPanel.rowsSelected", count);
            }
            break;
         case UPDATE:
            if (_largeScript)
            {
               _scriptRowsUpdated++;
            }
            else
            {
               // i18n[SQLResultExecuterPanel.rowsUpdated={0} Row(s) Updated]
               msg = s_stringMgr.getString("SQLResultExecuterPanel.rowsUpdated", count);
            }
            break;
         case DELETE:
            if (_largeScript)
            {
               _scriptRowsDeleted++;
            }
            else
            {
               // i18n[SQLResultExecuterPanel.rowsDeleted={0} Row(s) Deleted]
               msg = s_stringMgr.getString("SQLResultExecuterPanel.rowsDeleted", count);
            }
            break;
      }
      if (_largeScript)
      {
         return;
      }
      _session.showMessage(msg);
   }

   public void sqlResultSetAvailable(ResultSet rs, SQLExecutionInfo info, IDataSetUpdateableTableModel model)
         throws DataSetException
   {
	   // i18n[SQLResultExecuterPanel.outputStatus=Building output...]
	   String outputStatus = s_stringMgr.getString("SQLResultExecuterPanel.outputStatus");

	   _cancelPanelCtrl.setStatusLabel(outputStatus);
	   rsds = new ResultSetDataSet();
	   try {

		   SessionProperties props = _session.getProperties();

		   ResultSetMetaDataDataSet rsmdds = null;
		   if (props.getShowResultsMetaData())
		   {
			   rsmdds = new ResultSetMetaDataDataSet(rs);
		   }
		   DialectType dialectType =
				   DialectFactory.getDialectType(_session.getMetaData());

         // rsds.setContentsTabResultSet() reads the result set. So results processing on the DB is over
         // and this time is measured. None is interested in the time that it takes us to render Swing tables ...
		   info.resultsProcessingComplete(rsds.setContentsTabResultSet(rs, null, dialectType));

		   _executionHandlerListener.addResultsTab(info, rsds, rsmdds, model, _resultTabToReplace);

      }finally{
    	  /*
    	   * Make sure, that in any case, even when a exception occurs, the rsds is set to null, so that
    	   * the GC can clean them.
    	   */
    	  rsds = null;
	}

   }

   public void sqlExecutionWarning(SQLWarning warn)
   {
      _session.showMessage(warn);
   }

   public void sqlStatementCount(int statementCount)
   {
      _cancelPanelCtrl.setQueryCount(statementCount);
   }

   public void sqlCloseExecutionHandler(ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement)
   {
      _executionHandlerListener.removeCancelPanel(_cancelPanelCtrl, _resultTabToReplace);

      if (null != sqlExecErrorMsgs && 0 < sqlExecErrorMsgs.size() && _session.getProperties().getShowSQLErrorsInTab())
      {
         _executionHandlerListener.displayErrors(sqlExecErrorMsgs, lastExecutedStatement);
      }

      _executer = null;
   }

   public String sqlExecutionException(Throwable th, String postErrorString)
   {
      SQLExecutionException ex =
            new SQLExecutionException(th, postErrorString);

      String message = _session.formatException(ex);

      _session.showErrorMessage(message);

      if (_session.getProperties().getWriteSQLErrorsToLog())
      {
         s_log.info(message);
      }

      return message;
   }

}
