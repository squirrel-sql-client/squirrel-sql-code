package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeFinderFinishListener;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.tree.TreePath;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Note: NO THREADING is involved in this class.
 * Everything is done on the Event Dispatch Thread (EDT).
 * One reason why threads aren't used is
 * because expanders are hard to control.
 * They might for example access Swing classes
 * which must be accessed on the EDT only.
 */
class ObjectTreeSearchResultFutureIntern implements ObjectTreeSearchResultFuture
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ObjectTreeSearchResultFutureIntern.class);
   public static final int MAX_PROGRESS = 50;

   private TreePath _treePath;
   private ISession _session;
   private final long _startTime;

   private ArrayList<ObjectTreeFinderTaskInfo> _taskList = new ArrayList<>();

   private boolean _isExecuting = false;
   private boolean _isFinished = false;

   private Timer _timer = new Timer(100, e -> onTimerTriggered());
   private int _taskIx = 0;
   private ArrayList<ObjectTreeFinderFinishListener> _listeners = new ArrayList<>();
   private int _progressValue;
   private ActionListener _stopActionListener = e -> onStop();


   ObjectTreeSearchResultFutureIntern(ISession session)
   {
      _session = session;
      _startTime = System.currentTimeMillis();

      _session.addSimpleSessionListener(() -> disposeTimer());
   }

   void setTreePath(TreePath treePath)
   {
      //System.out.println("Found treePath = " + treePath);
      _treePath = treePath;
   }

   @Override
   public TreePath getTreePath()
   {
      return _treePath;
   }

   @Override
   public void addFinishedListenerOrdered(ObjectTreeFinderFinishListener listener)
   {
      if(_isFinished)
      {
         SwingUtilities.invokeLater(() ->listener.finderFinished(_treePath));
         return;
      }

      _listeners.remove(listener);
      _listeners.add(listener);
   }

   @Override
   public void executeTillFinishNow()
   {
      disposeTimer();

      while (hasNextTask())
      {
         nextTask().getTask().exec();
         if(null != _treePath)
         {
            // We quit on first result.
            // If one day we want to find more result don't quit here but collect.
            break;
         }
      }

      if (false == _isFinished)
      {
         fireFinished();
      }
   }


   /**
    * Tasks will be executed in the order added
    * and will be executed on EDT.
    */
   void addTask(String descr, ObjectTreeFinderTask task)
   {
      _taskList.add(new ObjectTreeFinderTaskInfo(descr, task));
   }


   void triggerExecution()
   {
      if(_isExecuting)
      {
         return;
      }
      _isExecuting = true;

      while(hasNextTask())
      {
         ObjectTreeFinderTaskInfo taskInfo = nextTask();
         taskInfo.getTask().exec();
         if(null != _treePath)
         {
            // We quit on first result.
            // If one day we want to find more result don't quit here but collect.
            break;
         }

         if(System.currentTimeMillis() - _startTime > 50)
         {
            _timer.start();
            _progressValue = 4;
            increaseProgress(taskInfo.getDescr());
            //System.out.println("Entering timer");
            return;
         }
      }

      // Because of th return in the loop we will reach here only when the timer wasn't used.
      //System.out.println("Finishing without timer");
      fireFinished();
   }

   private void increaseProgress(String taskDescr)
   {
      String msg = s_stringMgr.getString("ObjectTreeFinderResultFutureIntern.SearchingObjectTree", taskDescr);
      _session.getSessionPanel().setStatusBarProgress(msg,0, MAX_PROGRESS, ++_progressValue % MAX_PROGRESS, _stopActionListener);
   }

   private void onStop()
   {
      disposeTimer();
   }

   private void onTimerTriggered()
   {
      long thisTriggerEventsStartTime = System.currentTimeMillis();

      int counter = 0;
      while(hasNextTask())
      {
         ObjectTreeFinderTaskInfo taskInfo = nextTask();
         taskInfo.getTask().exec();
         if(null != _treePath)
         {
            // We quit on first result.
            // If one day we want to find more result don't quit here but collect.
            break;
         }


         ++counter;
         if(System.currentTimeMillis() - thisTriggerEventsStartTime> 50)
         {
            //System.out.println("Did " + counter + " Tasks in " + (System.currentTimeMillis() - thisTriggerEventsStartTime) + " millis");
            increaseProgress(taskInfo.getDescr());
            // Wait for next trigger
            return;
         }
      }

      //System.out.println("Finished running with timer");
      fireFinished();
   }

   private ObjectTreeFinderTaskInfo nextTask()
   {
      if(false == hasNextTask())
      {
         throw new IllegalStateException("No more tasks.");
      }

      return _taskList.get(_taskIx++);
   }

   private boolean hasNextTask()
   {
      return _taskList.size() > _taskIx;
   }

   private void fireFinished()
   {
      disposeTimer();
      for (ObjectTreeFinderFinishListener listener : _listeners.toArray(new ObjectTreeFinderFinishListener[0]))
      {
         listener.finderFinished(_treePath);
      }

      _isFinished = true;
   }

   private void disposeTimer()
   {
      _timer.stop();
      Arrays.stream(_timer.getActionListeners()).forEach(al -> _timer.removeActionListener(al));
      if (null != _session.getSessionPanel())
      {
         _session.getSessionPanel().setStatusBarProgressFinished();
      }
   }

}
