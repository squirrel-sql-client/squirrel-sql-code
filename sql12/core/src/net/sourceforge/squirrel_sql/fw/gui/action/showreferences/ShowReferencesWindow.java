package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import javax.swing.*;
import java.awt.*;

public class ShowReferencesWindow extends JDialog
{
   JTree tree;

   public ShowReferencesWindow(Frame owner, String title)
   {
      super(owner, title);

      JSplitPane split = new JSplitPane();
      getContentPane().add(split);

      tree = new JTree();
      split.setLeftComponent(tree);
      split.setRightComponent(new JPanel());
   }
}
