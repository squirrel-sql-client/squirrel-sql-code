package net.sourceforge.squirrel_sql.client.session.action.dbdiff;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions.DBDiffCompareAction;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions.DBDiffSelectAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;

public class DBDiffObjectTreeMenuFactory
{
   private final static StringManager s_stringMgr = StringManagerFactory.getStringManager(DBDiffObjectTreeMenuFactory.class);
   public static JMenu createMenu()
   {
      final JMenu dbdiffMenu = new JMenu(s_stringMgr.getString("DBDiffObjectTreeMenuFactory.parent.menu.name"));

      ActionCollection coll = Main.getApplication().getActionCollection();

      final JMenuItem selectItem = new JMenuItem(coll.get(DBDiffSelectAction.class));
      final JMenuItem compareItem = new JMenuItem(coll.get(DBDiffCompareAction.class));
      dbdiffMenu.add(selectItem);
      dbdiffMenu.add(compareItem);

      return dbdiffMenu;
   }
}
