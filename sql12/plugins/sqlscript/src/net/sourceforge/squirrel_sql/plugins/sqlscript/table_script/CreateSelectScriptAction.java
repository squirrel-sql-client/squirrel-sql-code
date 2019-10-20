package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.fw.resources.IResources;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

public class CreateSelectScriptAction extends SquirrelAction  implements IObjectTreeAction
{

   private final SQLScriptPlugin _plugin;
   private IObjectTreeAPI _objectTreeAPI;

   public CreateSelectScriptAction(IApplication app, IResources resources, SQLScriptPlugin plugin)
   {
      super(app, resources);
      _plugin = plugin;
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (_objectTreeAPI != null)
      {
         new CreateSelectScriptCommand(_objectTreeAPI, _plugin).execute();
      }
   }

   public void setObjectTree(IObjectTreeAPI objectTreeAPI)
   {
      _objectTreeAPI = objectTreeAPI;
      setEnabled(null != _objectTreeAPI);
   }
}
