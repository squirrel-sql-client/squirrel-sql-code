package org.squirrelsql.session;

import javafx.scene.Node;
import javafx.scene.control.*;
import org.squirrelsql.session.sql.NewSqlTabCtrl;

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

   public Tab addSqlTab(NewSqlTabCtrl newSqlTabCtrl)
   {
      Tab tab = newSqlTabCtrl.getSqlTab();
      return addTabAndSelect(tab);
   }

   private Tab addTabAndSelect(Tab tab)
   {
      _tabPane.getTabs().add(tab);
      _tabPane.getSelectionModel().select(tab);
      return tab;
   }

}
