package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.matchpatch;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.Color;
import java.util.LinkedList;

public class ChangeRenderer
{
   public static void renderChangeInTextPane(JTextPane txtFormerText, String sourceText, String targetText)
   {
      diff_match_patch dmp = new diff_match_patch();

      LinkedList<diff_match_patch.Diff> diffs = dmp.diff_main(sourceText, targetText);


      for (int i = 0; i < diffs.size(); i++)
      {
         diff_match_patch.Diff diff = diffs.get(i);

         switch (diff.operation)
         {
            case EQUAL:
               print(txtFormerText, diff.text, txtFormerText.getForeground(), txtFormerText.getBackground(), false, false);
               break;
//            case INSERT:
//               print(txtFormerText, diff.text, txtFormerText.getForeground(), txtFormerText.getBackground(), true);
//               break;
            case DELETE:
               print(txtFormerText, diff.text, txtFormerText.getForeground().darker().darker(), txtFormerText.getBackground(), true, false);
               break;
         }
      }

   }

   private static void print(JTextPane txtFormerText, String msg, Color foreground, Color background, boolean bold, boolean italic)
   {
      System.out.println("msg = " + msg);

      SimpleAttributeSet attributes = new SimpleAttributeSet(txtFormerText.getInputAttributes());
      StyleConstants.setForeground(attributes, foreground);
      StyleConstants.setBackground(attributes, background);
      StyleConstants.setBold(attributes, bold);
      StyleConstants.setItalic(attributes, italic);

      try
      {
         txtFormerText.getStyledDocument().insertString(txtFormerText.getDocument().getLength(), msg, attributes);
      }
      catch (BadLocationException ignored)
      {
      }
   }

}
