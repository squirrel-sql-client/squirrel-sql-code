package org.squirrelsql;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

public class DriverCell extends ListCell<SquirrelDriver>
{
   private Props _props = new Props(this.getClass());
   private ImageView _iconLoaded = new ImageView(_props.getImage("driver_loaded.png"));
   private ImageView _iconNotLoaded = new ImageView(_props.getImage("driver_not_loaded.png"));

   @Override
   protected void updateItem(SquirrelDriver squirrelDriver, boolean b)
   {
      super.updateItem(squirrelDriver, b);

      if(null == squirrelDriver)
      {
         return;
      }

      Label label = new Label(squirrelDriver.getName());
      label.setGraphic(getIcon(squirrelDriver));
      setGraphic(label);
   }

   private ImageView getIcon(SquirrelDriver squirrelDriver)
   {
      if (squirrelDriver.isLoaded())
      {
         return _iconLoaded;
      }
      else
      {
         return _iconNotLoaded;
      }
   }
}
