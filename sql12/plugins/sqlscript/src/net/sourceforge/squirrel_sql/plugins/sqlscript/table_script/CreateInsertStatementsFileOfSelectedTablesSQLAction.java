package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.fw.resources.IResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.scriptbuilder.FileScriptBuilder;

import java.awt.event.ActionEvent;

public class CreateInsertStatementsFileOfSelectedTablesSQLAction extends SquirrelAction implements IObjectTreeAction
{
   private static final String PREF_LAST_FILE_FILTER = "sqlscript.CreateDataFileOfCurrentSQLAction.last.file";


   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CreateInsertStatementsFileOfSelectedTablesSQLAction.class);

   private IObjectTreeAPI _objectTreeAPI;

   public CreateInsertStatementsFileOfSelectedTablesSQLAction(IResources resources)
   {
      super(Main.getApplication(), resources);
   }

   public void actionPerformed(ActionEvent evt)
   {
      System.out.println("CreateInsertStatementsFileOfSelectedTablesSQLAction.actionPerformed " + _objectTreeAPI.getSelectedTables());

      if (_objectTreeAPI == null)
      {
         return;
      }

      //generateInsertScriptForSelectedTables();
   }

   //private void generateInsertScriptForSelectedTables()
   //{
   //   final String lastFileString = Props.getString(PREF_LAST_FILE_FILTER, null);
   //
   //   File lastFile = new File(System.getProperty("user.home"));
   //
   //   if (false == StringUtilities.isEmpty(lastFileString, true))
   //   {
   //      File buf = new File(lastFileString);
   //
   //      if(buf.getParentFile().exists())
   //      {
   //         lastFile = buf;
   //      }
   //   }
   //
   //   JFileChooser fileChooser;
   //
   //   if (lastFile.isDirectory())
   //   {
   //      fileChooser = new JFileChooser(lastFile);
   //   }
   //   else
   //   {
   //      fileChooser = new JFileChooser(lastFile.getParentFile());
   //      fileChooser.setSelectedFile(lastFile);
   //   }
   //
   //   if (JFileChooser.APPROVE_OPTION != fileChooser.showSaveDialog(_sqlPanel.getOwningFrame()))
   //   {
   //      return;
   //   }
   //
   //   File selectedFile = fileChooser.getSelectedFile();
   //
   //   if(selectedFile.exists())
   //   {
   //      final String msg = s_stringMgr.getString("CreateDataFileOfCurrentSQLAction.file.exists", selectedFile.getAbsolutePath());
   //
   //      if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_sqlPanel.getOwningFrame(), msg))
   //      {
   //         return;
   //      }
   //   }
   //
   //   Props.putString(PREF_LAST_FILE_FILTER, selectedFile.getAbsolutePath());
   //
   //   FileScriptBuilder sbRows = new FileScriptBuilder(selectedFile);
   //   new CreateInsertScriptOfCurrentSQLCommand(_sqlPanel.getSession()).generateInserts(sbRows, () -> onScriptFinished(sbRows));
   //}

   private void onScriptFinished(FileScriptBuilder sbRows)
   {
      sbRows.flush();
      Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("CreateDataFileOfCurrentSQLAction.wrote.insert.statements", sbRows.getFileName()));
   }

   @Override
   public void setObjectTree(IObjectTreeAPI objectTreeAPI)
   {
      _objectTreeAPI = objectTreeAPI;
      setEnabled(null != _objectTreeAPI);
   }
}
