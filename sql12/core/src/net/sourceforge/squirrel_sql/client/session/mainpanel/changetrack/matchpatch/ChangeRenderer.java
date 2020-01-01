package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.matchpatch;

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.Color;
import java.util.LinkedList;

public class ChangeRenderer
{

   public static void renderChangeInTextPane(JTextPane txtRenderedPane, String sourceText, String targetText, ChangeRendererStyle changeRendererStyle)
   {
      diff_match_patch dmp = new diff_match_patch();

      LinkedList<diff_match_patch.Diff> diffs = dmp.diff_main(sourceText, targetText);


      for (int i = 0; i < diffs.size(); i++)
      {
         diff_match_patch.Diff diff = diffs.get(i);

         switch (diff.operation)
         {
            case EQUAL:
               print(txtRenderedPane, diff.text,false, previousIsInsert(diffs, i), nextIsInsert(diffs, i), changeRendererStyle);
               break;
//            case INSERT:
//               break;
            case DELETE:
               print(txtRenderedPane, diff.text,true, previousIsInsert(diffs, i), nextIsInsert(diffs, i), changeRendererStyle);
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

   private static void print(JTextPane txtFormerText, String str, boolean isDeleted, boolean previousIsInsert, boolean nextIsInsert, ChangeRendererStyle style)
   {
      Color defaultBg = txtFormerText.getBackground();
      Color defaultFg = txtFormerText.getForeground();

      SimpleAttributeSet attributes = new SimpleAttributeSet(txtFormerText.getInputAttributes());
      StyleConstants.setForeground(attributes, txtFormerText.getForeground());
      StyleConstants.setBackground(attributes, defaultBg);
      StyleConstants.setForeground(attributes, defaultFg);

      StyleConstants.setBold(attributes, isDeleted && style.isDeletedBold());
      StyleConstants.setItalic(attributes, isDeleted && style.isDeletedItalic());
      if (isDeleted)
      {
         StyleConstants.setForeground(attributes, style.getDeletedForgeGround());
      }

      if(previousIsInsert && nextIsInsert)
      {
         if (str.length() > 1)
         {
            StyleConstants.setBackground(attributes, style.getAfterInsertColor());
            TextPaneUtil.insert(txtFormerText, str.substring(0, 1), attributes);

            if (str.length() > 2)
            {
               StyleConstants.setBackground(attributes, defaultBg);
               TextPaneUtil.insert(txtFormerText, str.substring(1, str.length() - 1), attributes);
            }

            StyleConstants.setBackground(attributes, style.getBeforeInsertColor());
            TextPaneUtil.insert(txtFormerText, str.substring(str.length() - 1), attributes);
         }
         else
         {
            StyleConstants.setBackground(attributes, style.getBeforeInsertColor());
            TextPaneUtil.insert(txtFormerText, str, attributes);
         }
      }
      else if(false == previousIsInsert && nextIsInsert)
      {
         if (str.length() > 1)
         {
            StyleConstants.setBackground(attributes, defaultBg);
            TextPaneUtil.insert(txtFormerText, str.substring(0, str.length() - 1), attributes);
         }

         StyleConstants.setBackground(attributes, style.getBeforeInsertColor());
         TextPaneUtil.insert(txtFormerText, str.substring(str.length() - 1), attributes);

      }
      else if(previousIsInsert && false == nextIsInsert)
      {
         StyleConstants.setBackground(attributes, style.getAfterInsertColor());
         TextPaneUtil.insert(txtFormerText, str.substring(0, 1), attributes);

         if (str.length() > 1)
         {
            StyleConstants.setBackground(attributes, defaultBg);
            TextPaneUtil.insert(txtFormerText, str.substring(1), attributes);
         }
      }
      else
      {
         TextPaneUtil.insert(txtFormerText, str, attributes);
      }
   }

}
