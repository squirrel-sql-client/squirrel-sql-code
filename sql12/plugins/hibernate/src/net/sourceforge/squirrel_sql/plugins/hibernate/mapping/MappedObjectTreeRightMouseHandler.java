package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;

public class MappedObjectTreeRightMouseHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MappedObjectTreeRightMouseHandler.class);


   static void maybeShowTreePopup(MouseEvent evt, JTree treMappedObjects)
   {
      if(false == evt.isPopupTrigger())
      {
         return;
      }

      TreePath clickedPath  = treMappedObjects.getPathForLocation(evt.getX(), evt.getY());

      if(null == clickedPath)
      {
         return;
      }

      treMappedObjects.setSelectionPath(clickedPath);

      DefaultMutableTreeNode node = (DefaultMutableTreeNode) clickedPath.getLastPathComponent();

      JPopupMenu popUp = new JPopupMenu();

      JMenuItem mnuCopyName = new JMenuItem(s_stringMgr.getString("MappedObjectTreeRightMouseHandler.copy.object.name"));
      popUp.add(mnuCopyName);
      mnuCopyName.addActionListener(e -> onCopyName(node, false));

      JMenuItem mnuCopyQualifiedName = new JMenuItem(s_stringMgr.getString("MappedObjectTreeRightMouseHandler.copy.qualified.object.name"));
      popUp.add(mnuCopyQualifiedName);
      mnuCopyQualifiedName.addActionListener(e -> onCopyName(node, true));

      popUp.show(evt.getComponent(), evt.getX(), evt.getY());
   }

   private static void onCopyName(DefaultMutableTreeNode selectedNode, boolean qualified)
   {
      String name;
      if(selectedNode.getUserObject() instanceof MappedClassInfoTreeWrapper)
      {
         MappedClassInfoTreeWrapper mappedClassInfoTreeWrapper = (MappedClassInfoTreeWrapper) selectedNode.getUserObject();
         if (qualified)
         {
            name = mappedClassInfoTreeWrapper.getMappedClassInfo().getClassName();
         }
         else
         {
            name = mappedClassInfoTreeWrapper.getMappedClassInfo().getSimpleClassName();
         }
      }
      else
      {
         PropertyInfoTreeWrapper propertyInfoTreeWrapper = (PropertyInfoTreeWrapper) selectedNode.getUserObject();
         if (qualified)
         {
            MappedClassInfoTreeWrapper parent = (MappedClassInfoTreeWrapper) ((DefaultMutableTreeNode)selectedNode.getParent()).getUserObject();

            name = parent.getMappedClassInfo().getSimpleClassName() + "." + propertyInfoTreeWrapper.getPropertyInfo().getHibernatePropertyInfo().getPropertyName();
         }
         else
         {
            name = propertyInfoTreeWrapper.getPropertyInfo().getHibernatePropertyInfo().getPropertyName();
         }
      }

      Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
      StringSelection data = new StringSelection(name);
      clip.setContents(data, data);
   }
}
