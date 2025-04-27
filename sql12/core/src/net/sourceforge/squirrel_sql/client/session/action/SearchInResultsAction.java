package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.globalsearch.GlobalSearchCtrl;

import java.awt.event.ActionEvent;

public class SearchInResultsAction extends SquirrelAction
{
   public SearchInResultsAction()
   {
      super(Main.getApplication());
   }


   public void actionPerformed(ActionEvent e)
   {
      new GlobalSearchCtrl();
   }
}
