package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecuterPanelFacade;

import java.awt.event.ActionEvent;

public class CreateResultTabFrameAction extends SquirrelAction
{
   private SQLResultExecuterPanelFacade _sqlResultExecuterPanelFacade;
   private ResultTab _resultTab;

   public CreateResultTabFrameAction(SQLResultExecuterPanelFacade sqlResultExecuterPanelFacade, ResultTab resultTab)
   {
      super(Main.getApplication(), Main.getApplication().getResources());
      _sqlResultExecuterPanelFacade = sqlResultExecuterPanelFacade;
      _resultTab = resultTab;
   }

   public void actionPerformed(ActionEvent evt)
   {
      _sqlResultExecuterPanelFacade.createSQLResultFrame(_resultTab);
   }
}
