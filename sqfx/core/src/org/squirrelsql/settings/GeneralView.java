package org.squirrelsql.settings;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class GeneralView
{
   @FXML TextField txtStatementSeparator;
   @FXML RadioButton radStandardProps;
   @FXML RadioButton radUserDefinedProps;
   @FXML AnchorPane apProperties;
   @FXML TextField txtPropertiesFileLocation;
   @FXML CheckBox chkMultibleLinesInCells;
   @FXML CheckBox chkLimitRowsByDefault;
   @FXML TextField txtLimitRowsDefault;
   @FXML Button btnSaveStandardProperties;
   @FXML TextField txtResultTabsLimit;
   @FXML CheckBox chkCopyAliasProperties;
   @FXML CheckBox chkCopyQuotedToClip;
   @FXML CheckBox chkMarkCurrentSql;
   @FXML ColorPicker colPickCurrentSqlMark;


   @FXML AnchorPane apMarkCurrentSql;
   @FXML Slider sldLineHeightOffset;
   @FXML TextField txtLineHeightOffset;
   @FXML Button btnApplyLineHeightOffset;

}
