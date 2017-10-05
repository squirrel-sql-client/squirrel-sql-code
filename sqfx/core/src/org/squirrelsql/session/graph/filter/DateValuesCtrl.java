package org.squirrelsql.session.graph.filter;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.squirrelsql.Props;
import org.squirrelsql.session.sql.features.EscapeDateCtrl;

public class DateValuesCtrl implements FilterValueCtrl
{

   private final TextField _txtValue = new TextField();
   private Stage _dlg;

   public DateValuesCtrl(BorderPane bpValueContainer, Button btnDate, Label lblEncloseApostrophes, Stage dlg)
   {
      _dlg = dlg;
      bpValueContainer.setCenter(_txtValue);
      btnDate.setVisible(true);
      btnDate.setGraphic(new Props(getClass()).getImageView("datechooser.png"));

      btnDate.setOnAction(e -> onDate());

      lblEncloseApostrophes.setVisible(false);
   }

   private void onDate()
   {
      new EscapeDateCtrl(_txtValue::setText, _dlg);
   }

   @Override
   public void setFilterValueString(String filter)
   {
      _txtValue.setText(filter);
   }

   @Override
   public void setDisable(boolean b)
   {
      _txtValue.setDisable(b);
   }

   @Override
   public String getFilterValueString()
   {
      return _txtValue.getText();
   }
}
