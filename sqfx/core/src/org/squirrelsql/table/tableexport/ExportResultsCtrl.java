package org.squirrelsql.table.tableexport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
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
	private Workbook _workbook;
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
	//TODO move this into the ExcelWriterService
	private void onExportResults(TableLoader tableLoader){
		_exportResultsView.exportProgressIndicator.setProgress(-1.0);
		//TODO figure out how to use ProgressTask/ProgressUtil, they need to support updateProgress for a task 
		Task<Void> task = new Task<Void>() {
			@Override public Void call() {
				List<ColumnHandle> columns = tableLoader.getColumnHandles();
				List<List<SimpleObjectProperty>> rows = tableLoader.getSimpleObjectPropertyRows();
				Long totalCells = (long) (columns.size() * rows.size());
				if(_exportResultsView.excelXLSX.isSelected()){
					_workbook = new SXSSFWorkbook(100);
					_fileType = ExportFormatEnum.EXPORT_FORMAT_XLSX.getFileExtension();
				}
				else {
					_workbook = new HSSFWorkbook();
					_fileType = ExportFormatEnum.EXPORT_FORMAT_XLS.getFileExtension();
				}
				Sheet sh = _workbook.createSheet();
				Row columnRow = sh.createRow(0);
				for(int cellNum = 0; cellNum < columns.size(); cellNum++){
					Cell cell = columnRow.createCell(cellNum);
					cell.setCellValue(columns.get(cellNum).getHeader());
				}
				for(int rowNum = 1; rowNum < rows.size(); rowNum++){
		            Row row = sh.createRow(rowNum);
		            for(int cellNum = 0; cellNum < columns.size(); cellNum++){
		            	updateProgress(((cellNum+1) * rowNum) / totalCells, totalCells);
		                Cell cell = row.createCell(cellNum);
		                cell.setCellValue(rows.get(rowNum).get(cellNum).getValue().toString());
		            }
		        }		
				try {
					_outputFile = new FileOutputStream(_exportResultsView.exportTo.getText() + "\\" + _exportResultsView.fileName.getText() + _fileType);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
		        try {
		        	_workbook.write(_outputFile);
					_outputFile.close();
					updateProgress(Long.MAX_VALUE,Long.MAX_VALUE);
				} catch (IOException e) {
					e.printStackTrace();
				}
		        if(_exportResultsView.excelXLSX.isSelected()){
		        	((SXSSFWorkbook) _workbook).dispose();
		        }
				return null;
			}
		};
		_exportResultsView.exportProgressIndicator.progressProperty().bind(task.progressProperty());
		Thread th = new Thread(task);
		th.start();
	}
}