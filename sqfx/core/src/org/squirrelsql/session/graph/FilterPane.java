package org.squirrelsql.session.graph;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;
import org.squirrelsql.services.Utils;

public class FilterPane extends BorderPane
{
   private FilterPersistence _filterPersistence;

   public FilterPane(FilterPersistence filterPersistence)
   {
      _filterPersistence = filterPersistence;

      updateIcon();

      addEventHandler(MouseEvent.MOUSE_PRESSED, e -> onFilterCtrl());
   }

   private void onFilterCtrl()
   {
      new FilterCtrl(_filterPersistence);
      updateIcon();
   }

   private void updateIcon()
   {
      ImageView imageView;
      if (Utils.isEmptyString(_filterPersistence.getFilter()))
      {
         imageView = new ImageView(new Props(getClass()).getImage("filter.gif"));
      }
      else
      {
         imageView = new ImageView(new Props(getClass()).getImage("filter_checked.gif"));
      }
      setCenter(imageView);
   }

}
