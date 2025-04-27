package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.FirstSearchResult;
import net.sourceforge.squirrel_sql.fw.gui.EditableComboBoxHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GlobalSearchCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobalSearchCtrl.class);

   private static final String PREFS_KEY_GLOBAL_SEARCH_CTRL_SPLIT_POS = "net.sourceforge.squirrel_sql.client.globalsearch.split.pos";

   public static final String PREF_KEY_SELECTED_GLOBAL_SEARCH_TYPE = "net.sourceforge.squirrel_sql.client.globalsearch.type";

   private final GlobalSearchDlg _dlg = new GlobalSearchDlg();
   private final EditableComboBoxHandler _cboTextTeSearchHandler;

   public GlobalSearchCtrl()
   {
      _cboTextTeSearchHandler = new EditableComboBoxHandler(_dlg.cboTextToSearch, getClass().getName() + ".cboTextToSearch");

      _dlg.btnConfig.addActionListener(e -> onConfig());

      _dlg.getRootPane().setDefaultButton(_dlg.btnSearch);
      _dlg.btnSearch.addActionListener(e -> onSearchGlobally());

      GUIUtils.clearDefaultTreeEntries(_dlg.treeSearchResultNavi);

      GUIUtils.forceProperty(() -> onForceSplitDividerLocation());

      _cboTextTeSearchHandler.focus();

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

   private void onSearchGlobally()
   {
      if(StringUtilities.isEmpty(_cboTextTeSearchHandler.getItem(), true))
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("GlobalSearchCtrl.search.string.missing"));
         return;
      }

      _cboTextTeSearchHandler.saveCurrentItem();

      NodesToSearch nodesToSearch = GlobalSearchUtil.getNodesToSearch();

      DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("rootNonVisible");
      DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

      for( GlobSearchNodeSession gsnSession : nodesToSearch.globSearchNodeSessions() )
      {
         DefaultMutableTreeNode sessionNode = new DefaultMutableTreeNode(gsnSession);
         rootNode.add(sessionNode);

         for( GlobSearchNodeSqlPanel gsnSqlPanel : gsnSession.getGlobSearchNodeSqlPanels() )
         {
            DefaultMutableTreeNode sqlPanelNode = new DefaultMutableTreeNode(gsnSqlPanel);
            sessionNode.add(sqlPanelNode);

            for( GlobSearchNodeResultTabSqlResTable nodeResultTabSqlResTable : gsnSqlPanel.getGlobSearchNodeResultTabSqlResTables() )
            {
               if(nodeResultTabSqlResTable.executeSearch(_cboTextTeSearchHandler.getItem(), getSelectedGlobalSearchType()))
               {
                  DefaultMutableTreeNode resultTabNode = new DefaultMutableTreeNode(nodeResultTabSqlResTable);
                  sqlPanelNode.add(resultTabNode);
               }
            }
         }
      }

      for(GlobSearchNodeCellDataDialog nodeCellDataDialog : nodesToSearch.globSearchNodeCellDataDialogs())
      {
         if(nodeCellDataDialog.executeSearch(_cboTextTeSearchHandler.getItem(), getSelectedGlobalSearchType()))
         {
            DefaultMutableTreeNode resultTabNode = new DefaultMutableTreeNode(nodeCellDataDialog);
            rootNode.add(resultTabNode);
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

   private void onConfig()
   {
      JPopupMenu popup = new JPopupMenu();

      GlobalSearchType selectedGlobalSearchType = getSelectedGlobalSearchType();

      ButtonGroup bg = new ButtonGroup();
      for (GlobalSearchType searchType : GlobalSearchType.values())
      {
         JRadioButtonMenuItem radMnu = new JRadioButtonMenuItem(searchType.getDescription());
         if(searchType == selectedGlobalSearchType)
         {
            radMnu.setSelected(true);
         }
         radMnu.addActionListener(e -> Props.putString(PREF_KEY_SELECTED_GLOBAL_SEARCH_TYPE, searchType.name()));
         bg.add(radMnu);
         popup.add(radMnu);
      }

      popup.show(_dlg.btnConfig, 0,_dlg.btnConfig.getHeight());
   }

   private static GlobalSearchType getSelectedGlobalSearchType()
   {
      return GlobalSearchType.valueOf(Props.getString(PREF_KEY_SELECTED_GLOBAL_SEARCH_TYPE, GlobalSearchType.CONTAINS_IGNORE_CASE.name()));
   }

   private void onTreeSelectionChanged(TreeSelectionEvent e)
   {
      _dlg.txtPreview.setText(null);

      Object userObject = ((DefaultMutableTreeNode) e.getPath().getLastPathComponent()).getUserObject();
      if(userObject instanceof GlobSearchNodeResultTabSqlResTable nodeResultTabSqlResTable)
      {
         FirstSearchResult executorResult = nodeResultTabSqlResTable.getSearchExecutorResult();
         showFirstSearchResult(executorResult);
      }
      else if(userObject instanceof GlobSearchNodeCellDataDialog nodeCellDataDialog)
      {
         FirstSearchResult executorResult = nodeCellDataDialog.getSearchExecutorResult();
         showFirstSearchResult(executorResult);
      }
   }

   private void showFirstSearchResult(FirstSearchResult executorResult)
   {
      _dlg.txtPreview.appendToPane(executorResult.getCellTextTillFirstOccurrence(), false);
      _dlg.txtPreview.appendToPane(executorResult.getFirstMatchingText(), true);
      _dlg.txtPreview.appendToPane(executorResult.getCellTextAfterFirstOccurrence(), false);

      _dlg.txtPreview.setCaretPosition(Math.min(executorResult.getCellTextTillFirstOccurrence().length(), _dlg.txtPreview.getText().length()));
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
