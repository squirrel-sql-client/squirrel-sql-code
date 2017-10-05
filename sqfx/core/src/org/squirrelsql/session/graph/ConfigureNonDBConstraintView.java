package org.squirrelsql.session.graph;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ConfigureNonDBConstraintView
{
   @FXML TextField txtFkTableName;
   @FXML TextField txtPkTableName;
   @FXML TableView tblColumnPairs;
   @FXML Button btnRemoveSelectedEntry;
   @FXML Label lblFkColumns;
   @FXML Label lblPkColumns;
   @FXML ComboBox<GraphColumn> cboFkColumn;
   @FXML ComboBox<GraphColumn> cboPkColumn;
   @FXML Button btnAdd;
   @FXML Button btnOk;
   @FXML Button btnCancel;
}
