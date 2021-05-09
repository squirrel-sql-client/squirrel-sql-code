package net.sourceforge.squirrel_sql.client.session;

import java.util.ArrayList;

class ObjectTreeSearchCandidates
{
   ArrayList<ArrayList<String>> _candidates = new ArrayList<>();

   int _curIndex = 0;
   private String _searchString;

   public ObjectTreeSearchCandidates(String searchString)
   {
      _searchString = searchString;
   }


   public boolean hasNext()
   {
      return _curIndex < _candidates.size();
   }

   public ArrayList<String> next()
   {
      return _candidates.get(_curIndex++);
   }

   public String getSearchString()
   {
      return _searchString;
   }

   public void add(String catalog, String schema, String object)
   {
      ArrayList<String> candidate = new ArrayList<String>(3);
      candidate.add(catalog);
      candidate.add(schema);
      candidate.add(object);
      _candidates.add(candidate);
   }

   public int size()
   {
      return _candidates.size();
   }
}
