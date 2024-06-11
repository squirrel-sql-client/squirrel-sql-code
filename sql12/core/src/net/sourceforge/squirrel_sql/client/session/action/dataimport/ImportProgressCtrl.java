package net.sourceforge.squirrel_sql.client.session.action.dataimport;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.Timer;

import net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.UnsupportedFormatException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ImportProgressCtrl
{
   private static final StringManager stringMgr = StringManagerFactory.getStringManager(ImportProgressCtrl.class);

   private final static ILogger log = LoggerController.createLogger(ImportProgressCtrl.class);
   private final ImportProgressDialog _dlg;
   private ITableInfo _table;

   private volatile int _currentRow;
   private volatile boolean _canceled;

   private final Timer _timer;

   public ImportProgressCtrl(ITableInfo table)
   {
      _dlg = new ImportProgressDialog(table.getSimpleName());
      _table = table;

      _timer = new Timer(300, e -> _dlg.txtNumberOfRowsImported.setText("" + _currentRow));
      _timer.setRepeats(true);
      _timer.start();

      _dlg.btnCancel.addActionListener(e -> onCanceled());

      GUIUtils.centerWithinParent(_dlg);

      _dlg.setVisible(true);
   }

   private void onCanceled()
   {
      _canceled = true;
   }

   public void setCurrentRow(int currentRow)
   {
      _currentRow = currentRow;
   }

   public void finishedSuccessFully()
   {
      GUIUtils.processOnSwingEventThread(() -> close());

      EDTMessageBoxUtil.showMessageDialogOnEDT(stringMgr.getString("ImportDataIntoTableExecutor.inserted", _currentRow, _table.getSimpleName()));
   }

   public void failedWithSQLException(SQLException sqle, PreparedStatement stmt, StringBuffer insertSQL)
   {
      String query = stmt == null ? "null" : stmt.toString();
      log.error("Failing query: " + query);
      log.error("Failing INSERT statement: " + insertSQL);
      log.error("Failing line in CVS file: " + _currentRow);
      log.error("Database error", sqle);


      GUIUtils.processOnSwingEventThread(() -> close());

      EDTMessageBoxUtil.showMessageDialogOnEDT(
                                stringMgr.getString("ImportDataIntoTableExecutor.sqlException", Utilities.getExceptionStringSave(sqle),Integer.toString(_currentRow)),
                                stringMgr.getString("ImportDataIntoTableExecutor.error"));


   }


   public void failedWithIoException(IOException ioe)
   {
      log.error("Error while reading file", ioe);

      GUIUtils.processOnSwingEventThread(() -> close());

      EDTMessageBoxUtil.showMessageDialogOnEDT(
            stringMgr.getString("ImportDataIntoTableExecutor.ioException", Utilities.getExceptionStringSave(ioe), _currentRow),
            stringMgr.getString("ImportDataIntoTableExecutor.error"));


   }

   public void failedWithUnsupportedFormatException(UnsupportedFormatException ufe)
   {
      log.error("Unsupported format.", ufe);

      GUIUtils.processOnSwingEventThread(() -> close());

      EDTMessageBoxUtil.showMessageDialogOnEDT(
            stringMgr.getString("ImportDataIntoTableExecutor.UnsupportedFormatException", Utilities.getExceptionStringSave(ufe), _currentRow),
            stringMgr.getString("ImportDataIntoTableExecutor.error"));

   }

   public void failedWithUnknownException(Throwable t)
   {
      log.error("Unsupported format.", t);

      GUIUtils.processOnSwingEventThread(() -> close());

      EDTMessageBoxUtil.showMessageDialogOnEDT(
            stringMgr.getString("ImportDataIntoTableExecutor.UnknownException", Utilities.getExceptionStringSave(t), _currentRow),
            stringMgr.getString("ImportDataIntoTableExecutor.error"));

   }

   public boolean isCanceled()
   {
      return _canceled;
   }


   private void close()
   {
      _timer.stop();
      _dlg.setVisible(false);
      _dlg.dispose();
   }

}
