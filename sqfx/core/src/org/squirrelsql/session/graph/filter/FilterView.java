package org.squirrelsql.session.graph.filter;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.session.graph.Operator;

public class FilterView
{
   @FXML ComboBox<Operator> cboOperator;
   @FXML Button btnDate;
   @FXML BorderPane bpValueContainer;
   @FXML Label lblEncloseApostrophes;
   @FXML Button btnOk;
   @FXML Button btnCancel;
}
