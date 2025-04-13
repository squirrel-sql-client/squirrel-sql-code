package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class GlobalSearchCtrl
{
   private static final String PREFS_KEY_GLOBAL_SEARCH_CTRL_SPLIT_POS = "net.sourceforge.squirrel_sql.client.globalsearch.split.pos";


   private GlobalSearchDlg _dlg = new GlobalSearchDlg();

   private final List<GlobSearchNodeSession> _globSearchNodeSessions;

   public GlobalSearchCtrl(List<GlobSearchNodeSession> globSearchNodeSessions, String textToSearch, GlobalSearchType globalSearchType)
   {
      _globSearchNodeSessions = globSearchNodeSessions;
      initTree();

      _dlg.txtTextToSearch.setText(textToSearch);


      GUIUtils.forceProperty(() -> onForceSplitDividerLocation());

      _dlg.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onClose();
         }
      });
      GUIUtils.enableCloseByEscape(_dlg, dlg -> onClose());
      GUIUtils.initLocation(_dlg, 600, 600);
      _dlg.setVisible(true);
   }

   private void initTree()
   {
      DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("rootNonVisible");
      DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

      for( GlobSearchNodeSession gsnSession : _globSearchNodeSessions )
      {
         DefaultMutableTreeNode sessionNode = new DefaultMutableTreeNode(gsnSession);
         rootNode.add(sessionNode);

         for( GlobSearchNodeSqlPanel gsnSqlPanel : gsnSession.getGlobSearchNodeSqlPanels() )
         {
            DefaultMutableTreeNode sqlPanelNode = new DefaultMutableTreeNode(gsnSqlPanel);
            sessionNode.add(sqlPanelNode);

            for( GlobSearchNodeResultTab gsnResultTab : gsnSqlPanel.getGlobSearchNodeResultTabs() )
            {

               if(null == gsnResultTab.getGlobSearchNodeResultDetailDisplay())
               {
                  if(gsnResultTab.getGlobSearchNodeResultTabSqlResTable().executeSearch())
                  {
                     DefaultMutableTreeNode resultTabNode = new DefaultMutableTreeNode(gsnResultTab);
                     sqlPanelNode.add(resultTabNode);
                  }
               }
               else
               {
                  DefaultMutableTreeNode resultTabNode = new DefaultMutableTreeNode(gsnResultTab);
                  resultTabNode.add(new DefaultMutableTreeNode(gsnResultTab.getGlobSearchNodeResultTabSqlResTable(), false));

                  if(null != gsnResultTab.getGlobSearchNodeResultDetailDisplay())
                  {
                     resultTabNode.add(new DefaultMutableTreeNode(gsnResultTab.getGlobSearchNodeResultDetailDisplay(), false));
                  }
               }
            }
         }
      }

      _dlg.treeSearchResultNavi.setModel(treeModel);
      _dlg.treeSearchResultNavi.setRootVisible(false);
      GUIUtils.expandAllNodes(_dlg.treeSearchResultNavi);

      _dlg.treeSearchResultNavi.addTreeSelectionListener(new TreeSelectionListener()
      {
         @Override
         public void valueChanged(TreeSelectionEvent e)
         {
            onTreeSelectionChanged(e);
         }
      });
   }

   private void onTreeSelectionChanged(TreeSelectionEvent e)
   {
      _dlg.txtPreview.setText(null);

      Object userObject = ((DefaultMutableTreeNode) e.getPath().getLastPathComponent()).getUserObject();
      if(userObject instanceof GlobSearchNodeResultTab globSearchNodeResultTab)
      {
         if(null == globSearchNodeResultTab.getGlobSearchNodeResultDetailDisplay())
         {
            SearchExecutorResult executorResult = globSearchNodeResultTab.getGlobSearchNodeResultTabSqlResTable().getSearchExecutorResult();

            _dlg.txtPreview.appendToPane(executorResult.getCellTextTillFirstOccurence(),false);
            _dlg.txtPreview.appendToPane(executorResult.getFirstMatchingText(), true);
            _dlg.txtPreview.appendToPane(executorResult.getCellTextAfterFirstOccurence(), false);
            _dlg.txtPreview.appendToPane("\n" + globSearchNodeResultTab.toString(), false);
         }
      }
   }

   private boolean onForceSplitDividerLocation()
   {
      int dividerLoc = Props.getInt(PREFS_KEY_GLOBAL_SEARCH_CTRL_SPLIT_POS, 300);
      if( _dlg.splitPane.getDividerLocation() == dividerLoc )
      {
         return true;
      }
      _dlg.splitPane.setDividerLocation(dividerLoc);
      return false;
   }

   private void onClose()
   {
      Props.putInt(PREFS_KEY_GLOBAL_SEARCH_CTRL_SPLIT_POS, _dlg.splitPane.getDividerLocation());
   }
}
