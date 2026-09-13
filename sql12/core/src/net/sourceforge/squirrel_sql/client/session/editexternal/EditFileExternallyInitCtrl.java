package net.sourceforge.squirrel_sql.client.session.editexternal;

import java.awt.Frame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.sourceforge.squirrel_sql.fw.gui.EditableComboBoxHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

public class EditFileExternallyInitCtrl
{

   public static final String PREF_KEY_MILLIS = "EditFileExternallyInitCtrl.millis";
   public static final String PREF_KEY_COMMAND = "EditFileExternallyInitCtrl.command";
   public static final String PREF_KEY_LINE_COL_NUMBERING_STARTS_AT_ZERO = "EditFileExternallyInitCtrl.line.numbering.starts.at.zero";

   private static final String EXTERNAL_EDITOR_COMMAND_STRINGS_PREFIX = "EditFileExternallyInitCtrl.externalEditor.strings_";


   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(EditFileExternallyInitCtrl.class);

   private final EditFileExternallyInitDlg _dlg;
   private final EditableComboBoxHandler _externalEditorCommandCboHandler;
   private boolean _ok;


   public EditFileExternallyInitCtrl(Frame owningFrame)
   {
      _dlg = new EditFileExternallyInitDlg(owningFrame);

      GUIUtils.initLocation(_dlg, 600, 220);
      GUIUtils.enableCloseByEscape(_dlg);

      _dlg.txtMillis.setInt(Props.getInt(PREF_KEY_MILLIS, 500));

      _externalEditorCommandCboHandler = new EditableComboBoxHandler(_dlg.cboCommand,
                                                                     "EditFileExternallyInitCtrl.externalEditor.strings_",
                                                                     10,
                                                                     null,
                                                                     true);

      if(_externalEditorCommandCboHandler.isEmpty())
      {
         _externalEditorCommandCboHandler.addOrReplaceCurrentItem(Props.getString(PREF_KEY_COMMAND, "emacs +@line:@col @file"));
      }


      boolean zeroOrOne = Props.getBoolean(PREF_KEY_LINE_COL_NUMBERING_STARTS_AT_ZERO, true);
      _dlg.radStartsAtZero.setSelected(zeroOrOne);
      _dlg.radStartsAtOne.setSelected(!zeroOrOne);


      _dlg.btnOk.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> close());

      SwingUtilities.invokeLater(() -> _dlg.txtMillis.requestFocus());

      _dlg.setVisible(true);
   }

   private void onOk()
   {
      if(StringUtils.isBlank(_externalEditorCommandCboHandler.getItem()))
      {
         JOptionPane.showConfirmDialog(_dlg, s_stringMgr.getString("EditFileExternallyInitCtrl.command.empty"));
         return;
      }

      Props.putInt(PREF_KEY_MILLIS, _dlg.txtMillis.getInt());
      _externalEditorCommandCboHandler.saveCurrentItem();
      Props.putBoolean(PREF_KEY_LINE_COL_NUMBERING_STARTS_AT_ZERO, _dlg.radStartsAtZero.isSelected());

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

   public int getDelay()
   {
      return _dlg.txtMillis.getInt();
   }

   public boolean isLineNumberingStartsAtZero()
   {
      return _dlg.radStartsAtZero.isSelected();
   }
}
