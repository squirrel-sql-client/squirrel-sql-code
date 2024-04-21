package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.ConnectToAliasCallBack;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionJsonBean;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionLoader;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MultipleSavedSessionOpener
{
   public static void openSavedSessions(List<SavedSessionJsonBean> savedSessionsToOpen)
   {
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
            }

            @Override
            public void errorOccured(Throwable th, boolean connectingHasBeenCanceledByUser)
            {
               calledbackSessionsCounter.incrementAndGet();
               super.errorOccured(th, connectingHasBeenCanceledByUser);

               if(savedSessionsToOpen.size() == calledbackSessionsCounter.incrementAndGet())
               {
                  loadSavedSessionIntoOpenSession(sessionFramesToLoad);
               }
            }
         };

         new ConnectToAliasCommand(alias, true, callback).execute();

      }
   }

   private static void loadSavedSessionIntoOpenSession(List<Pair<SessionInternalFrame, SavedSessionJsonBean>> sessionFramesToLoad)
   {
      for (Pair<SessionInternalFrame, SavedSessionJsonBean> pair : sessionFramesToLoad)
      {
         SavedSessionLoader.load(pair.getLeft(), pair.getRight());

         if(pair.getRight().isActiveSessionInGroup())
         {
            Main.getApplication().getSessionManager().setActiveSession(pair.getLeft().getSession(), false);
         }
      }

      // was seen to help GC, shrug.
      sessionFramesToLoad.clear();
   }
}
