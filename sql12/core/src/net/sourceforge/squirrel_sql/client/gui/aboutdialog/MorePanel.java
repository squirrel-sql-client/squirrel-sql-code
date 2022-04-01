package net.sourceforge.squirrel_sql.client.gui.aboutdialog;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import java.awt.GridLayout;

public class MorePanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AboutBoxDialog.class);
   private final static ILogger s_log = LoggerController.createLogger(MorePanel.class);

   public MorePanel()
   {
      super(new GridLayout(1,1));

      final JTextArea textArea = new JTextArea();
      textArea.setTabSize(3);
      final JScrollPane scrollPane = new JScrollPane(textArea);
      add(scrollPane);

      textArea.setEditable(false);

      String text = getLookAndFeelInfo();


      textArea.setText(text);

      // To keep a long text from increasing the about dialog's width.
      GUIUtils.setPreferredWidth(scrollPane, 100);

      GUIUtils.forceScrollToBegin(scrollPane);
   }

   private String getLookAndFeelInfo()
   {
      String text;

      try
      {
         final LookAndFeel lookAndFeel = UIManager.getLookAndFeel();

         if(null == lookAndFeel)
         {
            text = s_stringMgr.getString("MorePanel.no.installed.laf.info");
         }
         else
         {
            text = s_stringMgr.getString("MorePanel.installed.laf.info",
                                         lookAndFeel.getName(),
                                         lookAndFeel.getDescription(),
                                         lookAndFeel.toString(),
                                         lookAndFeel);
         }
      }
      catch (Exception e)
      {
         s_log.error("Failed to read installed Look and feel", e);
         text = s_stringMgr.getString("MorePanel.failed.to.read.laf.info", e);
      }

      return text;
   }
}
