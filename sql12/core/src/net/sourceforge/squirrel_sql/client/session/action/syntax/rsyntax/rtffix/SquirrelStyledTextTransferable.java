package net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.rtffix;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

class SquirrelStyledTextTransferable implements Transferable
{
   private String html;
   private byte[] rtfBytes;
   private static final DataFlavor[] FLAVORS;

   SquirrelStyledTextTransferable(String html, byte[] rtfBytes)
   {
      this.html = html;
      this.rtfBytes = rtfBytes;
   }

   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
   {
      if (flavor.equals(FLAVORS[0]))
      {
         return this.html;
      }
      else if (flavor.equals(FLAVORS[1]))
      {
         return new ByteArrayInputStream(this.rtfBytes == null ? new byte[0] : this.rtfBytes);
      }
      else if (flavor.equals(FLAVORS[2]))
      {
         return this.rtfBytes == null ? "" : SquirrelRtfToText.getPlainText(this.rtfBytes);
      }
      else if (flavor.equals(FLAVORS[3]))
      {
         String text = "";
         if (this.rtfBytes != null)
         {
            text = SquirrelRtfToText.getPlainText(this.rtfBytes);
         }

         return new StringReader(text);
      }
      else
      {
         throw new UnsupportedFlavorException(flavor);
      }
   }

   public DataFlavor[] getTransferDataFlavors()
   {
      return FLAVORS.clone();
   }

   public boolean isDataFlavorSupported(DataFlavor flavor)
   {
      DataFlavor[] var2 = FLAVORS;
      int var3 = var2.length;

      for (int var4 = 0; var4 < var3; ++var4)
      {
         DataFlavor flavor1 = var2[var4];
         if (flavor.equals(flavor1))
         {
            return true;
         }
      }

      return false;
   }

   static
   {
      FLAVORS = new DataFlavor[]{DataFlavor.fragmentHtmlFlavor, new DataFlavor("text/rtf", "RTF"), DataFlavor.stringFlavor, DataFlavor.plainTextFlavor};
   }
}
