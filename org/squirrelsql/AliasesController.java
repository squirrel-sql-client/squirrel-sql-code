package org.squirrelsql;

import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class AliasesController
{
   private TextArea _node = new TextArea("Aliases");

   public Node getNode()
   {
      return _node;
   }
}
