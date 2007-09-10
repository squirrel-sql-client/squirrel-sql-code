package de.ixdb.squirrel_sql.plugins.cache;

import javax.swing.*;
import java.awt.*;

public class QueryPlanPanel extends JPanel
{
   public JEditorPane txtQueryPlan;

   public QueryPlanPanel()
   {
      setLayout(new GridLayout(1,1));
      txtQueryPlan = new JEditorPane();
      add(new JScrollPane(txtQueryPlan));
   }
}
