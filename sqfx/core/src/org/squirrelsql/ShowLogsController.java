package org.squirrelsql;

import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ShowLogsController
{
   private Stage _dialog;
   private TextArea _txtLog = new TextArea();

   private I18n _i18n = new I18n(getClass());
   private final ListView<LogFileWrapper> _lstLogs = new ListView<>();
   private final ShowLogsNorthView _view;

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


      _view = fxmlHelper.getView();

      _view.chkFilterErrors.setSelected(true);
      _view.chkFilterErrors.setOnAction(e -> showLogFiles());

      _view.chkFilterWarnings.setSelected(true);
      _view.chkFilterWarnings.setOnAction(e -> showLogFiles());

      _view.chkFilterInfo.setSelected(true);
      _view.chkFilterInfo.setOnAction(e -> showLogFiles());

      _view.txtLogDir.setText(Dao.getLogDir().getPath());

      _view.btnOpenLogDir.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.FOLDER));
      _view.btnOpenLogDir.setTooltip(new Tooltip(_i18n.t("showLogsController.btnOpenLogDir.tooltip")));
      _view.btnOpenLogDir.setOnAction(e -> Utils.runOnSwingEDT(this::onOpenLogDir));


      _txtLog.setEditable(false);


      _lstLogs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onListSelectionChanged());

      showLogFiles();



      AppState.get().addApplicationCloseListener(_dialog::close, ApplicationCloseListener.FireTime.AFTER_SESSION_FIRE_TIME );

      _dialog.showAndWait();

   }

   private void showLogFiles()
   {
      java.util.List<LogFileWrapper> wrap = LogFileWrapper.wrap(Dao.getLogFiles());

      List<LogFileWrapper> filtered = CollectionUtil.filter(wrap, this::matchesFilter);

      _lstLogs.getItems().clear();
      if(0 < filtered.size())
      {
         _lstLogs.getItems().addAll(filtered);
         _lstLogs.getSelectionModel().selectFirst();
      }
   }

   private boolean matchesFilter(LogFileWrapper w)
   {
      if(false == _view.chkFilterErrors.isSelected() && -1 < w.getLogFile().getName().toLowerCase().indexOf("error"))
      {
         return false;
      }

      if(false == _view.chkFilterWarnings.isSelected() && -1 < w.getLogFile().getName().toLowerCase().indexOf("warning"))
      {
         return false;
      }

      if(false == _view.chkFilterInfo.isSelected() && -1 < w.getLogFile().getName().toLowerCase().indexOf("info"))
      {
         return false;
      }

      return true;
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
