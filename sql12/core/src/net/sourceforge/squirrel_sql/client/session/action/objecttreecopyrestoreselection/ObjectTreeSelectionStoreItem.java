package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

public class ObjectTreeSelectionStoreItem
{
   private String _name;
   private ObjectTreeSelection _objectTreeSelection;

   public String getName()
   {
      return _name;
   }

   public void setName(String name)
   {
      _name = name;
   }

   public void setObjectTreeSelection(ObjectTreeSelection objectTreeSelection)
   {
      _objectTreeSelection = objectTreeSelection;
   }

   public ObjectTreeSelection getObjectTreeSelection()
   {
      return _objectTreeSelection;
   }
}
