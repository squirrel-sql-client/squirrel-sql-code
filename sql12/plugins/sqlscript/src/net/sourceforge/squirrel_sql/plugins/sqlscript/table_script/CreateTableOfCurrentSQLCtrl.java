package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;


public class CreateTableOfCurrentSQLCtrl
{
   private CreateTableOfCurrentSQLDialog _dlg;
   private boolean _isOk;
   private static final String PREFS_KEY_LAST_TABLE_NAME = "squirrel_sqlscript_tempSqlResultTable";
   private static final String PREFS_KEY_SCRIPT_ONLY = "squirrel_sqlscript_script_only";
   private static final String PREFS_KEY_DROP_TABLE = "squirrel_sqlscript_drop_table";


   public CreateTableOfCurrentSQLCtrl(ISession session)
   {
      _dlg = new CreateTableOfCurrentSQLDialog(session.getApplication().getMainFrame());

      _dlg.btnOK.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onOK();
         }
      });

      _dlg.btnCancel.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onCancel();
         }
      });


      String tempSqlResultTable = Preferences.userRoot().get(PREFS_KEY_LAST_TABLE_NAME, "tempSqlResultTable");
      boolean dropTable = Preferences.userRoot().getBoolean(PREFS_KEY_DROP_TABLE, false);
      boolean scriptOnly = Preferences.userRoot().getBoolean(PREFS_KEY_SCRIPT_ONLY, true);


      _dlg.txtTableName.setText(tempSqlResultTable);
      _dlg.chkDropTable.setSelected(dropTable);
      _dlg.chkScriptOnly.setSelected(scriptOnly);


      _dlg.setSize(360,160);
      GUIUtils.centerWithinParent(_dlg);

      _dlg.setVisible(true);

   }

   private void onCancel()
   {
      //System.out.println(_dlg.getSize());
      close();
   }

   private void onOK()
   {
      _isOk = true;
      Preferences.userRoot().put(PREFS_KEY_LAST_TABLE_NAME, _dlg.txtTableName.getText());
      Preferences.userRoot().putBoolean(PREFS_KEY_DROP_TABLE, _dlg.chkDropTable.isSelected());
      Preferences.userRoot().putBoolean(PREFS_KEY_SCRIPT_ONLY, _dlg.chkScriptOnly.isSelected());
      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }


   public boolean isOK()
   {
      return _isOk;
   }

   public String getTableName()
   {
      return _dlg.txtTableName.getText();
   }

   public boolean isScriptOnly()
   {
      return _dlg.chkScriptOnly.isSelected();
   }

   public boolean isDropTable()
   {
      return _dlg.chkDropTable.isSelected();
   }
}
