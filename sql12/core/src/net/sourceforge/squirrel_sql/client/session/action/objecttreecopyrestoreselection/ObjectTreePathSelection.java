package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import java.util.ArrayList;
import java.util.List;

public class ObjectTreePathSelection
{
   private List<String> _simpleNamePath = new ArrayList<>();

   public List<String> getSimpleNamePath()
   {
      return _simpleNamePath;
   }

   public void setSimpleNamePath(List<String> simpleNamePath)
   {
      _simpleNamePath = simpleNamePath;
   }
}
