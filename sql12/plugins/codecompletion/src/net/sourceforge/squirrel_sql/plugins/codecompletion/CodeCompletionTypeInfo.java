package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;

public class CodeCompletionTypeInfo extends CodeCompletionInfo
{
   private String _typeName;

   public CodeCompletionTypeInfo(String typeName)
   {
      _typeName = typeName;
   }

   public String getCompletionString()
   {
      return _typeName;
   }

   public String toString()
   {
      return _typeName;
   }
}
