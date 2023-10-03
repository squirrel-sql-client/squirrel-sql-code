package net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.scriptbuilder.FileScriptBuilder;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class CreateInsertStatementsFileOfSelectedTablesSQLAction extends SquirrelAction implements IObjectTreeAction
{
   private static final String PREF_LAST_FILE_FILTER = "sqlscript.CreateDataFileOfCurrentSQLAction.last.file";


   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CreateInsertStatementsFileOfSelectedTablesSQLAction.class);

   private IObjectTreeAPI _objectTreeAPI;

   public CreateInsertStatementsFileOfSelectedTablesSQLAction()
   {
      super(Main.getApplication(), Main.getApplication().getResources());
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (_objectTreeAPI == null)
      {
         return;
      }

      List<ITableInfo> selectedTables = _objectTreeAPI.getSelectedTables();
      if (selectedTables.isEmpty())
      {
         return;
      }

      String selectScript = ScriptUtil.createSelectScriptString(selectedTables.toArray(new IDatabaseObjectInfo[0]), _objectTreeAPI);
      generateInsertScriptForSelectedTables(selectScript);
   }

   private void generateInsertScriptForSelectedTables(String selectScript)
   {
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

      if (JFileChooser.APPROVE_OPTION != fileChooser.showSaveDialog(_objectTreeAPI.getOwningFrame()))
      {
         return;
      }

      File selectedFile = fileChooser.getSelectedFile();

      if(selectedFile.exists())
      {
         final String msg = s_stringMgr.getString("CreateDataFileOfCurrentSQLAction.file.exists", selectedFile.getAbsolutePath());

         if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_objectTreeAPI.getOwningFrame(), msg))
         {
            return;
         }
      }

      Props.putString(PREF_LAST_FILE_FILTER, selectedFile.getAbsolutePath());

      FileScriptBuilder sbRows = new FileScriptBuilder(selectedFile);
      new CreateInsertScriptOfCurrentSQLCommand(_objectTreeAPI.getSession()).generateInserts(selectScript, sbRows, () -> onScriptFinished(sbRows));
   }

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
