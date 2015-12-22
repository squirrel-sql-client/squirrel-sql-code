package org.squirrelsql.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import org.squirrelsql.services.Settings;
import org.squirrelsql.services.Utils;

public class LineHeightOffsetCtrl
{
   public LineHeightOffsetCtrl(Slider sldLineHeightOffset, TextField txtLineHeightOffset, Button btnApplyLineHeightOffset, Settings settings)
   {
      sldLineHeightOffset.setValue(settings.getLineHeightOffset());

      sldLineHeightOffset.setMin(0);
      sldLineHeightOffset.setMax(10);

      txtLineHeightOffset.setText("" + settings.getLineHeightOffset());

      settings.lineHeightOffsetProperty().bindBidirectional(sldLineHeightOffset.valueProperty());

      Utils.makePositiveDoubleField(txtLineHeightOffset);

      settings.lineHeightOffsetProperty().addListener(new ChangeListener<Number>()
      {
         @Override
         public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
         {
            txtLineHeightOffset.setText("" + newValue);
         }
      });

      btnApplyLineHeightOffset.setOnAction(e -> settings.setLineHeightOffset(Double.parseDouble(txtLineHeightOffset.getText())));

   }
}
