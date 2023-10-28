package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import java.util.ArrayList;
import java.util.List;

public class ObjectTreeSelectionStore
{
   private List<ObjectTreeSelectionStoreItem> _objectTreeSelectionStoreItems = new ArrayList<>();

   public List<ObjectTreeSelectionStoreItem> getObjectTreeSelectionStoreItems()
   {
      return _objectTreeSelectionStoreItems;
   }

   public void setObjectTreeSelectionStoreItems(List<ObjectTreeSelectionStoreItem> objectTreeSelectionStoreItems)
   {
      _objectTreeSelectionStoreItems = objectTreeSelectionStoreItems;
   }
}
