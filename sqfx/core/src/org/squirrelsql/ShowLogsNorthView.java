package org.squirrelsql;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class ShowLogsNorthView
{
   @FXML CheckBox chkFilterErrors;
   @FXML CheckBox chkFilterWarnings;
   @FXML CheckBox chkFilterInfo;
   @FXML TextField txtLogDir;
   @FXML Button btnOpenLogDir;
}
