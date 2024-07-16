package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import java.io.FileNotFoundException;
import java.util.Iterator;

public class DataTypePropertiesManager
{
   private static ILogger s_log = LoggerController.createLogger(DataTypePropertiesManager.class);

   DTProperties _dataTypeProperties;

   /**
    * Load the options previously selected by user for specific cols to use in WHERE clause when editing
    * cells.
    */
   public void loadDataTypeProperties()
   {
      try
      {
         XMLBeanReader doc = new XMLBeanReader();
         doc.load(new ApplicationFiles().getDTPropertiesFile());
         Iterator<Object> it = doc.iterator();
         if (it.hasNext())
         {
            _dataTypeProperties = (DTProperties) it.next();
         }
      }
      catch (FileNotFoundException ignore)
      {
         // First start.
         _dataTypeProperties = new DTProperties();
      }
      catch (Exception ex)
      {
         s_log.error("Unable to load DataType Properties selections from persistent storage.", ex);
      }
   }

   /**
    * Save the options selected by user for Cell Import Export.
    */
   public void saveDataTypeProperties()
   {
      try
      {
         XMLBeanWriter wtr = new XMLBeanWriter(_dataTypeProperties);
         wtr.save(new ApplicationFiles().getDTPropertiesFile());
      }
      catch (Exception ex)
      {
         s_log.error("Unable to write DataType properties to persistent storage.", ex);
      }
   }

   public DTProperties getDataTypeProperties()
   {
      if(null == _dataTypeProperties)
      {
         loadDataTypeProperties();
      }
      return _dataTypeProperties;
   }

}
