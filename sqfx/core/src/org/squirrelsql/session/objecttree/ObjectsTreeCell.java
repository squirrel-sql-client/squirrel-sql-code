package org.squirrelsql.session.objecttree;

import javafx.scene.control.TreeCell;

public class ObjectsTreeCell extends TreeCell<ObjectTreeNode>
{

   @Override
   protected void updateItem(ObjectTreeNode objectTreeNode, boolean empty)
   {
      super.updateItem(objectTreeNode, empty);

      if(empty)
      {
         setText(null);
         setGraphic(null);
         return;
      }

      setText(objectTreeNode.getNodeName());
      setGraphic(objectTreeNode.getImageView());

   }

}
