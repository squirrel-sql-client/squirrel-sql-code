package org.squirrelsql.session.sql.bookmark;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class BookmarkEditView
{
   @FXML TextField txtKey;
   @FXML TextField txtDescription;
   @FXML TextArea txtSQL;
   @FXML Button btnSave;
}
