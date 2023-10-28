package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import java.util.ArrayList;
import java.util.List;

public class ObjectTreeSelection
{
   private List<ObjectTreePathSelection> _objectTreePathSelections = new ArrayList<>();

   public List<ObjectTreePathSelection> getObjectTreePathSelections()
   {
      return _objectTreePathSelections;
   }

   public void setObjectTreePathSelections(List<ObjectTreePathSelection> objectTreePathSelections)
   {
      _objectTreePathSelections = objectTreePathSelections;
   }
}
