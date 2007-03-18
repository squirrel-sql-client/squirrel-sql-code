package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistoryController;
import net.sourceforge.squirrel_sql.client.IApplication;

import java.awt.event.ActionEvent;

public class OpenSqlHistoryAction extends SquirrelAction
   implements ISQLPanelAction
{
   private ISQLPanelAPI _panel;

   public OpenSqlHistoryAction(IApplication app)
   {
      super(app);
   }

   public void actionPerformed(ActionEvent e)
   {
      new SQLHistoryController(_panel.getSession(), _panel, _panel.getSQLHistoryItems());
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _panel = panel;

   }
}
