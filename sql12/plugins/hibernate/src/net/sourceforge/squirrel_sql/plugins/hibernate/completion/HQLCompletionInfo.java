package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;

public class HQLCompletionInfo extends CompletionInfo
{
   private String _compareString;


   public HQLCompletionInfo(String _compareString)
   {
      this._compareString = _compareString;
   }

   


   public String getCompareString()
   {
      return _compareString;
   }
}
