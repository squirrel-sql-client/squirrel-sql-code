package org.squirrelsql.table.tableexport;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class ExportResultsView
{
	@FXML Label lblExportTo;
	@FXML TextField exportTo;
	@FXML Button browseFile;
	@FXML Button exportOK;
	@FXML Button exportCancel;
	@FXML Label lblFileName;
	@FXML TextField fileName;
	@FXML Label lblExportAs;
	@FXML RadioButton excelXLSX;
	@FXML RadioButton excelXLS;
	@FXML ProgressIndicator exportProgressIndicator;
}
