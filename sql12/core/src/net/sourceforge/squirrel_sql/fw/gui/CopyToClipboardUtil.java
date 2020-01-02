package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class CopyToClipboardUtil
{
   public  static void copyToClip(StringBuffer buf)
   {
      copyToClip(buf.toString());
   }

   public static void copyToClip(StringBuilder sb)
   {
      copyToClip(sb.toString());
   }

   public static void copyToClip(String buf)
   {
      copyToClip(buf, false);
   }

   public static void copyToClip(String buf, boolean skipEmpty)
   {
      if(skipEmpty && StringUtilities.isEmpty(buf))
      {
         return;
      }

      final StringSelection ss = new StringSelection(buf);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);

      Main.getApplication().getPasteHistory().addToPasteHistory(buf);

   }

}
