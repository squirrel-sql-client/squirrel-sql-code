package net.sourceforge.squirrel_sql.client.gui.db.aliasdndtree;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.AliasFolder;
import net.sourceforge.squirrel_sql.client.gui.db.AliasFolderState;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.activation.DataHandler;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.stream.Collectors;

public class AliasDndTreeHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasDndTreeHandler.class);

   private JTree _treeDndAliases;

   public AliasDndTreeHandler(JTree treeDndAliases)
   {
      _treeDndAliases = treeDndAliases;
      _treeDndAliases.setRootVisible(false);
      DefaultMutableTreeNode rootNode = getRootNode();

      rootNode.removeAllChildren();
      getDefaultTreeModel().nodeStructureChanged(rootNode);
      rootNode.setUserObject(new AliasFolder("AliasDndTreeRootNode", AliasFolder.NO_COLOR_RGB));

      initAliasDrop();
      initAliasDrag();

      _treeDndAliases.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            onTriggerPopup(e);
         }

         @Override
         public void mouseReleased(MouseEvent e)
         {
            onTriggerPopup(e);
         }
      });
   }

   private void onTriggerPopup(MouseEvent me)
   {
      if(false == me.isPopupTrigger()
            || null == _treeDndAliases.getSelectionPaths()
            || 0 == _treeDndAliases.getSelectionPaths().length)
      {
         return;
      }

      JPopupMenu popupMenu = new JPopupMenu();

      JMenuItem mnuRemoveSelectedNodes = new JMenuItem(s_stringMgr.getString("AliasDndTreeHandler.remove.selected.nodes"));
      mnuRemoveSelectedNodes.addActionListener(e -> onRemoveSelectedNodes());
      popupMenu.add(mnuRemoveSelectedNodes);

      popupMenu.show(_treeDndAliases, me.getX(), me.getY());
   }

   private void onRemoveSelectedNodes()
   {
      for (TreePath selectionPath : _treeDndAliases.getSelectionPaths())
      {
         DefaultMutableTreeNode node = toDefaultNode(selectionPath.getLastPathComponent());
         TreeNode parent = node.getParent();
         getDefaultTreeModel().removeNodeFromParent(node);
         getDefaultTreeModel().nodeStructureChanged(parent);
      }
   }

   private void initAliasDrag()
   {
      TransferHandler aliasImportDndTransferHandler = new TransferHandler(AliasDndImport.ALIAS_DND_IMPORT_PROPERTY_NAME){
         @Override
         protected Transferable createTransferable(JComponent c)
         {
            return new DataHandler(createAliasDndImport(), DataFlavor.javaJVMLocalObjectMimeType);
         }

         public int getSourceActions(JComponent c)
         {
            return TransferHandler.COPY;
         }
      };

      _treeDndAliases.setTransferHandler(aliasImportDndTransferHandler);

   }

   private AliasDndImport createAliasDndImport()
   {
      return new AliasDndImport(_treeDndAliases.getSelectionPaths());
   }

   private void initAliasDrop()
   {
      try
      {
         _treeDndAliases.setDragEnabled(true);
         DropTarget dt = new DropTarget();

         dt.addDropTargetListener(new DropTargetAdapter()
         {
            public void drop(DropTargetDropEvent dtde)
            {
               onDrop(dtde);
            }
         });

         _treeDndAliases.setDropTarget(dt);
      }
      catch (TooManyListenersException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private void onDrop(DropTargetDropEvent dtde)
   {
      try
      {
         Object transferData = dtde.getTransferable().getTransferData(dtde.getTransferable().getTransferDataFlavors()[0]);

         if(false == transferData instanceof AliasDndExport)
         {
            return;
         }

         AliasDndExport aliasDndExport = (AliasDndExport) transferData;

         if(null != aliasDndExport.getListSelectedAlias())
         {
            if (isAlreadyContained(aliasDndExport.getListSelectedAlias().getIdentifier()))
            {
               Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("AliasDndTreeHandler.aliasAlreadyContained", aliasDndExport.getListSelectedAlias().getName()));
               return;
            }
            getRootNode().add(new DefaultMutableTreeNode(aliasDndExport.getListSelectedAlias()));
         }
         else if(null != aliasDndExport.getTreeSelectionPaths())
         {
            List<DefaultMutableTreeNode> clonedNodes = cloneAndCheckAlreadyContained(aliasDndExport.getTreeSelectionPaths());

            for (DefaultMutableTreeNode clonedNode : clonedNodes)
            {
               getRootNode().add(clonedNode);
            }
         }

         getDefaultTreeModel().nodeStructureChanged(getRootNode());

      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private List<DefaultMutableTreeNode> cloneAndCheckAlreadyContained(List<TreePath> treeSelectionPaths)
   {
      List<DefaultMutableTreeNode> ret = new ArrayList<>();
      for (TreePath path : treeSelectionPaths)
      {
         DefaultMutableTreeNode node = toDefaultNode(path.getLastPathComponent());

         DefaultMutableTreeNode cloneNode = cloneNode(node);
         if (null != cloneNode)
         {
            ret.add(cloneNode);
         }
      }

      return ret;
   }

   /**
    * We intentionally do not create a clone of an {@link SQLAlias} here.
    * Creating NO clone is required to make {@link #isAlreadyContained(IIdentifier)} work.
    *
    * Clones are created on import.
    */
   private DefaultMutableTreeNode cloneNode(DefaultMutableTreeNode node)
   {
      Object userObject = node.getUserObject();

      if(userObject instanceof SQLAlias)
      {
         SQLAlias sqlAlias = (SQLAlias) userObject;
         if (isAlreadyContained(sqlAlias.getIdentifier()))
         {
            Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("AliasDndTreeHandler.aliasAlreadyContained", sqlAlias.getName()));
            return null;
         }
      }

      DefaultMutableTreeNode ret;

      if (userObject instanceof AliasFolder)
      {
         ret = GUIUtils.createFolderNode(userObject);
      }
      else
      {
         ret = new DefaultMutableTreeNode(userObject);
      }

      for (int i = 0; i < node.getChildCount(); i++)
      {
         DefaultMutableTreeNode clonedChild = cloneNode(toDefaultNode(node.getChildAt(i)));

         if(null != clonedChild)
         {
            ret.add(clonedChild);
         }
      }
      return ret;
   }

   private boolean isAlreadyContained(IIdentifier aliasIdentifier)
   {
      DefaultMutableTreeNode node = getRootNode();

      return null != findNodeByIdentifier(aliasIdentifier, node);
   }

   private DefaultMutableTreeNode findNodeByIdentifier(IIdentifier aliasIdentifier, DefaultMutableTreeNode node)
   {
      for (int i = 0; i < node.getChildCount(); i++)
      {
         DefaultMutableTreeNode childNode = toDefaultNode(node.getChildAt(i));
         if(childNode.getUserObject() instanceof SQLAlias
               && ((SQLAlias) childNode.getUserObject()).getIdentifier().equals(aliasIdentifier))
         {
            return childNode;
         }

         DefaultMutableTreeNode ret = findNodeByIdentifier(aliasIdentifier, childNode);

         if(null != ret)
         {
            return ret;
         }
      }

      return null;
   }

   private DefaultTreeModel getDefaultTreeModel()
   {
      return (DefaultTreeModel) _treeDndAliases.getModel();
   }

   private DefaultMutableTreeNode toDefaultNode(Object defaullMutableTreeNode)
   {
      return (DefaultMutableTreeNode) defaullMutableTreeNode;
   }


   public AliasFolderState getAliasFolderState()
   {
      return new AliasFolderState(getRootNode(), _treeDndAliases);
   }

   private DefaultMutableTreeNode getRootNode()
   {
      return toDefaultNode(getDefaultTreeModel().getRoot());
   }

   public List<SQLAlias> getSqlAliasList()
   {
//      List<SQLAlias> ret = new ArrayList<>();
//      gatherAliases(getRootNode(), ret);
//      return ret;

      final ArrayList<DefaultMutableTreeNode> aliasNodes = new ArrayList<>();
      gatherAliasNodes(getRootNode(), aliasNodes);
      return aliasNodes.stream().map(n -> (SQLAlias)n.getUserObject()).collect(Collectors.toList());

   }

   private void gatherAliases(DefaultMutableTreeNode node, List<SQLAlias> toFill)
   {
      for (int i = 0; i < node.getChildCount(); i++)
      {
         DefaultMutableTreeNode childNode = toDefaultNode(node.getChildAt(i));
         if(childNode.getUserObject() instanceof SQLAlias)
         {
            toFill.add((SQLAlias) childNode.getUserObject());
         }

         gatherAliases(childNode, toFill);
      }
   }

   private void gatherAliasNodes(DefaultMutableTreeNode node, List<DefaultMutableTreeNode> toFillAliasNodes)
   {
      for (int i = 0; i < node.getChildCount(); i++)
      {
         DefaultMutableTreeNode childNode = toDefaultNode(node.getChildAt(i));
         if(childNode.getUserObject() instanceof SQLAlias)
         {
            toFillAliasNodes.add(childNode);
         }

         gatherAliasNodes(childNode, toFillAliasNodes);
      }
   }

   public boolean isEmpty()
   {
      return 0 == getRootNode().getChildCount();
   }

   public void load(List<SQLAlias> sqlAliases, AliasFolderState aliasFolderState)
   {
      getRootNode().removeAllChildren();

      DefaultMutableTreeNode node = getRootNode();

      buildTree(sqlAliases, aliasFolderState, node);


      getDefaultTreeModel().nodeStructureChanged(getRootNode());
   }

   private void buildTree(List<SQLAlias> sqlAliases, AliasFolderState aliasFolderState, DefaultMutableTreeNode node)
   {
      for (AliasFolderState kid : aliasFolderState.getKids())
      {
         DefaultMutableTreeNode newChild = new DefaultMutableTreeNode();

         if(null != kid.getAliasIdentifier())
         {
            SQLAlias sqlAlias = sqlAliases.stream().filter(a -> kid.getAliasIdentifier().equals(a.getIdentifier())).findFirst().get();
            newChild.setUserObject(sqlAlias);
         }
         else
         {
            newChild.setUserObject(new AliasFolder(kid.getFolderName(), kid.getColorRGB()));
            buildTree(sqlAliases, kid, newChild);
         }

         node.add(newChild);
      }
   }

   public void removeAliases(List<SQLAlias> updatedAliases)
   {
      final ArrayList<DefaultMutableTreeNode> aliasNodes = new ArrayList<>();
      gatherAliasNodes(getRootNode(), aliasNodes);

      for (DefaultMutableTreeNode aliasNode : aliasNodes)
      {
         for (SQLAlias updatedAlias : updatedAliases)
         {
            if(updatedAlias.equals(aliasNode.getUserObject()))
            {
               getDefaultTreeModel().removeNodeFromParent(aliasNode);
            }
         }
      }
   }

   public SQLAlias getSelectedAlias()
   {
      TreePath selectionPath = _treeDndAliases.getSelectionPath();
      if(null == selectionPath)
      {
         return null;
      }

      if(toDefaultNode(selectionPath.getLastPathComponent()).getUserObject() instanceof SQLAlias)
      {
         return (SQLAlias) toDefaultNode(selectionPath.getLastPathComponent()).getUserObject();
      }
      else
      {
         return null;
      }
   }
}
