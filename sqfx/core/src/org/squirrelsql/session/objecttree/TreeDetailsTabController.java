package org.squirrelsql.session.objecttree;

import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import org.squirrelsql.session.sql.TableDecorator;
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
         TableLoader tableLoader = objectTreeTableLoaderFactory.createTableLoader();

         StackPane stackPane = TableDecorator.decorateNonSqlEditableTable(tableLoader);

         _tab.setContent(stackPane);
         _loaded = true;
      }
   }

   public Tab getTab()
   {
      return _tab;
   }
}
