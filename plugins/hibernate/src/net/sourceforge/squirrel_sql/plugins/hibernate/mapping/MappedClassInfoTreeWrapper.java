package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

public class MappedClassInfoTreeWrapper extends Object implements Comparable
{
   private MappedClassInfo _mappedClassInfo;
   private boolean _expanded;
   private String _toString;

   public MappedClassInfoTreeWrapper(MappedClassInfo mappedClassInfo, boolean showQualified)
   {
      _mappedClassInfo = mappedClassInfo;
      initToString(showQualified);
   }


   public String toString()
   {
      return _toString;
   }

   private void initToString(boolean showQualified)
   {
      if(showQualified)
      {
         _toString = _mappedClassInfo.getClassName() + " ->" + _mappedClassInfo.getTableName();
      }
      else
      {
         _toString = _mappedClassInfo.getSimpleClassName() + " ->" + _mappedClassInfo.getTableName();
      }
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

   public void setQualified(boolean b)
   {
      initToString(b);
   }

   public int compareTo(Object o)
   {
      MappedClassInfoTreeWrapper other = (MappedClassInfoTreeWrapper) o;

      return _toString.compareTo(other.toString());
   }
}
