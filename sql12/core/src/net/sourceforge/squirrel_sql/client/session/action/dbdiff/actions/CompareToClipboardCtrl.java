package net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions;

import net.sourceforge.squirrel_sql.client.session.action.dbdiff.JMeldPanelHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.*;
import java.nio.file.Path;

public class CompareToClipboardCtrl
{
   private final CompareToClipboardDlg _dialog;
   private String _textToSave = null;
   public CompareToClipboardCtrl(Window owningFrame, Path leftClipboardTempFile, Path rightEditorTextTempFile, String title, boolean allowSaveToSqlEditor)
   {
      JMeldPanelHandler meldPanelHandler = new JMeldPanelHandler(allowSaveToSqlEditor, text -> _textToSave = text);
      try
      {
         meldPanelHandler.showDiff(leftClipboardTempFile, rightEditorTextTempFile);

         _dialog = new CompareToClipboardDlg(owningFrame, meldPanelHandler.getMeldPanel(), title);


         GUIUtils.enableCloseByEscape(_dialog);
         GUIUtils.initLocation(_dialog, 600, 600);

         _dialog.setVisible(true);
      }
      finally
      {
         meldPanelHandler.cleanMeldPanel();
      }
   }

   public String getTextToSave()
   {
      return _textToSave;
   }
}
