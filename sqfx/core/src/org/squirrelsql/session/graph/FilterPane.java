package org.squirrelsql.session.graph;

import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;

public class FilterPane extends BorderPane
{
   public FilterPane(ColumnConfigurationListener columnConfigurationListener)
   {
      ImageView imageView = new ImageView(new Props(getClass()).getImage("filter.gif"));
      setCenter(imageView);
   }
}
