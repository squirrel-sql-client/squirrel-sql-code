package org.squirrelsql.session.graph.filter;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.session.graph.Operator;

public class StringValuesCtrl implements FilterValueCtrl
{

   private final TextField _txtValue = new TextField();

   public StringValuesCtrl(BorderPane bpValueContainer, Button btnDate, Label lblEncloseApostrophes, ComboBox<Operator> cboOperator)
   {
      bpValueContainer.setCenter(_txtValue);
      btnDate.setVisible(false);
      lblEncloseApostrophes.setVisible(true);

      cboOperator.valueProperty().addListener((observable, oldValue, newValue) -> onChangedOperator(newValue, lblEncloseApostrophes));

      onChangedOperator(cboOperator.getValue(), lblEncloseApostrophes);
   }

   private void onChangedOperator(Operator newValue, Label lblEncloseApostrophes)
   {
      if(newValue == Operator.IN || newValue == Operator.NOT_IN || newValue == Operator.IS_NULL)
      {
         lblEncloseApostrophes.setVisible(false);
      }
      else
      {
         lblEncloseApostrophes.setVisible(true);
      }
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
