package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePluginResources;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ObjectResultTabController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ObjectResultTabController.class);

   private ObjectResultTab _tab;
   private ResultsController _resultsController;

   public ObjectResultTabController(List objects, int maxNumResults, HibernateConnection con, String hqlQuery, HibernatePluginResources resource, final ObjectResultTabControllerListener l, ISession session)
   {
      _tab = new ObjectResultTab(resource);

      initHqlQueryLabel(objects, hqlQuery, objects.size(), maxNumResults);

      _tab.btnClose.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            l.closeTab(ObjectResultTabController.this);
         }
      });


      if(0 == objects.size())
      {
         return;
      }

      //String hql = "from Best";
      //String hql = "from BestPos";
      //String hql = "from pack.Kv";
      //String hql = "from pack.KvPos";
      //String hql = "from Best be inner join be.bestPosses bep";  --> two result types --> TupelType
      //String hql = "select be from Best be inner join be.bestPosses bep";
      //String hql = "select be from Best be inner join fetch be.bestPosses bep";


      Class persistenCollectionClass = con.getPersistenCollectionClass();

      ArrayList<MappedClassInfo> mappedClassInfos = con.getMappedClassInfos();
      _resultsController = new ResultsController(_tab.pnlResults, hqlQuery, persistenCollectionClass, mappedClassInfos, session);



      RootType qrmr = new RootType(objects, con.getMappedClassInfos(), persistenCollectionClass);

      DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(qrmr);
      _tab.treeTypes.setModel(new DefaultTreeModel(rootNode));

      _tab.treeTypes.addTreeExpansionListener(new TreeExpansionListener()
      {
         @Override
         public void treeExpanded(TreeExpansionEvent event)
         {
            onTreeExpanded(event);
         }

         @Override
         public void treeCollapsed(TreeExpansionEvent event) {}
      });

      _tab.treeTypes.addTreeSelectionListener(new TreeSelectionListener()
      {
         @Override
         public void valueChanged(TreeSelectionEvent e)
         {
            onTreeSelectionChanged(e);
         }
      });

      initRoot(rootNode);

      //CommandLineOutput.displayObjects(mappedClassInfos, qrmr,  con.getPersistenCollectionClass());
   }

   private void initHqlQueryLabel(List objects, String hqlQuery, int numResults, int maxNumResults)
   {
      if (numResults == maxNumResults)
      {
         _tab.lblHqlQuery.setText(s_stringMgr.getString("ObjectResultTabController.queryInfoLimited", objects.size(), hqlQuery) );
      }
      else
      {
         _tab.lblHqlQuery.setText(s_stringMgr.getString("ObjectResultTabController.queryInfo", objects.size(), hqlQuery) );
      }
   }

   private void onTreeSelectionChanged(TreeSelectionEvent e)
   {

      TreePath path = e.getNewLeadSelectionPath();

      if(null == path)
      {
         _resultsController.clear();
         return;
      }

      DefaultMutableTreeNode n = (DefaultMutableTreeNode) path.getLastPathComponent();

      if(null != n && null != n.getUserObject())
      {
         _resultsController.typeChanged(n.getUserObject());
      }
      else
      {
         _resultsController.clear();
      }
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

         IType type = (IType) kidNode.getUserObject();

         for (IType kidType : type.getKidTypes())
         {
            kidNode.add(new DefaultMutableTreeNode(kidType));
         }
      }
      ViewObjectsUtil.nodeStructurChanged(node, _tab.treeTypes);
   }

   private void initRoot(DefaultMutableTreeNode rootNode)
   {
      RootType rootType = (RootType) rootNode.getUserObject();
      IType resultType = rootType.getResultType();
      DefaultMutableTreeNode kidNode = new DefaultMutableTreeNode(resultType);
      rootNode.add(kidNode);

      for (IType kidResultType : resultType.getKidTypes())
      {
         kidNode.add(new DefaultMutableTreeNode(kidResultType));
      }
      ViewObjectsUtil.nodeStructurChanged(rootNode, _tab.treeTypes);
   }

   public ObjectResultTab getPanel()
   {
      return _tab;
   }
}
