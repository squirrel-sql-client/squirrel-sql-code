package org.squirrelsql.aliases;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class EditFolderNameView
{
   @FXML TextField txtFolderName;
   @FXML RadioButton radToRoot;
   @FXML RadioButton radToSelectedAsChild;
   @FXML RadioButton radToSelectedParentAsAncestor;
   @FXML RadioButton radToSelectedParentAsSuccessor;
   @FXML Button btnOk;
   @FXML Button btnCancel;
}
