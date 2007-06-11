package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

public class MappingUtils
{
   static String getSimpleClassName(String mappedClassName)
   {
      String[] cpTokens = mappedClassName.split("\\.");
      return cpTokens[cpTokens.length - 1];
   }
}
