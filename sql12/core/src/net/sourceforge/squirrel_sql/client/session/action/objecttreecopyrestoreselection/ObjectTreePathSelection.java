package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import java.util.ArrayList;
import java.util.List;

public class ObjectTreePathSelection
{
   private List<String> _simpleNamePath = new ArrayList<>();
   private String _typeName;

   public List<String> getSimpleNamePath()
   {
      return _simpleNamePath;
   }

   public void setSimpleNamePath(List<String> simpleNamePath)
   {
      _simpleNamePath = simpleNamePath;
   }

   public void setTypeName(String typeName)
   {
      _typeName = typeName;
   }

   public String getTypeName()
   {
      return _typeName;
   }
}
