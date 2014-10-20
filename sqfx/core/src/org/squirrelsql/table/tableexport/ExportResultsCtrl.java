package org.squirrelsql.table.tableexport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.squirrelsql.AppState;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.I18n;
import org.squirrelsql.table.ColumnHandle;
import org.squirrelsql.table.TableLoader;

public class ExportResultsCtrl {

	private ExportResultsView _exportResultsView;
	private final Stage _dialog;
	private I18n _i18n = new I18n(this.getClass());
	private FileOutputStream _outputFile = null;
	private Workbook _wb;
	private String _fileType;

	public ExportResultsCtrl(TableLoader tableLoader) {
		FxmlHelper<ExportResultsView> fxmlHelper = new FxmlHelper<>(ExportResultsView.class);
		_exportResultsView = fxmlHelper.getView();
		_dialog = new Stage();
		_dialog.setTitle(_i18n.t("export.title.exportresults"));
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.initOwner(AppState.get().getPrimaryStage());		
		Region region = fxmlHelper.getRegion();
		_dialog.setScene(new Scene(region));
		GuiUtils.makeEscapeClosable(region);
		_exportResultsView.browseFile.setOnAction(e -> onBrowseFile());
		_exportResultsView.exportOK.setOnAction(e -> onExportResults(tableLoader));
				
	}
	public void showWindow(){
		_dialog.showAndWait();
	}
	private void onBrowseFile(){
		final DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle(_i18n.t("export.title.directorychooser"));
		final File selectedDirectory = dc.showDialog(_dialog);
		if(null != selectedDirectory) {
			_exportResultsView.exportTo.setText(selectedDirectory.getAbsolutePath());
		}
	}
	private void onExportResults(TableLoader tableLoader){
		List<ColumnHandle> columns = tableLoader.getColumnHandles();
		List<List<SimpleObjectProperty>> rows = tableLoader.getSimpleObjectPropertyRows();
		if(_exportResultsView.excelXLSX.isSelected()){
			_wb = new SXSSFWorkbook(100);
			_fileType = ".xlsx";
		}
		else {
			_wb = new HSSFWorkbook();
			_fileType = ".xls";
		}
		Sheet sh = _wb.createSheet();
		Row columnRow = sh.createRow(0);
		for(int cellnum = 0; cellnum < columns.size(); cellnum++){
			Cell cell = columnRow.createCell(cellnum);
			cell.setCellValue(columns.get(cellnum).getHeader());
		}
		for(int rownum = 1; rownum < rows.size(); rownum++){
            Row row = sh.createRow(rownum);
            for(int cellnum = 0; cellnum < columns.size(); cellnum++){
                Cell cell = row.createCell(cellnum);
                //cell.setCellValue(tableLoader.getCellValue(cellnum,tableLoader.getSimpleObjectPropertyRows().get(rownum)).getValue().toString());
            }
        }		
		try {
			_outputFile = new FileOutputStream(_exportResultsView.exportTo.getText() + "\\" + _exportResultsView.fileName.getText() + _fileType);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        try {
        	_wb.write(_outputFile);
			_outputFile.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}
        ((SXSSFWorkbook) _wb).dispose();
	}
}