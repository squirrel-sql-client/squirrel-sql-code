package org.squirrelsql.session;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;
import org.squirrelsql.session.sql.SqlTabController;

public class SessionTabbedPaneCtrl
{
   private TabPane _tabPane = new TabPane();

   public Node getNode()
   {
      return _tabPane;
   }


   public Tab addSessionTab(SessionCtrl sessionCtrl)
   {
      Tab tab = sessionCtrl.getSessionTab();
      return addTabAndSelect(tab);
   }

   public Tab addSqlTab(SqlTabController sqlTabController)
   {
      Tab tab = sqlTabController.getSqlTab();
      return addTabAndSelect(tab);
   }

   private Tab addTabAndSelect(Tab tab)
   {
      _tabPane.getTabs().add(tab);
      _tabPane.getSelectionModel().select(tab);
      return tab;
   }

}
