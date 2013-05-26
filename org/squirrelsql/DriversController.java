package org.squirrelsql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collections;

public class DriversController
{
   private Props _props = new Props(this.getClass());

   private ListView _lstDrivers;
   private final BorderPane _borderPane;
   private DockPaneChanel _dockPaneChanel;


   public DriversController(DockPaneChanel dockPaneChanel)
   {
      _dockPaneChanel = dockPaneChanel;
      _borderPane = new BorderPane();

      createToolBar();

      _lstDrivers = new ListView();

      _borderPane.setTop(createToolBar());
      _borderPane.setCenter(_lstDrivers);

      ObservableList<SquirrelDriver> observableList = FXCollections.observableArrayList();

      ArrayList<SquirrelDriver> driversToDisplay =  Dao.loadSquirrelDrivers();

      ArrayList<SquirrelDriver> preDefinedDrivers = PreDefinedDrivers.get();


      for (SquirrelDriver preDefinedDriver : preDefinedDrivers)
      {
         if(false == driversToDisplay.contains(preDefinedDriver))
         {
            driversToDisplay.add(preDefinedDriver);
         }
      }

      Collections.sort(driversToDisplay);

      observableList.addAll(driversToDisplay);


      _lstDrivers.setItems(observableList);

      _lstDrivers.setCellFactory(new Callback<ListView, ListCell>()
      {
         @Override
         public ListCell call(ListView listView)
         {
            return new DriverCell();
         }
      });

      if (0 < _lstDrivers.getItems().size())
      {
         _lstDrivers.getSelectionModel().select(0);
      }

   }

   private BorderPane createToolBar()
   {
      BorderPane ret = new BorderPane();

      ToolBar toolBarDrivers = new ToolBar();
      addButton("driver_add.png", toolBarDrivers);
      addButton("driver_remove.png", toolBarDrivers);
      addButton("driver_edit.png", toolBarDrivers).setOnAction(new EventHandler<ActionEvent>()
      {
         @Override
         public void handle(ActionEvent actionEvent)
         {
            onEdit();
         }
      });

      addToggleButton("driver_filter.gif", toolBarDrivers);

      ret.setCenter(toolBarDrivers);


      ToolBar toolBarDockWin = new ToolBar();
      // addToggleButton("dock_win_stick.png", toolBarDockWin);
      addButton("dock_win_close.png", toolBarDockWin).setOnAction(new EventHandler<ActionEvent>()
      {
         @Override
         public void handle(ActionEvent actionEvent)
         {
            _dockPaneChanel.closeDriver();
         }
      });

      ret.setRight(toolBarDockWin);

      return ret;

   }

   private void onEdit()
   {
      SquirrelDriver  squirrelDriver = (SquirrelDriver) _lstDrivers.getSelectionModel().getSelectedItem();
      new DriverEditCtrl(squirrelDriver);
   }

   private ToggleButton addToggleButton(String icon, ToolBar toolBar)
   {
      ToggleButton btn = new ToggleButton ();
      btn.setGraphic(_props.getImageView(icon));
      toolBar.getItems().add(btn);

      return btn;
   }

   private Button addButton(String icon, ToolBar toolBar)
   {
      Button btn = new Button();
      btn.setGraphic(_props.getImageView(icon));
      toolBar.getItems().add(btn);

      return btn;
   }

   public Node getNode()
   {
      return _borderPane;
   }
}
