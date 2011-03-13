package net.sourceforge.squirrel_sql.plugins.graph;

public class QueryBuilder
{
   private GraphController _queryGraphController;
   private GraphMainPanelTab _graphMainPanelTab;

   public QueryBuilder(GraphController queryGraphController, GraphMainPanelTab graphMainPanelTab)
   {
      _queryGraphController = queryGraphController;
      _graphMainPanelTab = graphMainPanelTab;
   }

   public GraphController getQueryGraphController()
   {
      return _queryGraphController;
   }

   public GraphMainPanelTab getGraphMainPanelTab()
   {
      return _graphMainPanelTab;
   }
}
