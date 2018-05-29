package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;

import java.awt.event.ActionEvent;

public class CloseAction extends SquirrelAction
{
   private ResultTab _resultTab;

   public CloseAction(ISession session, ResultTab resultTab)
   {
      super(session.getApplication(),session.getApplication().getResources());
      _resultTab = resultTab;
   }

   public void actionPerformed(ActionEvent evt)
   {
      _resultTab.closeTab();
   }
}
