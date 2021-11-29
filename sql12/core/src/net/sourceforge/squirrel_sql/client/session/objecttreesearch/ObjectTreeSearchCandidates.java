package net.sourceforge.squirrel_sql.client.session.objecttreesearch;

import java.util.ArrayList;
import java.util.List;

class ObjectTreeSearchCandidates
{
   private ArrayList<ObjectTreeSearchCandidate> _candidates = new ArrayList<>();

   private int _curIndex = 0;
   private String _searchString;

   public ObjectTreeSearchCandidates(String searchString)
   {
      _searchString = searchString;
   }


   public boolean hasNext()
   {
      return _curIndex < _candidates.size();
   }

   public ObjectTreeSearchCandidate next()
   {
      return _candidates.get(_curIndex++);
   }

   public String getSearchString()
   {
      return _searchString;
   }

   public void add(String catalog, String schema, String object)
   {
      ObjectTreeSearchCandidate candidate = new ObjectTreeSearchCandidate(catalog, schema, object);
      _candidates.add(candidate);
   }

   public int size()
   {
      return _candidates.size();
   }

   public List<ObjectTreeSearchCandidate> getList()
   {
      return _candidates;
   }

   public void addFirst(String catalog, String schema, String object)
   {
      ObjectTreeSearchCandidate candidate = new ObjectTreeSearchCandidate(catalog, schema, object);
      _candidates.add(0, candidate);
   }
}
