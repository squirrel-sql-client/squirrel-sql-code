package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;

public class PropertyInfo  extends CompletionInfo
{
   private String _propertyName;
   private String _mappedClassNameWithEndingDot;
   private String _fullQualifiedName;
   private String _simpleQualifiedName;
   private String _simpleMappedClassNameWithEndingDot;

   public PropertyInfo(String propertyName, String mappedClassName)
   {
      _propertyName = propertyName;
      _fullQualifiedName = mappedClassName + "." +propertyName;
      _simpleQualifiedName = MappingUtils.getSimpleClassName(mappedClassName) + "." + propertyName;

      _mappedClassNameWithEndingDot = mappedClassName + ".";
      _simpleMappedClassNameWithEndingDot = MappingUtils.getSimpleClassName(mappedClassName) + ".";

   }


   public String getCompareString()
   {
      return _propertyName;
   }

   public boolean matchesQualified(CompletionParser parser)
   {
      String stringToParse = parser.getStringToParse();

      return
         (_fullQualifiedName.startsWith(stringToParse) && stringToParse.startsWith(_mappedClassNameWithEndingDot)) ||
         (_simpleQualifiedName.startsWith(stringToParse) && stringToParse.startsWith(_simpleMappedClassNameWithEndingDot));
   }
}
