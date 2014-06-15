package org.squirrelsql.session.sql;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SQLHistorySearchView
{
   @FXML ComboBox<SqlHistoryFilterType> cboFilterType;
   @FXML TextField txtFilter;
   @FXML Button btnApply;
   @FXML CheckBox chkFiltered;
   @FXML SplitPane split;
}
