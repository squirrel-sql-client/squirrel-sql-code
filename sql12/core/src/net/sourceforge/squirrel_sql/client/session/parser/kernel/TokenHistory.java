package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class TokenHistory
{
   private List<String> _previousTokens = new ArrayList<>();
   public void addPrevious(String token)
   {
      if(StringUtils.isNotBlank(token))
      {
         _previousTokens.add(token);
      }
   }

   public String previous(int i)
   {
      int index = _previousTokens.size() - 1 - i;

      if(index < 0 || index > _previousTokens.size() -1)
      {
         return null;
      }

      return _previousTokens.get(index);
   }
}
