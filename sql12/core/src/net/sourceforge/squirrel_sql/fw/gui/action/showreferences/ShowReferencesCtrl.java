package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SimpleSessionListener;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;

public class ShowReferencesCtrl
{
   private static final String PREF_KEY_SHOW_REFERENCES_WIDTH = "Squirrel.showReferencesWidth";
   private static final String PREF_KEY_SHOW_REFERENCES_HEIGHT = "Squirrel.showReferencesHeight";
   private static final String PREF_KEY_SHOW_REFERENCES_QUALIFIED = "Squirrel.showReferencesHeight.qualified";


   static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ShowReferencesCtrl.class);
   private final ShowReferencesWindow _window;
   private final DefaultTreeModel _treeModel;
   private ArrayList<ShowQualifiedListener> _showQualifiedListeners = new ArrayList<ShowQualifiedListener>();
   private ISession _session;

   public ShowReferencesCtrl(final ISession session, JFrame owningFrame, RootTable rootTable, HashMap<String, ExportedKey> fkName_exportedKeys)
   {
      _session = session;
      _window = new ShowReferencesWindow(_session, owningFrame, s_stringMgr.getString("ShowReferencesCtrl.window.title", rootTable.getGlobalDbTable().getQualifiedName()));

      DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootTable);
      _treeModel = new DefaultTreeModel(root);


      createChildExportedKeyNodes(root, fkName_exportedKeys);
      initShowQualifiedListeners(fkName_exportedKeys);

      _window.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

      _window.tree.setModel(_treeModel);

      _window.tree.addTreeExpansionListener(new TreeExpansionListener()
      {
         @Override
         public void treeExpanded(TreeExpansionEvent event)
         {
            onExpanded(event);
         }

         @Override
         public void treeCollapsed(TreeExpansionEvent event)
         {
         }
      });

      _session.addSimpleSessionListener(new SimpleSessionListener()
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


      _window.chkShowQualified.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onChkShowQualified();
         }
      });

      _window.chkShowQualified.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_SHOW_REFERENCES_QUALIFIED, false));
      onChkShowQualified();

      _window.setVisible(true);
   }

   private void initShowQualifiedListeners(HashMap<String, ExportedKey> exportedKeys)
   {
      for (ExportedKey exportedKey : exportedKeys.values())
      {
         _showQualifiedListeners.add(exportedKey.getShowQualifiedListener());
      }

   }

   private void onChkShowQualified()
   {
      for (ShowQualifiedListener showQualifiedListener : _showQualifiedListeners)
      {
         showQualifiedListener.showQualifiedChanged(_window.chkShowQualified.isSelected());
      }

      _window.tree.setModel(null);
      _window.tree.setModel(_treeModel);

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


      JoinSQLInfo joinSQLInfo = ShowReferencesUtil.generateJoinSQLInfo(path);

      CodeReformator cr = new CodeReformator(CodeReformatorConfigFactory.createConfig(_session));
//      System.out.println(cr.reformat(joinSQLInfo.getSql()));
//      System.out.println();

      _window.resultExecuterPanel.executeSQL(cr.reformat(joinSQLInfo.getSql()), joinSQLInfo.getTableToBeEdited());

   }

   private void createChildExportedKeyNodes(DefaultMutableTreeNode parent, HashMap<String, ExportedKey> fkName_exportedKeys)
   {
      for (ExportedKey exportedKey : fkName_exportedKeys.values())
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

   private void onExpanded(TreeExpansionEvent event)
   {
      DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

      if(0 < parentNode.getChildCount())
      {
         return;
      }


      ExportedKey parentExportedKey = (ExportedKey) parentNode.getUserObject();

      HashMap<String, ExportedKey> fkName_exportedKeys = ShowReferencesUtil.getExportedKeys(parentExportedKey.getFkResultMetaDataTable(), null, _session);

      initShowQualifiedListeners(fkName_exportedKeys);


      createChildExportedKeyNodes(parentNode, fkName_exportedKeys);

      _treeModel.nodeStructureChanged(parentNode);



   }

   private void onClose()
   {
      Preferences.userRoot().putInt(PREF_KEY_SHOW_REFERENCES_WIDTH, _window.getSize().width);
      Preferences.userRoot().putInt(PREF_KEY_SHOW_REFERENCES_HEIGHT, _window.getSize().height);
      Preferences.userRoot().putBoolean(PREF_KEY_SHOW_REFERENCES_QUALIFIED, _window.chkShowQualified.isSelected());
   }
}
