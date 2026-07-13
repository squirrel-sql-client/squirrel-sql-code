package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.apache.commons.lang3.StringUtils;

public class AiConfigMdWriter
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AiConfigMdWriter.class);

   public static void saveAiConfigMd(JPanel parentPanel)
   {
      try
      {
         JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
         fileChooser.setSelectedFile(new File("SQuirreL_AI_config.md"));
         fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Markdown (*.md)", "md"));

         if( JFileChooser.APPROVE_OPTION != fileChooser.showSaveDialog(GUIUtils.getOwningFrame(parentPanel)) )
         {
            return;
         }

         File selectedFile = fileChooser.getSelectedFile();

         if(false == StringUtils.endsWithIgnoreCase(selectedFile.getName(), ".md"))
         {
            selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".md");
         }

         if( null == selectedFile )
         {
            JOptionPane.showMessageDialog(parentPanel,
                                          s_stringMgr.getString("McpBarCtrl.saveAiConfigMd.no.file.selected.message"),
                                          s_stringMgr.getString("McpBarCtrl.saveAiConfigMd.no.file.selected.title"),
                                          JOptionPane.ERROR_MESSAGE
            );
            return;
         }

         if( selectedFile.exists() )
         {
            int opt = JOptionPane.showConfirmDialog(parentPanel,
                                                    s_stringMgr.getString("McpBarCtrl.saveAiConfigMd.no.file.overwrite.message"),
                                                    s_stringMgr.getString("McpBarCtrl.saveAiConfigMd.no.file.overwrite.title"),
                                                    JOptionPane.YES_NO_CANCEL_OPTION);

            if( opt != JOptionPane.YES_OPTION )
            {
               return;
            }
         }

         // TODO AI: Write the contents of SquirrelMcpAiConfig.md
         try (InputStream in = AiConfigMdWriter.class.getResourceAsStream("SquirrelMcpAiConfig.md"))
         {
            Files.copy(in, selectedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
         }

         int opt = JOptionPane.showConfirmDialog(parentPanel,
                                                 s_stringMgr.getString("McpBarCtrl.savedAiConfigMd.file.title.message", selectedFile.getAbsolutePath()),
                                                 s_stringMgr.getString("McpBarCtrl.savedAiConfigMd.file.title"),
                                                 JOptionPane.YES_NO_CANCEL_OPTION);

         if( opt == JOptionPane.YES_OPTION )
         {
            String prompt = s_stringMgr.getString("McpBarCtrl.savedAiConfigMd.file.prompt.to.import", selectedFile.getCanonicalPath());
            ClipboardUtil.copyToClip(prompt);

            Main.getApplication().getMessageHandler().showMessage(
                  s_stringMgr.getString("McpBarCtrl.savedAiConfigMd.file.prompt.to.import.copied", prompt));
         }
      }
      catch(IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
