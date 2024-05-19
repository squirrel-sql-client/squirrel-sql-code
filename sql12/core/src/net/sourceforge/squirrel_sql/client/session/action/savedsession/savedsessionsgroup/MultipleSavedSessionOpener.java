package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.gui.db.ConnectToAliasCallBack;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SQLEditorActivator;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionJsonBean;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionLoader;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionUtil;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MultipleSavedSessionOpener
{
   private static final ILogger s_log = LoggerController.createLogger(MultipleSavedSessionOpener.class);

   public static void openSavedSessions(List<SavedSessionJsonBean> savedSessionsToOpen)
   {
      ExecutorService executorService = Executors.newSingleThreadExecutor();

      executorService.submit(() -> _openSavedSessions(savedSessionsToOpen));
   }

   private static void _openSavedSessions(List<SavedSessionJsonBean> savedSessionsToOpen)
   {
      try
      {
         Thread theExecutorServicesThread = Thread.currentThread();

         AtomicInteger calledbackSessionsCounter = new AtomicInteger();

         List<Pair<SessionInternalFrame, SavedSessionJsonBean>> sessionFramesToLoad = new ArrayList<>();

         for (SavedSessionJsonBean savedSession : savedSessionsToOpen)
         {
            final SQLAlias alias = SavedSessionUtil.getAliasForIdString(savedSession.getDefaultAliasIdString());

            final ConnectToAliasCallBack callback = new ConnectToAliasCallBack(alias)
            {
               @Override
               public void sessionInternalFrameCreated(SessionInternalFrame sessionInternalFrame)
               {
                  sessionFramesToLoad.add(Pair.of(sessionInternalFrame, savedSession));
                  if(savedSessionsToOpen.size() == calledbackSessionsCounter.incrementAndGet())
                  {
                     loadSavedSessionIntoOpenSession(sessionFramesToLoad);
                  }
                  theExecutorServicesThread.interrupt();
               }

               @Override
               public void errorOccurred(Throwable th, boolean connectingHasBeenCanceledByUser)
               {
                  calledbackSessionsCounter.incrementAndGet();
                  super.errorOccurred(th, connectingHasBeenCanceledByUser);

                  if(savedSessionsToOpen.size() == calledbackSessionsCounter.incrementAndGet())
                  {
                     loadSavedSessionIntoOpenSession(sessionFramesToLoad);
                  }
                  theExecutorServicesThread.interrupt();
               }
            };

            SwingUtilities.invokeAndWait(() -> new ConnectToAliasCommand(alias, true, callback).execute());
            try
            {
               Thread.sleep(Integer.MAX_VALUE);
            }
            catch (InterruptedException e)
            {
               String msg = "MultipleSavedSessionOpener received interrupt to continue opening Sessions after Saved Session "
                     + (null == savedSession.getGroupId() ? savedSession.getName() : savedSession.getAliasNameForDebug())
                     + " was opened";

               s_log.info(msg);
            }
         }
      }
      catch (Exception e)
      {
         s_log.error(e);
      }
   }

   /**
    * Note that this method is called exactly once from each {@link #_openSavedSessions(List)} call
    */
   private static void loadSavedSessionIntoOpenSession(List<Pair<SessionInternalFrame, SavedSessionJsonBean>> sessionFramesToLoad)
   {
      SQLEditorActivator activatorOfActiveSessionInGroup = null;
      for (Pair<SessionInternalFrame, SavedSessionJsonBean> pair : sessionFramesToLoad)
      {
         SQLEditorActivator buf = SavedSessionLoader.load(pair.getLeft(), pair.getRight());
         if(pair.getRight().isActiveSessionInGroup())
         {
            activatorOfActiveSessionInGroup = buf;
         }
      }

      if(null != activatorOfActiveSessionInGroup)
      {
         SQLEditorActivator finalActivatorOfActiveSessionInGroup = activatorOfActiveSessionInGroup;
         SwingUtilities.invokeLater(() -> finalActivatorOfActiveSessionInGroup.activate());
         //GUIUtils.executeDelayed(() -> finalActivatorOfActiveSessionInGroup.activate());
      }

      // was seen to help GC, shrug.
      sessionFramesToLoad.clear();
   }
}
