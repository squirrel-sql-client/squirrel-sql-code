package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;


import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;

public class SelectGitRepoRootDirController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SelectGitRepoRootDirController.class);

   private JDialog _dlg;
   private JTree _tree;

   private boolean _ok;

   public File getDir(File startDir)
   {
      createUI();

      GUIUtils.initLocation(_dlg, 300, 300);

      GUIUtils.enableCloseByEscape(_dlg);

      initTree(startDir);


      // Stops here;
      _dlg.setVisible(true);

      if(false == _ok)
      {
         return null;
      }

      DefaultMutableTreeNode selLeave = (DefaultMutableTreeNode) _tree.getSelectionPath().getLastPathComponent();

      if(selLeave == null)
      {
         return null;
      }

      return ((GitRepoTreeDirWrapper)selLeave.getUserObject()).getFile();

   }

   private void initTree(File startDir)
   {
      _tree.setRootVisible(false);

      ArrayList<File> dirs = new ArrayList<>();

      File dir = startDir;

      while (null != dir)
      {
         dirs.add(0, dir);

         dir = dir.getParentFile();
      }


      DefaultMutableTreeNode root = (DefaultMutableTreeNode) _tree.getModel().getRoot();
      root.removeAllChildren();



      DefaultMutableTreeNode curNode = root;
      for (File file : dirs)
      {
         DefaultMutableTreeNode buf = new DefaultMutableTreeNode(new GitRepoTreeDirWrapper(file));

         curNode.add(buf);
         curNode = buf;
      }


      ((DefaultTreeModel)_tree.getModel()).nodeStructureChanged((TreeNode) _tree.getModel().getRoot());
      TreeNode[] path = ((DefaultTreeModel) _tree.getModel()).getPathToRoot(curNode);

      _tree.expandPath(new TreePath(path));
      _tree.setSelectionPath(new TreePath(path));

      ImageIcon icon = Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.DIR_GIF);
      ((DefaultTreeCellRenderer)_tree.getCellRenderer()).setClosedIcon(icon);
      ((DefaultTreeCellRenderer)_tree.getCellRenderer()).setOpenIcon(icon);
      ((DefaultTreeCellRenderer)_tree.getCellRenderer()).setLeafIcon(icon);

   }

   private void onCancel()
   {
      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void onOk()
   {
      _ok = true;
      close();
   }



   private void createUI()
   {
      _dlg = new JDialog(Main.getApplication().getMainFrame(), s_stringMgr.getString("SelectGitRepoRootDirController.title"), true);

      _dlg.getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      _tree = new JTree();
      _dlg.getContentPane().add(new JScrollPane(_tree), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      _dlg.getContentPane().add(createButtonPanel(), gbc);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new FlowLayout(FlowLayout.CENTER, 5,5));

      JButton btnOk = new JButton(s_stringMgr.getString("SelectGitRepoRootDirController.ok"));
      ret.add(btnOk);
      btnOk.addActionListener(e -> onOk());

      JButton btnCancel = new JButton(s_stringMgr.getString("SelectGitRepoRootDirController.cancel"));
      ret.add(btnCancel);
      btnCancel.addActionListener(e -> onCancel());

      return ret;
   }

}
