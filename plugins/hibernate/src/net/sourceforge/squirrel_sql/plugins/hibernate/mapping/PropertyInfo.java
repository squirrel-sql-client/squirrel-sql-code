package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;

public class PropertyInfo  extends CompletionInfo
{
   private HibernatePropertyInfo _hibernatePropertyInfo;
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

      return
         (_fullQualifiedName.startsWith(stringToParse) && stringToParse.startsWith(_mappedClassNameWithEndingDot)) ||
         (_simpleQualifiedName.startsWith(stringToParse) && stringToParse.startsWith(_simpleMappedClassNameWithEndingDot));
   }

   public boolean matchesUnQualified(CompletionParser parser)
   {
      return _hibernatePropertyInfo.getPropertyName().startsWith(parser.getLastToken());
   }
}
