package org.squirrelsql.table.tableexport;

import java.io.File;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.squirrelsql.AppState;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.I18n;

public class ExportResultsCtrl {

	private ExportResultsView _exportResultsView;
	private final Stage _dialog;
	private I18n _i18n = new I18n(this.getClass());

	public ExportResultsCtrl() {
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
		_exportResultsView.exportOK.setOnAction(e -> onExportResults());
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
	private void onExportResults(){
		System.out.println("Export");
	}
}