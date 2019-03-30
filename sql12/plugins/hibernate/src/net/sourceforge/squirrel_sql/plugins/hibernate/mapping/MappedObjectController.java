package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.plugins.hibernate.ConnectionListener;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateChannel;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePluginResources;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernateConfiguration;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class MappedObjectController
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MappedObjectController.class);


   private static final String PERF_KEY_OBJ_TAB_CHKSHOWQUALIFIED = "Squirrel.hibernateplugin.chkShowQualified";


   private MappedObjectPanel _panel;
   private HibernateChannel _hibernateChannel;
   private ISession _session;
   private DefaultMutableTreeNode _root;
   private HashMap<String, MappedClassInfo> _mappedClassInfoByClassName;
   private DetailPanelController _detailPanelController;
   private ArrayList<MappedClassInfoTreeWrapper> _mappedClassInfoTreeWrappers;

   public MappedObjectController(HibernateChannel hibernateChannel, ISession session, HibernatePluginResources resource)
   {
      _hibernateChannel = hibernateChannel;
      _session = session;


      _detailPanelController = new DetailPanelController(_session);
      _panel = new MappedObjectPanel(_detailPanelController.getDetailComponent());


      _root = new DefaultMutableTreeNode(new MappingRoot());
      _panel.treMappedObjects.setModel(new DefaultTreeModel(_root));

      _panel.treMappedObjects.setCellRenderer(new MappingTreeCellRenderer(resource));

      nodeStructurChanged(_root);

      _panel.treMappedObjects.addTreeExpansionListener(new TreeExpansionListener()
      {
         public void treeExpanded(TreeExpansionEvent event)
         {
            onTreeExpanded(event);
         }

         public void treeCollapsed(TreeExpansionEvent event) {}
      });


      _panel.treMappedObjects.addTreeSelectionListener(e -> onTreeSelectionChanged(e));


      _panel.treMappedObjects.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            MappedObjectTreeRightMouseHandler.maybeShowTreePopup(e, MappedObjectController.this._panel.treMappedObjects);
         }

         @Override
         public void mouseReleased(MouseEvent e)
         {
            MappedObjectTreeRightMouseHandler.maybeShowTreePopup(e, MappedObjectController.this._panel.treMappedObjects);
         }
      });


      _hibernateChannel.addConnectionListener(new ConnectionListener()
      {
         public void connectionOpened(HibernateConnection con, HibernateConfiguration cfg)
         {
            initRoot(con, cfg);
            initTree(con);
         }

         public void connectionClosed()
         {
            onConnectionClosed();
         }
      });

      _panel.chkShowQualified.addActionListener(e -> onChkQualified());

      _panel.chkShowQualified.setSelected(Props.getBoolean(PERF_KEY_OBJ_TAB_CHKSHOWQUALIFIED, false));

   }

   private void initRoot(HibernateConnection con, HibernateConfiguration cfg)
   {
      MappingRoot mr = (MappingRoot) _root.getUserObject();

      mr.init(con, cfg);

   }

   private void onChkQualified()
   {
      HibernateConnection con = _hibernateChannel.getHibernateConnection();

      if(null == con)
      {
         return;
      }

      _root.removeAllChildren();

      initTree(con);
   }

   private void onTreeSelectionChanged(TreeSelectionEvent e)
   {
      DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();

      if(null != n && null != n.getUserObject())
      {
         _detailPanelController.selectionChanged(n.getUserObject());
      }
      else
      {
         _detailPanelController.clearDetail();
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
            addMappedClassInfoNode(createMappedClassInfoTreeWrapper(mappedClassInfo), propertyInfoNode);
         }

         ((MappedClassInfoTreeWrapper) userObject).setExpanded(true);

         nodeStructurChanged(mappedClassInfoWrapperNode);
      }
   }

   private MappedClassInfoTreeWrapper createMappedClassInfoTreeWrapper(MappedClassInfo mappedClassInfo)
   {
      return new MappedClassInfoTreeWrapper(mappedClassInfo, _panel.chkShowQualified.isSelected());
   }

   private void onConnectionClosed()
   {
      MappingRoot mr = (MappingRoot) _root.getUserObject();
      mr.clear();

      _root.removeAllChildren();
      nodeStructurChanged(_root);
      _detailPanelController.clearDetail();
   }

   private void initTree(HibernateConnection con)
   {
      ArrayList<MappedClassInfo> mappedClassInfos = con.getMappedClassInfos();

      ArrayList<MappedClassInfoTreeWrapper> wrappers = initMappedClassInfos(mappedClassInfos);

      for (MappedClassInfoTreeWrapper wrapper : wrappers)
      {
         addMappedClassInfoNode(wrapper, _root);
      }

      nodeStructurChanged(_root);
   }

   private void nodeStructurChanged(DefaultMutableTreeNode node)
   {
      ((DefaultTreeModel)_panel.treMappedObjects.getModel()).nodeStructureChanged(node);
   }

   private void addMappedClassInfoNode(MappedClassInfoTreeWrapper mappedClassInfoTreeWrapper, DefaultMutableTreeNode parent)
   {
      _mappedClassInfoTreeWrappers.add(mappedClassInfoTreeWrapper);

      DefaultMutableTreeNode mappedClassInfoNode = new DefaultMutableTreeNode(mappedClassInfoTreeWrapper);

      PropertyInfo[] propertyInfos = mappedClassInfoTreeWrapper.getMappedClassInfo().getAttributes();

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

   private ArrayList<MappedClassInfoTreeWrapper> initMappedClassInfos(ArrayList<MappedClassInfo> mappedClassInfos)
   {
      ArrayList<MappedClassInfoTreeWrapper> ret = new ArrayList<MappedClassInfoTreeWrapper>();

      _mappedClassInfoByClassName = new HashMap<>();
      for (MappedClassInfo mappedClassInfo : mappedClassInfos)
      {
         _mappedClassInfoByClassName.put(mappedClassInfo.getClassName(), mappedClassInfo);
         ret.add(createMappedClassInfoTreeWrapper(mappedClassInfo));
      }

      _mappedClassInfoTreeWrappers = new ArrayList<>();

      Collections.sort(ret);

      return ret;
   }

   public JComponent getComponent()
   {
      return _panel;
   }

   public void closing()
   {
      _panel.closing();
      Props.putBoolean(PERF_KEY_OBJ_TAB_CHKSHOWQUALIFIED, _panel.chkShowQualified.isSelected());
   }

   public boolean viewInMappedObjects(String wordAtCursor)
   {
      for (int i = 0; i < _root.getChildCount(); i++)
      {
         DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) _root.getChildAt(i);

         MappedClassInfoTreeWrapper mappedClassInfoTreeWrapper = (MappedClassInfoTreeWrapper) childNode.getUserObject();

         if(mappedClassInfoTreeWrapper.getMappedClassInfo().getClassName().endsWith(wordAtCursor))
         {
            TreePath selectionPath = new TreePath(childNode.getPath());
            _panel.treMappedObjects.setSelectionPath(selectionPath);
            _panel.treMappedObjects.scrollPathToVisible(selectionPath);

            return true;
         }
      }

      if (false == StringUtilities.isEmpty(wordAtCursor, true))
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("MappedObjectController.failed.to.locate", wordAtCursor));
      }

      return false;
   }
}
