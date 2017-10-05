package org.squirrelsql.session.graph.filter;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class DefaultValuesCtrl implements FilterValueCtrl
{

   private final TextField _txtValue = new TextField();

   public DefaultValuesCtrl(BorderPane bpValueContainer, Button btnDate, Label lblEncloseApostrophes)
   {
      bpValueContainer.setCenter(_txtValue);
      btnDate.setVisible(false);
      lblEncloseApostrophes.setVisible(false);
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
