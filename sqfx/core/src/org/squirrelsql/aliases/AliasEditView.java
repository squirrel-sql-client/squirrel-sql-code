package org.squirrelsql.aliases;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.squirrelsql.drivers.SQLDriver;

public class AliasEditView
{
   @FXML Hyperlink lnkSetToSampleUrl;
   @FXML Label lblChangeDriver;
   @FXML TextField txtName;
   @FXML ComboBox<SQLDriver> cboDriver;
   @FXML TextField txtUrl;
   @FXML TextField txtUserName;
   @FXML CheckBox chkUserNull;
   @FXML CheckBox chkUserEmpty;
   @FXML CheckBox chkConnectAtStartUp;
   @FXML Button btnProperties;
   @FXML CheckBox chkAutoLogon;
   @FXML Button btnNewDriver;
   @FXML CheckBox chkSavePassword;
   @FXML Button btnOk;
   @FXML Button btnClose;
   @FXML Button btnTest;
   @FXML PasswordField txtPassword;
   @FXML CheckBox chkPasswordNull;
   @FXML CheckBox chkPasswordEmpty;
   @FXML Label lblPassword;

   //////////////////////////////////////////////////////////////////////
   // Naming convention, see:
   // http://docs.oracle.com/javafx/2/fxml_get_started/whats_new2.htm
   public TreePositionView treePositionViewController; // fx:id in EditFolderNameView.fxml plus postfix "Controller"
   public AnchorPane treePositionView; // fx:id in EditFolderNameView.fxml
   //
   ///////////////////////////////////////////////////////////////////////

}
