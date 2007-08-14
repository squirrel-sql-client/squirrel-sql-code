package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import javax.swing.*;
import java.awt.*;

public class MappedObjectPanel extends JPanel
{
   JTree _objectTree;

   public MappedObjectPanel()
   {
      super(new GridLayout(1,1));

      _objectTree = new JTree();

      add(new JScrollPane(_objectTree));
   }
}
