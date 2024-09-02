package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePluginResources;

public class ObjectResultTab extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ObjectResultTab.class);

   JButton btnProjectionDisplay;
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
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,3, 1, 0), 0,0);
      btnProjectionDisplay = new JButton(resource.getIcon(HibernatePluginResources.IKeys.DISPLAY_CHOICE_IMAGE));
      btnProjectionDisplay.setToolTipText(s_stringMgr.getString("ObjectResultTab.chooseProjectionDisplay.tooltip"));
      btnProjectionDisplay.setBorder(BorderFactory.createRaisedBevelBorder());
      ret.add(btnProjectionDisplay, gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,3, 1, 0), 0,0);
      lblHqlQuery = new JLabel();
      ret.add(lblHqlQuery, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,3, 1, 3), 0,0);
      ret.add(createRightButtonPanel(resource), gbc);

      return ret;
   }

   private JPanel createRightButtonPanel(HibernatePluginResources resource)
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
