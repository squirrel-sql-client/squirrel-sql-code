package net.sourceforge.squirrel_sql.client.session.mainpanel.multiclipboard;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;

public class PasteFromHistoryAttach
{
   public PasteFromHistoryAttach(ISQLEntryPanel sqlEntry)
   {
      //sqlEntry.getTextComponent().getActionMap().get("paste-from-clipboard");

      ClipboardCopyActionProxyListener clipboardCopyActionProxyListener = new ClipboardCopyActionProxyListener()
      {
         @Override
         public void copyToClipboard(String clipContent)
         {
            onCopyToClipboard(clipContent);
         }
      };

      Action action;
      ClipboardCopyActionProxy actionProxy;

      action = sqlEntry.getTextComponent().getActionMap().get(DefaultEditorKit.copyAction);
      actionProxy = new ClipboardCopyActionProxy(action, clipboardCopyActionProxyListener);
      sqlEntry.getTextComponent().getActionMap().put(DefaultEditorKit.copyAction, actionProxy);

      action = sqlEntry.getTextComponent().getActionMap().get(DefaultEditorKit.cutAction);
      actionProxy = new ClipboardCopyActionProxy(action, clipboardCopyActionProxyListener);
      sqlEntry.getTextComponent().getActionMap().put(DefaultEditorKit.cutAction, actionProxy);
   }

   private void onCopyToClipboard(String clipContent)
   {
      PasteHistory pasteHistory = Main.getApplication().getPasteHistory();

      pasteHistory.addToPasteHistory(clipContent);
   }
}
