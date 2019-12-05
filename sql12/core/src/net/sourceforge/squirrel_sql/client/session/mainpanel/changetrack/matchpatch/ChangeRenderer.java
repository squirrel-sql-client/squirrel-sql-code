package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.matchpatch;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.Color;
import java.util.LinkedList;

public class ChangeRenderer
{

   public static final Color BEFORE_INSERT_COLOR = new Color(204, 204, 255);
   public static final Color AFTER_INSERT_COLOR = new Color(204, 255, 255);

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
               print(txtFormerText, diff.text, txtFormerText.getForeground(), txtFormerText.getBackground(), false, previousIsInsert(diffs, i), nextIsInsert(diffs, i));
               break;
//            case INSERT:
//               print(txtFormerText, diff.text, txtFormerText.getForeground(), txtFormerText.getBackground(), true);
//               break;
            case DELETE:
               print(txtFormerText, diff.text, txtFormerText.getForeground().darker().darker(), txtFormerText.getBackground(), true, previousIsInsert(diffs, i), nextIsInsert(diffs, i));
               break;
         }
      }

   }

   private static boolean nextIsInsert(LinkedList<diff_match_patch.Diff> diffs, int ix)
   {
      if (ix < diffs.size() - 1 && diffs.get(ix + 1).operation == diff_match_patch.Operation.INSERT)
      {
         return true;
      }

      return false;

   }

   private static boolean previousIsInsert(LinkedList<diff_match_patch.Diff> diffs, int ix)
   {
      if (ix > 0 && diffs.get(ix-1).operation == diff_match_patch.Operation.INSERT)
      {
         return true;
      }

      return false;
   }

   private static void print(JTextPane txtFormerText, String str, Color foreground, Color background, boolean bold, boolean previousIsInsert, boolean nextIsInsert)
   {

      SimpleAttributeSet attributes = new SimpleAttributeSet(txtFormerText.getInputAttributes());
      StyleConstants.setForeground(attributes, foreground);
      StyleConstants.setBackground(attributes, background);
      StyleConstants.setBold(attributes, bold);

      Color defaultBg = txtFormerText.getBackground();

      if(previousIsInsert && nextIsInsert)
      {
         if (str.length() > 1)
         {
            StyleConstants.setBackground(attributes, AFTER_INSERT_COLOR);
            insert(txtFormerText, str.substring(0, 1), attributes);

            if (str.length() > 2)
            {
               StyleConstants.setBackground(attributes, defaultBg);
               insert(txtFormerText, str.substring(1, str.length() - 1), attributes);
            }

            StyleConstants.setBackground(attributes, BEFORE_INSERT_COLOR);
            insert(txtFormerText, str.substring(str.length() - 1), attributes);
         }
         else
         {
            insert(txtFormerText, str, attributes);
         }
      }
      else if(false == previousIsInsert && nextIsInsert)
      {
         if (str.length() > 1)
         {
            StyleConstants.setBackground(attributes, defaultBg);
            insert(txtFormerText, str.substring(0, str.length() - 1), attributes);
         }

         StyleConstants.setBackground(attributes, BEFORE_INSERT_COLOR);
         insert(txtFormerText, str.substring(str.length() - 1), attributes);

      }
      else if(previousIsInsert && false == nextIsInsert)
      {
         StyleConstants.setBackground(attributes, AFTER_INSERT_COLOR);
         insert(txtFormerText, str.substring(0, 1), attributes);

         if (str.length() > 1)
         {
            StyleConstants.setBackground(attributes, defaultBg);
            insert(txtFormerText, str.substring(1, str.length() - 1), attributes);
         }
      }
      else
      {
         insert(txtFormerText, str, attributes);
      }

   }

   private static void insert(JTextPane txtFormerText, String msg, SimpleAttributeSet attributes)
   {
      try
      {
         txtFormerText.getStyledDocument().insertString(txtFormerText.getDocument().getLength(), msg, attributes);
      }
      catch (BadLocationException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

}
