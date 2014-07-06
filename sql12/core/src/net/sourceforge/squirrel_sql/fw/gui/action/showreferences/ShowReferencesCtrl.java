package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SimpleSessionListener;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang.ArrayUtils;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class ShowReferencesCtrl
{
   private static final String PREF_KEY_SHOW_REFERENCES_WIDTH = "Squirrel.showReferencesWidth";
   private static final String PREF_KEY_SHOW_REFERENCES_HEIGHT = "Squirrel.showReferencesHeight";


   static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ShowReferencesCtrl.class);
   private final ShowReferencesWindow _window;
   private final DefaultTreeModel _treeModel;

   public ShowReferencesCtrl(final ISession session, JFrame owningFrame, ResultMetaDataTable globalDbTable, ColumnDisplayDefinition pkColDef, ArrayList<ExportedKey> exportedKeys)
   {
      _window = new ShowReferencesWindow(session, owningFrame, s_stringMgr.getString("ShowReferencesCtrl.window.title", globalDbTable.getQualifiedName(), pkColDef.getColumnName()));

      DefaultMutableTreeNode root = new DefaultMutableTreeNode(globalDbTable);
      _treeModel = new DefaultTreeModel(root);


      createChildExportedKeyNodes(root, exportedKeys);

      _window.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

      _window.tree.setModel(_treeModel);

      _window.tree.addTreeExpansionListener(new TreeExpansionListener()
      {
         @Override
         public void treeExpanded(TreeExpansionEvent event)
         {
            onExpanded(session, event);
         }

         @Override
         public void treeCollapsed(TreeExpansionEvent event)
         {
         }
      });

      session.addSimpleSessionListener(new SimpleSessionListener()
      {
         @Override
         public void sessionClosed()
         {
            close();
         }
      });

      _window.tree.addTreeSelectionListener(new TreeSelectionListener()
      {
         @Override
         public void valueChanged(TreeSelectionEvent e)
         {
            onTreeSelectionChanged(e);
         }
      });

      _window.tree.expandPath(new TreePath(root));

      GUIUtils.enableCloseByEscape(_window);


      int width = Preferences.userRoot().getInt(PREF_KEY_SHOW_REFERENCES_WIDTH, 300);
      int height = Preferences.userRoot().getInt(PREF_KEY_SHOW_REFERENCES_HEIGHT, 300);

      _window.setSize(width, height);
      GUIUtils.centerWithinParent(_window);

      _window.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onClose();
         }
      });

      _window.setVisible(true);
   }

   private void close()
   {
      onClose();
      _window.setVisible(false);
      _window.dispose();
   }

   private void onTreeSelectionChanged(TreeSelectionEvent e)
   {

      Object[] path = e.getPath().getPath();

      if(path.length < 2)
      {
         return;
      }


      ArrayUtils.reverse(path);


      //ExportedKey parentExportedKey = (ExportedKey) ((DefaultMutableTreeNode)path[0]).getUserObject();

      //String sql =  "SELECT * FROM " + parentExportedKey.getResultMetaDataTable().getQualifiedName() + " WHERE " + parentExportedKey.getColumn() + " IN ";
      String sql =  "";

      for (int i = 0; i < path.length - 1; i++) // path.length - 1 because we exclude root
      {
         ExportedKey exportedKey = (ExportedKey) ((DefaultMutableTreeNode)path[i]).getUserObject();


         String selection;

         if (0 == i)
         {
            selection = "*";
         }
         else
         {
            sql += "(";
            selection = exportedKey.getTablesPrimaryKey();
         }

         sql +=  "SELECT " + selection + " FROM " + exportedKey.getResultMetaDataTable().getQualifiedName() + " WHERE " + exportedKey.getFkColumn() + " IN ";

         if(i == path.length - 2)
         {
            sql += exportedKey.getInStat();
         }
      }

      for (int i = 0; i < path.length - 2; i++)
      {
         sql += ")";
      }

      _window.resultExecuterPanel.executeSQL(sql);
      //System.out.println(sql);

   }

   private void createChildExportedKeyNodes(DefaultMutableTreeNode parent, ArrayList<ExportedKey> exportedKeys)
   {
      for (ExportedKey exportedKey : exportedKeys)
      {
         DefaultMutableTreeNode child = new DefaultMutableTreeNode(exportedKey)
         {
            @Override
            public boolean isLeaf()
            {
               return false;
            }
         };


         child.setAllowsChildren(true);
         parent.add(child);
      }
   }

   private void onExpanded(ISession session, TreeExpansionEvent event)
   {
      DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

      if(0 < parentNode.getChildCount())
      {
         return;
      }


      ExportedKey parentExportedKey = (ExportedKey) parentNode.getUserObject();

      if(false == parentExportedKey.hasSingleColumnPk())
      {
         JOptionPane.showMessageDialog(_window, s_stringMgr.getString("ShowReferencesCtrl.tableHasNoSingleColumnPk", parentExportedKey.getResultMetaDataTable().getQualifiedName()));
         return;
      }

      ArrayList<ExportedKey> exportedKeys = ShowReferencesUtil.getExportedKeys(parentExportedKey.getResultMetaDataTable(), null, session);

      createChildExportedKeyNodes(parentNode, exportedKeys);

      _treeModel.nodeStructureChanged(parentNode);



   }

   private void onClose()
   {
      Preferences.userRoot().putInt(PREF_KEY_SHOW_REFERENCES_WIDTH, _window.getSize().width);
      Preferences.userRoot().putInt(PREF_KEY_SHOW_REFERENCES_HEIGHT, _window.getSize().height);
   }
}
