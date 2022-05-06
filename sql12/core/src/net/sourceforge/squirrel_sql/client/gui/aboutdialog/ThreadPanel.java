package net.sourceforge.squirrel_sql.client.gui.aboutdialog;

import java.awt.BorderLayout;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

final class ThreadPanel extends JPanel
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ThreadPanel.class);

   private final JScrollPane scrollPane;

   private JTextArea content;

   ThreadPanel()
   {
      setLayout(new BorderLayout());

      content = new JTextArea(5, 20);
      content.setEditable(false);
      content.setLineWrap(false);
      scrollPane = new JScrollPane(content);
      add(scrollPane, BorderLayout.CENTER);
      add(createButtons(), BorderLayout.SOUTH);
      doThreadDump();
   }

   private JPanel createButtons()
   {
      JPanel buttonPanel = new JPanel(new BorderLayout());
      JButton refreshButton = new JButton(s_stringMgr.getString("ThreadPanel.refresh"));
      buttonPanel.add(refreshButton, BorderLayout.WEST);

      refreshButton.addActionListener(e -> doThreadDump());

      return buttonPanel;
   }

   private void doThreadDump()
   {
      StringBuilder sb = new StringBuilder();

      ThreadInfo[] threadInfos = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
      for (ThreadInfo threadInfo : threadInfos)
      {
         sb.append(threadInfo.toString());
         sb.append(StringUtilities.getEolStr());
      }

      content.setText(sb.toString());

      GUIUtils.forceScrollToBegin(scrollPane);

   }
}
