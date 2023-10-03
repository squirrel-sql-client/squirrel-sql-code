package net.sourceforge.squirrel_sql.client.session.action.sqlscript;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.SQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.sqltofile.SQLToFileHandler;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.*;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.util.List;

public class SQLScriptMenuFactory
{
   private final static StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLScriptMenuFactory.class);

   public static JMenu getObjectTreeMenu(DatabaseObjectType databaseObjectType)
   {
      if(databaseObjectType != DatabaseObjectType.TABLE && databaseObjectType != DatabaseObjectType.VIEW)
      {
         throw new IllegalStateException("Call for DatabaseObjectType.TABLE and DatabaseObjectType.VIEW only.");
      }

      IApplication app = Main.getApplication();
      ActionCollection coll = app.getActionCollection();
      SquirrelResources resources = Main.getApplication().getResources();

      JMenu menu = new JMenu(s_stringMgr.getString("SQLScriptMenuFactory.objecttree.menu.title"));
      resources.addToMenu(coll.get(CreateDataScriptAction.class), menu);
      resources.addToMenu(coll.get(CreateTemplateDataScriptAction.class), menu);
      resources.addToMenu(coll.get(CreateTableScriptAction.class), menu);
      resources.addToMenu(coll.get(CreateSelectScriptAction.class), menu);
      resources.addToMenu(coll.get(CreateFileOfSelectedTablesAction.class), menu);
      resources.addToMenu(coll.get(CreateInsertStatementsFileOfSelectedTablesSQLAction.class), menu);

      if (databaseObjectType == DatabaseObjectType.TABLE)
      {
         resources.addToMenu(coll.get(DropTableScriptAction.class), menu);
      }
      return menu;
   }

   public static JMenu getSessionMenu()
   {
      IApplication app = Main.getApplication();
      ActionCollection coll = app.getActionCollection();

      SquirrelResources resources = Main.getApplication().getResources();
      JMenu menu = new JMenu(s_stringMgr.getString("SQLScriptMenuFactory.session.menu.title"));
      resources.addToMenu(coll.get(CreateDataScriptAction.class), menu);
      resources.addToMenu(coll.get(CreateTemplateDataScriptAction.class), menu);
      resources.addToMenu(coll.get(CreateTableScriptAction.class), menu);
      resources.addToMenu(coll.get(CreateSelectScriptAction.class), menu);
      resources.addToMenu(coll.get(DropTableScriptAction.class), menu);
      resources.addToMenu(coll.get(CreateDataScriptOfCurrentSQLAction.class), menu);
      resources.addToMenu(coll.get(CreateInsertStatementsFileOfCurrentSQLAction.class), menu);
      resources.addToMenu(coll.get(CreateTableOfCurrentSQLAction.class), menu);
      resources.addToMenu(coll.get(CreateFileOfCurrentSQLAction.class), menu);
      return menu;
   }

   public static void addMenuItemsToSQLPanelApi(SQLPanelAPI sqlPanelAPI)
   {
      JMenuItem mnu;
      sqlPanelAPI.addSeparatorToSQLEntryAreaMenu();

      IApplication app = Main.getApplication();
      mnu = sqlPanelAPI.addToSQLEntryAreaMenu(app.getActionCollection().get(CreateTableOfCurrentSQLAction.class));
      app.getResources().configureMenuItem(app.getActionCollection().get(CreateTableOfCurrentSQLAction.class), mnu);

      mnu = sqlPanelAPI.addToSQLEntryAreaMenu(app.getActionCollection().get(CreateDataScriptOfCurrentSQLAction.class));
      app.getResources().configureMenuItem(app.getActionCollection().get(CreateDataScriptOfCurrentSQLAction.class), mnu);

      mnu = sqlPanelAPI.addToSQLEntryAreaMenu(app.getActionCollection().get(CreateInsertStatementsFileOfCurrentSQLAction.class));
      app.getResources().configureMenuItem(app.getActionCollection().get(CreateInsertStatementsFileOfCurrentSQLAction.class), mnu);

      mnu = sqlPanelAPI.addToSQLEntryAreaMenu(app.getActionCollection().get(CreateFileOfCurrentSQLAction.class));
      app.getResources().configureMenuItem(app.getActionCollection().get(CreateFileOfCurrentSQLAction.class), mnu);

      sqlPanelAPI.addSQLExecutionListener(new SQLToFileHandler(sqlPanelAPI.getSession(), sqlPanelAPI));
   }

   public static List<Action> getSessionToolbarActions()
   {
      ActionCollection coll = Main.getApplication().getActionCollection();
      return List.of(coll.get(CreateTableOfCurrentSQLAction.class), coll.get(CreateFileOfCurrentSQLAction.class));
   }

   public static List<Action> getSQLInternalFrameToolbarActions()
   {
      return getSessionToolbarActions();
   }
}
