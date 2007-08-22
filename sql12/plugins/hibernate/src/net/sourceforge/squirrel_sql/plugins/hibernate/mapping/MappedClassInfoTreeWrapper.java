package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import java.util.ArrayList;

public class MappedClassInfoTreeWrapper extends Object
{
   private MappedClassInfo _mappedClassInfo;
   private boolean _expanded;
   private String _toString;

   public MappedClassInfoTreeWrapper(MappedClassInfo mappedClassInfo)
   {
      _mappedClassInfo = mappedClassInfo;
      initToString();
   }


   public String toString()
   {
      return _toString;
   }

   private void initToString()
   {
      _toString = _mappedClassInfo.getSimpleClassName() + " ->" + _mappedClassInfo.getTableName();
   }

   public boolean isExpanded()
   {
      return _expanded;
   }


   public void setExpanded(boolean expanded)
   {
      _expanded = expanded;
   }

   public MappedClassInfo getMappedClassInfo()
   {
      return _mappedClassInfo;
   }
}
