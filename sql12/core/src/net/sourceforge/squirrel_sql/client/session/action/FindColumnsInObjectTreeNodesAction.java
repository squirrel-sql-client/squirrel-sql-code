package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.action.findcolums.FindColumnsCtrl;
import net.sourceforge.squirrel_sql.client.session.action.findcolums.FindColumnsScope;

import java.awt.event.ActionEvent;

public class FindColumnsInObjectTreeNodesAction extends SquirrelAction implements IObjectTreeAction
{
   private IObjectTreeAPI _tree;
   public FindColumnsInObjectTreeNodesAction(IApplication app)
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

      new FindColumnsCtrl(new FindColumnsScope(_tree));
   }
}
