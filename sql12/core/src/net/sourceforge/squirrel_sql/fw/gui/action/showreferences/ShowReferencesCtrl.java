package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
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
      _window = new ShowReferencesWindow(owningFrame, s_stringMgr.getString("ShowReferencesCtrl.window.title", globalDbTable.getQualifiedName(), pkColDef.getColumnName()));

      DefaultMutableTreeNode root = new DefaultMutableTreeNode(globalDbTable);
      _treeModel = new DefaultTreeModel(root);


      createChildExportedKeyNodes(root, exportedKeys);


      _window.tree.setModel(_treeModel);

      _window.tree.addTreeExpansionListener(new TreeExpansionListener()
      {
         @Override
         public void treeExpanded(TreeExpansionEvent event)
         {
            onExpanded(session, event);
         }

         @Override
         public void treeCollapsed(TreeExpansionEvent event) {}
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
            onClose(_window);
         }
      });

      _window.setVisible(true);
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

      ArrayList<ExportedKey> exportedKeys = ShowReferencesUtil.getExportedKeys(parentExportedKey.getResultMetaDataTable(), new String("TODO INSTAT"), session);

      createChildExportedKeyNodes(parentNode, exportedKeys);

      _treeModel.nodeStructureChanged(parentNode);



   }

   private void onClose(ShowReferencesWindow window)
   {
      Preferences.userRoot().putInt(PREF_KEY_SHOW_REFERENCES_WIDTH, window.getSize().width);
      Preferences.userRoot().putInt(PREF_KEY_SHOW_REFERENCES_HEIGHT, window.getSize().height);
   }
}
