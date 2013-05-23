package org.squirrelsql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriversController
{
   private ListView _lst;


   public DriversController()
   {
      _lst = new ListView();

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


      _lst.setItems(observableList);

      _lst.setCellFactory(new Callback<ListView, ListCell>()
      {
         @Override
         public ListCell call(ListView listView)
         {
            return new DriverCell();
         }
      });

   }

   public Node getNode()
   {
      return _lst;
   }
}
