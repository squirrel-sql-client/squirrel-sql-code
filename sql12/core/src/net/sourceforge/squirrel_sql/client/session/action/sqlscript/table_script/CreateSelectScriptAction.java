package net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;

import java.awt.event.ActionEvent;

public class CreateSelectScriptAction extends SquirrelAction  implements IObjectTreeAction
{
   private IObjectTreeAPI _objectTreeAPI;

   public CreateSelectScriptAction()
   {
      super(Main.getApplication(), Main.getApplication().getResources());
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (_objectTreeAPI != null)
      {
         new CreateSelectScriptCommand(_objectTreeAPI).execute();
      }
   }

   public void setObjectTree(IObjectTreeAPI objectTreeAPI)
   {
      _objectTreeAPI = objectTreeAPI;
      setEnabled(null != _objectTreeAPI);
   }
}
