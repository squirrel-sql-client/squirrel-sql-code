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


   public OrderByPane()
   {
      super(new ImageView(new Props(OrderByPane.class).getImage("sort.png")));
      addEventHandler(MouseEvent.MOUSE_PRESSED, e -> showPopup());

   }

   private void showPopup()
   {
      MenuItem none = new MenuItem(_i18n.t("order.none"), new ImageView(new Props(getClass()).getImage("sort.png")));
      MenuItem asc = new MenuItem(_i18n.t("order.asc"), new ImageView(new Props(getClass()).getImage("sort_asc.gif")));
      MenuItem desc = new MenuItem(_i18n.t("order.desc"), new ImageView(new Props(getClass()).getImage("sort_desc.gif")));


      ContextMenu popup = new ContextMenu(none, asc, desc);

      Point2D localToScene = localToScreen(0, 0);

      popup.show(AppState.get().getPrimaryStage(), localToScene.getX(), localToScene.getY());


   }


}
