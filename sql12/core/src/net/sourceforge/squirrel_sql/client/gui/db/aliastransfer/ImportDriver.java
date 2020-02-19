package net.sourceforge.squirrel_sql.client.gui.db.aliastransfer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ImportDriver
{
   private final String _driverIdentifier;
   private final String _driverName;

   public ImportDriver(String driverIdentifier, String driverName)
   {
      _driverIdentifier = driverIdentifier;
      _driverName = driverName;
   }

   public String getDriverIdentifier()
   {
      return _driverIdentifier;
   }

   public String getDriverName()
   {
      return _driverName;
   }

   @Override
   public String toString()
   {
      return _driverName;
   }

   public static List<ImportDriver> create(Properties driverIdentifierToName)
   {
      ArrayList<ImportDriver> ret = new ArrayList<>();

      for (Map.Entry<Object, Object> entry : driverIdentifierToName.entrySet())
      {
         ret.add(new ImportDriver((String)entry.getKey(), (String)entry.getValue()));
      }

      ret.sort(Comparator.comparing(ImportDriver::getDriverName));

      return ret;
   }
}
