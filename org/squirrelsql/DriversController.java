package org.squirrelsql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class DriversController
{
   private ListView _lst;


   public DriversController()
   {
      _lst = new ListView();

      ObservableList<SquirrelDriver> observableList = FXCollections.observableArrayList();

      observableList.addAll(new SquirrelDriver(), new SquirrelDriver(), new SquirrelDriver());


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
