package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.awt.Window;

public class EditExcelTabOrFileNameCtrl
{
   private final EditExcelTabOrFileNameDlg _dlg;
   private String _previousSqlResultName;
   private boolean _ok;

   public EditExcelTabOrFileNameCtrl(Window owningWindow, String sqlResultName)
   {
      _dlg = new EditExcelTabOrFileNameDlg(owningWindow);
      _previousSqlResultName = sqlResultName;
      _dlg.txtSqlResultName.setText(sqlResultName);

      _dlg.btnOk.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> close());

      GUIUtils.enableCloseByEscape(_dlg);
      GUIUtils.initLocation(_dlg, 200, 100);

      _dlg.txtSqlResultName.setSelectionStart(0);
      _dlg.txtSqlResultName.setSelectionEnd(_dlg.txtSqlResultName.getText().length());

      GUIUtils.forceFocus(_dlg.txtSqlResultName);

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
      if(StringUtilities.isEmpty(_dlg.txtSqlResultName.getText(), true))
      {
         return _previousSqlResultName;
      }

      return  StringUtilities.fileNameNormalize(_dlg.txtSqlResultName.getText());

   }
}
