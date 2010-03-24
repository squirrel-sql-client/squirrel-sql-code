package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePluginResources;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

public class ObjectResultTab extends JPanel
{
   JLabel lblHqlQuery;
   JButton btnClose;
   JTree treeTypes;
   JPanel pnlResults;

   public ObjectResultTab(HibernatePluginResources resource)
   {
      setLayout(new BorderLayout());

      add(createTopPanel(resource), BorderLayout.NORTH);
      add(createSplitPane(), BorderLayout.CENTER);
   }

   private JSplitPane createSplitPane()
   {
      treeTypes = new JTree(new DefaultMutableTreeNode());

      treeTypes.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

      pnlResults = new JPanel(new GridLayout(1,1));
      final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(treeTypes), pnlResults);

      Runnable runnable = new Runnable()
      {
         public void run()
         {
            splitPane.setLastDividerLocation(getWidth() / 2 );
         }
      };


      SwingUtilities.invokeLater(runnable);

      return splitPane;
   }

   private JPanel createTopPanel(HibernatePluginResources resource)
   {
      JPanel ret = new JPanel(new BorderLayout());
      
      lblHqlQuery = new JLabel();
      ret.add(lblHqlQuery, BorderLayout.CENTER);

      btnClose = new JButton(resource.getIcon(HibernatePluginResources.IKeys.CLOSE_IMAGE));

      btnClose.setMargin(new Insets(0, 0, 0, 0));
      btnClose.setBorderPainted(false);
      
      ret.add(btnClose, BorderLayout.EAST);

      return ret;
   }
}
