package org.squirrelsql;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class DriverEditView
{
   @FXML Label lblChangeDriver;
   @FXML ListView lstClasspath;
   @FXML TextField txtName;
   @FXML TextField txtUrl;
   @FXML TextField txtWebUrl;
   @FXML Button btnDriverCPAdd;
   @FXML Button btnDriverCPRemove;
   @FXML Button btnDriverCPUp;
   @FXML Button btnDriverCPDown;
   @FXML Button btnListDrivers;
   @FXML ListView lstDriverClasses;
   @FXML Button btnOk;
   @FXML Button btnClose;
}
