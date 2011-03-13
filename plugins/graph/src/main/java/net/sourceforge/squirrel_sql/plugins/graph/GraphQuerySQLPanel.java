package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;
import java.awt.*;

public class GraphQuerySQLPanel extends JPanel
{
   public GraphQuerySQLPanel(JComponent editor)
   {
      setLayout(new GridLayout(1,1));
      add(editor);
   }
}
