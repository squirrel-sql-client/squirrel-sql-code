package org.squirrelsql.services.dndpositionmarker;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public interface ModifiableChildrenAccessor
{
   public ObservableList<Node> getChildrenModifiable();
}
