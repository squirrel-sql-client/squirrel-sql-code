package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.client.Main;

import java.util.HashMap;

public class DataTypeProps
{
   public static void putDataTypeProperty(Class<? extends IDataTypeComponent> dataTypeClass, String propertyName, String propertyValue)
   {
      putDataTypeProperty(dataTypeClass.getName(), propertyName, propertyValue);
   }

   /**
    * add or replace a table-name/hashmap-of-column-names mapping.
    * If map is null, remove the entry from the tables.
    */
   public static void putDataTypeProperty(String dataTypeName, String propertyName, String propertyValue)
   {
      // get the hashmap for this type, or create it if this is a new property
      DataTypePropertiesManager dataTypePropertiesManager = Main.getApplication().getDataTypePropertiesManager();

      HashMap<String, String> buf = dataTypePropertiesManager.getDataTypeProperties().fetchDataTypes().get(dataTypeName);
      if(buf == null)
      {
         buf = new HashMap<>();
         dataTypePropertiesManager.getDataTypeProperties().fetchDataTypes().put(dataTypeName, buf);
      }
      buf.put(propertyName, propertyValue);
   }

   /**
    * get the HashMap of column names for the given table name.
    * it will be null if the table does not have any limitation on the columns to use.
    */
   public static String getProperty(String dataTypeName, String propertyName)
   {
      DataTypePropertiesManager dataTypePropertiesManager = Main.getApplication().getDataTypePropertiesManager();

      HashMap<String, String> h = dataTypePropertiesManager.getDataTypeProperties().fetchDataTypes().get(dataTypeName);
      if(h == null)
      {
         return null;
      }
      return h.get(propertyName);
   }

   public static Boolean getBooleanProperty(Class<? extends IDataTypeComponent> dataTypeClass, String propertyName, Boolean defaultValueWhenNull)
   {
      String val = getProperty(dataTypeClass.getName(), propertyName);

      if(null == val)
      {
         return defaultValueWhenNull;
      }

      return "true".equals(val);
   }

   public static Integer getIntegerProperty(Class<? extends IDataTypeComponent> dataTypeClass, String propertyName, Integer defaultValueWhenNull)
   {
      String val = getProperty(dataTypeClass.getName(), propertyName);

      if(null == val)
      {
         return defaultValueWhenNull;
      }

      return Integer.parseInt(val);
   }
}
