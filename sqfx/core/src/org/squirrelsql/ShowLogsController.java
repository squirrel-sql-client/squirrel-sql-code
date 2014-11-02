package org.squirrelsql;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.*;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ShowLogsController
{
   private Stage _dialog;
   private TextArea _txtLog = new TextArea();

   private I18n _i18n = new I18n(getClass());
   private final ListView<LogFileWrapper> _lstLogs = new ListView<>();

   public ShowLogsController()
   {
      _dialog = new Stage();
      _dialog.setTitle(_i18n.t("showLogsController.title"));
      _dialog.initModality(Modality.NONE);
      _dialog.initOwner(AppState.get().getPrimaryStage());


      SplitPane splitPane = new SplitPane();
      _dialog.setScene(new Scene(splitPane));

      splitPane.setOrientation(Orientation.HORIZONTAL);


      FxmlHelper<ShowLogsNorthView> fxmlHelper = new FxmlHelper<>(ShowLogsNorthView.class);

      BorderPane bp = new BorderPane();

      bp.setTop(fxmlHelper.getRegion());

      bp.setCenter(_lstLogs);

      splitPane.getItems().add(bp);

      splitPane.getItems().add(_txtLog);

      GuiUtils.makeEscapeClosable(splitPane);

      new StageDimensionSaver("showLogsController", _dialog, new Pref(getClass()), 300, 500, _dialog.getOwner());


      ShowLogsNorthView view = fxmlHelper.getView();
      view.chkFilterErrors.setSelected(true);
      view.chkFilterWarnings.setSelected(true);
      view.chkFilterInfo.setSelected(true);

      view.txtLogDir.setText(Dao.getLogDir().getPath());

      view.btnOpenLogDir.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.FOLDER));
      view.btnOpenLogDir.setTooltip(new Tooltip(_i18n.t("showLogsController.btnOpenLogDir.tooltip")));
      view.btnOpenLogDir.setOnAction(e -> Utils.runOnSwingEDT(this::onOpenLogDir));


      _txtLog.setEditable(false);

      File[] logFiles = Dao.getLogFiles();


      _lstLogs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onListSelectionChanged());

      if(0 < logFiles.length)
      {
         _lstLogs.getItems().addAll(LogFileWrapper.wrap(logFiles));
         _lstLogs.getSelectionModel().selectFirst();
      }



      AppState.get().addApplicationCloseListener(_dialog::close, ApplicationCloseListener.FireTime.AFTER_SESSION_FIRE_TIME );

      _dialog.showAndWait();

   }

   private void onListSelectionChanged()
   {
      try
      {
         LogFileWrapper selectedItem = _lstLogs.getSelectionModel().getSelectedItem();
         if(null == selectedItem)
         {
            _txtLog.setText("");
            return;
         }

         FileReader fr = new FileReader(selectedItem.getLogFile());

         BufferedReader br = new BufferedReader(fr);

         StringBuilder sb = new StringBuilder();

         String line = br.readLine();

         while(null != line)
         {
            sb.append(line).append('\n');
            line = br.readLine();
         }

         br.close();
         fr.close();

         _txtLog.setText(sb.toString());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onOpenLogDir()
   {
      try
      {
         Desktop desktop = Desktop.getDesktop();
         desktop.open(Dao.getLogDir());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}
