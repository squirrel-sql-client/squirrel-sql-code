package org.squirrelsql.session.graph;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ConfigureNonDBConstraintView
{
   @FXML TextField txtFkTableName;
   @FXML TextField txtPkTableName;
   @FXML TableView tblColumnPairs;
   @FXML Button btnRemoveSelectedEntry;
   @FXML Label lblFkColumns;
   @FXML Label lblPkColumns;
   @FXML ComboBox cboFkColumn;
   @FXML ComboBox cboPkColumn;
   @FXML Button btnAdd;
   @FXML Button btnOk;
   @FXML Button btnCancel;
}
