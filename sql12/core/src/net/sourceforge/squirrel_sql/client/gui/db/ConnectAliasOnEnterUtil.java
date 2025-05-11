package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasAction;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ConnectAliasOnEnterUtil
{
   static void connectToSelectedAlias(KeyEvent e)
   {
      ActionEvent dummyActionEvent = new ActionEvent(e.getSource(), e.getID(), "EnterOnAlias");
      Main.getApplication().getActionCollection().get(ConnectToAliasAction.class).actionPerformed(dummyActionEvent);
   }
}
