package net.sourceforge.squirrel_sql.client.globalsearch;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;

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
               DefaultMutableTreeNode resultTabNode = new DefaultMutableTreeNode(gsnResultTab);
               sqlPanelNode.add(resultTabNode);

               if(null != gsnResultTab.getGlobSearchNodeResultDetailDisplay())
               {
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
