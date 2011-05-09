package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import javax.swing.*;

public class GraphQueryOrderPanelCtrl
{

   public JPanel getGraphQueryOrderPanel()
   {
      JPanel ret = new JPanel();
      ret.add(new JLabel("Order"));
      return ret;
   }
}
