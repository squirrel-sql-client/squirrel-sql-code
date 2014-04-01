package org.squirrelsql.aliases;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;

public class AliasPropertiesEditView
{
   @FXML RadioButton radLoadAllCacheNon;
   @FXML RadioButton radLoadAndCacheAll;
   @FXML RadioButton radSpecifyLoading;
   @FXML Button btnConnectDb;
   @FXML TableView tblSchemas;

   @FXML ComboBox cboObjectTypes;
   @FXML ComboBox<SchemaLoadOptions> cboSchemaLoadOptions;
   @FXML Button btnApply;

   @FXML Button btnOk;
   @FXML Button btnClose;
}
