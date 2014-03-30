package org.squirrelsql.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import org.squirrelsql.AppState;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.AliasProperties;
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
   public static final String FILE_NAME_ALIAS_PROPERTIES = "aliasProperties.json";

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
      return FILE_NAME_ALIAS_PROPERTIES + "_" + aliasId;
   }

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

   public static AliasTreeStructureNode loadAliasTree()
   {
      try
      {
         File aliasTreeFile = new File(AppState.get().getUserDir(), FILE_NAME_ALIAS_TREE);

         if(false == aliasTreeFile.exists())
         {
            return new AliasTreeStructureNode();
         }

         ObjectMapper mapper = new ObjectMapper();
         AliasTreeStructureNode aliasTree = mapper.readValue(aliasTreeFile, SimpleType.construct(AliasTreeStructureNode.class));

         return aliasTree;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static ArrayList<Alias> loadAliases()
   {
      try
      {
         File aliasesFile = new File(AppState.get().getUserDir(), FILE_NAME_ALIASES);

         if(false == aliasesFile.exists())
         {
            return new ArrayList<>();
         }

         ObjectMapper mapper = new ObjectMapper();
         ArrayList<Alias> aliases = mapper.readValue(aliasesFile, CollectionType.construct(ArrayList.class, SimpleType.construct(Alias.class)));

         return aliases;
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


   public static AliasProperties loadAliasProperties(String aliasId)
   {
      return null;
   }
}
