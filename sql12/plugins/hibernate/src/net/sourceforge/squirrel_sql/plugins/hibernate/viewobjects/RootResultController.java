package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;

public class RootResultController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RootResultController.class);


   private ArrayList<MappedClassInfo> _mappedClassInfos;
   private JTree _resultTree;

   public RootResultController(RootType rootType, JPanel pnlResults, ArrayList<MappedClassInfo> mappedClassInfos, ResultControllerChannel resultControllerChannel)
   {
      RootType rootType1 = rootType;
      _mappedClassInfos = mappedClassInfos;
      resultControllerChannel.setActiveControllersListener(() -> onTypedValuesDisplayModeChanged());

      if(rootType1.getResultType() instanceof TupelType)
      {
         String msg = s_stringMgr.getString("RootResultController.MutibleTypesMessage", getClassNames((TupelType) rootType1.getResultType()));
         pnlResults.removeAll();
         pnlResults.add(new MultipleLineLabel(msg));
         return;
      }

      _resultTree = new JTree();

      DefaultMutableTreeNode root = new DefaultMutableTreeNode("DumyNode");
      DefaultTreeModel model = new DefaultTreeModel(root);
      _resultTree.setModel(model);
      _resultTree.setRootVisible(false);

      initRoot((SingleType) rootType1.getResultType(), root);


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

   private void onTypedValuesDisplayModeChanged()
   {
      GUIUtils.getExpandedLeafNodes(_resultTree).forEach(n -> ((DefaultTreeModel)_resultTree.getModel()).nodeChanged(n));
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
            ViewObjectsUtil.addSingleResultKids(kidNode, (SingleResult) kidNode.getUserObject(), _mappedClassInfos);
         }
      }
      ViewObjectsUtil.nodeStructurChanged(node, _resultTree);
   }

   private void initRoot(SingleType singleType, DefaultMutableTreeNode root)
   {
      for (SingleResult singleResult : singleType.getResults())
      {
         DefaultMutableTreeNode singleResultNode = new DefaultMutableTreeNode(singleResult);
         root.add(singleResultNode);
         ViewObjectsUtil.addSingleResultKids(singleResultNode, singleResult, _mappedClassInfos);
      }

      ViewObjectsUtil.nodeStructurChanged(root, _resultTree);
   }


   private String getClassNames(TupelType tupelType)
   {
      String ret = "";

      for (IType type : tupelType.getKidTypes())
      {
         SingleType singleType = (SingleType) type;
         ret += singleType.getMappedClassInfo().getClassName() + "\n";
      }

      return ret;
   }
}
