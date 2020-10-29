package net.sourceforge.squirrel_sql.client.gui.db.recentalias;

import java.util.ArrayList;
import java.util.List;

public class RecentAliasesJsonBean
{
   private int _maxRecentAliases = 10;
   private List<String> _recentAliasesIdentifier = new ArrayList<>();

   public int getMaxRecentAliases()
   {
      return _maxRecentAliases;
   }

   public void setMaxRecentAliases(int maxRecentAliases)
   {
      _maxRecentAliases = maxRecentAliases;
   }

   public List<String> getRecentAliasesIdentifier()
   {
      return _recentAliasesIdentifier;
   }

   public void setRecentAliasesIdentifier(List<String> recentAliasesIdentifier)
   {
      _recentAliasesIdentifier = recentAliasesIdentifier;
   }
}
