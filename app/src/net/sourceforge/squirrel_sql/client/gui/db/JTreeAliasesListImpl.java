package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.ApplicationListener;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.*;
import java.io.File;

public class JTreeAliasesListImpl implements IAliasesList, IAliasTreeInterface
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(JTreeAliasesListImpl.class);


   JTree _tree = new JTree()
   {
      public String getToolTipText(MouseEvent event)
      {
         return JTreeAliasesListImpl.this.getToolTipText(event);    //To change body of overridden methods use File | Settings | File Templates.
      }
   };

   private JScrollPane _comp = new JScrollPane(_tree);
   private IApplication _app;
   private AliasesListModel _aliasesListModel;
   private TreePath _cutPath;

   public JTreeAliasesListImpl(IApplication app, AliasesListModel aliasesListModel)
   {
      _app = app;
      _aliasesListModel = aliasesListModel;
      _tree.setRootVisible(false);
      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
      root.removeAllChildren();
      _tree.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      _tree.setToolTipText("init");


//      for(int i=0; i < _aliasesListModel.getSize(); ++i)
//      {
//         ISQLAlias alias = (ISQLAlias) _aliasesListModel.get(i);
//         DefaultMutableTreeNode tn = new DefaultMutableTreeNode(alias);
//         root.add(tn);
//      }
//      treeModel.nodeStructureChanged(root);

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


      _app.addApplicationListener(new ApplicationListener()
      {
         public void saveApplicationState()
         {
            onSaveApplicationState();
         }
      });

      initTree();

   }

   private void initTree()
   {
      try
      {
         DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
         root.removeAllChildren();


         File file = new ApplicationFiles().getDatabaseAliasesTreeStructureFile();

         if(file.exists())
         {
            XMLBeanReader rdr = new XMLBeanReader();
            rdr.load(file);
            AliasFolderState rootState = (AliasFolderState) rdr.iterator().next();


            for (AliasFolderState aliasFolderState : rootState.getKids())
            {
               aliasFolderState.applyNodes(root, _aliasesListModel);
            }

            ArrayList<SQLAlias> unknownAliases = new ArrayList<SQLAlias>();
            for (int i = 0; i < _aliasesListModel.size(); i++)
            {
               SQLAlias sqlAlias = (SQLAlias) _aliasesListModel.get(i);
               if(null == findNode(sqlAlias, root))
               {
                  unknownAliases.add(sqlAlias);
               }
            }

            for (SQLAlias alias : unknownAliases)
            {
               root.add(new DefaultMutableTreeNode(alias));
            }
            treeModel.nodeStructureChanged(root);

            for (AliasFolderState aliasFolderState : rootState.getKids())
            {
               aliasFolderState.applyExpansionAndSelection(_tree);
            }
         }
         else
         {
            for (int i = 0; i < _aliasesListModel.size(); i++)
            {
               root.add(new DefaultMutableTreeNode(_aliasesListModel.get(i)));
            }
            treeModel.nodeStructureChanged(root);
         }





      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
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

      DefaultMutableTreeNode node = findNode(changedAlias, root);
      treeModel.nodeChanged(node);
   }

   private DefaultMutableTreeNode findNode(SQLAlias sqlAlias, DefaultMutableTreeNode tn)
   {
      if(sqlAlias.equals(tn.getUserObject()))
      {
         return tn;
      }

      for (int i = 0; i < tn.getChildCount(); i++)
      {
         DefaultMutableTreeNode ret = findNode(sqlAlias, (DefaultMutableTreeNode) tn.getChildAt(i));
         if(null != ret)
         {
            return ret;
         }
      }

      return null;
   }

   private void onAliasRemoved(ListDataEvent e)
   {
      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();

      DefaultMutableTreeNode delNode = findRemovedNode();

      DefaultMutableTreeNode nextToSel;
      nextToSel = delNode.getNextSibling();

      if(null == nextToSel)
      {
         nextToSel = delNode.getPreviousSibling();
      }

      DefaultMutableTreeNode parent = (DefaultMutableTreeNode) delNode.getParent();
      treeModel.removeNodeFromParent(delNode);
      treeModel.nodeStructureChanged(parent);

      if(null != nextToSel)
      {
         _tree.setSelectionPath(new TreePath(nextToSel.getPath()));
      }
      else
      {
         _tree.setSelectionPath(new TreePath(parent.getPath()));
      }
   }

   private DefaultMutableTreeNode findRemovedNode()
   {
      ArrayList<SQLAlias> buf = new ArrayList<SQLAlias>();

      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
      fillAllAliasesFrom(root, buf);

      for (SQLAlias sqlAlias : buf)
      {
         if(-1 == _aliasesListModel.getIndex(sqlAlias))
         {
            return findNode(sqlAlias, root);
         }
      }

      return null;
   }

   private void fillAllAliasesFrom(DefaultMutableTreeNode node, ArrayList<SQLAlias> toFill)
   {
      if(node.getUserObject() instanceof SQLAlias)
      {
         toFill.add((SQLAlias) node.getUserObject());
      }
      else
      {
         for (int i = 0; i < node.getChildCount(); i++)
         {
              fillAllAliasesFrom((DefaultMutableTreeNode) node.getChildAt(i), toFill);
         }
      }
   }

   private void onAliasAdded(ListDataEvent e)
   {
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
            treeModel.insertNodeInto(newNode, parentNode, formerSilblingIx + 1);
         }
         else
         {
            selNode.add(newNode);
         }
      }

      treeModel.nodeStructureChanged((DefaultMutableTreeNode)treeModel.getRoot());

      _tree.setSelectionPath(new TreePath(treeModel.getPathToRoot(newNode)));
   }

   public SQLAlias getSelectedAlias()
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

      DefaultMutableTreeNode tn = (DefaultMutableTreeNode) path.getLastPathComponent();

      if(false == tn.getUserObject() instanceof ISQLAlias)
      {
         return null;
      }

      return (SQLAlias) tn.getUserObject();

   }

   public void sortAliases()
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void requestFocus()
   {
      _tree.requestFocus();
   }

   public void deleteSelected()
   {
      TreePath selectionPath = _tree.getSelectionPath();

      if(null != selectionPath)
      {
         DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
         TreeNode parent = selNode.getParent();

         if(selNode.getUserObject() instanceof SQLAlias)
         {
            SQLAlias toDel = (SQLAlias) selNode.getUserObject();
            if (Dialogs.showYesNo(_app.getMainFrame(), s_stringMgr.getString("JTreeAliasesListImpl.confirmDelete", toDel.getName())))
            {
               removeAlias(toDel);
            }
         }
         else
         {
            if (Dialogs.showYesNo(_app.getMainFrame(), s_stringMgr.getString("JTreeAliasesListImpl.confirmDeleteFolder", selNode.getUserObject())))
            {
               removeAllAliasesFromNode(selNode);

               selNode.removeFromParent();
               DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();
               dtm.nodeStructureChanged(parent);
            }
         }

      }
   }

   private void removeAllAliasesFromNode(DefaultMutableTreeNode selNode)
   {
      if(selNode.getUserObject() instanceof SQLAlias)
      {
         SQLAlias toDel = (SQLAlias) selNode.getUserObject();
         removeAlias(toDel);

      }
      else
      {
         ArrayList<DefaultMutableTreeNode> buf = new ArrayList<DefaultMutableTreeNode>();

         for (int i = 0; i < selNode.getChildCount(); i++)
         {
            buf.add((DefaultMutableTreeNode) selNode.getChildAt(i));
         }

         for (DefaultMutableTreeNode defaultMutableTreeNode : buf)
         {
            removeAllAliasesFromNode(defaultMutableTreeNode);
         }
      }
   }

   private void removeAlias(SQLAlias toDel)
   {
      _aliasesListModel.remove(_aliasesListModel.getIndex(toDel));
      _app.getDataCache().removeAlias(toDel);
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
      EditAliasFolderDlg dlg = new EditAliasFolderDlg(_app.getMainFrame(), title, text);
      GUIUtils.centerWithinParent(dlg);

      dlg.setVisible(true);

      String folderName = dlg.getNewFolderName();

      if(null == folderName)
      {
         return;
      }


      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      TreePath selPath = _tree.getSelectionPath();

      DefaultMutableTreeNode newFolder = AliasesTreeUtil.createFolderNode(folderName);


      if(null != selPath)
      {
         DefaultMutableTreeNode tn = (DefaultMutableTreeNode) selPath.getLastPathComponent();

         if(tn.isLeaf())
         {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tn.getParent();
            parent.insert(newFolder, parent.getIndex(tn) + 1);
            treeModel.nodeStructureChanged(parent);
         }
         else
         {
            tn.add(newFolder);
            treeModel.nodeStructureChanged(tn);
         }
      }
      else
      {
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) _tree.getModel().getRoot();
         root.add(newFolder);
         treeModel.nodeStructureChanged(root);
      }

      //_tree.expandPath(new TreePath(newFolder.getPath()));
      _tree.setSelectionPath(new TreePath(newFolder.getPath()));
      
   }

   public void cutSelected()
   {
      _cutPath = _tree.getSelectionPath();
   }

   public void pasteSelected()
   {
      try
      {
         if(null == _cutPath)
         {
            return;
         }

         DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();


         TreePath selPath = _tree.getSelectionPath();

         DefaultMutableTreeNode cutNode = (DefaultMutableTreeNode) _cutPath.getLastPathComponent();
         dtm.removeNodeFromParent(cutNode);

         if(null == selPath)
         {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();

            if(root.isNodeChild(cutNode))
            {
               return;
            }

            root.add(cutNode);

            dtm.nodeStructureChanged(root);
         }
         else
         {
            DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();

            if(selNode.isLeaf())
            {
               DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selNode.getParent();
               parent.insert(cutNode, parent.getIndex(selNode) + 1);

               dtm.nodeStructureChanged(parent);

            }
            else
            {
               selNode.add(cutNode);
               dtm.nodeStructureChanged(selNode);
            }
         }

         //_tree.expandPath(new TreePath(cutNode.getPath()));
         _tree.setSelectionPath(new TreePath(cutNode.getPath()));
      }
      finally
      {
         _cutPath = null;
      }

   }

}
