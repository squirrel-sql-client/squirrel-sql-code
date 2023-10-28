package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;


import java.util.List;
import java.util.stream.Collectors;

public class ObjectTreeSelectionStoreItemWrapper
{
   private final ObjectTreeSelectionStoreItem _item;

   public static List<ObjectTreeSelectionStoreItemWrapper> wrap(List<ObjectTreeSelectionStoreItem> items)
   {
      return items.stream().map(it -> new ObjectTreeSelectionStoreItemWrapper(it)).collect(Collectors.toList());
   }

   public ObjectTreeSelectionStoreItemWrapper(ObjectTreeSelectionStoreItem item)
   {
      _item = item;
   }

   @Override
   public String toString()
   {
      return _item.getName();
   }

   public ObjectTreeSelection getObjectTreeSelection()
   {
      return _item.getObjectTreeSelection();
   }

   public ObjectTreeSelectionStoreItem getItem()
   {
      return _item;
   }
}
