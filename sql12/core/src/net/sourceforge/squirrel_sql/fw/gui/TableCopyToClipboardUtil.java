package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.client.Main;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class TableCopyToClipboardUtil
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
      final StringSelection ss = new StringSelection(buf);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);

      Main.getApplication().getPasteHistory().addToPasteHistory(buf);

   }

}
