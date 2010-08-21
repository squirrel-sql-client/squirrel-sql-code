package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;

public class PropertyInfo  extends CompletionInfo
{
   private HibernatePropertyInfo _hibernatePropertyInfo;
   private MappedClassInfo _mappedClassInfo;
   private String _mappedClassNameWithEndingDot;
   private String _fullQualifiedName;
   private String _simpleQualifiedName;
   private String _simpleMappedClassNameWithEndingDot;

   public PropertyInfo(HibernatePropertyInfo hibernatePropertyInfo, String mappedClassName)
   {
      _hibernatePropertyInfo = hibernatePropertyInfo;
      _fullQualifiedName = mappedClassName + "." +hibernatePropertyInfo;
      _simpleQualifiedName = MappingUtils.getSimpleClassName(mappedClassName) + "." + hibernatePropertyInfo;

      _mappedClassNameWithEndingDot = mappedClassName + ".";
      _simpleMappedClassNameWithEndingDot = MappingUtils.getSimpleClassName(mappedClassName) + ".";

   }


   public String getCompareString()
   {
      return _hibernatePropertyInfo.getPropertyName();
   }


   public String toString()
   {
      return _hibernatePropertyInfo.toString();
   }

   public boolean matchesQualified(CompletionParser parser)
   {
      String stringToParse = parser.getStringToParse();

      if(
         (_fullQualifiedName.startsWith(stringToParse) && stringToParse.startsWith(_mappedClassNameWithEndingDot)) ||
         (_simpleQualifiedName.startsWith(stringToParse) && stringToParse.startsWith(_simpleMappedClassNameWithEndingDot))
      )
      {
         return true;
      }

      return false;
   }

   public boolean matchesUnQualified(String attrCandidate)
   {
      return _hibernatePropertyInfo.getPropertyName().startsWith(attrCandidate);
   }

   public String getClassName()
   {
      return _hibernatePropertyInfo.getClassName();
   }

   public HibernatePropertyInfo getHibernatePropertyInfo()
   {
      return _hibernatePropertyInfo;
   }

   public MappedClassInfo getMappedClassInfo()
   {
      return _mappedClassInfo;
   }

   public void setMappedClassInfo(MappedClassInfo mappedClassInfo)
   {
      _mappedClassInfo = mappedClassInfo;
   }
}
