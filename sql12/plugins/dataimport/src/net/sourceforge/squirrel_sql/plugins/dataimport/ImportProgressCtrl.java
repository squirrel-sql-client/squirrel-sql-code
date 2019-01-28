package net.sourceforge.squirrel_sql.plugins.dataimport;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.UnsupportedFormatException;

import javax.swing.Timer;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

   public void failedWithSQLException(SQLException sqle, PreparedStatement stmt)
   {
      GUIUtils.processOnSwingEventThread(() -> close());

      EDTMessageBoxUtil.showMessageDialogOnEDT(
            stringMgr.getString("ImportDataIntoTableExecutor.sqlException", sqle.getMessage(), Integer.toString(_currentRow)),
            stringMgr.getString("ImportDataIntoTableExecutor.error"));


      String query = stmt == null ? "null" : stmt.toString();
      log.error("Failing query: " + query);
      log.error("Failing line in CVS file: " + _currentRow);
      log.error("Database error", sqle);
   }


   public void failedWithIoException(IOException ioe)
   {
      GUIUtils.processOnSwingEventThread(() -> close());

      EDTMessageBoxUtil.showMessageDialogOnEDT(
            stringMgr.getString("ImportDataIntoTableExecutor.ioException", ioe.getMessage(), _currentRow),
            stringMgr.getString("ImportDataIntoTableExecutor.error"));

      log.error("Error while reading file", ioe);

   }

   public void failedWithUnsupportedFormatException(UnsupportedFormatException ufe)
   {
      GUIUtils.processOnSwingEventThread(() -> close());

      EDTMessageBoxUtil.showMessageDialogOnEDT(
            stringMgr.getString("ImportDataIntoTableExecutor.UnsupportedFormatException", ufe.getMessage(), _currentRow),
            stringMgr.getString("ImportDataIntoTableExecutor.error"));

      log.error("Unsupported format.", ufe);
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
