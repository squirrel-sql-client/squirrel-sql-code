package org.squirrelsql.aliases;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;

public class DndDragPositionMarkableTreeCell<T> extends TreeCell<T>
{

   public ObservableList<Node> getChildrenModifiable()
   {
      return getChildren();
   }

}
