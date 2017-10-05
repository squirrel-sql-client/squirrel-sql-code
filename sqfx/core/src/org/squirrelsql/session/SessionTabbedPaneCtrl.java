package org.squirrelsql.session;

import javafx.scene.Node;
import javafx.scene.control.TabPane;
import org.squirrelsql.session.sql.NewSqlTabCtrl;

public class SessionTabbedPaneCtrl
{
   private TabPane _tabPane = new TabPane();

   public Node getNode()
   {
      return _tabPane;
   }


   public SessionTabAdmin addSessionTab(SessionCtrl sessionCtrl)
   {
      SessionTabAdmin tabAdmin = sessionCtrl.getSessionTabAdmin();
      return addTabAndSelect(tabAdmin);
   }

   public SessionTabAdmin addSqlTab(NewSqlTabCtrl newSqlTabCtrl)
   {
      SessionTabAdmin tabAdmin = newSqlTabCtrl.getSessionTabAdmin();
      return addTabAndSelect(tabAdmin);
   }

   private SessionTabAdmin addTabAndSelect(SessionTabAdmin sessionTabAdmin)
   {
      _tabPane.getTabs().add(sessionTabAdmin.getTab());
      _tabPane.getSelectionModel().select(sessionTabAdmin.getTab());
      return sessionTabAdmin;
   }

}
