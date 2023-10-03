package net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.scriptbuilder.FileScriptBuilder;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class CreateInsertStatementsFileOfCurrentSQLAction extends SquirrelAction implements ISQLPanelAction
{
   private static final String PREF_LAST_FILE_FILTER = "sqlscript.CreateDataFileOfCurrentSQLAction.last.file";


   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CreateInsertStatementsFileOfCurrentSQLAction.class);

   private ISQLPanelAPI _sqlPanel;

   public CreateInsertStatementsFileOfCurrentSQLAction()
   {
      super(Main.getApplication(), Main.getApplication().getResources());
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (_sqlPanel == null)
      {
         return;
      }

      final String lastFileString = Props.getString(PREF_LAST_FILE_FILTER, null);

      File lastFile = new File(System.getProperty("user.home"));

      if (false == StringUtilities.isEmpty(lastFileString, true))
      {
         File buf = new File(lastFileString);

         if(buf.getParentFile().exists())
         {
            lastFile = buf;
         }
      }

      JFileChooser fileChooser;

      if (lastFile.isDirectory())
      {
         fileChooser = new JFileChooser(lastFile);
      }
      else
      {
         fileChooser = new JFileChooser(lastFile.getParentFile());
         fileChooser.setSelectedFile(lastFile);
      }

      if (JFileChooser.APPROVE_OPTION != fileChooser.showSaveDialog(_sqlPanel.getOwningFrame()))
      {
         return;
      }

      File selectedFile = fileChooser.getSelectedFile();

      if(selectedFile.exists())
      {
         final String msg = s_stringMgr.getString("CreateDataFileOfCurrentSQLAction.file.exists", selectedFile.getAbsolutePath());

         if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_sqlPanel.getOwningFrame(), msg))
         {
            return;
         }
      }

      Props.putString(PREF_LAST_FILE_FILTER, selectedFile.getAbsolutePath());

      FileScriptBuilder sbRows = new FileScriptBuilder(selectedFile);
      new CreateInsertScriptOfCurrentSQLCommand(_sqlPanel.getSession()).generateInserts(sbRows, () -> onScriptFinished(sbRows));

   }

   private void onScriptFinished(FileScriptBuilder sbRows)
   {
      sbRows.flush();
      Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("CreateDataFileOfCurrentSQLAction.wrote.insert.statements", sbRows.getFileName()));
   }

   public void setSQLPanel(ISQLPanelAPI sqlPanel)
   {
      _sqlPanel = sqlPanel;
      setEnabled(null != _sqlPanel);
   }
}
