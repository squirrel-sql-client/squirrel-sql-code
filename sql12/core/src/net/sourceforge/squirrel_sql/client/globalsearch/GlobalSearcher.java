package net.sourceforge.squirrel_sql.client.globalsearch;

public class GlobalSearcher
{
   public void searchGlobally(String toSearch, GlobalSearchType globalType)
   {
      System.out.println("toSearch = " + toSearch);
      System.out.println("globalType = " + globalType.name());
   }
}
