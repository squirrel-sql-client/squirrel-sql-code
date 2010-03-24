package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;

public class TupelResultController
{
   private JTree _resultTree;
   private Class _persistenCollectionClass;
   private ArrayList<MappedClassInfo> _allMappedClassInfos;

   public TupelResultController(TupelType tupelType, JPanel pnlResults, Class persistenCollectionClass, ArrayList<MappedClassInfo> allMappedClassInfos)
   {
      _persistenCollectionClass = persistenCollectionClass;
      _allMappedClassInfos = allMappedClassInfos;
      _resultTree = new JTree();

      DefaultMutableTreeNode root = new DefaultMutableTreeNode("DumyNode");
      DefaultTreeModel model = new DefaultTreeModel(root);
      _resultTree.setModel(model);
      _resultTree.setRootVisible(false);

      initRoot(tupelType, root);


      _resultTree.addTreeExpansionListener(new TreeExpansionListener()
      {
         @Override
         public void treeExpanded(TreeExpansionEvent event)
         {
            onTreeExpanded(event);
         }

         @Override
         public void treeCollapsed(TreeExpansionEvent event) {}
      });

      pnlResults.removeAll();
      pnlResults.add(new JScrollPane(_resultTree));


   }

   private void onTreeExpanded(TreeExpansionEvent event)
   {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

      for (int i = 0; i < node.getChildCount(); i++)
      {
         DefaultMutableTreeNode kidNode = (DefaultMutableTreeNode) node.getChildAt(i);

         if(0 < kidNode.getChildCount())
         {
            continue;
         }

         if(kidNode.getUserObject() instanceof PersistentCollectionResult)
         {
            ViewObjectsUtil.addPersistentCollectionKids(kidNode);
         }
         else if(kidNode.getUserObject() instanceof SingleResult)
         {
            ViewObjectsUtil.addSingleResultKids(kidNode, (SingleResult) kidNode.getUserObject(), _persistenCollectionClass, _allMappedClassInfos);
         }
      }
      ViewObjectsUtil.nodeStructurChanged(node, _resultTree);
   }


   private void initRoot(TupelType tupelType, DefaultMutableTreeNode root)
   {
      for (TupelResult tupelResult : tupelType.getResults())
      {
         DefaultMutableTreeNode tupelNode = new DefaultMutableTreeNode(tupelResult);
         root.add(tupelNode);

         for (SingleResult singleResult : tupelResult.getSingleResults())
         {
            tupelNode.add(new DefaultMutableTreeNode(singleResult));
         }
      }


      ViewObjectsUtil.nodeStructurChanged(root, _resultTree);
   }
}
