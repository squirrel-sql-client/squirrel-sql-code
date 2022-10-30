package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.gui.EditableComboBoxHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.lang3.StringUtils;

import java.awt.Window;

public class EditExcelTabOrFileNameCtrl
{
   private final EditExcelTabOrFileNameDlg _dlg;
   private final EditableComboBoxHandler _editableComboBoxHandler;
   private String _previousSqlResultName;
   private boolean _ok;

   public EditExcelTabOrFileNameCtrl(Window owningWindow, String sqlResultName)
   {
      _dlg = new EditExcelTabOrFileNameDlg(owningWindow);
      _previousSqlResultName = sqlResultName;

      _editableComboBoxHandler = new EditableComboBoxHandler(_dlg.cboSqlResultName, "fileExport.EditExcelTabOrFileNameCtrl_", 10, _previousSqlResultName);
      _editableComboBoxHandler.fillComboBox();

      _dlg.btnOk.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> close());

      GUIUtils.enableCloseByEscape(_dlg);
      GUIUtils.initLocation(_dlg, 200, 100);

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

   public String getNewSqlResultName()
   {
      if(StringUtilities.isEmpty(_editableComboBoxHandler.getItem(), true))
      {
         return _previousSqlResultName;
      }

      final String ret = StringUtilities.fileNameNormalize(_editableComboBoxHandler.getItem());

      if(false == StringUtils.equals(ret, _previousSqlResultName))
      {
         _editableComboBoxHandler.addToComboList(ret);
      }

      return ret;

   }
}
