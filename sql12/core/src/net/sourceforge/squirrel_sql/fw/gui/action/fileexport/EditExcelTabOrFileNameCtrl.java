package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.EditableComboBoxHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.lang3.StringUtils;

import java.awt.Window;

public class EditExcelTabOrFileNameCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(EditExcelTabOrFileNameCtrl.class);

   private final EditExcelTabOrFileNameDlg _dlg;
   private final EditableComboBoxHandler _editableComboBoxHandler;
   private String _previousSqlResultName;
   private boolean _ok;

   public EditExcelTabOrFileNameCtrl(Window owningWindow, String sqlResultName)
   {
      _dlg = new EditExcelTabOrFileNameDlg(owningWindow);
      _previousSqlResultName = sqlResultName;

      _editableComboBoxHandler = new EditableComboBoxHandler(_dlg.cboSqlResultName, "fileExport.EditExcelTabOrFileNameCtrl_", 10, _previousSqlResultName);

      _dlg.btnOk.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> close());

      GUIUtils.enableCloseByEscape(_dlg);
      GUIUtils.initLocation(_dlg, 500, 130);

      _editableComboBoxHandler.focus();

      _dlg.setVisible(true);
   }

   private void onOk()
   {
      _ok = true;
      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   public boolean isOk()
   {
      return _ok;
   }

   public String getNewSqlResultNameFileNormalized()
   {
      if(StringUtilities.isEmpty(_editableComboBoxHandler.getItem(), true))
      {
         return _previousSqlResultName;
      }

      final String ret = StringUtilities.fileNameNormalize(_editableComboBoxHandler.getItem());

      if(false == StringUtils.equals(ret, _editableComboBoxHandler.getItem()))
      {
         final String msg = s_stringMgr.getString("EditExcelTabOrFileNameCtrl.normalized.to.file.name", _editableComboBoxHandler.getItem(), ret);
         Main.getApplication().getMessageHandler().showMessage(msg);
      }

      if(false == StringUtils.equals(ret, _previousSqlResultName))
      {
         _editableComboBoxHandler.addOrReplaceCurrentItem(ret);
      }

      return ret;

   }
}
