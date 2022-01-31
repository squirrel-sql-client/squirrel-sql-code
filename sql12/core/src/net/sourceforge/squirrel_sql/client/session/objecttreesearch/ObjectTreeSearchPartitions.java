package net.sourceforge.squirrel_sql.client.session.objecttreesearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows to partition the Object tree in order to allow multiple search runs (tasks)
 * which can be searched through on the EDT using a {@link javax.swing.Timer}.
 */
class ObjectTreeSearchPartitions
{
   private ArrayList<ObjectTreeSearchPartition> _candidates = new ArrayList<>();

   private int _curIndex = 0;
   private String _searchString;

   public ObjectTreeSearchPartitions(String searchString)
   {
      _searchString = searchString;
   }


   public boolean hasNext()
   {
      return _curIndex < _candidates.size();
   }

   public ObjectTreeSearchPartition next()
   {
      return _candidates.get(_curIndex++);
   }

   public String getSearchString()
   {
      return _searchString;
   }

   public void add(String catalog, String schema, String object)
   {
      ObjectTreeSearchPartition candidate = new ObjectTreeSearchPartition(catalog, schema, object);
      _candidates.add(candidate);
   }

   public int size()
   {
      return _candidates.size();
   }

   public List<ObjectTreeSearchPartition> getList()
   {
      return _candidates;
   }

   public void addFirst(String catalog, String schema, String object)
   {
      ObjectTreeSearchPartition candidate = new ObjectTreeSearchPartition(catalog, schema, object);
      _candidates.add(0, candidate);
   }
}
