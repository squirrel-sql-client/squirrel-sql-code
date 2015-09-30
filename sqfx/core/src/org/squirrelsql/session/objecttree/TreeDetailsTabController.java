package org.squirrelsql.session.objecttree;

import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import org.squirrelsql.table.TableLoader;

public class TreeDetailsTabController
{
   private final Tab _tab;
   private boolean _loaded;

   public TreeDetailsTabController(String tabName, ObjectTreeTableLoaderFactory objectTreeTableLoaderFactory)
   {
      _tab = new Tab(tabName);
      _tab.setClosable(false);

      _tab.tabPaneProperty().addListener((observable, oldValue, newValue) -> onSelected(objectTreeTableLoaderFactory));
      _tab.setOnSelectionChanged(event -> onSelected(objectTreeTableLoaderFactory));
   }

   private void onSelected(ObjectTreeTableLoaderFactory objectTreeTableLoaderFactory)
   {
      if(_tab.isSelected() && false == _loaded)
      {
         TableView tableView = new TableView();
         TableLoader tableLoader = objectTreeTableLoaderFactory.createTableLoader();
         tableLoader.load(tableView);
         _tab.setContent(tableView);
         _loaded = true;
      }
   }

   public Tab getTab()
   {
      return _tab;
   }
}
