package org.squirrelsql.session.graph;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class FilterView
{
   @FXML ComboBox cboOperator;
   @FXML TextField txtValue;
   @FXML Button btnOk;
   @FXML Button btnCancel;
}
