package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;

public class SimpleHQLCompletionInfo extends CompletionInfo
{
   private String _infoString;


   public SimpleHQLCompletionInfo(String infoString)
   {
      _infoString = infoString;
   }

   public String getCompareString()
   {
      return _infoString;
   }

   public boolean matches(CompletionParser parser)
   {
      return 1 == parser.size() && _infoString.startsWith(parser.getToken(0));
   }
}
