package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;

import java.awt.event.ActionEvent;

public class FindColumnsAction extends SquirrelAction implements IObjectTreeAction
{
   private IObjectTreeAPI _tree;

   public FindColumnsAction(IApplication app)
   {
      super(app);
   }

   public void setObjectTree(IObjectTreeAPI tree)
   {
      _tree = tree;
      setEnabled(null != _tree);
   }

   public void actionPerformed(ActionEvent e)
   {
      if (_tree == null)
      {
         return;
      }

      //new FindColumnsCtrl(_tree);
   }
}
