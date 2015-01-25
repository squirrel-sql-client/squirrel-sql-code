package org.squirrelsql.services.progress;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class ProgressRegion extends BorderPane
{
   private boolean _cancelable;
   private Button _btnCancel;

   public ProgressRegion(boolean cancelable)
   {
      _cancelable = cancelable;
      setCenter(new Pane());
      if (_cancelable)
      {
         _btnCancel = new Button("Cancel");
         _btnCancel.setPrefWidth(Double.MAX_VALUE);
         setBottom(_btnCancel);
      }
   }

   public boolean isCancelable()
   {
      return _cancelable;
   }

   public Button getCancelButton()
   {
      return _btnCancel;
   }
}
