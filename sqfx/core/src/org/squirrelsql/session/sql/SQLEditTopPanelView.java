package org.squirrelsql.session.sql;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class SQLEditTopPanelView
{
   @FXML CheckBox chkLimitRows;
   @FXML TextField txtRowLimit;
   @FXML Button btnOpenHistory;
   @FXML Button btnAppendToEditor;
   @FXML ComboBox<SQLHistoryEntry> cboLatestSqls;
}
