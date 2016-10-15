package org.squirrelsql.session.graph;

import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;

public class FilterCtrl extends BorderPane
{
   public FilterCtrl()
   {
      ImageView imageView = new ImageView(new Props(getClass()).getImage("filter.gif"));
      setCenter(imageView);
   }
}
