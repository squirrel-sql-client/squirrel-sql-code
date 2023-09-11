package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import net.sourceforge.squirrel_sql.client.session.SQLEntryPanelUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rtextarea.RTextArea;

import java.awt.event.ActionEvent;

class SQuirrelSelectWordAction extends RSyntaxTextAreaEditorKit.SelectWordAction
{
   public void actionPerformedImpl(ActionEvent e, RTextArea textArea)
   {
      int[] newSelectionBounds = SQLEntryPanelUtil.getWordBoundsAtCursor(textArea, false);

      if(newSelectionBounds[0] == textArea.getSelectionStart() && newSelectionBounds[1] == textArea.getSelectionEnd())
      {
         // Select till end of line
         newSelectionBounds[1] = SQLEntryPanelUtil.getLineBoundsAtCursor(textArea)[1]-1;
      }
      if(SQLEntryPanelUtil.getLineBoundsAtCursor(textArea)[1]-1 == textArea.getSelectionEnd() && textArea.getSelectionStart() != textArea.getSelectionEnd())
      {
         // Select whole line
         newSelectionBounds = SQLEntryPanelUtil.getLineBoundsAtCursor(textArea);
         --newSelectionBounds[1];
      }

      textArea.setSelectionStart(newSelectionBounds[0]);
      textArea.setSelectionEnd(newSelectionBounds[1]);
   }
}
