package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import javax.swing.*;
import java.awt.*;

final class ObjectTreePanelLeft extends JPanel
{
   ObjectTreePanelLeft(FindInObjectTreeController findInObjectTreeController, ObjectTree tree)
   {
      super(new BorderLayout());
      add(findInObjectTreeController.getFindInObjectTreePanel(), BorderLayout.NORTH);
      JScrollPane sp = new JScrollPane();
      sp.setBorder(BorderFactory.createEmptyBorder());
      sp.setViewportView(tree);
      sp.setPreferredSize(new Dimension(200, 200));
      sp.setMinimumSize(new Dimension(0, 0));
      add(sp, BorderLayout.CENTER);
   }
}
