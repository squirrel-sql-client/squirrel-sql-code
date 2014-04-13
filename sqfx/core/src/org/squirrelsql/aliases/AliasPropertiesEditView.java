package org.squirrelsql.aliases;

import javafx.fxml.FXML;
import javafx.scene.control.*;

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

   @FXML CheckBox chkHideEmptySchemas;

   @FXML Button btnOk;
   @FXML Button btnClose;
}
