package net.sourceforge.squirrel_sql.plugins.dbcopy.actions;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.*;

public class EditPasteTableNameCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(EditPasteTableNameCtrl.class);

   private String _tableName;
   private String _whereClause;
   private final EditPasteTableNameDialog _dlg;


   public EditPasteTableNameCtrl(Frame owner, String destTableName)
   {
      _dlg = new EditPasteTableNameDialog(owner, destTableName);
      _dlg.btnOK.addActionListener(e -> onOK());
      _dlg.btnCancel.addActionListener(e -> onCancel());

      GUIUtils.forceFocus(_dlg.txtTableName);

      GUIUtils.centerWithinParent(_dlg);

      _dlg.setVisible(true);

   }

   private void onCancel()
   {
      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void onOK()
   {
      if(StringUtilities.isEmpty(_dlg.txtTableName.getText(), true))
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("EditPasteTableNameDlg.TableNameEmpty"));
         return;
      }

      _tableName = _dlg.txtTableName.getText();


      if(false == StringUtilities.isEmpty(_dlg.txtWhere.getText(), true))
      {
         if(false == _dlg.txtWhere.getText().trim().toUpperCase().startsWith("WHERE"))
         {
            _whereClause = "WHERE " + _dlg.txtWhere.getText();
         }
         else
         {
            _whereClause = _dlg.txtWhere.getText();
         }
      }

      close();
   }



   public String getTableName()
   {
      return _tableName;
   }

   public String getWhereClause()
   {
      return _whereClause;
   }
}
