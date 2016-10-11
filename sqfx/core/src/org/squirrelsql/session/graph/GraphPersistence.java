package org.squirrelsql.session.graph;

import org.squirrelsql.services.I18n;

import java.util.ArrayList;
import java.util.UUID;

public class GraphPersistence
{
   public static final String DEFAULT_GRAPH_NAME = new I18n(GraphPersistence.class).t("graph.new.graph.title");

   private String _tabTitle = DEFAULT_GRAPH_NAME;
   private String _id = UUID.randomUUID().toString();

   private ArrayList<GraphTablePersistence> _graphTablePersistences = new ArrayList<>();
   private boolean _hideNoJoins;

   public ArrayList<GraphTablePersistence> getGraphTablePersistences()
   {
      return _graphTablePersistences;
   }

   public void setGraphTablePersistences(ArrayList<GraphTablePersistence> graphTablePersistences)
   {
      _graphTablePersistences = graphTablePersistences;
   }

   public String getTabTitle()
   {
      return _tabTitle;
   }

   public void setTabTitle(String tabTitle)
   {
      _tabTitle = tabTitle;
   }

   public String getId()
   {
      return _id;
   }

   public void setId(String id)
   {
      _id = id;
   }

   public boolean isHideNoJoins()
   {
      return _hideNoJoins;
   }

   public void setHideNoJoins(boolean hideNoJoins)
   {
      _hideNoJoins = hideNoJoins;
   }
}
