package org.squirrelsql.session.sql;

import javafx.scene.control.Tab;
import javafx.scene.input.KeyEvent;
import org.squirrelsql.AppState;
import org.squirrelsql.session.SessionTabContext;
import org.squirrelsql.session.action.ActionHandle;
import org.squirrelsql.session.action.ActionManager;
import org.squirrelsql.session.action.StandardActionConfiguration;
import org.squirrelsql.workaround.KeyMatchWA;


public class NewSqlTabHelper
{
   public static void registerSessionTabListener(SessionTabContext sessionTabContext, Tab sessionTab)
   {
      registerListener(sessionTabContext, sessionTab);
   }

   public static void registerNewSqlTabListener(SessionTabContext newSqlTabContext, Tab newSqlTab)
   {
      registerListener(newSqlTabContext, newSqlTab);
   }

   private static void registerListener(SessionTabContext sessionTabContext, Tab sessionTab)
   {
      ActionHandle actionHandle = new ActionManager().getActionHandle(StandardActionConfiguration.NEW_SQL_TAB, sessionTabContext);
      actionHandle.setOnAction(() -> openNewSqlTab(sessionTabContext));
      sessionTab.getContent().setOnKeyTyped(e -> onKeyTyped((KeyEvent) e, sessionTabContext));
   }

   private static void onKeyTyped(KeyEvent event, SessionTabContext sessionTabContext)
   {
      if(KeyMatchWA.matches(event, StandardActionConfiguration.NEW_SQL_TAB.getActionConfiguration().getKeyCodeCombination()))
      {
         openNewSqlTab(sessionTabContext);
         event.consume();
      }
   }

   private static void openNewSqlTab(SessionTabContext sessionTabContext)
   {
      AppState.get().getSessionManager().createSqlTab(sessionTabContext);
   }

}
