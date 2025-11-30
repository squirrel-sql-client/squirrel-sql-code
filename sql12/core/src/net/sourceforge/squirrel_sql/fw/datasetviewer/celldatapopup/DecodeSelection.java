package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import java.nio.charset.StandardCharsets;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.RestorableRSyntaxTextArea;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.Base64DecodeHelper;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

public class DecodeSelection
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DecodeSelection.class);

   public static JMenu getParentMenu(RestorableRSyntaxTextArea textArea)
   {
      JMenu ret = new JMenu(s_stringMgr.getString("DecodeSelection.menu"));
      ret.setToolTipText(s_stringMgr.getString("DecodeSelection.menu.tooltip"));

      JMenuItem menuItem;

      menuItem = new JMenuItem(s_stringMgr.getString("DecodeSelection.menu.item.decode.base64"));
      menuItem.addActionListener(e -> decodeBase64(textArea));
      ret.add(menuItem);

      menuItem = new JMenuItem(s_stringMgr.getString("DecodeSelection.menu.item.decode.base32"));
      menuItem.addActionListener(e -> decodeBase32(textArea));
      ret.add(menuItem);

      menuItem = new JMenuItem(s_stringMgr.getString("DecodeSelection.menu.item.decode.hex"));
      menuItem.addActionListener(e -> decodeHex(textArea));
      ret.add(menuItem);

      return ret;
   }

   private static void decodeHex(RestorableRSyntaxTextArea textArea)
   {
      try
      {
         String selectedText = textArea.getSelectedText();

         if(StringUtils.isBlank(selectedText))
         {
            Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("DecodeSelection.no.text.selected.to.decode"));
            return;
         }

         Main.getApplication().getMessageHandler().showMessage(new String(Hex.decodeHex(selectedText), StandardCharsets.UTF_8));
      }
      catch(DecoderException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static void decodeBase32(RestorableRSyntaxTextArea textArea)
   {
      String selectedText = textArea.getSelectedText();

      if(StringUtils.isBlank(selectedText))
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("DecodeSelection.no.text.selected.to.decode"));
         return;
      }

      Main.getApplication().getMessageHandler().showMessage(new String(new Base32().decode(selectedText), StandardCharsets.UTF_8));
   }

   private static void decodeBase64(RestorableRSyntaxTextArea textArea)
   {
      String selectedText = textArea.getSelectedText();

      if(StringUtils.isBlank(selectedText))
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("DecodeSelection.no.text.selected.to.decode"));
         return;
      }

      Main.getApplication().getMessageHandler().showMessage(
         new String(Base64DecodeHelper.decodeBase64OmittingInvalidBase64Chars(selectedText), StandardCharsets.UTF_8));

   }


}
