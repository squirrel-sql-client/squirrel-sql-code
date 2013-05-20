package org.squirrelsql;

import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class DriversController
{
   private TextArea _node = new TextArea("Drivers");

   public Node getNode()
   {
      return _node;
   }
}
