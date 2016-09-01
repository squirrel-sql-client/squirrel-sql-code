package org.squirrelsql.session.graph;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

public class ConfigureNonDBConstraintView
{
   @FXML TableView tblColumnPairs;
   @FXML Button btnRemoveSelectedEntry;
   @FXML ComboBox cboSourceColumn;
   @FXML ComboBox cboTargetColumn;
   @FXML Button btnAdd;
   @FXML Button btnOk;
   @FXML Button btnCancel;
}
