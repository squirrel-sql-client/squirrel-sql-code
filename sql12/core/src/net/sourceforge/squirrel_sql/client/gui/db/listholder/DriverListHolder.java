package net.sourceforge.squirrel_sql.client.gui.db.listholder;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DriverListHolder implements ListHolder
{
   private static ILogger s_log = LoggerController.createLogger(DriverListHolder.class);

   private List<SQLDriver> _sqlDrivers = new ArrayList<>();
   private ArrayList<IObjectCacheChangeListener> _listeners = new ArrayList<>();


   public SQLDriver get(IIdentifier id)
   {
      return _sqlDrivers.stream().filter(d -> Utilities.equalsRespectNull(id, d.getIdentifier())).findFirst().orElse(null);
   }

   public void add(ISQLDriver sqlDriver)
   {
      if(_sqlDrivers.contains(sqlDriver))
      {
         return;
      }

      _sqlDrivers.add((SQLDriver) sqlDriver);

      new ArrayList<>(_listeners).forEach(l -> l.objectAdded(new ObjectCacheChangeEvent(sqlDriver)));
   }

   public void remove(IIdentifier identifier)
   {
      final SQLDriver sqlDriver = get(identifier);

      if (null != sqlDriver)
      {
         _sqlDrivers.remove(sqlDriver);
         new ArrayList<>(_listeners).forEach(l -> l.objectRemoved(new ObjectCacheChangeEvent(sqlDriver)));
      }

   }

   public List<SQLDriver> getAll()
   {
      return new ArrayList<>(_sqlDrivers);
   }

   public void addChangesListener(IObjectCacheChangeListener lis)
   {
      _listeners.remove(lis);
      _listeners.add(lis);
   }

   public void removeChangesListener(IObjectCacheChangeListener lis)
   {
      _listeners.remove(lis);
   }

   public void load(String path)
   {
      try(FileReader fileReader = new FileReader(path))
      {
         load(fileReader);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         s_log.error(e);
         throw Utilities.wrapRuntime(e);
      }
   }

   public void load(InputStreamReader isr)
   {
      try
      {
         XMLBeanReader xmlBeanReader = new XMLBeanReader();
         xmlBeanReader.load(isr, getClass().getClassLoader());

         for (Object bean : xmlBeanReader.getBeans())
         {
            final SQLDriver sqlDriver = (SQLDriver) bean;
            if (false == _sqlDrivers.contains(sqlDriver))
            {
               _sqlDrivers.add(sqlDriver);
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         s_log.error(e);
         throw Utilities.wrapRuntime(e);
      }

   }

   @Override
   public void save(String path)
   {
      try
      {
         XMLBeanWriter xmlBeanWriter = new XMLBeanWriter();
         xmlBeanWriter.addIteratorToRoot(_sqlDrivers.iterator());
         xmlBeanWriter.save(path);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         s_log.error(e);
         throw Utilities.wrapRuntime(e);
      }

   }
}
