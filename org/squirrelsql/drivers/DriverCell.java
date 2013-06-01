package org.squirrelsql.drivers;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import org.squirrelsql.Props;

public class DriverCell extends ListCell<SQLDriver>
{
   private Props _props = new Props(this.getClass());
   private ImageView _iconLoaded = new ImageView(_props.getImage("driver_loaded.png"));
   private ImageView _iconNotLoaded = new ImageView(_props.getImage("driver_not_loaded.png"));

   @Override
   protected void updateItem(SQLDriver SQLDriver, boolean b)
   {
      super.updateItem(SQLDriver, b);

      if(null == SQLDriver)
      {
         return;
      }

      Label label = new Label(SQLDriver.getName());
      label.setGraphic(getIcon(SQLDriver));
      setGraphic(label);
   }

   private ImageView getIcon(SQLDriver SQLDriver)
   {
      if (SQLDriver.isLoaded())
      {
         return _iconLoaded;
      }
      else
      {
         return _iconNotLoaded;
      }
   }
}
