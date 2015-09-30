package org.squirrelsql.session.objecttree;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.Session;

public class TreeDetailsController
{
   private TabPane _objectTreeDetailsTabPane = new TabPane();
   private I18n _i18n = new I18n(getClass());
   private Session _session;

   public TreeDetailsController(TreeView<ObjectTreeNode> objectsTree, Session session)
   {
      _session = session;
      objectsTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ObjectTreeNode>>()
      {
         @Override
         public void changed(ObservableValue<? extends TreeItem<ObjectTreeNode>> observable, TreeItem<ObjectTreeNode> oldValue, TreeItem<ObjectTreeNode> newValue)
         {
            onTreeNodeSelected(newValue);
         }
      });
   }

   private void onTreeNodeSelected(TreeItem<ObjectTreeNode> selectedItem)
   {
      _objectTreeDetailsTabPane.getTabs().clear();

      if (null == selectedItem)
      {
         return;
      }


      if (ObjectTreeNodeTypeKey.ALIAS_TYPE_KEY.equals(selectedItem.getValue().getTypeKey()))
      {
         addTreeDetailsTab(_i18n.t("objecttree.details.alias.metadata"), () -> _session.getSchemaCacheValue().get().getDatabaseMetaData());
         addTreeDetailsTab(_i18n.t("objecttree.details.alias.dataTypes"), () -> _session.getSchemaCacheValue().get().getTypes());
         addTreeDetailsTab(_i18n.t("objecttree.details.alias.numericFunctions"), () -> _session.getSchemaCacheValue().get().getNumericFunctions());
         addTreeDetailsTab(_i18n.t("objecttree.details.alias.stringFunctions"), () -> _session.getSchemaCacheValue().get().getStringFunctions());
         addTreeDetailsTab(_i18n.t("objecttree.details.alias.systemFunctions"), () -> _session.getSchemaCacheValue().get().getSystemFunctions());
         addTreeDetailsTab(_i18n.t("objecttree.details.alias.timedateFunctions"), () -> _session.getSchemaCacheValue().get().getTimeDateFunctions());
         addTreeDetailsTab(_i18n.t("objecttree.details.alias.keywords"), () -> _session.getSchemaCacheValue().get().getKeywords());
      }
      else if(ObjectTreeNodeTypeKey.TABLE_TYPE_KEY.equals(selectedItem.getValue().getTypeKey()))
      {
         addTreeDetailsTab(_i18n.t("objecttree.details.table.columns"), () -> _session.getSchemaCacheValue().get().getColumnsAsTableLoader(selectedItem.getValue().getTableInfo()));
         //addTreeDetailsTab(_i18n.t("objecttree.details.table.columns"), TableDetailsReader.readColumns(_session, selectedItem.getValue()));

         addTreeDetailsTab(_i18n.t("objecttree.details.table.content"), () -> TableDetailsReader.readContent(_session, selectedItem.getValue()));

         addTreeDetailsTab(_i18n.t("objecttree.details.table.primaryKey"), () -> TableDetailsReader.readPrimaryKey(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.exportedKeys"), () -> TableDetailsReader.readExportedKeys(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.importedKeys"), () -> TableDetailsReader.readImportedKeys(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.indexes"), () -> TableDetailsReader.readIndexes(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.tablePrivileges"), () -> TableDetailsReader.readTablePrivileges(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.columnPrivileges"), () -> TableDetailsReader.readColumnPrivileges(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.bestRowIdentifier"), () -> TableDetailsReader.readBestRowIdentifier(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.versionColumns"), () -> TableDetailsReader.readVersionColumns(_session, selectedItem.getValue()));
      }

   }


   private void addTreeDetailsTab(String tabName, ObjectTreeTableLoaderFactory objectTreeTableLoaderFactory)
   {

      TreeDetailsTabController treeDetailsTabController = new TreeDetailsTabController(tabName, objectTreeTableLoaderFactory);
      _objectTreeDetailsTabPane.getTabs().add(treeDetailsTabController.getTab());
   }


   public Node getComponent()
   {
      return _objectTreeDetailsTabPane;
   }
}
