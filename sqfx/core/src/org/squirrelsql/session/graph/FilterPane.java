package org.squirrelsql.session.graph;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;

public class FilterPane extends BorderPane
{
   private ColumnConfiguration _columnConfiguration;

   public FilterPane(ColumnConfiguration columnConfiguration)
   {
      _columnConfiguration = columnConfiguration;
      ImageView imageView = new ImageView(new Props(getClass()).getImage("filter.gif"));
      setCenter(imageView);
      addEventHandler(MouseEvent.MOUSE_PRESSED, e -> onFilterCtrl());
   }

   private void onFilterCtrl()
   {
      new FilterCtrl(_columnConfiguration.getFilterData());
   }
}
