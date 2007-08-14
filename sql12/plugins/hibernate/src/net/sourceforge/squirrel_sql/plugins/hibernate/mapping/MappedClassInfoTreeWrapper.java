package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import java.util.ArrayList;

public class MappedClassInfoTreeWrapper extends Object
{
   private MappedClassInfo _mappedClassInfo;
   private boolean _expanded;

   public MappedClassInfoTreeWrapper(MappedClassInfo mappedClassInfo)
   {
      _mappedClassInfo = mappedClassInfo;
   }


   public String toString()
   {
      return _mappedClassInfo.getSimpleClassName();
   }

   public ArrayList<MappedClassInfo> getMappedClassInfoProperies()
   {
      return null;  //To change body of created methods use File | Settings | File Templates.
   }

   public boolean isExpanded()
   {
      return _expanded;
   }


   public void setExpanded(boolean expanded)
   {
      _expanded = expanded;
   }
}
