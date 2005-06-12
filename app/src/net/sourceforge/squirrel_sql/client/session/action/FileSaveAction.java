package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

import java.awt.event.ActionEvent;


public class FileSaveAction extends SquirrelAction  implements ISQLPanelAction
{
   private ISQLPanelAPI m_panel;

   public FileSaveAction(IApplication app)
   {
      super(app);
   }

   public void actionPerformed(ActionEvent e)
   {
      m_panel.fileSave();
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      m_panel = panel;
   }
}
