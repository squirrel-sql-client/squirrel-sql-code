package net.sourceforge.squirrel_sql.client.gui.db.mainframetitle;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionUtil;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainFrameTitleHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MainFrameTitleHandler.class);
   private MainFrame _mainFrame;

   public MainFrameTitleHandler(MainFrame mainFrame)
   {
      _mainFrame = mainFrame;
      updateMainFrameTitle();

      Main.getApplication().getSessionManager().addSessionListener(new SessionAdapter(){
         @Override
         public void allSessionsClosed()
         {
            _updateMainFrameTitle(true);
         }

         @Override
         public void sessionActivated(SessionEvent evt)
         {
            _updateMainFrameTitle(false);
         }
      });
   }

   public void updateMainFrameTitle()
   {
      _updateMainFrameTitle(false);
   }

   private void _updateMainFrameTitle(boolean allClosed)
   {
      List<TitlePartWithPosition> titlePartWithPositions = new ArrayList<>();

      titlePartWithPositions.add(new TitlePartWithPosition(Version.getApplicationName(), PositionInMainFrameTitle.getApplicationNamePos()));

      titlePartWithPositions.add(new TitlePartWithPosition(Version.getVersionExclAppName(), PositionInMainFrameTitle.getVersionPos()));

      titlePartWithPositions.add(new TitlePartWithPosition(s_stringMgr.getString("MainFrame.title.homedir", new ApplicationFiles().getSquirrelHomeDir()),
                                                           PositionInMainFrameTitle.getHomeDirPos()));

      titlePartWithPositions.add(new TitlePartWithPosition(s_stringMgr.getString("MainFrame.title.userdir", new ApplicationFiles().getUserSettingsDirectory()),
                                                           PositionInMainFrameTitle.getUserDirPos()));

      final ISession activeSession = Main.getApplication().getSessionManager().getActiveSession();

      if(false == allClosed && null != activeSession )
      {
         if(null != activeSession.getSessionInternalFrame())
         {
            titlePartWithPositions.add(new TitlePartWithPosition(s_stringMgr.getString("MainFrame.title.session", activeSession.getSessionInternalFrame().getTitle()),
                                                                 PositionInMainFrameTitle.getSessionNamePos()));
         }
         if(null != activeSession.getSavedSession())
         {
            titlePartWithPositions.add(new TitlePartWithPosition(SavedSessionUtil.getMainFrameTitleString(activeSession.getSavedSession()),
                                                                 PositionInMainFrameTitle.getSavedSessionOrGroupNamePos()));
         }
      }

      _mainFrame.setTitle(buildTitle(titlePartWithPositions));
   }

   private String buildTitle(List<TitlePartWithPosition> titlePartWithPositions)
   {
      StringBuilder ret = new StringBuilder();

      titlePartWithPositions.sort(Comparator.comparingInt(e -> e.getPos().getOrder()));

      for(TitlePartWithPosition titlePartWithPosition : titlePartWithPositions)
      {
         if(titlePartWithPosition.getPos() == PositionInMainFrameTitle.POS_NONE)
         {
            break;
         }

         if(0 == ret.length())
         {
            ret.append(titlePartWithPosition.getTitlePart());
         }
         else
         {
            ret.append(" / ").append(titlePartWithPosition.getTitlePart());
         }
      }

      return ret.toString();
   }

}
