package net.sourceforge.squirrel_sql.fw.gui.stdtextpopup;

import net.sourceforge.squirrel_sql.client.session.SQLEntryPanelUtil;

import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;

public class TextActionUtil
{
   public static void wrapCopyActionToSelectLineOnEmptySelection(JTextComponent textComponent, ActionEvent evt, Runnable copyActionPerformedCall)
   {
      if(textComponent == null)
      {
         return;
      }

      Integer previousCaretPos = null;
      if(null == textComponent.getSelectedText())
      {
         previousCaretPos = textComponent.getCaretPosition();
         selectLineAtCursor(textComponent);
      }

      copyActionPerformedCall.run();

      if(null != previousCaretPos)
      {
         textComponent.setCaretPosition(previousCaretPos);
      }
   }

   public static void wrapCutActionToSelectLineOnEmptySelection(JTextComponent textComponent, ActionEvent evt, Runnable cutActionPerformedCall)
   {
      if(textComponent == null)
      {
         return;
      }

      if(null == textComponent.getSelectedText())
      {
         selectLineAtCursor(textComponent);
      }

      cutActionPerformedCall.run();

   }

   private static void selectLineAtCursor(JTextComponent textComponent)
   {
      final int[] lineBounds = SQLEntryPanelUtil.getLineBoundsAtCursor(textComponent);
      if(lineBounds[0] < lineBounds[1])
      {
         textComponent.setSelectionStart(lineBounds[0]);
         textComponent.setSelectionEnd(lineBounds[1]);
      }
   }
}
