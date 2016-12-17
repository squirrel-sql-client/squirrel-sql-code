package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.AppState;
import org.squirrelsql.Props;
import org.squirrelsql.services.I18n;

public class OrderByPane extends BorderPane
{
   private I18n _i18n = new I18n(getClass());
   private ColumnConfigurationListener _columnConfigurationListener;


   public OrderByPane(ColumnConfigurationListener columnConfigurationListener)
   {
      super(new ImageView(new Props(OrderByPane.class).getImage("sort.png")));
      _columnConfigurationListener = columnConfigurationListener;
      addEventHandler(MouseEvent.MOUSE_PRESSED, e -> showPopup());



   }

   private void showPopup()
   {

      ImageView noneIcon = OrderBy.createSortImage();
      MenuItem none = new MenuItem(OrderBy.getOrderByNoneText(), noneIcon);
      none.setOnAction(e -> onFctSelected(noneIcon));


      ImageView ascIcon = OrderBy.ASC.createImage();
      MenuItem asc = new MenuItem(OrderBy.ASC.getTitle(), ascIcon);
      asc.setOnAction(e -> onFctSelected(ascIcon));

      ImageView descIcon = OrderBy.DESC.createImage();
      MenuItem desc = new MenuItem(OrderBy.DESC.getTitle(), descIcon);
      desc.setOnAction(e -> onFctSelected(descIcon));


      ContextMenu popup = new ContextMenu(none, asc, desc);

      Point2D localToScene = localToScreen(0, 0);

      popup.show(AppState.get().getPrimaryStage(), localToScene.getX(), localToScene.getY());


   }

   private void onFctSelected(ImageView icon)
   {
      setCenter(icon);
      _columnConfigurationListener.requestLayout();
   }

}
