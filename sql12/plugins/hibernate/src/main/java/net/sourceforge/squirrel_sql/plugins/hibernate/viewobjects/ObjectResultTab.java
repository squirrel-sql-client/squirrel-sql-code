package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePluginResources;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

public class ObjectResultTab extends JPanel
{
   private static final StringManager s_stringMgr =
         StringManagerFactory.getStringManager(ObjectResultTab.class);


   JLabel lblHqlQuery;
   JButton btnClose;
   JTree treeTypes;
   JPanel pnlResults;
   JButton btnCopySql;

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

      ret.add(createButtonPanel(resource), BorderLayout.EAST);

      return ret;
   }

   private JPanel createButtonPanel(HibernatePluginResources resource)
   {
      JPanel ret = new JPanel(new GridLayout(1,2,3,2));

      btnCopySql = new JButton(resource.getIcon(HibernatePluginResources.IKeys.SQL_COPY_IMAGE));
      btnCopySql.setToolTipText(s_stringMgr.getString("ObjectResultTab.CopySqlToClip"));
      btnCopySql.setBorder(BorderFactory.createRaisedBevelBorder());
      ret.add(btnCopySql);

      btnClose = new JButton(resource.getIcon(HibernatePluginResources.IKeys.CLOSE_IMAGE));
      btnClose.setBorder(BorderFactory.createRaisedBevelBorder());
      ret.add(btnClose);

      return ret;
   }
}
