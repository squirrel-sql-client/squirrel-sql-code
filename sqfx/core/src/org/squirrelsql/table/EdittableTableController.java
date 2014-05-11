package org.squirrelsql.table;

import javafx.scene.control.TableView;

public class EdittableTableController
{
   public EdittableTableController(TableLoader tableLoader, TableView tv)
   {
      tableLoader.load(tv);
   }
}
