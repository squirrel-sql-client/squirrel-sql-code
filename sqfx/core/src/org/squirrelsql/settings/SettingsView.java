package org.squirrelsql.settings;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class SettingsView
{
   @FXML RadioButton radStandardProps;
   @FXML RadioButton radUserDefinedProps;
   @FXML AnchorPane apProperties;
   @FXML TextField txtPropertiesFileLocation;
   @FXML CheckBox chkMultibleLinesInCells;
   @FXML CheckBox chkLimitRowsByDefault;
   @FXML TextField txtLimitRowsDefault;
   @FXML Button btnSaveStandardProperties;

   @FXML Button btnOk;
   @FXML Button btnCancel;

}
