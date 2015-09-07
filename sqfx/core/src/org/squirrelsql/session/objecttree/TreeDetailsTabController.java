package org.squirrelsql.session.objecttree;

import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import org.squirrelsql.table.TableLoader;

public class TreeDetailsTabController
{
   private final Tab _tab;
   private boolean _loaded;

   public TreeDetailsTabController(String tabName, TableLoader tableLoader)
   {
      _tab = new Tab(tabName);
      _tab.setClosable(false);

      _tab.tabPaneProperty().addListener((observable, oldValue, newValue) -> onSelected(tableLoader));
      _tab.setOnSelectionChanged(event -> onSelected(tableLoader));
   }

   private void onSelected(TableLoader tableLoader)
   {
      if(_tab.isSelected() && false == _loaded)
      {
         TableView tableView = new TableView();
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
