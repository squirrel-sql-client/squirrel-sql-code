package org.squirrelsql.drivers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.DockPaneChanel;
import org.squirrelsql.PreDefinedDrivers;
import org.squirrelsql.Props;
import org.squirrelsql.services.Dao;
import org.squirrelsql.services.I18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;

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

      createToolBar();

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
      BorderPane ret = new BorderPane();

      ToolBar toolBarDrivers = new ToolBar();
      addButton("driver_add.png", _i18n.t("tooltip.add"), toolBarDrivers);
      addButton("driver_remove.png", _i18n.t("tooltip.remove"), toolBarDrivers);
      addButton("driver_edit.png", _i18n.t("tooltip.edit"), toolBarDrivers).setOnAction(e -> onEdit());

      _btnFilter = addToggleButton("driver_filter.gif", _i18n.t("tooltip.filter"), toolBarDrivers);
      _btnFilter.setOnAction(e -> onFilter());

      ret.setCenter(toolBarDrivers);


      ToolBar toolBarDockWin = new ToolBar();
      // addToggleButton("dock_win_stick.png", toolBarDockWin);
      addButton("dock_win_close.png", _i18n.t("tooltip.close"), toolBarDockWin).setOnAction(e -> _dockPaneChanel.closeDriver());

      ret.setRight(toolBarDockWin);

      return ret;

   }

   private void onFilter()
   {
      _lstDrivers.getItems().setAll(_driversManager.getDrivers(_btnFilter.isSelected()));
   }

   private void onEdit()
   {
      int selectedIndex = _lstDrivers.getSelectionModel().getSelectedIndex();
      SQLDriver selectedDriver = (SQLDriver) _lstDrivers.getSelectionModel().getSelectedItem();
      DriverEditCtrl driverEditCtrl = new DriverEditCtrl(selectedDriver);

      if(driverEditCtrl.isOk())
      {
         selectedDriver.update(driverEditCtrl.getDriver());
         Dao.writeDrivers(new ArrayList<SQLDriver>(_lstDrivers.getItems()));

         _lstDrivers.getItems().set(selectedIndex, selectedDriver);
      }
   }

   private ToggleButton addToggleButton(String icon, String tooltip, ToolBar toolBar)
   {
      ToggleButton btn = new ToggleButton ();
      btn.setGraphic(_props.getImageView(icon));
      btn.setTooltip(new Tooltip(tooltip));
      toolBar.getItems().add(btn);

      return btn;
   }

   private Button addButton(String icon, String tooltip, ToolBar toolBar)
   {
      Button btn = new Button();
      btn.setGraphic(_props.getImageView(icon));
      btn.setTooltip(new Tooltip(tooltip));
      toolBar.getItems().add(btn);

      return btn;
   }

   public Node getNode()
   {
      return _borderPane;
   }
}
