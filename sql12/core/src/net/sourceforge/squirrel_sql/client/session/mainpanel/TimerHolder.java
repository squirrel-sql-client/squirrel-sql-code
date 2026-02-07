package net.sourceforge.squirrel_sql.client.session.mainpanel;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.Timer;
import net.sourceforge.squirrel_sql.client.session.mainpanel.notificationsound.FinishedNotificationSoundHandler;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;


/**
 * This timer treatment seems to give better Session GCing behaviour.
 */
public class TimerHolder
{
   private final JCheckBox _chkPlaySoundWhenFinished;
   private final FinishedNotificationSoundHandler _finishedNotificationSoundHandler;
   private Timer _timer;
   private JTextField _txtExecTimeCounter;

   private final long _beginMillis;
   private ResultSetDataSet _rsds;
   private JTextField _txtNumberOfRowsRead;


   public TimerHolder(JTextField txtExecTimeCounter, JTextField txtNumberOfRowsRead, JCheckBox chkPlaySoundWhenFinished, FinishedNotificationSoundHandler finishedNotificationSoundHandler)
   {
      _chkPlaySoundWhenFinished = chkPlaySoundWhenFinished;
      _finishedNotificationSoundHandler = finishedNotificationSoundHandler;
      _beginMillis = System.currentTimeMillis();

      _txtExecTimeCounter = txtExecTimeCounter;
      _txtNumberOfRowsRead = txtNumberOfRowsRead;

      _timer = new Timer(300, e -> onUpdateExecutionTime());
      _timer.setRepeats(true);
      _timer.start();

   }

   private void onUpdateExecutionTime()
   {
      long elapsedMillis = System.currentTimeMillis() - _beginMillis;
      _txtExecTimeCounter.setText("" + elapsedMillis);

      _chkPlaySoundWhenFinished.setSelected(_finishedNotificationSoundHandler.isToPlayNotificationSound(elapsedMillis));

      if(null != _rsds && null != _rsds.getAllDataForReadOnly())
      {
         _txtNumberOfRowsRead.setText("" + _rsds.getAllDataForReadOnly().size());
      }
   }

   public void stop()
   {
      _timer.stop();
      _txtExecTimeCounter = null;
   }

   public void setResultSetDataSetInProgress(ResultSetDataSet rsds)
   {
      _rsds = rsds;
   }
}
