package org.squirrelsql.drivers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.squirrelsql.PreDefinedDrivers;
import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.services.Dao;

import java.util.ArrayList;
import java.util.Collection;
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
         ArrayList<SQLDriver> filteredList = CollectionUtil.filter(_allDrivers, SQLDriver::isLoaded);
         return FXCollections.observableList(filteredList);
      }

      return FXCollections.observableList(_allDrivers);
   }

   public ArrayList<SQLDriver> getFilteredOutDrivers()
   {
      return CollectionUtil.filter(_allDrivers, d -> false == d.isLoaded());
   }

   public void remove(SQLDriver selectedItem)
   {
      _allDrivers.remove(selectedItem);
      Dao.writeDrivers(_allDrivers);
   }

   public void add(SQLDriver newDriver)
   {
      _allDrivers.add(newDriver);
      Dao.writeDrivers(_allDrivers);
   }

   public void editedDriver(SQLDriver selectedDriver)
   {
      Dao.writeDrivers(_allDrivers);
   }

   public void applicationClosing()
   {
      Dao.writeDrivers(_allDrivers);
   }
}
