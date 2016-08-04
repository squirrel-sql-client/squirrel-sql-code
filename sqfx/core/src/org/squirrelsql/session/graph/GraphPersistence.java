package org.squirrelsql.session.graph;

import java.util.ArrayList;

public class GraphPersistence
{
   private ArrayList<GraphTableInfo> _graphTableInfos = new ArrayList<>();

   public ArrayList<GraphTableInfo> getGraphTableInfos()
   {
      return _graphTableInfos;
   }

   public void setGraphTableInfos(ArrayList<GraphTableInfo> graphTableInfos)
   {
      _graphTableInfos = graphTableInfos;
   }
}
