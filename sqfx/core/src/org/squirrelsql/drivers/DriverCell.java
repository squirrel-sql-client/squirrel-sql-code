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

   private Paint _stdTextFill;

   public DriverCell()
   {
      Label label = new Label();
      _stdTextFill = label.getTextFill();
   }

   @Override
   protected void updateItem(SQLDriver sqlDriver, boolean empty)
   {
      super.updateItem(sqlDriver, empty);

      if(empty)
      {
         setText(null);
         setGraphic(null);
         setTextFill(_stdTextFill);
         return;
      }


      setText(sqlDriver.getName());
      setGraphic(getIcon(sqlDriver));
      setTextFill(getTextFillColor(sqlDriver));
   }

   private Paint getTextFillColor(SQLDriver sqlDriver)
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
         return new ImageView(DriversController.getDriverloadedimage());
      }
      else
      {
         return new ImageView(DriversController.getDrivernotloadedimage());
      }
   }
}
