package org.squirrelsql.session.objecttree;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.Session;
import org.squirrelsql.table.TableLoader;

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

      if (ObjectTreeNodeTypeKey.ALIAS_TYPE_KEY.equals(selectedItem.getValue().getTypeKey()))
      {
         addTreeDetailsTab(_i18n.t("objecttree.details.alias.metadata"), _session.getSchemaCache().getDatabaseMetaData());
         addTreeDetailsTab(_i18n.t("objecttree.details.alias.dataTypes"), _session.getSchemaCache().getTypes());
         addTreeDetailsTab(_i18n.t("objecttree.details.alias.numericFunctions"), _session.getSchemaCache().getNumericFunctions());
         addTreeDetailsTab(_i18n.t("objecttree.details.alias.stringFunctions"), _session.getSchemaCache().getStringFunctions());
         addTreeDetailsTab(_i18n.t("objecttree.details.alias.systemFunctions"), _session.getSchemaCache().getSystemFunctions());
         addTreeDetailsTab(_i18n.t("objecttree.details.alias.timedateFunctions"), _session.getSchemaCache().getTimeDateFunctions());
         addTreeDetailsTab(_i18n.t("objecttree.details.alias.keywords"), _session.getSchemaCache().getKeywords());
      }
      else if(ObjectTreeNodeTypeKey.TABLE_TYPE_KEY.equals(selectedItem.getValue().getTypeKey()))
      {
         addTreeDetailsTab(_i18n.t("objecttree.details.table.columns"), TableDetailsReader.readColumns(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.content"), TableDetailsReader.readContent(_session, selectedItem.getValue()));

         addTreeDetailsTab(_i18n.t("objecttree.details.table.primaryKey"), TableDetailsReader.readPrimaryKey(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.exportedKeys"), TableDetailsReader.readExportedKeys(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.importedKeys"), TableDetailsReader.readImportedKeys(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.indexes"), TableDetailsReader.readIndexes(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.tablePrivileges"), TableDetailsReader.readTablePrivileges(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.columnPrivileges"), TableDetailsReader.readColumnPrivileges(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.bestRowIdentifier"), TableDetailsReader.readBestRowIdentifier(_session, selectedItem.getValue()));
         addTreeDetailsTab(_i18n.t("objecttree.details.table.versionColumns"), TableDetailsReader.readVersionColumns(_session, selectedItem.getValue()));
      }

   }


   private void addTreeDetailsTab(String tabName, TableLoader tableLoader)
   {
      Tab tab = new Tab(tabName);
      tab.setClosable(false);
      _objectTreeDetailsTabPane.getTabs().add(tab);

      TableView tableView = new TableView();
      tableLoader.load(tableView);
      tab.setContent(tableView);
   }


   public Node getComponent()
   {
      return _objectTreeDetailsTabPane;
   }
}
