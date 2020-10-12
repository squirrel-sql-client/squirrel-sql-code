package net.sourceforge.squirrel_sql.client.session.action.file;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.filemanager.FileHandler;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;

import java.awt.event.ActionEvent;

public class FileSaveAllAction extends SquirrelAction  implements ISessionAction
{
   private ISession _session;

   public FileSaveAllAction()
   {
      super(Main.getApplication());
      setEnabled(false);
   }

   public void actionPerformed(ActionEvent evt)
   {
      for (ISession session : Main.getApplication().getSessionManager().getConnectedSessions())
      {
         for (SQLPanel sqlPanel : session.getSessionPanel().getAllSQLPanels())
         {
            final FileHandler fileHandler = sqlPanel.getSQLPanelAPI().getFileHandler();
            if(null != fileHandler.getFile())
            {
               fileHandler.fileSave();
            }
         }

         for (ISessionWidget sessionWidget : Main.getApplication().getWindowManager().getAllFramesOfSession(session.getIdentifier()))
         {
            if(sessionWidget instanceof SQLInternalFrame)
            {
               final FileHandler fileHandler = ((SQLInternalFrame) sessionWidget).getMainSQLPanelAPI().getFileHandler();
               if(null != fileHandler.getFile())
               {
                  fileHandler.fileSave();
               }
            }
         }
      }

   }

   @Override
   public void setSession(ISession session)
   {
      _session = session;
      setEnabled(null != _session);
   }
}
