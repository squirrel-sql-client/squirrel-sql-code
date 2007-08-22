package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.plugins.hibernate.IHibernateConnectionProvider;
import net.sourceforge.squirrel_sql.plugins.hibernate.ConnectionListener;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePluginResources;
import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.*;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.HashMap;

public class MappedObjectPanelManager
{
   private MappedObjectPanel _panel;
   private IHibernateConnectionProvider _connectionProvider;
   private ISession _session;
   private DefaultMutableTreeNode _root;
   private HashMap<String, MappedClassInfo> _mappedClassInfoByClassName;
   private DetailPanelController _detailPanelController;

   public MappedObjectPanelManager(IHibernateConnectionProvider connectionProvider, ISession session, HibernatePluginResources resource)
   {
      _connectionProvider = connectionProvider;
      _session = session;


      _detailPanelController = new DetailPanelController(); 
      _panel = new MappedObjectPanel(_detailPanelController.getDetailComponent());


      _root = new DefaultMutableTreeNode("Mapping");
      _panel.objectTree.setModel(new DefaultTreeModel(_root));

      _panel.objectTree.setCellRenderer(new MappingTreeCellRenderer(resource));

      nodeStructurChanged(_root);

      _panel.objectTree.addTreeExpansionListener(new TreeExpansionListener()
      {
         public void treeExpanded(TreeExpansionEvent event)
         {
            onTreeExpanded(event);
         }

         public void treeCollapsed(TreeExpansionEvent event) {}
      });


      _panel.objectTree.addTreeSelectionListener(new TreeSelectionListener()
      {
         public void valueChanged(TreeSelectionEvent e)
         {
            onTreeSelectionChanged(e);
         }
      });

      _connectionProvider.addConnectionListener(new ConnectionListener()
      {
         public void connectionOpened(HibernateConnection con)
         {
            onConnectionOpened(con);
         }

         public void connectionClosed()
         {
            onConnectionClosed();
         }
      });

   }

   private void onTreeSelectionChanged(TreeSelectionEvent e)
   {
      DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();

      if(null != n && n.getUserObject() instanceof MappedClassInfoTreeWrapper)
      {
         _detailPanelController.selectionChanged((MappedClassInfoTreeWrapper)n.getUserObject());
      }
      else
      {
         _detailPanelController.selectionChanged(null);
      }
   }

   private void onTreeExpanded(TreeExpansionEvent event)
   {
      DefaultMutableTreeNode mappedClassInfoWrapperNode = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

      Object userObject = mappedClassInfoWrapperNode.getUserObject();

      if(  userObject instanceof MappedClassInfoTreeWrapper
         && false == ((MappedClassInfoTreeWrapper) userObject).isExpanded())
      {

         for(int i=0; i < mappedClassInfoWrapperNode.getChildCount(); ++i)
         {
            DefaultMutableTreeNode propertyInfoNode = (DefaultMutableTreeNode) mappedClassInfoWrapperNode.getChildAt(i);
            PropertyInfoTreeWrapper propertyInfoTreeWrapper = (PropertyInfoTreeWrapper) propertyInfoNode.getUserObject();
            MappedClassInfo mappedClassInfo = propertyInfoTreeWrapper.getMappedClassInfo();
            addMappedClassInfoNode(mappedClassInfo, propertyInfoNode);
         }

         ((MappedClassInfoTreeWrapper) userObject).setExpanded(true);

         nodeStructurChanged(mappedClassInfoWrapperNode);
      }
   }

   private void onConnectionClosed()
   {
      _root.removeAllChildren();
      nodeStructurChanged(_root);
      _detailPanelController.selectionChanged(null);
   }

   private void onConnectionOpened(HibernateConnection con)
   {
      ArrayList<MappedClassInfo> mappedClassInfos = con.getMappedClassInfos();

      initMappedClassInfos(mappedClassInfos);

      for (MappedClassInfo mappedClassInfo : mappedClassInfos)
      {
         addMappedClassInfoNode(mappedClassInfo, _root);
      }

      nodeStructurChanged(_root);
   }

   private void nodeStructurChanged(DefaultMutableTreeNode node)
   {
      ((DefaultTreeModel)_panel.objectTree.getModel()).nodeStructureChanged(node);
   }

   private void addMappedClassInfoNode(MappedClassInfo mappedClassInfo, DefaultMutableTreeNode parent)
   {
      DefaultMutableTreeNode mappedClassInfoNode = new DefaultMutableTreeNode(new MappedClassInfoTreeWrapper(mappedClassInfo));

      PropertyInfo[] propertyInfos = mappedClassInfo.getAttributes();

      for (PropertyInfo propertyInfo : propertyInfos)
      {
         String className = propertyInfo.getHibernatePropertyInfo().getClassName();
         if(_mappedClassInfoByClassName.containsKey(className))
         {
            PropertyInfoTreeWrapper propertyInfoTreeWrapper = new PropertyInfoTreeWrapper(propertyInfo, _mappedClassInfoByClassName.get(className));
            DefaultMutableTreeNode propertyInfoNode = new DefaultMutableTreeNode(propertyInfoTreeWrapper);
            mappedClassInfoNode.add(propertyInfoNode);
         }
      }

      parent.add(mappedClassInfoNode);
   }

   private void initMappedClassInfos(ArrayList<MappedClassInfo> mappedClassInfos)
   {
      _mappedClassInfoByClassName = new HashMap<String, MappedClassInfo>();
      for (MappedClassInfo mappedClassInfo : mappedClassInfos)
      {
         _mappedClassInfoByClassName.put(mappedClassInfo.getClassName(), mappedClassInfo);
      }
   }

   public JComponent getComponent()
   {
      return _panel;
   }

   public void closing()
   {
      _panel.closing();
   }
}
