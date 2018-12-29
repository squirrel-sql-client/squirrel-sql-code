package net.sourceforge.squirrel_sql.client.gui.aboutdialog;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.HashtableDataSet;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

final class SystemPanel extends JPanel
{
   private final static ILogger s_log = LoggerController.createLogger(SystemPanel.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SystemPanel.class);


   private MemoryPanel _memoryPnl;

   SystemPanel()
   {
      setLayout(new BorderLayout());
      DataSetViewerTablePanel propsPnl = new DataSetViewerTablePanel();
      propsPnl.init(null, null);
      try
      {
         propsPnl.show(new HashtableDataSet(System.getProperties()));
      }
      catch (DataSetException ex)
      {
         // i18n[AboutBoxDialog.error.systemprops=Error occurred displaying System Properties]
         s_log.error(s_stringMgr.getString("AboutBoxDialog.error.systemprops"), ex);
      }

      _memoryPnl = new MemoryPanel();
      add(new JScrollPane(propsPnl.getComponent()), BorderLayout.CENTER);
      add(_memoryPnl, BorderLayout.SOUTH);

      //setPreferredSize(new Dimension(400, 400));
   }

   public MemoryPanel getMemoryPanel()
   {
      return _memoryPnl;
   }
}
