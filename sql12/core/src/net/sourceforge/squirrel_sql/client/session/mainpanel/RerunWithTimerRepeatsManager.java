package net.sourceforge.squirrel_sql.client.session.mainpanel;

import javax.swing.Timer;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ReRunChooserCtrl;

public class RerunWithTimerRepeatsManager
{
   private Timer _timer;

   private record ResultTabData(ResultTab resultTab, ReRunChooserCtrl reRunChooserCtrl){}

   private boolean _newResultTabData;
   private ResultTabData _currentResultTabData;

   void startTimerRepeats(int repeatSeconds, ResultTab resultTab, ReRunChooserCtrl reRunChooserCtrl)
   {
      initNewResultTabData(resultTab, reRunChooserCtrl);

      _timer = new Timer(1000 * repeatSeconds, e -> onTimerTriggered());
      _timer.setRepeats(true);
      _timer.start();
      onTimerTriggered();
   }

   private void onTimerTriggered()
   {
      // If the ResultTabData aren't new the rerun previously triggered is still running.
      if(_newResultTabData)
      {
         _newResultTabData = false;
         _currentResultTabData.resultTab.reRunSQLIntern();
      }
   }

   void resultWasTabReplaced(ResultTab newResultTab, ReRunChooserCtrl newReRunChooserCtrl)
   {
      if(null == _timer || false == _timer.isRunning())
      {
         return;
      }

      initNewResultTabData(newResultTab, newReRunChooserCtrl);
   }

   private void initNewResultTabData(ResultTab resultTab, ReRunChooserCtrl reRunChooserCtrl)
   {
      if(null != _currentResultTabData)
      {
         _currentResultTabData.reRunChooserCtrl.cleanUp();
      }

      _currentResultTabData = new ResultTabData(resultTab, reRunChooserCtrl);
      _currentResultTabData.reRunChooserCtrl.setResultTab(resultTab);
      _currentResultTabData.reRunChooserCtrl.switchToStopButton(e -> _timer.stop());
      _newResultTabData = true;
   }

}
