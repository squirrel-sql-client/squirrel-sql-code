package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.recentfiles.RecentFilesController;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.event.ActionEvent;
import java.io.File;


public class FileOpenRecentAction extends SquirrelAction  implements ISQLPanelAction
{
   private ISQLPanelAPI _panel;

   public FileOpenRecentAction(IApplication app)
   {
      super(app);
   }

   public void actionPerformed(ActionEvent e)
   {
      if(null == _panel)
      {
         return;
      }

      File fileToOpen = new RecentFilesController(_panel).getFileToOpen();

      System.out.println("fileToOpen = " + fileToOpen);
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _panel = panel;
      setEnabled(null != _panel);


   }
}
