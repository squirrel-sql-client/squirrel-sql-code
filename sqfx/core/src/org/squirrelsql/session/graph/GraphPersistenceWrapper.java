package org.squirrelsql.session.graph;

public class GraphPersistenceWrapper
{
   private GraphPersistence _delegate;


   public GraphPersistenceWrapper()
   {
      this(new GraphPersistence());
   }


   public GraphPersistenceWrapper(GraphPersistence delegate)
   {
      _delegate = delegate;
   }


   public GraphPersistence getDelegate()
   {
      return _delegate;
   }

   public String getTabTitle()
   {
      return _delegate.getTabTitle();
   }

   public boolean isNew()
   {
      return GraphPersistence.DEFAULT_GRAPH_NAME.equals(_delegate.getTabTitle());
   }
}
