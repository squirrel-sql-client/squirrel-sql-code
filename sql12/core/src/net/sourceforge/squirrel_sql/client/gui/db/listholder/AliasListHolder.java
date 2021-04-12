package net.sourceforge.squirrel_sql.client.gui.db.listholder;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AliasListHolder implements ListHolder
{
   private static ILogger s_log = LoggerController.createLogger(AliasListHolder.class);

   private List<SQLAlias> _sqlAliases = new ArrayList<>();
   private ArrayList<IObjectCacheChangeListener> _listeners = new ArrayList<>();


   public SQLAlias get(IIdentifier id)
   {
      return _sqlAliases.stream().filter(a -> Utilities.equalsRespectNull(id, a.getIdentifier())).findFirst().orElse(null);
   }

   public List<SQLAlias> getAll()
   {
      return new ArrayList<>(_sqlAliases);
   }

   public void add(ISQLAlias alias)
   {
      if(_sqlAliases.contains(alias))
      {
         return;
      }


      _sqlAliases.add((SQLAlias) alias);

      new ArrayList<>(_listeners).forEach(l -> l.objectAdded(new ObjectCacheChangeEvent(alias)));

   }

   public void remove(IIdentifier identifier)
   {
      final SQLAlias sqlAlias = get(identifier);

      if (null != sqlAlias)
      {
         _sqlAliases.remove(sqlAlias);
         new ArrayList<>(_listeners).forEach(l -> l.objectRemoved(new ObjectCacheChangeEvent(sqlAlias)));
      }
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

   public void load(File aliasesFile)
   {
      try
      {
         if(false == aliasesFile.exists())
         {
            return;
         }

         XMLBeanReader xmlBeanReader = new XMLBeanReader();
         xmlBeanReader.load(aliasesFile, getClass().getClassLoader());

         for (Object bean : xmlBeanReader.getBeans())
         {
            final SQLAlias sqlAlias = (SQLAlias) bean;
            if (false == _sqlAliases.contains(sqlAlias))
            {
               _sqlAliases.add(sqlAlias);
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
         xmlBeanWriter.addIteratorToRoot(_sqlAliases.iterator());
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
