package net.sourceforge.squirrel_sql.client.mainframe.action.findaliases;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.AliasesListInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.IAliasesList;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewAliasesAction;

import java.awt.event.ActionEvent;

public class AliasesUtil
{
   public static void viewInAliasesDockWidget(SQLAlias aliasToView)
   {
      viewInAliasesDockWidget(aliasToView, Main.getApplication().getWindowManager().getAliasesListInternalFrame().getAliasesList(), AliasesUtil.class);
   }

   public static void viewInAliasesDockWidget(SQLAlias selectedAlias, IAliasesList al, java.lang.Object eventSource)
   {
      viewInAliasesDockWidget(new AliasSearchWrapper(selectedAlias), al, eventSource);
   }

   public static void viewInAliasesDockWidget(AliasSearchWrapper aliasSearchWrapper, IAliasesList al, java.lang.Object eventSource)
   {
      IApplication app = Main.getApplication();
      AliasesListInternalFrame aliasesFrame = Main.getApplication().getWindowManager().getAliasesListInternalFrame();

      new ViewAliasesAction(app, aliasesFrame).actionPerformed(new ActionEvent(eventSource, 1, "Dummy"));

      if (null != aliasSearchWrapper.getAlias())
      {
         al.goToAlias(aliasSearchWrapper.getAlias());
      }
      else
      {
         al.goToAliasFolder(aliasSearchWrapper.getAliasFolder());
      }
   }
}
