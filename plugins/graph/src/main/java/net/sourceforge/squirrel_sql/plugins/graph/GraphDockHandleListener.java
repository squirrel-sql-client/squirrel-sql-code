package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;

public interface GraphDockHandleListener
{
   void show(JPanel panel, int lastHeight);
   void hide();
}
