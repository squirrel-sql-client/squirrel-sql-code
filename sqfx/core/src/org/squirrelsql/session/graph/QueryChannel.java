package org.squirrelsql.session.graph;

import java.util.ArrayList;

public class QueryChannel
{
   private ArrayList<QueryChannelListener> _listeners =new ArrayList<>();

   public void fireChanged()
   {
      for (QueryChannelListener queryChannelListener : _listeners.toArray(new QueryChannelListener[0]))
      {
         queryChannelListener.changed();
      }
   }

   public void addQueryChannelListener(QueryChannelListener queryChannelListener)
   {
      _listeners.add(queryChannelListener);
   }
}
