package net.sourceforge.squirrel_sql.client.session.mainpanel.multiclipboard;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;

public class PasteFromHistoryAttach
{
   public PasteFromHistoryAttach(ISQLEntryPanel sqlEntry)
   {
      this(sqlEntry.getTextComponent());
   }

   public PasteFromHistoryAttach(JTextArea textComponent)
   {
      //textComponent.getActionMap().get("paste-from-clipboard");



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

      action = textComponent.getActionMap().get(DefaultEditorKit.copyAction);
      actionProxy = new ClipboardCopyActionProxy(action, clipboardCopyActionProxyListener);
      textComponent.getActionMap().put(DefaultEditorKit.copyAction, actionProxy);

      action = textComponent.getActionMap().get(DefaultEditorKit.cutAction);
      actionProxy = new ClipboardCopyActionProxy(action, clipboardCopyActionProxyListener);
      textComponent.getActionMap().put(DefaultEditorKit.cutAction, actionProxy);
   }

   private void onCopyToClipboard(String clipContent)
   {
      PasteHistory pasteHistory = Main.getApplication().getPasteHistory();

      pasteHistory.addToPasteHistory(clipContent);
   }
}
