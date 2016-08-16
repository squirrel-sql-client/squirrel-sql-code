package org.squirrelsql.session.graph;

import org.squirrelsql.services.I18n;

import java.util.ArrayList;
import java.util.UUID;

public class GraphPersistence
{
   public static final String DEFAULT_GRAPH_NAME = new I18n(GraphPersistence.class).t("graph.new.graph.title");

   private String _tabTitle = DEFAULT_GRAPH_NAME;
   private String _id = UUID.randomUUID().toString();

   private ArrayList<GraphTableInfo> _graphTableInfos = new ArrayList<>();

   public ArrayList<GraphTableInfo> getGraphTableInfos()
   {
      return _graphTableInfos;
   }

   public void setGraphTableInfos(ArrayList<GraphTableInfo> graphTableInfos)
   {
      _graphTableInfos = graphTableInfos;
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
}
