package net.sourceforge.squirrel_sql.client.gui.db.recentalias;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewInAliasesAction extends SquirrelAction
{
   public ViewInAliasesAction()
   {
      super(Main.getApplication());
   }

   /**
    * Just a dummy action, see {@link RecentAliasesListCtrl#proxy(Class, ActionListener)}
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
   }
}
