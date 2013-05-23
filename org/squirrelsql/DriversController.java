package org.squirrelsql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
   private ToolBar _toolBar;
   private final BorderPane _borderPane;


   public DriversController()
   {
      _borderPane = new BorderPane();

      _toolBar = new ToolBar();
      addButton("driver_add.png");
      addButton("driver_remove.png");
      addButton("driver_edit.png");
      addToggleButton("driver_filter.gif");

      _lstDrivers = new ListView();

      _borderPane.setTop(_toolBar);
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

   }

   private ToggleButton addToggleButton(String icon)
   {
      ToggleButton btn = new ToggleButton ();
      btn.setGraphic(_props.getImageView(icon));
      _toolBar.getItems().add(btn);

      return btn;
   }

   private Button addButton(String icon)
   {
      Button btn = new Button();
      btn.setGraphic(_props.getImageView(icon));
      _toolBar.getItems().add(btn);

      return btn;
   }

   public Node getNode()
   {
      return _borderPane;
   }
}
