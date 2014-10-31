package org.squirrelsql.table.tableexport;

import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.squirrelsql.AppState;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.I18n;
import org.squirrelsql.table.TableLoader;

public class ExportResultsCtrl {

	private ExportResultsView _exportResultsView;
	private final Stage _dialog;
	private I18n _i18n = new I18n(this.getClass());
	private FileTypeEnum _fileTypeEnum;

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
		_dialog.showAndWait();
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
	
	//TODO figure out how to use ProgressTask/ProgressUtil, they need to support updateProgress for a task
	private void onExportResults(TableLoader tableLoader){
		_exportResultsView.exportOK.setDisable(true);
		//TODO Figure out how to get selected value as a string instead of doing if/else
		if(_exportResultsView.excelXLSX.isSelected()){
			_fileTypeEnum = FileTypeEnum.EXPORT_FORMAT_XLSX;
		}
		else if(_exportResultsView.excelXLS.isSelected()){
			_fileTypeEnum = FileTypeEnum.EXPORT_FORMAT_XLS;
		}
		ExcelService es = new ExcelService();
		es.set_tableLoader(tableLoader);
		es.set_fileTypeEnum(_fileTypeEnum);
		es.set_outputPath(_exportResultsView.exportTo.getText());
		es.set_fileName(_exportResultsView.fileName.getText());
		es.stateProperty().addListener(new ChangeListener<Worker.State>() {
			@Override
			public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState){
				switch(newState){
				case SUCCEEDED:
					_exportResultsView.exportProgressIndicator.progressProperty().unbind();
					_exportResultsView.exportProgressIndicator.setProgress(1.0);
					break;				
				case RUNNING:
					_exportResultsView.exportProgressIndicator.progressProperty().bind(es.progressProperty());
					break;
				case SCHEDULED:
					_exportResultsView.exportProgressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
				default:
					break;
				}
			}
		});
		es.start();
		
//		Task<Void> task = es.excelWriteTask(tableLoader, _fileTypeEnum, _exportResultsView.exportTo.getText(), _exportResultsView.fileName.getText());
//		_exportResultsView.exportProgressIndicator.progressProperty().bind(task.progressProperty());
//		new Thread(task).start();
//		_exportResultsView.exportProgressIndicator.progressProperty().unbind();
//		_exportResultsView.exportProgressIndicator.setProgress(1.0);
	}
}