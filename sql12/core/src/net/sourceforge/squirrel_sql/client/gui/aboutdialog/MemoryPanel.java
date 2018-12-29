package net.sourceforge.squirrel_sql.client.gui.aboutdialog;

import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MemoryPanel extends PropertyPanel implements ActionListener
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MemoryPanel.class);


   private final JLabel _totalMemoryLbl = new JLabel();
   private final JLabel _usedMemoryLbl = new JLabel();
   private final JLabel _freeMemoryLbl = new JLabel();
   private Timer _timer;

   MemoryPanel()
   {
      add(new JLabel(s_stringMgr.getString("AboutBoxDialog.heapsize")), _totalMemoryLbl);
      add(new JLabel(s_stringMgr.getString("AboutBoxDialog.usedheap")), _usedMemoryLbl);
      add(new JLabel(s_stringMgr.getString("AboutBoxDialog.freeheap")), _freeMemoryLbl);

      JButton gcBtn = new JButton(s_stringMgr.getString("AboutBoxDialog.gc"));
      gcBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            Utilities.garbageCollect();
         }
      });
      add(gcBtn, new JLabel(""));
   }

   public void removeNotify()
   {
      super.removeNotify();
      stopTimer();
   }

   /**
    * Update component with the current memory status.
    *
    * @param   evt      The current event.
    */
   public void actionPerformed(ActionEvent evt)
   {
      updateMemoryStatus();
   }

   synchronized void startTimer()
   {
      if (_timer == null)
      {
         updateMemoryStatus();
         _timer = new Timer(2000, this);
         _timer.start();
      }
   }

   synchronized void stopTimer()
   {
      if (_timer != null)
      {
         _timer.stop();
         _timer = null;
      }
   }

   private void updateMemoryStatus()
   {
      Runtime rt = Runtime.getRuntime();
      final long totalMemory = rt.totalMemory();
      final long freeMemory = rt.freeMemory();
      final long usedMemory = totalMemory - freeMemory;
      _totalMemoryLbl.setText(Utilities.formatSize(totalMemory, 1));
      _usedMemoryLbl.setText(Utilities.formatSize(usedMemory, 1));
      _freeMemoryLbl.setText(Utilities.formatSize(freeMemory, 1));
   }
}
