package org.squirrelsql.session.graph;

import org.squirrelsql.services.I18n;

import java.util.ArrayList;

public class GraphPersistence
{
   private String _tabTitle = new I18n(GraphPersistence.class).t("graph.new.graph.title");

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
}
