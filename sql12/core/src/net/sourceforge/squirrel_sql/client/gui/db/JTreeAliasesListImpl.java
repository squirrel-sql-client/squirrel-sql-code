package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.aliascolor.TreeAliasColorSelectionHandler;
import net.sourceforge.squirrel_sql.client.gui.db.aliastransfer.AliasDndExport;
import net.sourceforge.squirrel_sql.client.gui.db.aliastransfer.AliasDndImport;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.TreeDnDHandler;
import net.sourceforge.squirrel_sql.fw.gui.TreeDnDHandlerCallback;
import net.sourceforge.squirrel_sql.fw.gui.TreeDndDropPosition;
import net.sourceforge.squirrel_sql.fw.gui.TreeDndDropPositionData;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import javax.activation.DataHandler;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class JTreeAliasesListImpl implements IAliasesList, IAliasTreeInterface
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(JTreeAliasesListImpl.class);

	private final static ILogger s_log = LoggerController.createLogger(JTreeAliasesListImpl.class);

   private final AliasDragState _aliasDragState;

   private TreeDnDHandler _treeDnDHandler;
   private AliasSortState _aliasSortState;

   private JTree _tree = new JTree()
   {
		public String getToolTipText(MouseEvent event)
      {
         return JTreeAliasesListImpl.this.getToolTipText(event);
      }
   };

   private JScrollPane _comp = new JScrollPane(_tree);
   private IApplication _app;
   private AliasesListModel _aliasesListModel;

   private AliasTreePasteState _aliasPasteState = new AliasTreePasteState();

   private boolean _dontReactToAliasAdd = false ;

   public JTreeAliasesListImpl(IApplication app, AliasesListModel aliasesListModel)
   {
      _app = app;
      _aliasesListModel = aliasesListModel;

      _tree.setRootVisible(false);
      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
      root.removeAllChildren();

      root.setUserObject(new AliasFolder("AliasRootNode", AliasFolder.NO_COLOR_RGB));

      _tree.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      _tree.setToolTipText("init");

      _aliasDragState = new AliasDragState(_tree);

      _tree.setCellRenderer(new AliasTreeCellRenderer(_aliasPasteState, _aliasDragState));

      initCancelCutAction();

      initDnD();

      initAliasExportDnd();

      _aliasesListModel.addListDataListener(new ListDataListener()
      {
         public void intervalAdded(ListDataEvent e)
         {
            onAliasAdded(e);
         }

         public void intervalRemoved(ListDataEvent e)
         {
            onAliasRemoved(e);
         }

         public void contentsChanged(ListDataEvent e)
         {
            onAliasChanged(e);
         }
      });


      _app.addApplicationListener(() -> onSaveApplicationState());

      initTree();

      _aliasSortState = new AliasSortState(_tree);

   }

   private void initCancelCutAction()
   {
      AbstractAction cancelCutAction = new AbstractAction()
      {
			public void actionPerformed(ActionEvent actionEvent)
         {
            if (null != _aliasPasteState.getPathsToPaste() && AliasTreePasteMode.CUT.equals(_aliasPasteState.getPasteMode()))
            {
               _aliasPasteState.setPathsToPaste(null);
               _tree.repaint();
            }
         }
      };

      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      _tree.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "cancelCutAction");
      _tree.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "cancelCutAction");
      _tree.getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "cancelCutAction");
      _tree.getActionMap().put("cancelCutAction", cancelCutAction);
   }


   private void initDnD()
   {
      TreeDnDHandlerCallback treeDnDHandlerCallback = new TreeDnDHandlerCallback()
      {
         @Override
         public boolean nodeAcceptsKids(DefaultMutableTreeNode selNode)
         {
            return onNodeAcceptsKids(selNode);
         }

         @Override
         public void updateDragPosition(TreeDndDropPositionData treeDndDropPositionInfo)
         {
            _aliasDragState.updateDragPosition(treeDndDropPositionInfo);
         }

         @Override
         public ArrayList<DefaultMutableTreeNode> getPasteTreeNodesFromExternalTransfer(DropTargetDropEvent dtde, TreePath targetPath)
         {
            return null;
         }

         @Override
         public TreePath[] getPasteTreeNodesFromInternalTransfer(DropTargetDropEvent dtde, TreePath targetPath, TreePath[] selectionPaths)
         {
            return onGetPasteTreeNodesFromInternalTransfer(dtde);
         }
      };

      _treeDnDHandler = new TreeDnDHandler(_tree, treeDnDHandlerCallback);
   }

   private void initAliasExportDnd()
   {
      TransferHandler aliasExportDndTransferHandler = new TransferHandler(AliasDndExport.EXPORT_PROPERTY_NAME){
         @Override
         protected Transferable createTransferable(JComponent c)
         {
            return new DataHandler(new AliasDndExport(_tree.getSelectionPaths()), DataFlavor.javaJVMLocalObjectMimeType);
         }

         public int getSourceActions(JComponent c)
         {
            return TransferHandler.COPY;
         }
      };

      _tree.setTransferHandler(aliasExportDndTransferHandler);
   }

   private TreePath[] onGetPasteTreeNodesFromInternalTransfer(DropTargetDropEvent dtde)
   {
      try
      {
         Object transferData = dtde.getTransferable().getTransferData(dtde.getTransferable().getTransferDataFlavors()[0]);
         if (transferData instanceof AliasDndExport)
         {
            // AliasDndExport was created here, see method createAliasDndExport() in this class.
            // So its just the usual In-Tree DnD.
            return _tree.getSelectionPaths();
         }
         else if (transferData instanceof AliasDndImport)
         {
            ArrayList<DefaultMutableTreeNode> nodesToImport = ((AliasDndImport) transferData).getNodesToImport();

            ArrayList<TreePath> ret = new ArrayList<>();

            for (DefaultMutableTreeNode defaultMutableTreeNode : nodesToImport)
            {
               DefaultMutableTreeNode copiedNode = createCopy(defaultMutableTreeNode);
               ret.add(new TreePath(copiedNode));
            }

            return ret.toArray(new TreePath[0]);
         }
         else
         {
            return new TreePath[0];
         }
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private boolean onNodeAcceptsKids(DefaultMutableTreeNode selNode)
   {
      return selNode.isRoot() || false == selNode.isLeaf();
   }


   private void initTree()
	{
		DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
		root.removeAllChildren();

		File file = new ApplicationFiles().getDatabaseAliasesTreeStructureFile();

		if (!readTreeStructureFile(root, file))
		{
			for (int i = 0; i < _aliasesListModel.size(); i++)
			{
				root.add(new DefaultMutableTreeNode(_aliasesListModel.get(i)));
			}
			treeModel.nodeStructureChanged(root);
		}
	}

   /**
    * Bug 2942351 (Program doesn't launch)
	 * Safely performs the reading/parsing of the tree structure from the aliases tree structure file so that
	 * the tree structure can be ignored if the file is somehow corrupt.
	 * 
	 * @param root
	 *           the root node of the treemodel for _tree
	 * @param file
	 *           the file that contains the tree structure xml.
	 * @return true if the file existed and was parsed successfully; false otherwise.
	 */
	private boolean readTreeStructureFile(final DefaultMutableTreeNode root, final File file)
	{
		boolean result = false;
		try
		{
			if (file.exists() && file.length() > 0)
			{
				XMLBeanReader rdr = new XMLBeanReader();
				rdr.load(file);
				AliasFolderState rootState = (AliasFolderState) rdr.iterator().next();
				applyAliasFolderState(root, rootState);
				result = true;
			}
		}
		catch (Exception e)
		{
			// Throwing a runtime exception here will result in failure to launch the application. Since the tree
			// structure can be recovered more easily than all of the user's aliases, we log an error and forget
			// about the previous tree structure. Nanoxml will throw a runtime exception for any invalid xml
			// that it finds, and we squelch that here with a log message so that launch can proceed. 
			s_log.error("Unexpected exception while applying Aliases tree structure from file: "
				+ file.getAbsolutePath(), e);
		}
		return result;
	}
   
   private void applyAliasFolderState(DefaultMutableTreeNode rootNode, AliasFolderState rootState)
   {
      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();

      for (AliasFolderState aliasFolderState : rootState.getKids())
      {
         aliasFolderState.applyNodes(rootNode, _aliasesListModel);
      }

      ArrayList<SQLAlias> unknownAliases = new ArrayList<>();
      for (int i = 0; i < _aliasesListModel.size(); i++)
      {
         SQLAlias sqlAlias = (SQLAlias) _aliasesListModel.get(i);
         if(null == AliasTreeUtil.findAliasNode(sqlAlias, rootNode))
         {
            unknownAliases.add(sqlAlias);
         }
      }

      for (SQLAlias alias : unknownAliases)
      {
         rootNode.add(new DefaultMutableTreeNode(alias));
      }
      treeModel.nodeStructureChanged(rootNode);

      for (AliasFolderState aliasFolderState : rootState.getKids())
      {
         aliasFolderState.applyExpansionAndSelection(_tree);
      }
   }

   private void onSaveApplicationState()
   {
      try
      {
         DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();

         AliasFolderState state = new AliasFolderState(root, _tree);

         XMLBeanWriter wrt = new XMLBeanWriter(state);
         wrt.save(new ApplicationFiles().getDatabaseAliasesTreeStructureFile());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onAliasChanged(ListDataEvent e)
   {
      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      SQLAlias changedAlias = (SQLAlias) _aliasesListModel.get(e.getIndex0());

      DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

      DefaultMutableTreeNode node = AliasTreeUtil.findAliasNode(changedAlias, root);
      treeModel.nodeChanged(node);
   }

   private void onAliasRemoved(ListDataEvent e)
   {
      if(_aliasesListModel.isBeingSortedForListImpl())
      {
         return;
      }

      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();

      ArrayList<DefaultMutableTreeNode> delNodes = findRemovedNodes();

      if(0 == delNodes.size())
      {
         return;
      }

      DefaultMutableTreeNode nextToSel;
      DefaultMutableTreeNode toSelectNextTo = delNodes.get(delNodes.size() - 1);
      nextToSel = toSelectNextTo.getNextSibling();

      if(null == nextToSel)
      {
         nextToSel = toSelectNextTo.getPreviousSibling();
      }

      DefaultMutableTreeNode parent = (DefaultMutableTreeNode) toSelectNextTo.getParent();

      for (DefaultMutableTreeNode delNode : delNodes)
      {
         if (null != delNode.getParent())
         {
            treeModel.removeNodeFromParent(delNode);
         }
      }


      if(null != nextToSel)
      {
         _tree.setSelectionPath(new TreePath(nextToSel.getPath()));
      }
      else
      {
         if(parent != _tree.getModel().getRoot())
         {
            _tree.setSelectionPath(new TreePath(parent.getPath()));
         }
      }
   }

   private ArrayList<DefaultMutableTreeNode> findRemovedNodes()
   {
      ArrayList<SQLAlias> buf = new ArrayList<SQLAlias>();

      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
      fillAllAliasesFrom(root, buf);

      ArrayList<DefaultMutableTreeNode> ret = new ArrayList<DefaultMutableTreeNode>();

      for (SQLAlias sqlAlias : buf)
      {
         if(-1 == _aliasesListModel.getIndex(sqlAlias))
         {
            ret.add(AliasTreeUtil.findAliasNode(sqlAlias, root));
         }
      }

      return ret;
   }

   private void fillAllAliasesFrom(DefaultMutableTreeNode node, ArrayList<SQLAlias> toFill)
   {
      if(node.getUserObject() instanceof SQLAlias)
      {
         toFill.add((SQLAlias) node.getUserObject());
      }
      else if(node.getUserObject() instanceof AliasFolder)
      {
         for (int i = 0; i < node.getChildCount(); i++)
         {
            fillAllAliasesFrom((DefaultMutableTreeNode) node.getChildAt(i), toFill);
         }
      }
      else
      {
         AliasTreeUtil.throwUnknownUserObjectException(node);
      }

   }

   private void onAliasAdded(ListDataEvent e)
   {
      if(_dontReactToAliasAdd || _aliasesListModel.isBeingSortedForListImpl())
      {
         return;
      }

      SQLAlias newAlias = (SQLAlias) _aliasesListModel.get(e.getIndex0());
      DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newAlias);

      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      TreePath selPath = _tree.getSelectionPath();

      if(null == selPath)
      {
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
         root.add(newNode);
      }
      else
      {
         DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
         if(selNode.getUserObject() instanceof SQLAlias)
         {

            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selPath.getParentPath().getLastPathComponent();

            int formerSilblingIx = treeModel.getIndexOfChild(parentNode, selNode);
            int insertIndex = formerSilblingIx + 1;
            parentNode.insert(newNode, insertIndex);
            treeModel.nodesWereInserted(parentNode, new int[]{insertIndex});
         }
         else if(selNode.getUserObject() instanceof AliasFolder)
         {
            selNode.insert(newNode, 0);
            treeModel.nodesWereInserted(selNode, new int[]{0});
         }
         else
         {
            AliasTreeUtil.throwUnknownUserObjectException(selNode);
         }

      }

      _tree.setSelectionPath(new TreePath(treeModel.getPathToRoot(newNode)));
   }

   public SQLAlias getSelectedAlias(MouseEvent evt)
   {
      TreePath path = _tree.getSelectionPath();

      if(null == path)
      {
         return null;
      }

      if(false == path.getLastPathComponent() instanceof DefaultMutableTreeNode)
      {
         return null;
      }

      if(null != evt && false == _tree.getPathBounds(path).contains(evt.getPoint()))
      {
         // If the mouse wasn't placed on the selected Alias we do nothing. 
         return null;
      }

      DefaultMutableTreeNode tn = (DefaultMutableTreeNode) path.getLastPathComponent();

      if(false == tn.getUserObject() instanceof ISQLAlias)
      {
         return null;
      }

      return (SQLAlias) tn.getUserObject();

   }

   public void sortAliases()
   {
      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
      AliasFolderState state = new AliasFolderState(root, _tree);

      state = _aliasSortState.sort(state);

      root.removeAllChildren();

      _aliasSortState.disableListener();
      applyAliasFolderState(root, state);
      _aliasSortState.enableListener();
   }

   public void requestFocus()
   {
      _tree.requestFocus();
   }

   public void deleteSelected()
   {
      TreePath[] selectionPaths = _tree.getSelectionPaths();

      if(1 == selectionPaths.length)
      {
         DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectionPaths[0].getLastPathComponent();
         TreeNode parent = selNode.getParent();

         if(selNode.getUserObject() instanceof SQLAlias)
         {
            SQLAlias toDel = (SQLAlias) selNode.getUserObject();
            if (Dialogs.showYesNo(_app.getMainFrame(), s_stringMgr.getString("JTreeAliasesListImpl.confirmDelete", toDel.getName())))
            {
               removeAlias(toDel);
            }
         }
         else if(selNode.getUserObject() instanceof AliasFolder)
         {
            if (Dialogs.showYesNo(_app.getMainFrame(), s_stringMgr.getString("JTreeAliasesListImpl.confirmDeleteFolder", selNode.getUserObject())))
            {
               removeAllAliasesFromNode(selNode);

               DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();
               int indexOfChild = dtm.getIndexOfChild(parent, selNode);
               selNode.removeFromParent();
               dtm.nodesWereRemoved(parent, new int[]{indexOfChild}, new Object[]{selNode});
            }
         }
         else
         {
            AliasTreeUtil.throwUnknownUserObjectException(selNode);
         }
      }
      else if(1 < selectionPaths.length)
      {
         if (Dialogs.showYesNo(_app.getMainFrame(), s_stringMgr.getString("JTreeAliasesListImpl.confirmDeleteMultible")))
         {
            final HashSet<TreeNode> parentsRemovedFrom = new HashSet<TreeNode>();
            for (TreePath selectionPath : selectionPaths)
            {
               DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
               parentsRemovedFrom.add(selNode.getParent());

               if(selNode.getUserObject() instanceof SQLAlias)
               {
                  SQLAlias toDel = (SQLAlias) selNode.getUserObject();
                 removeAlias(toDel);
               }
               else if(selNode.getUserObject() instanceof AliasFolder)
               {
                  removeAllAliasesFromNode(selNode);
                  selNode.removeFromParent();
               }
               else
               {
                  AliasTreeUtil.throwUnknownUserObjectException(selNode);
               }

            }

            final DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();

            SwingUtilities.invokeLater(
               new Runnable()
               {
                  public void run()
                  {
                     for (TreeNode node : parentsRemovedFrom)
                     {
                        dtm.nodeStructureChanged(node);
                     }
                  }
               });
         }
      }
   }

   public void modifySelected()
   {
      TreePath selPath = _tree.getSelectionPath();

      if(null == selPath)
      {
         return;
      }

      DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();

      if(selNode.getUserObject() instanceof SQLAlias)
      {
         AliasWindowManager.showModifyAliasInternalFrame((ISQLAlias) selNode.getUserObject());
      }
      else if(selNode.getUserObject() instanceof AliasFolder)
      {
         String title = s_stringMgr.getString("JTreeAliasesListImpl.EditAliasFolderDlgTitle");
         String text = s_stringMgr.getString("JTreeAliasesListImpl.EditAliasFolderDlgText");
         EditAliasFolderDlg dlg = new EditAliasFolderDlg(_app.getMainFrame(), title, text, selNode.getUserObject().toString());

         GUIUtils.centerWithinParent(dlg);

         dlg.setVisible(true);

         String folderName = dlg.getFolderName();

         if(null == folderName)
         {
            return;
         }

         selNode.setUserObject(new AliasFolder(folderName, (((AliasFolder) selNode.getUserObject()).getColorRGB())));

         DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
         treeModel.nodeChanged(selNode);
      }
      else
      {
         AliasTreeUtil.throwUnknownUserObjectException(selNode);
      }

   }

   public boolean isEmpty()
   {
      return 0 == _aliasesListModel.getSize();
   }

   @Override
   public void goToAlias(ISQLAlias aliasToGoTo)
   {
      DefaultMutableTreeNode root = (DefaultMutableTreeNode)_tree.getModel().getRoot();
      DefaultMutableTreeNode node = AliasTreeUtil.findAliasNode((SQLAlias) aliasToGoTo, root);

      if(null == node)
      {
         return;
      }

      goToNode(node);

   }

   @Override
   public void goToAliasFolder(AliasFolder aliasFolder)
   {
      DefaultMutableTreeNode root = (DefaultMutableTreeNode)_tree.getModel().getRoot();
      DefaultMutableTreeNode node = AliasTreeUtil.findAliasFolderNode(aliasFolder, root);

      if(null == node)
      {
         return;
      }

      goToNode(node);
   }

   private void goToNode(DefaultMutableTreeNode node)
   {
      TreePath treePath = new TreePath(node.getPath());

      _tree.clearSelection();

      _tree.expandPath(treePath);

      _tree.setSelectionPath(treePath);

      Rectangle bounds = _tree.getPathBounds(treePath);

      _tree.scrollRectToVisible(bounds);
   }


   @Override
   public void colorSelected()
   {
      TreeAliasColorSelectionHandler.selectColor(_tree);
   }

   private void removeAllAliasesFromNode(DefaultMutableTreeNode selNode)
   {
      if(selNode.getUserObject() instanceof SQLAlias)
      {
         SQLAlias toDel = (SQLAlias) selNode.getUserObject();
         removeAlias(toDel);

      }
      else if(selNode.getUserObject() instanceof AliasFolder)
      {
         ArrayList<DefaultMutableTreeNode> buf = new ArrayList<>();

         for (int i = 0; i < selNode.getChildCount(); i++)
         {
            buf.add((DefaultMutableTreeNode) selNode.getChildAt(i));
         }

         for (DefaultMutableTreeNode defaultMutableTreeNode : buf)
         {
            removeAllAliasesFromNode(defaultMutableTreeNode);
         }
      }
      else
      {
         AliasTreeUtil.throwUnknownUserObjectException(selNode);
      }

   }

   private void removeAlias(SQLAlias toDel)
   {
      _aliasesListModel.remove(_aliasesListModel.getIndex(toDel));
      _app.getAliasesAndDriversManager().removeAlias(toDel);
   }

   public void selectListEntryAtPoint(Point point)
   {
      TreePath path = _tree.getPathForLocation(point.x, point.y);

      if(null != path)
      {
         _tree.setSelectionPath(path);
      }
   }

   public JComponent getComponent()
   {
      return _comp;
   }

   public void addMouseListener(MouseListener mouseListener)
   {
      _tree.addMouseListener(mouseListener);
   }

   public void removeMouseListener(MouseListener mouseListener)
   {
      _tree.removeMouseListener(mouseListener);
   }


   public String getToolTipText(MouseEvent evt)
   {
      TreePath path = _tree.getPathForLocation(evt.getPoint().x, evt.getPoint().y);

      if(null == path)
      {
         return null;
      }

      if(false == path.getLastPathComponent() instanceof DefaultMutableTreeNode)
      {
         return null;
      }

      Object userObj = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

      if(false == userObj instanceof ISQLAlias)
      {
         return null;
      }

      return ((ISQLAlias)userObj).getName();
   }

   public void createNewFolder()
   {
      String title = s_stringMgr.getString("JTreeAliasesListImpl.NewAliasFolderDlgTitle");
      String text = s_stringMgr.getString("JTreeAliasesListImpl.NewAliasFolderDlgText");
      EditAliasFolderDlg dlg = new EditAliasFolderDlg(_app.getMainFrame(), title, text, null);
      GUIUtils.centerWithinParent(dlg);

      dlg.setVisible(true);

      String folderName = dlg.getFolderName();

      if(null == folderName)
      {
         return;
      }


      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      TreePath selPath = _tree.getSelectionPath();

      DefaultMutableTreeNode newFolder = GUIUtils.createFolderNode(new AliasFolder(folderName, AliasFolder.NO_COLOR_RGB));


      if(null != selPath)
      {
         DefaultMutableTreeNode tn = (DefaultMutableTreeNode) selPath.getLastPathComponent();

         if(tn.isLeaf())
         {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tn.getParent();
            int childIndex = parent.getIndex(tn) + 1;
            parent.insert(newFolder, childIndex);
            treeModel.nodesWereInserted(parent, new int[]{childIndex});
         }
         else
         {
            tn.insert(newFolder, 0);
            treeModel.nodesWereInserted(tn, new int[]{0});
         }
      }
      else
      {
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) _tree.getModel().getRoot();

         int[] childIndices = new int[]{root.getChildCount()};
         root.add(newFolder);
         treeModel.nodesWereInserted(root, childIndices);
      }

      //_tree.expandPath(new TreePath(newFolder.getPath()));
      _tree.setSelectionPath(new TreePath(newFolder.getPath()));

      ((DefaultTreeModel) _tree.getModel()).nodeChanged(newFolder);
      
   }

   public void cutSelected()
   {
      _aliasPasteState.setPathsToPaste(_tree.getSelectionPaths());
      _aliasPasteState.setPasteMode(AliasTreePasteMode.CUT);
      _tree.repaint();
   }

   public void pasteSelected()
   {
      try
      {
         if (null == _aliasPasteState.getPathsToPaste())
         {
            return;
         }

         switch (_aliasPasteState.getPasteMode())
         {
            case COPY:
               execCopyToPaste(_aliasPasteState.getPathsToPaste(), _tree.getSelectionPath());
               break;
            case CUT:
               DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) _tree.getSelectionPath().getLastPathComponent();
               TreeDndDropPositionData treeDndDropPositionData;

               if (onNodeAcceptsKids(targetNode))
               {
                  treeDndDropPositionData = new TreeDndDropPositionData(targetNode, TreeDndDropPosition.INTO);
               }
               else
               {
                  treeDndDropPositionData = new TreeDndDropPositionData(targetNode, TreeDndDropPosition.BELOW);
               }

               _treeDnDHandler.execCopyOrMove(_aliasPasteState.getPathsToPaste(), treeDndDropPositionData);
               break;
         }
      }
      finally
      {
         _aliasPasteState.setPathsToPaste(null);
      }
   }

   private void execCopyToPaste(TreePath[] pathsToPaste, TreePath targetPath)
   {
      DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();

      DefaultMutableTreeNode[] copiedNodes = new DefaultMutableTreeNode[pathsToPaste.length];

      for (int i = 0; i < pathsToPaste.length; i++)
      {
         copiedNodes[i] = createCopy((DefaultMutableTreeNode) pathsToPaste[i].getLastPathComponent());
      }


      if (null == targetPath)
      {
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();

         for (int i = 0; i < copiedNodes.length; i++)
         {
            root.insert(copiedNodes[i], 0);
            dtm.nodesWereInserted(root, new int[]{0});
         }
      }
      else
      {
         DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) targetPath.getLastPathComponent();

         if (selNode.isLeaf())
         {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selNode.getParent();
            for (int i = 0; i < copiedNodes.length; i++)
            {
               int insertIndex = parent.getIndex(selNode) + 1;
               parent.insert(copiedNodes[i], insertIndex);
               dtm.nodesWereInserted(parent, new int[]{insertIndex});
            }

         }
         else
         {
            for (int i = 0; i < copiedNodes.length; i++)
            {
               selNode.insert(copiedNodes[i], 0);
               dtm.nodesWereInserted(selNode, new int[]{0});
            }
         }
      }

      TreePath[] newSelPaths = new TreePath[copiedNodes.length];
      for (int i = 0; i < newSelPaths.length; i++)
      {
         newSelPaths[i] = new TreePath(copiedNodes[i].getPath());
      }
      _tree.setSelectionPaths(newSelPaths);
   }

   public void copyToPasteSelected()
   {
      _aliasPasteState.setPathsToPaste(_tree.getSelectionPaths());
      _aliasPasteState.setPasteMode(AliasTreePasteMode.COPY);
   }

   public void collapseAll()
   {
      for (int i = 0; i < _tree.getRowCount(); i++)
      {
         _tree.collapseRow(i);
      }
   }

   public void expandAll()
   {
      for (int i = 0; i < _tree.getRowCount(); i++)
      {
         _tree.expandRow(i);
      }
   }

   @Override
   public void collapseSelected()
   {
      AliasTreeUtil.collapseRecursively(_tree.getSelectionPath(), _tree);
   }

   @Override
   public void expandSelected()
   {
      AliasTreeUtil.expandRecursively(_tree.getSelectionPath(), _tree);
   }

   @Override
   public List<AliasFolder> getAllAliasFolders()
   {
      return AliasTreeUtil.getAllAliasFolders(_tree);
   }

   @Override
   public void aliasChanged(ISQLAlias sqlAlias)
   {
      DefaultMutableTreeNode node = AliasTreeUtil.findAliasNode((SQLAlias) sqlAlias, (DefaultMutableTreeNode) _tree.getModel().getRoot());

      if (null != node)
      {
         ((DefaultTreeModel)_tree.getModel()).nodeChanged(node);
      }
   }

   private DefaultMutableTreeNode createCopy(DefaultMutableTreeNode nodeToCopy)
   {
      try
      {
         if(nodeToCopy.getUserObject() instanceof SQLAlias)
         {
            SQLAlias source = (SQLAlias) nodeToCopy.getUserObject();
            SQLAlias newAlias = copySqlAlias(source);
            return new DefaultMutableTreeNode(newAlias);
         }
         else if(nodeToCopy.getUserObject() instanceof AliasFolder)
         {
            DefaultMutableTreeNode ret = GUIUtils.createFolderNode(nodeToCopy.getUserObject());

            for (int i = 0; i < nodeToCopy.getChildCount(); i++)
            {
               ret.add(createCopy((DefaultMutableTreeNode) nodeToCopy.getChildAt(i)));
            }
            return ret;
         }
         else
         {
            throw AliasTreeUtil.createUnknownUserObjectException(nodeToCopy);
         }

      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private SQLAlias copySqlAlias(SQLAlias source)
   {
      try
      {
         IIdentifierFactory factory = IdentifierFactory.getInstance();
         SQLAlias newAlias = Main.getApplication().getAliasesAndDriversManager().createAlias(factory.createIdentifier());
         newAlias.assignFrom(source, false);

         try
         {
            _dontReactToAliasAdd = true;
            Main.getApplication().getAliasesAndDriversManager().addAlias(newAlias);
         }
         finally
         {
            _dontReactToAliasAdd = false;
         }

         return newAlias;
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

}
