package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeFinderFinishListener;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Note: NO THREADING is involved in this class.
 * Everything is done on the Event Dispatch Thread.
 */
class ObjectTreeFinderResultFutureIntern implements ObjectTreeFinderResultFuture
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ObjectTreeFinderResultFutureIntern.class);
   public static final int MAX_PROGRESS = 50;

   private TreePath _treePath;
   private ISession _session;
   private final long _startTime;

   private ArrayList<ObjectTreeFinderTask> _taskList = new ArrayList<>();

   private boolean _isExecuting = false;
   private boolean _isFinished = false;

   private Timer _timer = new Timer(100, e -> onTimerTriggered());
   private int _taskIx = 0;
   private ArrayList<ObjectTreeFinderFinishListener> _listeners = new ArrayList<>();
   private int _progressValue;


   ObjectTreeFinderResultFutureIntern(ISession session)
   {
      _session = session;
      _startTime = System.currentTimeMillis();

      _session.addSimpleSessionListener(() -> disposeTimer());
   }

   void setTreePath(TreePath treePath)
   {
      System.out.println("Found treePath = " + treePath);
      _treePath = treePath;
   }

   @Override
   public TreePath getTreePath()
   {
      return _treePath;
   }

   @Override
   public void addListenerOrdered(ObjectTreeFinderFinishListener listener)
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
         nextTask().exec();
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
   void addTask(ObjectTreeFinderTask task)
   {
      _taskList.add(task);
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
         nextTask().exec();
         if(null != _treePath)
         {
            // We quit on first result.
            // If one day we want to find more result don't quit here but collect.
            break;
         }

         if(System.currentTimeMillis() - _startTime > 500)
         {
            _timer.start();
            _progressValue = 4;
            increaseProgress();
            System.out.println("Entering timer");
            return;
         }
      }

      // Because of th return in the loop we will reach here only when the timer wasn't used.
      System.out.println("Finishing without timer");
      fireFinished();
   }

   private void increaseProgress()
   {
      _session.getSessionPanel().setStatusBarProgress(s_stringMgr.getString("ObjectTreeFinderResultFutureIntern.SearchingObjectTree"), 0, MAX_PROGRESS, ++_progressValue % MAX_PROGRESS);
   }

   private void onTimerTriggered()
   {
      while(hasNextTask())
      {
         nextTask().exec();
         if(null != _treePath)
         {
            // We quit on first result.
            // If one day we want to find more result don't quit here but collect.
            break;
         }

         increaseProgress();

         if(_startTime - System.currentTimeMillis() > 30);
         {
            // Wait for next trigger
            return;
         }
      }

      System.out.println("Finished running with timer");
      fireFinished();
   }

   private ObjectTreeFinderTask nextTask()
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
      _session.getSessionPanel().setStatusBarProgressFinished();
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
   }

}
