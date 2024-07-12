package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;

import java.util.ArrayList;
import java.util.List;

public class DataTypeComponentFactoryRegistry
{
   /* list of DBMS-specific registered data handlers.
    * The key is a string of the form:
    *   <SQL type as a string>:<SQL type name>
    * and the value is a factory that can create instances of DBMS-specific
    * DataTypeComponets.
    */
   private List<IDataTypeComponentFactory> _pluginDataTypeFactories = new ArrayList<>(); // TODO Remove static

   public void registerDataTypeFactory(IDataTypeComponentFactory factory)
   {
      _pluginDataTypeFactories.add(factory);
   }

   public IDataTypeComponentFactory findMatchingFactory(DialectType dialectType, int sqlType, String sqlTypeName)
   {
      for (IDataTypeComponentFactory factory : _pluginDataTypeFactories)
      {
         if (factory.matches(dialectType, sqlType, sqlTypeName))
         {
            return factory;
         }
      }
      return null;
   }

}
