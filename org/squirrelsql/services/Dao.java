package org.squirrelsql.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import org.squirrelsql.AppState;
import org.squirrelsql.drivers.SQLDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Dao
{

   public static final String FILE_NAME_DRIVERS = "drivers.json";

   public static ArrayList<SQLDriver> loadSquirrelDrivers()
   {
      try
      {
         File driversFile = new File(AppState.get().getUserDir(), FILE_NAME_DRIVERS);

         if(false == driversFile.exists())
         {
            return new ArrayList<>();
         }

         ObjectMapper mapper = new ObjectMapper();
         ArrayList<SQLDriver> drivers = mapper.readValue(driversFile, CollectionType.construct(ArrayList.class, SimpleType.construct(SQLDriver.class)));

         return drivers;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static void writeDrivers(ArrayList<SQLDriver> sqlDrivers)
   {
      try
      {
         File driversFile = new File(AppState.get().getUserDir(), FILE_NAME_DRIVERS);

         ObjectMapper mapper = new ObjectMapper();
         ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();
         objectWriter.writeValue(new FileWriter(driversFile), sqlDrivers);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}
