package org.squirrelsql.session.sql;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;

public class SQLHistorySearchView
{
   @FXML ComboBox<SqlHistoryFilterType> cboFilterType;
   @FXML TextField txtFilter;
   @FXML Button btnApply;
   @FXML CheckBox chkFiltered;
   @FXML SplitPane split;
}
