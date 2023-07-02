package net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions;

import net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui.JMeldCore;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.*;
import java.nio.file.Path;

public class CompareToClipboardCtrl
{
   private final CompareToClipboardDlg _dialog;
   private String _textToSave = null;
   public CompareToClipboardCtrl(Window owningFrame, Path leftClipboardTempFile, Path rightEditorTextTempFile, String title, boolean allowSaveToSqlEditor)
   {
      JMeldCore meldCore = new JMeldCore(true);
      try
      {
         if (allowSaveToSqlEditor)
         {
            meldCore.executeDiff(leftClipboardTempFile.toFile().getAbsolutePath(), rightEditorTextTempFile.toFile().getAbsolutePath(), null, savedText -> _textToSave = savedText);
         }
         else
         {
            meldCore.executeDiff(leftClipboardTempFile.toFile().getAbsolutePath(), rightEditorTextTempFile.toFile().getAbsolutePath(), null, null);
         }

         _dialog = new CompareToClipboardDlg(owningFrame, meldCore.getConfigurableMeldPanel(), title);


         GUIUtils.enableCloseByEscape(_dialog);
         GUIUtils.initLocation(_dialog, 600, 600);

         _dialog.setVisible(true);
      }
      finally
      {
         meldCore.cleanMeldPanel();
      }
   }

   public String getTextToSave()
   {
      return _textToSave;
   }
}
