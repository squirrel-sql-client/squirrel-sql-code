package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class ClipboardUtil
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

   public static String getClipboardAsString()
   {
      try
      {
         Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
         Transferable contents = clip.getContents(null);

         String clipContent = (String)contents.getTransferData(DataFlavor.stringFlavor);
         return clipContent;
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
