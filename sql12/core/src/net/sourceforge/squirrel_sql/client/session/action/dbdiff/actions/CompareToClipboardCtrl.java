package net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions;

import net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui.JMeldDiffPresentation;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.*;
import java.nio.file.Path;

public class CompareToClipboardCtrl
{
   private final CompareToClipboardDlg _dialog;
   private String _textToSave = null;
   public CompareToClipboardCtrl(Window owningFrame, Path leftClipboardTempFile, Path rightEditorTextTempFile, String title, boolean allowSaveToSqlEditor)
   {
      JMeldDiffPresentation  diffPresentation = new JMeldDiffPresentation(true, null);
      try
      {
         diffPresentation.executeDiff(leftClipboardTempFile.toFile().getAbsolutePath(), rightEditorTextTempFile.toFile().getAbsolutePath());

         _dialog = new CompareToClipboardDlg(owningFrame, diffPresentation.getConfigurableMeldPanel(), title);


         GUIUtils.enableCloseByEscape(_dialog);
         GUIUtils.initLocation(_dialog, 600, 600);

         _dialog.setVisible(true);
      }
      finally
      {
         diffPresentation.cleanMeldPanel();
      }
   }

   public String getTextToSave()
   {
      return _textToSave;
   }
}
