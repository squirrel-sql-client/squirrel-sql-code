package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;

public class CopyRestoreSelectionMenuFactory
{
   private final static StringManager s_stringMgr = StringManagerFactory.getStringManager(CopyRestoreSelectionMenuFactory.class);

   public static JMenu getObjectTreeMenu()
   {
      ActionCollection coll = Main.getApplication().getActionCollection();
      SquirrelResources resources = Main.getApplication().getResources();

      JMenu menu = new JMenu(s_stringMgr.getString("CopyRestoreSelectionMenuFactory.objecttree.menu.title"));
      resources.addToMenu(coll.get(CopyObjectTreeSelectionToClipAction.class), menu);
      resources.addToMenu(coll.get(ApplyObjectTreeSelectionFromClipAction.class), menu);
      resources.addToMenu(coll.get(StoreObjectTreeSelectionAction.class), menu);
      resources.addToMenu(coll.get(StoreObjectTreeSelectionNamedAction.class), menu);
      resources.addToMenu(coll.get(ApplyStoredObjectTreeSelectionAction.class), menu);

      return menu;
   }
}
