package org.squirrelsql.session;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.I18n;

public class SessionCtrl
{
   private DbConnectorResult _dbConnectorResult;

   private I18n _i18n = new I18n(getClass());

   public SessionCtrl(DbConnectorResult dbConnectorResult)
   {
      _dbConnectorResult = dbConnectorResult;
   }

   public Node getTabHeaderNode()
   {
      Alias alias = _dbConnectorResult.getAlias();
      return new Label(_i18n.t("session.tab.header", alias.getName(), alias.getUserName()));
   }

   public Node getTabNode()
   {
      TabPane tp = new TabPane();


      return new Label("Session goes here");
   }

   public void setSessionTab(Tab sessionTab)
   {
      sessionTab.setOnClosed(e -> onClose());
   }

   private void onClose()
   {
      _dbConnectorResult.getSQLConnection().close();
   }
}
