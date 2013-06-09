package org.squirrelsql.drivers;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.DockPaneChanel;
import org.squirrelsql.PreDefinedDrivers;
import org.squirrelsql.Props;
import org.squirrelsql.services.Dao;
import org.squirrelsql.services.DockToolbarBuilder;
import org.squirrelsql.services.FXMessageBox;
import org.squirrelsql.services.I18n;

import java.util.ArrayList;

public class DriversController
{
   private Props _props = new Props(this.getClass());
   private I18n _i18n = new I18n(this.getClass());

   private ListView<SQLDriver> _lstDrivers;
   private final BorderPane _borderPane;
   private DockPaneChanel _dockPaneChanel;
   private DriversManager _driversManager = new DriversManager();
   private ToggleButton _btnFilter;


   public DriversController(DockPaneChanel dockPaneChanel)
   {
      _dockPaneChanel = dockPaneChanel;
      _borderPane = new BorderPane();

      _lstDrivers = new ListView();

      _borderPane.setTop(createToolBar());
      _borderPane.setCenter(_lstDrivers);


      onFilter();

      _lstDrivers.setCellFactory(listView -> new DriverCell());

      if (0 < _lstDrivers.getItems().size())
      {
         _lstDrivers.getSelectionModel().select(0);
      }

   }

   private BorderPane createToolBar()
   {
      DockToolbarBuilder dockToolbarBuilder = new DockToolbarBuilder();

      dockToolbarBuilder.addButtonLeft(_props.getImageView("driver_add.png"), _i18n.t("tooltip.add")).setOnAction(e -> onAdd());
      dockToolbarBuilder.addButtonLeft(_props.getImageView("driver_remove.png"), _i18n.t("tooltip.remove")).setOnAction(e -> onRemove());
      dockToolbarBuilder.addButtonLeft(_props.getImageView("driver_edit.png"), _i18n.t("tooltip.edit")).setOnAction(e -> onEdit());

      _btnFilter = dockToolbarBuilder.addToggleButtonLeft(_props.getImageView("driver_filter.gif"), _i18n.t("tooltip.filter"));
      _btnFilter.setOnAction(e -> onFilter());

      dockToolbarBuilder.addButtonRight(_props.getImageView("dock_win_close.png"), _i18n.t("tooltip.close")).setOnAction(e -> _dockPaneChanel.closeDriver());

      return dockToolbarBuilder.getToolbarPane();
   }

   private void onAdd()
   {
      SQLDriver newDriver = new SQLDriver();
      DriverEditCtrl driverEditCtrl = new DriverEditCtrl(newDriver);

      if(driverEditCtrl.isOk())
      {
         newDriver.update(driverEditCtrl.getDriver());
         _lstDrivers.getItems().add(newDriver);
         _lstDrivers.getSelectionModel().select(newDriver);
         _lstDrivers.scrollTo(_lstDrivers.getItems().size() - 1);

         Dao.writeDrivers(new ArrayList<>(_lstDrivers.getItems()));
      }
   }

   private void onRemove()
   {
      SQLDriver selectedItem = _lstDrivers.getSelectionModel().getSelectedItem();
      int  selIx = _lstDrivers.getSelectionModel().getSelectedIndex();

      Stage stage = AppState.get().getPrimaryStage();

      if(null == selectedItem)
      {
         FXMessageBox.showInfoOk(stage, _i18n.t("driver.delete.noselection.message"));
         return;
      }


      if(selectedItem.isSquirrelPredefinedDriver())
      {
         String delMsg = _i18n.t("predef.driver.delete.message");
         String optRevert = _i18n.t("predef.driver.revert");

         String selOpt = FXMessageBox.showMessageBox(stage, FXMessageBox.Icon.ICON_INFORMATION, FXMessageBox.TITLE_TEXT_INFORMATION, delMsg, 0, FXMessageBox.CANCEL, optRevert);

         if(optRevert.equals(selOpt))
         {
            selectedItem.update(PreDefinedDrivers.find(selectedItem.getId()));
            _lstDrivers.getItems().set(selIx, selectedItem);
         }

      }
      else
      {
         String opt = FXMessageBox.showYesNo(stage, _i18n.t("driver.delete.confirm"));

         if(FXMessageBox.YES.equals(opt))
         {
            _lstDrivers.getItems().remove(selectedItem);
            Dao.writeDrivers(new ArrayList<>(_lstDrivers.getItems()));
         }
      }
   }

   private void onFilter()
   {
      _lstDrivers.getItems().setAll(_driversManager.getDrivers(_btnFilter.isSelected()));
   }

   private void onEdit()
   {
      int selectedIndex = _lstDrivers.getSelectionModel().getSelectedIndex();
      SQLDriver selectedDriver = _lstDrivers.getSelectionModel().getSelectedItem();

      if(null == selectedDriver)
      {
         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), _i18n.t("driver.edit.noselection.message"));
         return;
      }



      DriverEditCtrl driverEditCtrl = new DriverEditCtrl(selectedDriver);

      if(driverEditCtrl.isOk())
      {
         selectedDriver.update(driverEditCtrl.getDriver());
         Dao.writeDrivers(new ArrayList<>(_lstDrivers.getItems()));

         _lstDrivers.getItems().set(selectedIndex, selectedDriver);
      }
   }

   public Node getNode()
   {
      return _borderPane;
   }
}
