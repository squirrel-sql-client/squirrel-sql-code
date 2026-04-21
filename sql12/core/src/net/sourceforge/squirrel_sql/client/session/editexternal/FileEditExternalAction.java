package net.sourceforge.squirrel_sql.client.session.editexternal;

import java.awt.event.ActionEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.SwingUtilities;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelEvent;
import net.sourceforge.squirrel_sql.client.session.filemanager.FileReloadInfo;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

public class FileEditExternalAction extends SquirrelAction implements ISQLPanelAction
{
   private static ILogger s_log = LoggerController.createLogger(FileEditExternalAction.class);

   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(FileEditExternalAction.class);

   private ISQLPanelAPI _sqlPanelApi;

   @Override
   public void actionPerformed(ActionEvent e)
   {
      ISQLPanelAPI currentSQLPanelAPI = _sqlPanelApi;

      doActionPerformed(currentSQLPanelAPI);
   }

   /**
    * @param currentSQLPanelAPI Using this parameter instead of the member {@link #_sqlPanelApi} ensures listeners use the right version.
    */
   private void doActionPerformed(ISQLPanelAPI currentSQLPanelAPI)
   {
      File fileToEditExternally = null;

      try
      {
         // emacs +4:11 /home/gerd/work/java/squirrel/testsqls/fucksql.sql
         if(false == _sqlPanelApi.getFileHandler().fileSave())
         {
            String msg = s_stringMgr.getString("FileEditExternalAction.failed.to.save.file.for.external.edit");
            Main.getApplication().getMessageHandler().showErrorMessage(msg);
            return;
         }

         EditFileExternallyInitCtrl ctrl = new EditFileExternallyInitCtrl(GUIUtils.getOwningFrame(currentSQLPanelAPI.getSQLEntryPanel().getTextComponent()));

         if(false == ctrl.isOk())
         {
            return;
         }

         fileToEditExternally = currentSQLPanelAPI.getFileHandler().getFile();
         Main.getApplication().getFileNotifier().watchFileCustom(fileToEditExternally, ctrl.getDelay(), f -> onFileChanged(f));

         String cliCommand = ctrl.getCliCommand();

         int caretLineNumber = currentSQLPanelAPI.getSQLEntryPanel().getCaretLineNumber() + (ctrl.isLineNumberingStartsAtZero() ? 0 : 1);
         int caretLinePosition = currentSQLPanelAPI.getSQLEntryPanel().getCaretLinePosition() + (ctrl.isLineNumberingStartsAtZero() ? 0 : 1);
         int caretPosition = currentSQLPanelAPI.getSQLEntryPanel().getCaretPosition() + (ctrl.isLineNumberingStartsAtZero() ? 0 : 1);

         cliCommand= StringUtils.replace(cliCommand, "@line", "" + caretLineNumber);
         cliCommand= StringUtils.replace(cliCommand, "@col", "" + caretLinePosition);
         cliCommand= StringUtils.replace(cliCommand, "@pos", "" + caretPosition);
         cliCommand= StringUtils.replace(cliCommand, "@file", fileToEditExternally.getAbsolutePath());

         Date externalEditingStart = new Date();
         String editExternalStartMsg = s_stringMgr.getString("FileEditExternalAction.start.watching.external.editing",
                                                             fileToEditExternally.getAbsolutePath(),
                                                             cliCommand,
                                                             new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(externalEditingStart)
         );

         Main.getApplication().getMessageHandler().showMessage(editExternalStartMsg);

         startExternalEditorProcessAndWaitInThread(cliCommand, fileToEditExternally, externalEditingStart, currentSQLPanelAPI);

         File finalFileToEditExternally = fileToEditExternally;
         currentSQLPanelAPI.addSQLPanelListener(new ISQLPanelAdapter()
         {
            @Override
            public void sqlEntryAreaClosed(SQLPanelEvent evt)
            {
               currentSQLPanelAPI.removeSQLPanelListener(this);
               finishEditingFileExternally(finalFileToEditExternally, externalEditingStart, null, ExternalFileEditingState.SQUIRREL_EDITOR_CLOSED);
            }
         });
      }
      catch(Exception ex)
      {
         Main.getApplication().getFileNotifier().unwatchFileCustom(fileToEditExternally);
         throw Utilities.wrapRuntime(ex);
      }
   }

   private void onFileChanged(File file)
   {
      _sqlPanelApi.getFileHandler().fileReload(new FileReloadInfo(file, false));
      String msg = s_stringMgr.getString("FileEditExternalAction.reloading.file.automatically.on.external.change", file.getAbsolutePath());
      s_log.info(msg);
   }

   private void finishEditingFileExternally(File fileEditedExternally,
                                            Date externalEditingStart,
                                            ISQLPanelAPI sqlPanelAPIForFinalReloadAfterExternalProcessFinished,
                                            ExternalFileEditingState externalFileEditingState)
   {
      Main.getApplication().getFileNotifier().unwatchFileCustom(fileEditedExternally);

      Date externalEditingStop = new Date();
      String editExternalStopMsg = s_stringMgr.getString("FileEditExternalAction.stop.watching.external.editing",
                                                         fileEditedExternally.getAbsolutePath(),
                                                         new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(externalEditingStop),
                                                         externalEditingStop.getTime() - externalEditingStart.getTime(),
                                                         externalFileEditingState.name());


      Main.getApplication().getMessageHandler().showMessage(editExternalStopMsg);

      if(null != sqlPanelAPIForFinalReloadAfterExternalProcessFinished)
      {
         sqlPanelAPIForFinalReloadAfterExternalProcessFinished.getFileHandler().fileReload(new FileReloadInfo(fileEditedExternally, false));
      }
   }


   private void startExternalEditorProcessAndWaitInThread(String cliCommand, File fileToEditExternally, Date externalEditingStart, ISQLPanelAPI currentSQLPanelAPI)
   {
      Main.getApplication().getThreadPool().addTask(() ->
                                                    {
                                                       ExternalFileEditingState externalFileEditingState = ExternalFileEditingState.SUCCESS;
                                                       try
                                                       {
                                                          //ProcessBuilder pb = new ProcessBuilder(cliCommand);
                                                          Process process = Runtime.getRuntime().exec(cliCommand);;

                                                          process.waitFor(); // blocks until process exits

                                                          s_log.info("External editor process started with command \"%s\" exited.".formatted(cliCommand));
                                                       }
                                                       catch(Exception e)
                                                       {
                                                          s_log.error("Error executing command: \"%s\"   ".formatted(cliCommand), e);
                                                          externalFileEditingState = ExternalFileEditingState.ERROR;
                                                       }

                                                       ExternalFileEditingState finalExternalFileEditingState = externalFileEditingState;
                                                       SwingUtilities.invokeLater(() -> finishEditingFileExternally(fileToEditExternally, externalEditingStart, currentSQLPanelAPI, finalExternalFileEditingState));
                                                    }
      );
   }


   @Override
   public void setSQLPanel(ISQLPanelAPI sqlPanelApi)
   {
      setEnabled(null != sqlPanelApi);
      _sqlPanelApi = sqlPanelApi;
   }
}
