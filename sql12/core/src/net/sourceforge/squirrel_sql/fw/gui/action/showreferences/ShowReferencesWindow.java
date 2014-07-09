package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecuterPanel;

import javax.swing.*;
import java.awt.*;

public class ShowReferencesWindow extends JDialog
{
   JTree tree;
   SQLResultExecuterPanel resultExecuterPanel;

   public ShowReferencesWindow(ISession session, Frame owner, String title)
   {
      super(owner, title);

      JSplitPane split = new JSplitPane();
      getContentPane().add(split);

      tree = new JTree();
      split.setLeftComponent(new JScrollPane(tree));
      resultExecuterPanel = new SQLResultExecuterPanel(session);

      resultExecuterPanel.setMinimumSize(new Dimension(0,0));
      split.setRightComponent(resultExecuterPanel);
   }
}
