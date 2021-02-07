package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;

import java.awt.event.ActionEvent;

public class FindResultColumnAction extends SquirrelAction implements ISQLPanelAction
{
   private ResultTabProvider _resultTabProvider;

   public FindResultColumnAction(ResultTab resultTab)
   {
      super(Main.getApplication(), Main.getApplication().getResources());
      _resultTabProvider = new ResultTabProvider(resultTab);
   }

   public FindResultColumnAction()
   {
      this(null);
   }

   @Override
   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _resultTabProvider.setSQLPanelAPI(panel);
   }


   public void actionPerformed(ActionEvent evt)
   {
      if(_resultTabProvider.hasResultTab())
      {
         _resultTabProvider.getResultTab().findColumn();
      }
   }
}
