package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;

import java.awt.event.ActionEvent;

public class FindColumnAction extends SquirrelAction
{
   private ResultTab _resultTab;

   public FindColumnAction(ResultTab resultTab)
   {
      super(Main.getApplication(), Main.getApplication().getResources());
      _resultTab = resultTab;
   }

   public void actionPerformed(ActionEvent evt)
   {
      _resultTab.findColumn();
   }
}
