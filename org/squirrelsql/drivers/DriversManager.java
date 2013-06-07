package org.squirrelsql.drivers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.squirrelsql.PreDefinedDrivers;
import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.services.Dao;

import java.util.ArrayList;
import java.util.Collections;

public class DriversManager
{
   private ArrayList<SQLDriver> _allDrivers;

   public DriversManager()
   {
      _allDrivers = Dao.loadSquirrelDrivers();

      ArrayList<SQLDriver> preDefinedDrivers = PreDefinedDrivers.get();


      for (SQLDriver preDefinedDriver : preDefinedDrivers)
      {
         if(false == _allDrivers.contains(preDefinedDriver))
         {
            _allDrivers.add(preDefinedDriver);
         }
      }

      for (SQLDriver sqlDriver : _allDrivers)
      {
         sqlDriver.setLoaded(DriversUtil.checkDriverLoading(sqlDriver));
      }

      Collections.sort(_allDrivers);
   }

   public ObservableList<SQLDriver> getDrivers(boolean filtered)
   {
      if(filtered)
      {
         ArrayList<SQLDriver> filteredList = CollectionUtil.filter(_allDrivers, sqlDriver -> sqlDriver.isLoaded());
         return FXCollections.observableList(filteredList);
      }

      return FXCollections.observableList(_allDrivers);
   }

}
