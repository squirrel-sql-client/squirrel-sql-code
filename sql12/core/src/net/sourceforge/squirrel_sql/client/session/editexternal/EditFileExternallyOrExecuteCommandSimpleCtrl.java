package net.sourceforge.squirrel_sql.client.session.editexternal;

import java.awt.Frame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.sourceforge.squirrel_sql.fw.gui.EditableComboBoxHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

public class EditFileExternallyOrExecuteCommandSimpleCtrl
{
   private static final String EXTERNAL_EDITOR_COMMAND_STRINGS_PREFIX = "EditFileExternallyOrExecuteCommandSimpleCtrl.externalEditor.strings_";

   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(EditFileExternallyOrExecuteCommandSimpleCtrl.class);

   private final EditFileExternallyOrExecuteCommandSimpleDlg _dlg;
   private final EditableComboBoxHandler _externalEditorCommandCboHandler;
   private boolean _ok;


   public EditFileExternallyOrExecuteCommandSimpleCtrl(Frame owningFrame)
   {
      _dlg = new EditFileExternallyOrExecuteCommandSimpleDlg(owningFrame);

      GUIUtils.initLocation(_dlg, 600, 220);
      GUIUtils.enableCloseByEscape(_dlg);


      _externalEditorCommandCboHandler = new EditableComboBoxHandler(_dlg.cboCommand,
                                                                     EXTERNAL_EDITOR_COMMAND_STRINGS_PREFIX,
                                                                     10,
                                                                     null,
                                                                     true);

      if(_externalEditorCommandCboHandler.isEmpty())
      {
         _externalEditorCommandCboHandler.addOrReplaceCurrentItem( "emacs @file");
      }


      _dlg.btnOk.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> close());

      SwingUtilities.invokeLater(() -> _dlg.cboCommand.requestFocus());

      _dlg.setVisible(true);
   }

   private void onOk()
   {
      if(StringUtils.isBlank(_externalEditorCommandCboHandler.getItem()))
      {
         JOptionPane.showConfirmDialog(_dlg, s_stringMgr.getString("EditFileExternallyOrExecuteCommandSimpleCtrl.command.empty"));
         return;
      }

      _externalEditorCommandCboHandler.saveCurrentItem();
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

   public String getCliCommand()
   {
      return _externalEditorCommandCboHandler.getItem();
   }

}
