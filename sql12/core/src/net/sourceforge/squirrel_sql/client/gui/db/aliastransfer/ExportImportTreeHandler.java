package net.sourceforge.squirrel_sql.client.gui.db.aliastransfer;

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
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

public class ExportImportTreeHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExportImportTreeHandler.class);

   private JTree _treeExportedAliases;

   public ExportImportTreeHandler(JTree treeExportedAliases)
   {
      _treeExportedAliases = treeExportedAliases;
      _treeExportedAliases.setRootVisible(false);
      DefaultMutableTreeNode rootNode = getRootNode();

      rootNode.removeAllChildren();
      getDefaultTreeModel().nodeStructureChanged(rootNode);
      rootNode.setUserObject(new AliasFolder("ExportAliasRootNode", AliasFolder.NO_COLOR_RGB));

      initAliasDrop();
      initAliasDrag();

      _treeExportedAliases.addMouseListener(new MouseAdapter() {
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
            || null == _treeExportedAliases.getSelectionPaths()
            || 0 == _treeExportedAliases.getSelectionPaths().length)
      {
         return;
      }

      JPopupMenu popupMenu = new JPopupMenu();

      JMenuItem mnuRemoveSelectedNodes = new JMenuItem(s_stringMgr.getString("ExportImportTreeHandler.remove.selected.nodes"));
      mnuRemoveSelectedNodes.addActionListener(e -> onRemoveSelectedNodes());
      popupMenu.add(mnuRemoveSelectedNodes);

      popupMenu.show(_treeExportedAliases, me.getX(), me.getY());
   }

   private void onRemoveSelectedNodes()
   {
      for (TreePath selectionPath : _treeExportedAliases.getSelectionPaths())
      {
         DefaultMutableTreeNode node = toDefaultNode(selectionPath.getLastPathComponent());
         TreeNode parent = node.getParent();
         getDefaultTreeModel().removeNodeFromParent(node);
         getDefaultTreeModel().nodeStructureChanged(parent);
      }
   }

   private void initAliasDrag()
   {
      TransferHandler aliasImportDndTransferHandler = new TransferHandler(AliasDndImport.IMPORT_PROPERTY_NAME){
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

      _treeExportedAliases.setTransferHandler(aliasImportDndTransferHandler);

   }

   private AliasDndImport createAliasDndImport()
   {
      return new AliasDndImport(_treeExportedAliases.getSelectionPaths());
   }

   private void initAliasDrop()
   {
      try
      {
         _treeExportedAliases.setDragEnabled(true);
         DropTarget dt = new DropTarget();

         dt.addDropTargetListener(new DropTargetAdapter()
         {
            public void drop(DropTargetDropEvent dtde)
            {
               onDrop(dtde);
            }
         });

         _treeExportedAliases.setDropTarget(dt);
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
            if (isAlreadyExported(aliasDndExport.getListSelectedAlias().getIdentifier()))
            {
               Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("ExportImportTreeHandler.aliasAlreadyExported", aliasDndExport.getListSelectedAlias().getName()));
               return;
            }
            getRootNode().add(new DefaultMutableTreeNode(aliasDndExport.getListSelectedAlias()));
         }
         else if(null != aliasDndExport.getTreeSelectionPaths())
         {
            List<DefaultMutableTreeNode> clonedNodes = cloneAndCheckAlreadyExported(aliasDndExport.getTreeSelectionPaths());

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

   private List<DefaultMutableTreeNode> cloneAndCheckAlreadyExported(List<TreePath> treeSelectionPaths)
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
    * Creating NO clone is required to make {@link #isAlreadyExported(IIdentifier)} work.
    *
    * Clones are created on import.
    */
   private DefaultMutableTreeNode cloneNode(DefaultMutableTreeNode node)
   {
      Object userObject = node.getUserObject();

      if(userObject instanceof SQLAlias)
      {
         SQLAlias sqlAlias = (SQLAlias) userObject;
         if (isAlreadyExported(sqlAlias.getIdentifier()))
         {
            Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("ExportImportTreeHandler.aliasAlreadyExported", sqlAlias.getName()));
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

   private boolean isAlreadyExported(IIdentifier aliasIdentifier)
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
      return (DefaultTreeModel) _treeExportedAliases.getModel();
   }

   private DefaultMutableTreeNode toDefaultNode(Object defaullMutableTreeNode)
   {
      return (DefaultMutableTreeNode) defaullMutableTreeNode;
   }


   public AliasFolderState getAliasFolderState()
   {
      return new AliasFolderState(getRootNode(), _treeExportedAliases);
   }

   private DefaultMutableTreeNode getRootNode()
   {
      return toDefaultNode(getDefaultTreeModel().getRoot());
   }

   public List<SQLAlias> getSqlAliasesToExport()
   {
      List<SQLAlias> ret = new ArrayList<>();
      gatherAliases(getRootNode(), ret);
      return ret;
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
}
