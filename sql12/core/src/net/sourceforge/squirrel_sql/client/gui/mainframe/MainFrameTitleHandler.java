package net.sourceforge.squirrel_sql.client.gui.mainframe;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

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
      String title = s_stringMgr.getString("MainFrame.title.version.userdir", Version.getVersion(), new ApplicationFiles().getUserSettingsDirectory());

      final ISession activeSession = Main.getApplication().getSessionManager().getActiveSession();

      if(false == allClosed && null != activeSession && null != activeSession.getSavedSession())
      {
         title += " / " + s_stringMgr.getString("MainFrameTitleHandler.saved.session", activeSession.getSavedSession().getName());
      }
      _mainFrame.setTitle(title);
   }

}
