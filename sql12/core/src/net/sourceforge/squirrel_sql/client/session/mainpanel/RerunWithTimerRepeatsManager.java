package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.util.List;
import javax.swing.Timer;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ReRunChooserCtrl;

public class RerunWithTimerRepeatsManager
{
   private Timer _timer;
   private ResultTabCloseListener _resultTabCloseListener = () -> disposeTimer();

   private record ResultTabData(ResultTab resultTab, ReRunChooserCtrl reRunChooserCtrl){}

   private boolean _newResultTabData;
   private ResultTabData _currentResultTabData;

   void startTimerRepeats(int repeatSeconds, ResultTab resultTab, ReRunChooserCtrl reRunChooserCtrl)
   {
      initNewResultTabData(resultTab, reRunChooserCtrl);

      disposeTimer();
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

   void aboutToBeReplacedBy(ResultTab newResultTab, ReRunChooserCtrl newReRunChooserCtrl)
   {
      if(null == _timer || false == _timer.isRunning())
      {
         return;
      }

      initNewResultTabData(newResultTab, newReRunChooserCtrl);
   }

   public void disposeTimer()
   {
      if(null == _timer)
      {
         return;
      }
      _timer.stop();
      List.of(_timer.getActionListeners()).forEach(l -> _timer.removeActionListener(l));
   }


   private void initNewResultTabData(ResultTab resultTab, ReRunChooserCtrl reRunChooserCtrl)
   {
      if(null != _currentResultTabData)
      {
         _currentResultTabData.reRunChooserCtrl.cleanUp();

         // Note: When this method is called by aboutToBeReplacedBy() this method will be called before the ResultTabCloseListener is fired.
         // The following command makes sure that disposeTimer() isn't called when the ResultTab is replaced
         _currentResultTabData.resultTab.removeResultTabCloseListener(_resultTabCloseListener);
      }

      _currentResultTabData = new ResultTabData(resultTab, reRunChooserCtrl);
      _currentResultTabData.reRunChooserCtrl.setResultTab(resultTab);
      _currentResultTabData.reRunChooserCtrl.switchToStopButton(e -> disposeTimer());
      _currentResultTabData.resultTab.addResultTabCloseListener(_resultTabCloseListener);
      _newResultTabData = true;
   }

}
