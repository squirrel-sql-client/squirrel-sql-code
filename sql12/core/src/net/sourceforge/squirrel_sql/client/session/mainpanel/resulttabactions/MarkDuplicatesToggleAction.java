package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions;

import java.awt.event.ActionEvent;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;

public class MarkDuplicatesToggleAction extends SquirrelAction implements ISQLPanelAction
{
   private ResultTabProvider _resultTabProvider;

   public MarkDuplicatesToggleAction(IResultTab resultTab)
   {
      super(Main.getApplication(), Main.getApplication().getResources());
      _resultTabProvider = new ResultTabProvider(resultTab);
   }

   /**
    * Just to load this action into Session menu.
    */
   public MarkDuplicatesToggleAction()
   {
      this(null);
   }


   @Override
   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _resultTabProvider.setSQLPanelAPI(panel);
      doEnable();
   }

   public void setResultTab(IResultTab resultTab)
   {
      _resultTabProvider.setResultTab(resultTab);
      doEnable();
   }

   private void doEnable()
   {
      setEnabled(_resultTabProvider.hasResultTab());
   }

   public void actionPerformed(ActionEvent evt)
   {
      if(_resultTabProvider.hasResultTab())
      {
         _resultTabProvider.getResultTab().markDuplicates(evt);
      }
   }
}
