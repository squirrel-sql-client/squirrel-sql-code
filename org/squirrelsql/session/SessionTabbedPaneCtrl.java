package org.squirrelsql.session;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;

public class SessionTabbedPaneCtrl
{
   private TabPane _tabPane = new TabPane();

   public Node getNode()
   {
      return _tabPane;
   }


   public void addSessionTab(SessionCtrl sessionCtrl)
   {
      Tab tab = sessionCtrl.getSessionTab();
      _tabPane.getTabs().add(tab);
   }
}
