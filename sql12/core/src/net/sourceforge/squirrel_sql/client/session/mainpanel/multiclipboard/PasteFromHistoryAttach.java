package net.sourceforge.squirrel_sql.client.session.mainpanel.multiclipboard;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.*;

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

      action = sqlEntry.getTextComponent().getActionMap().get("copy-to-clipboard");
      actionProxy = new ClipboardCopyActionProxy(action, clipboardCopyActionProxyListener);
      sqlEntry.getTextComponent().getActionMap().put("copy-to-clipboard", actionProxy);

      action = sqlEntry.getTextComponent().getActionMap().get("cut-to-clipboard");
      actionProxy = new ClipboardCopyActionProxy(action, clipboardCopyActionProxyListener);
      sqlEntry.getTextComponent().getActionMap().put("cut-to-clipboard", actionProxy);


   }

   private void onCopyToClipboard(String clipContent)
   {
      PasteHistory pasteHistory = Main.getApplication().getPasteHistroy();

      pasteHistory.addToPasteHistory(clipContent);
   }
}
