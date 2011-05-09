package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.client.session.EntryPanelManager;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.TreeDnDHandler;
import net.sourceforge.squirrel_sql.fw.gui.TreeDnDHandlerCallback;
import net.sourceforge.squirrel_sql.plugins.graph.*;
import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.sqlgen.QueryBuilderSQLGenerator;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GraphQueryWherePanelCtrl
{

   private GraphQueryWherePanel _panel;
   private EntryPanelManager _entryPanelManager;
   private DefaultMutableTreeNode _rootNode;
   private ISession _session;
   private WhereTreeNodeStructure _initialWhereTreeNodeStructure;


   public GraphQueryWherePanelCtrl(ISession session, HideDockButtonHandler hideDockButtonHandler, GraphPluginResources rsrc, WhereTreeNodeStructure whereTreeNodeStructure)
   {
      _session = session;
      _initialWhereTreeNodeStructure = whereTreeNodeStructure;
      _entryPanelManager = new EntryPanelManager(session);
      _entryPanelManager.init(null, null);
      _panel = new GraphQueryWherePanel(hideDockButtonHandler, _entryPanelManager.getComponent(), rsrc);

      _rootNode = new DefaultMutableTreeNode(WhereClauseOperator.WHERE);
      _panel.treeWhere.setModel(new DefaultTreeModel(_rootNode));

      initDnd();


      _panel.btnAddNewAndFolder.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onAddNewFolder(WhereClauseOperator.AND);
         }
      });

      _panel.btnAddNewOrFolder.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onAddNewFolder(WhereClauseOperator.OR);
         }
      });

      _panel.btnDeleteFolder.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onDeleteFolder();
         }
      });

      _panel.btnDeleteFolder.setEnabled(false);
      _panel.treeWhere.addTreeSelectionListener(new TreeSelectionListener()
      {
         @Override
         public void valueChanged(TreeSelectionEvent e)
         {
            onTreeSelectionChanged();
         }
      });
   }

   private void onTreeSelectionChanged()
   {
      TreePath[] selectionPaths = _panel.treeWhere.getSelectionPaths();

      if(null == selectionPaths)
      {
         _panel.btnDeleteFolder.setEnabled(false);
         return;
      }

      boolean deleteable = false;

      for (TreePath treePath : selectionPaths)
      {
         DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
         if(isDeleteable(selNode.getUserObject()))
         {
            deleteable = true;
            break;
         }
      }

      _panel.btnDeleteFolder.setEnabled(deleteable);
   }

   private void onDeleteFolder()
   {
      TreePath[] paths = _panel.treeWhere.getSelectionPaths();

      for (TreePath path : paths)
      {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
         Object userObject = node.getUserObject();

         if (isDeleteable(userObject))
         {
            new WhereTreeSync().removeFolder(node);
         }
      }

      nodeChanged(_rootNode);
   }

   private boolean isDeleteable(Object userObject)
   {
      if (userObject instanceof WhereClauseOperator)
      {
         WhereClauseOperator op = (WhereClauseOperator) userObject;

         if(op == WhereClauseOperator.OR || op == WhereClauseOperator.AND)
         {
            return true;
         }

      }

      return false;
   }



   private void initDnd()
   {
      TreeDnDHandlerCallback treeDnDHandlerCallback = new TreeDnDHandlerCallback()
      {
         @Override
         public boolean nodeAcceptsKids(DefaultMutableTreeNode selNode)
         {
            return onNodeAcceptsKids(selNode);
         }

         @Override
         public void dndExecuted()
         {
            syncWherePreview();
         }
      };

      new TreeDnDHandler(_panel.treeWhere, treeDnDHandlerCallback);
   }

   private boolean onNodeAcceptsKids(DefaultMutableTreeNode selNode)
   {
      return selNode.getUserObject() instanceof WhereClauseOperator;
   }


   private void onAddNewFolder(WhereClauseOperator oper)
   {
      TreePath selPath = _panel.treeWhere.getSelectionPath();

      DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(oper);

      newNode.setAllowsChildren(true);

      if (null == selPath)
      {
         _rootNode.insert(newNode, 0);
         nodeChanged(_rootNode);
         _panel.treeWhere.setSelectionPath(new TreePath(newNode.getPath()));
         return;
      }

      DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();

      if (selNode.getUserObject() instanceof WhereClauseOperator)
      {
         selNode.add(newNode);
         nodeChanged(selNode);
         _panel.treeWhere.expandPath(new TreePath(selNode.getPath()));

      }
      else
      {
         DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selNode.getParent();

         if (parent == null)
         {
            _rootNode.insert(newNode, 0);
            nodeChanged(_rootNode);
            _panel.treeWhere.setSelectionPath(new TreePath(newNode.getPath()));
            return;
         }

         int selIx = parent.getIndex(selNode);
         parent.insert(newNode, selIx + 1);
         nodeChanged(parent);
         _panel.treeWhere.setSelectionPath(new TreePath(newNode.getPath()));
      }
   }

   WhereTreeNodeStructure syncWhereCols(TableFramesModel tfm)
   {
      ArrayList<WhereConditionColumnWrapper> filteredCols = new ArrayList<WhereConditionColumnWrapper>();

      for (TableFrameController tfc : tfm.getTblCtrls())
      {
         for (ColumnInfo ci : tfc.getColumnInfoModel().getAll())
         {
            if (ci.getQueryData().isFiltered())
            {
               filteredCols.add(new WhereConditionColumnWrapper(tfc.getTableInfo(), ci));
            }
         }
      }

      if(null != _initialWhereTreeNodeStructure)
      {
         _initialWhereTreeNodeStructure.initTree(_rootNode, filteredCols, _panel.treeWhere);
         _initialWhereTreeNodeStructure = null;
      }
      else
      {
         new WhereTreeSync().sync(_rootNode, filteredCols);
      }

      WhereTreeNodeStructure ts = new WhereTreeNodeStructure(_rootNode, _panel.treeWhere);
      nodeChanged(_rootNode);
      //ts.applyExpansion(_rootNode, _panel.treeWhere);

      return ts;
   }

   private void nodeChanged(DefaultMutableTreeNode node)
   {
      WhereTreeNodeStructure ts = new WhereTreeNodeStructure(node, _panel.treeWhere);

      DefaultTreeModel dtm = (DefaultTreeModel) _panel.treeWhere.getModel();
      dtm.nodeStructureChanged(node);

      ts.applyExpansion(node, _panel.treeWhere);

      syncWherePreview();
   }

   private void syncWherePreview()
   {
      String whereClause = new WhereTreeNodeStructure(_rootNode, _panel.treeWhere).generateWhereClause();
      _entryPanelManager.getEntryPanel().setText(QueryBuilderSQLGenerator.format(whereClause, _session), false);

      Runnable runnable = new Runnable()
      {
         public void run()
         {
            _entryPanelManager.getEntryPanel().getTextComponent().scrollRectToVisible(new Rectangle(0,0));
            _entryPanelManager.getEntryPanel().setCaretPosition(0);

         }
      };

      SwingUtilities.invokeLater(runnable);
   }

   public JPanel getGraphQueryWherePanel()
   {
      return _panel;
   }

   public WhereTreeNodeStructure getWhereTreeNodeStructure()
   {
      return new WhereTreeNodeStructure(_rootNode, _panel.treeWhere);

   }
}
