package org.squirrelsql.aliases;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.squirrelsql.drivers.SQLDriver;

public class AliasEditView
{
   @FXML Hyperlink lnkSetToSampleUrl;
   @FXML Label lblChangeDriver;
   @FXML TextField txtName;
   @FXML ComboBox<SQLDriver> cboDriver;
   @FXML TextField txtUrl;
   @FXML TextField txtUserName;
   @FXML CheckBox chkConnectAtStartUp;
   @FXML Button btnProperties;
   @FXML CheckBox chkAutoLogon;
   @FXML Button btnNewDriver;
   @FXML CheckBox chkSavePassword;
   @FXML Button btnOk;
   @FXML Button btnClose;
   @FXML Button btnTest;
   @FXML PasswordField txtPassword;
   @FXML Label lblPassword;
}
