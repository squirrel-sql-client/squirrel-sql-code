package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.io.File;
import java.util.List;

public class ObjectTreeSelectionStoreManager
{
   private static final ILogger s_log = LoggerController.createLogger(ObjectTreeSelectionStoreManager.class);

   private ObjectTreeSelectionStore _objectTreeSelectionStore;

   public void save()
   {
      if(null != _objectTreeSelectionStore)
      {
         JsonMarshalUtil.writeObjectToFile(new ApplicationFiles().getObjectTreeSelectionStoreJsonFile(), _objectTreeSelectionStore);
      }
   }

   public void store(ObjectTreeSelection objectTreeSelection, String name)
   {
      init();

      for (ObjectTreeSelectionStoreItem objectTreeSelectionStoreItem : _objectTreeSelectionStore.getObjectTreeSelectionStoreItems())
      {
         if(name.equals(objectTreeSelectionStoreItem.getName()))
         {
            _objectTreeSelectionStore.getObjectTreeSelectionStoreItems().remove(objectTreeSelectionStoreItem);
            break;
         }
      }

      ObjectTreeSelectionStoreItem buf = new ObjectTreeSelectionStoreItem();
      buf.setName(name);
      buf.setObjectTreeSelection(objectTreeSelection);

      _objectTreeSelectionStore.getObjectTreeSelectionStoreItems().add(0, buf);
   }

   private void init()
   {
      if(null != _objectTreeSelectionStore)
      {
         return;
      }

      _objectTreeSelectionStore = new ObjectTreeSelectionStore();
      try
      {
         File objectTreeSelectionStoreJsonFile = new ApplicationFiles().getObjectTreeSelectionStoreJsonFile();
         if(objectTreeSelectionStoreJsonFile.exists())
         {
            _objectTreeSelectionStore = JsonMarshalUtil.readObjectFromFile(objectTreeSelectionStoreJsonFile, ObjectTreeSelectionStore.class);
         }
      }
      catch (Exception e)
      {
         s_log.error("Failed to read file " + new ApplicationFiles().getObjectTreeSelectionStoreJsonFile(), e);
      }
   }

   public List<ObjectTreeSelectionStoreItem> getItems()
   {
      init();
      return _objectTreeSelectionStore.getObjectTreeSelectionStoreItems();
   }
}
