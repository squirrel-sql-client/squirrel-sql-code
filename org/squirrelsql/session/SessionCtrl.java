package org.squirrelsql.session;

import javafx.scene.Node;
import javafx.scene.control.*;
import org.squirrelsql.AppState;
import org.squirrelsql.Props;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.objecttree.*;

public class SessionCtrl
{
   private final Session _session;

   private I18n _i18n = new I18n(getClass());

   private Props _props = new Props(getClass());
   private TabPane _sessionTabPane;

   public SessionCtrl(DbConnectorResult dbConnectorResult)
   {
      _session = new Session(dbConnectorResult);

      AppState.get().addApplicationCloseListener(this::onClose);

      _sessionTabPane = new TabPane();

      Tab objectsTab = createObjectsTab();
      _sessionTabPane.getTabs().add(objectsTab);




      Tab sqlTab = new Tab(_i18n.t("session.tab.sql"));
      sqlTab.setClosable(false);
      _sessionTabPane.getTabs().add(sqlTab);

   }

   private Tab createObjectsTab()
   {
      Tab objectsTab = new Tab(_i18n.t("session.tab.objects"));
      objectsTab.setClosable(false);

      TreeView<ObjectTreeNode> objectsTree = new TreeView();

      objectsTree.setCellFactory(cf -> new ObjectsTreeCell());

      AliasCatalogsSchemasAndTypesCreator.createNodes(objectsTree, _session);

      TablesProceduresAndUDTsCreator.createNodes(objectsTree, _session);


      objectsTab.setContent(objectsTree);

      return objectsTab;
   }


   public Node getTabHeaderNode()
   {
      Alias alias = _session.getAlias();
      return new Label(_i18n.t("session.tab.header", alias.getName(), alias.getUserName()));
   }

   public Node getTabNode()
   {
      return _sessionTabPane;
   }

   public void setSessionTab(Tab sessionTab)
   {
      sessionTab.setOnClosed(e -> onClose());
   }

   private void onClose()
   {
      _session.close();
   }
}
