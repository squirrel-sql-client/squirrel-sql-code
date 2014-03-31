package org.squirrelsql.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import org.squirrelsql.AppState;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.AliasProperties;
import org.squirrelsql.aliases.AliasPropertiesDecorator;
import org.squirrelsql.aliases.AliasTreeStructureNode;
import org.squirrelsql.drivers.SQLDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Dao
{

   public static final String FILE_NAME_DRIVERS = "drivers.json";
   public static final String FILE_NAME_ALIASES = "aliases.json";
   public static final String FILE_NAME_ALIAS_TREE = "aliasTree.json";

   public static void writeDrivers(ArrayList<SQLDriver> sqlDrivers)
   {
      writeObject(sqlDrivers, FILE_NAME_DRIVERS);
   }

   public static void writeAliases(ArrayList<Alias> aliases, AliasTreeStructureNode treeStructureNode)
   {
      writeObject(aliases, FILE_NAME_ALIASES);
      writeObject(treeStructureNode, FILE_NAME_ALIAS_TREE);
   }

   public static void writeAliasProperties(AliasProperties aliasProperties)
   {
      writeObject(aliasProperties, getAliasPropertiesFileName(aliasProperties.getAliasId()));
   }

   private static String getAliasPropertiesFileName(String aliasId)
   {
      return "aliasProperties" + "_" + aliasId + ".json";
   }

   public static ArrayList<SQLDriver> loadSquirrelDrivers()
   {
      return loadObjectArray(FILE_NAME_DRIVERS, SQLDriver.class);
   }


   public static AliasTreeStructureNode loadAliasTree()
   {
      return loadObject(FILE_NAME_ALIAS_TREE, new AliasTreeStructureNode());
   }

   public static ArrayList<Alias> loadAliases()
   {
      return loadObjectArray(FILE_NAME_ALIASES, Alias.class);
   }

   public static AliasPropertiesDecorator loadAliasProperties(String aliasId)
   {
      return new AliasPropertiesDecorator(loadObject(getAliasPropertiesFileName(aliasId), new AliasProperties()));
   }


   private static<T> T loadObject(String fileName, T defaultObject)
   {
      try
      {
         File file = new File(AppState.get().getUserDir(), fileName);

         if(false == file.exists())
         {
            return defaultObject;
         }

         ObjectMapper mapper = new ObjectMapper();
         T ret = mapper.readValue(file, SimpleType.construct(defaultObject.getClass()));

         return ret;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }


   private static<T> ArrayList<T> loadObjectArray(String fileName, Class<T> objectType)
   {
      try
      {
         File file = new File(AppState.get().getUserDir(), fileName);

         if(false == file.exists())
         {
            return new ArrayList<>();
         }

         ObjectMapper mapper = new ObjectMapper();
         ArrayList<T> drivers = mapper.readValue(file, CollectionType.construct(ArrayList.class, SimpleType.construct(objectType)));

         return drivers;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private static void writeObject(Object aliasProperties, String unqualifiedFileName)
   {
      try
      {

         ObjectMapper mapper = new ObjectMapper();
         ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();

         File file = new File(AppState.get().getUserDir(), unqualifiedFileName);
         FileWriter fileWriter = new FileWriter(file);
         objectWriter.writeValue(fileWriter, aliasProperties);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

}
