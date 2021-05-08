package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortFactoryCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.sql.SQLException;
import java.sql.Statement;

public class ProgressAbortFactoryCallbackImpl implements ProgressAbortFactoryCallback
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ProgressAbortFactoryCallbackImpl.class);


   private final ISession _session;
   private final String _sql;
   private final ExportFileProvider _exportFileProvider;
   private final Statement _stmt;
   private ProgressAbortCallback progressDialog;

   public ProgressAbortFactoryCallbackImpl(ISession session, String sql, ExportFileProvider exportFileProvider, Statement stmt)
   {
      _session = session;
      _sql = sql;
      _exportFileProvider = exportFileProvider;
      _stmt = stmt;
   }

   public ProgressAbortCallback getOrCreate()
   {
      return getOrCreate(null);
   }
   public ProgressAbortCallback getOrCreate(DisplayReachedCallBack displayReachedCallBack)
   {
      if (null == progressDialog)
      {
         createProgressAbortDialog(displayReachedCallBack);
      }
      return progressDialog;
   }

   /**
    * Create and show a new  progress monitor with the ability to cancel the task.
    * @param displayReachedCallBack
    */
   private void createProgressAbortDialog(DisplayReachedCallBack displayReachedCallBack)
   {
      GUIUtils.processOnSwingEventThread(() -> showProgressDialog(displayReachedCallBack), true);
   }

   private void showProgressDialog(DisplayReachedCallBack displayReachedCallBack)
   {
      /*
       *  Copied from FormatSQLCommand.
       *  Is there a better way to get the CommentSpec[] ?
       */

      CodeReformator cr = new CodeReformator(CodeReformatorConfigFactory.createConfig(_session));

      String reformatedSQL = cr.reformat(_sql);

      String targetFile = _exportFileProvider.getExportFile().getAbsolutePath();

      // i18n[CreateFileOfCurrentSQLCommand.progress.title=Exporting to a file.]
      String title = s_stringMgr.getString("CreateFileOfCurrentSQLCommand.progress.title", targetFile);
      progressDialog = new ProgressAbortDialog(Main.getApplication().getMainFrame(), title, targetFile, reformatedSQL, 0, () -> onCancel(), displayReachedCallBack);

      progressDialog.setVisible(true);
   }

   private void onCancel()
   {
      /*
       * We need to cancel the statement at this point for the case, that we are waiting for the first rows.
       */
      if (_stmt != null)
      {
         try
         {
            _stmt.cancel();
         }
         catch (SQLException e1)
         {
            // nothing todo
         }
      }
   }

   /**
    * Hide the progress monitor.
    * The progress monitor will not be destroyed.
    */
   public void hideProgressMonitor()
   {
      if (progressDialog != null)
      {
         progressDialog.setVisible(false);
         progressDialog.dispose();
      }
   }

   /**
    * Check, if the user has canceled the task.
    *
    * @return true, if the user has canceled the task, otherwise false.
    */
   public boolean isAborted()
   {
      if (progressDialog != null && progressDialog.isStop())
      {
         return true;
      }
      return false;

   }

}
