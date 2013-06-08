package org.squirrelsql.drivers;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.squirrelsql.Props;

public class DriverCell extends ListCell<SQLDriver>
{

   private Props _props = new Props(this.getClass());
   private ImageView _iconLoaded = new ImageView(_props.getImage("driver_loaded.png"));
   private ImageView _iconNotLoaded = new ImageView(_props.getImage("driver_not_loaded.png"));
   private Label _label = new Label();

   private Paint _stdTextFill;

   public DriverCell()
   {
      _stdTextFill = _label.getTextFill();
   }

   @Override
   protected void updateItem(SQLDriver sqlDriver, boolean b)
   {
      super.updateItem(sqlDriver, b);

      if(null == sqlDriver)
      {
         return;
      }


      _label.setText(sqlDriver.getName());
      _label.setGraphic(getIcon(sqlDriver));
      _label.setTextFill(getTextFill(sqlDriver));

      setGraphic(_label);
   }

   private Paint getTextFill(SQLDriver sqlDriver)
   {
      if(sqlDriver.isSquirrelPredefinedDriver())
      {
         return Color.NAVY;
      }
      else
      {
         return _stdTextFill;
      }
   }

   private ImageView getIcon(SQLDriver sqlDriver)
   {
      if (sqlDriver.isLoaded())
      {
         return _iconLoaded;
      }
      else
      {
         return _iconNotLoaded;
      }
   }
}
