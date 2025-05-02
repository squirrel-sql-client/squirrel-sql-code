package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.FirstSearchResult;
import net.sourceforge.squirrel_sql.fw.gui.EditableComboBoxHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

      _dlg.btnClose.addActionListener(e -> close());

      GUIUtils.clearDefaultTreeEntries(_dlg.treeSearchResultNavi);
      _dlg.treeSearchResultNavi.addTreeSelectionListener(e -> onTreeSelectionChanged(e));

      _dlg.treeSearchResultNavi.addMouseListener(new MouseAdapter()
      {
         public void mouseClicked(MouseEvent evt)
         {
            onMouseClickedTree(evt);
         }

         @Override
         public void mousePressed(MouseEvent evt)
         {
            maybeShowTreePopup(evt);
         }

         @Override
         public void mouseReleased(MouseEvent evt)
         {
            maybeShowTreePopup(evt);
         }
      });



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

   private void onMouseClickedTree(MouseEvent evt)
   {
      TreePath path = _dlg.treeSearchResultNavi.getSelectionPath();

      if(null == path)
      {
         return;
      }

      if (null != evt)
      {
         if (evt.getClickCount() < 2)
         {
            return;
         }

         if (false == _dlg.treeSearchResultNavi.getPathBounds(path).contains(evt.getPoint()))
         {
            return;
         }
      }

      DefaultMutableTreeNode tn = (DefaultMutableTreeNode) path.getLastPathComponent();

      Object userObject = tn.getUserObject();
      if(userObject instanceof GlobSearchNodeResultTabSqlResTable nodeResultTabSqlResTable)
      {
         bringSqlResultToFront(nodeResultTabSqlResTable, tn);
      }
      else if(userObject instanceof GlobSearchNodeCellDataDialog nodeCellDataDialog)
      {
         bringCellDataDialogToFront(nodeCellDataDialog);
      }
   }

   private static void bringCellDataDialogToFront(GlobSearchNodeCellDataDialog nodeCellDataDialog)
   {
      nodeCellDataDialog.bringDialogToFront();
   }

   private static void bringSqlResultToFront(GlobSearchNodeResultTabSqlResTable nodeResultTabSqlResTable, DefaultMutableTreeNode tn)
   {
      DefaultMutableTreeNode tnParent = (DefaultMutableTreeNode) tn.getParent();
      GlobSearchNodeSqlPanel globSearchNodeSqlPanel = (GlobSearchNodeSqlPanel) tnParent.getUserObject();
      nodeResultTabSqlResTable.bringResultToFront(globSearchNodeSqlPanel.getSqlPanelApiInfo());
   }

   private void maybeShowTreePopup(MouseEvent evt)
   {
      if(false == evt.isPopupTrigger())
      {
         return;
      }

      TreePath clickedPath  = _dlg.treeSearchResultNavi.getPathForLocation(evt.getX(), evt.getY());

      if(null == clickedPath)
      {
         return;
      }

      DefaultMutableTreeNode tn = (DefaultMutableTreeNode) clickedPath.getLastPathComponent();

      JPopupMenu popUp = null;

      Object userObject = tn.getUserObject();
      if(userObject instanceof GlobSearchNodeResultTabSqlResTable nodeResultTabSqlResTable)
      {
         popUp = new JPopupMenu();
         JMenuItem mnuBringResultToFront = new JMenuItem(s_stringMgr.getString("GlobalSearchCtrl.bring.result.to.front"));
         mnuBringResultToFront.addActionListener(e -> bringSqlResultToFront(nodeResultTabSqlResTable, tn));
         popUp.add(mnuBringResultToFront);
      }
      else if(userObject instanceof GlobSearchNodeCellDataDialog nodeCellDataDialog)
      {
         popUp = new JPopupMenu();

         JMenuItem mnuCellDataDialogToFront = new JMenuItem(s_stringMgr.getString("GlobalSearchCtrl.bring.result.to.front"));
         mnuCellDataDialogToFront.addActionListener(e -> bringCellDataDialogToFront(nodeCellDataDialog));
         popUp.add(mnuCellDataDialogToFront);
      }

      if(null != popUp)
      {
         popUp.show(evt.getComponent(), evt.getX(), evt.getY());
      }
   }


   private void close()
   {
      onClose();
      _dlg.setVisible(false);
      _dlg.dispose();
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

         boolean sessionNodeHasResults = false;
         for( GlobSearchNodeSqlPanel gsnSqlPanel : gsnSession.getGlobSearchNodeSqlPanels() )
         {
            DefaultMutableTreeNode sqlPanelNode = new DefaultMutableTreeNode(gsnSqlPanel);

            boolean sqlPanelNodeHasSearchResult = false;
            for( GlobSearchNodeResultTabSqlResTable nodeResultTabSqlResTable : gsnSqlPanel.getGlobSearchNodeResultTabSqlResTables() )
            {
               if(nodeResultTabSqlResTable.executeSearch(_cboTextTeSearchHandler.getItem(), getSelectedGlobalSearchType()))
               {
                  DefaultMutableTreeNode resultTabNode = new DefaultMutableTreeNode(nodeResultTabSqlResTable);
                  sqlPanelNode.add(resultTabNode);
                  sessionNodeHasResults = true;
                  sqlPanelNodeHasSearchResult = true;
               }
            }

            if(sqlPanelNodeHasSearchResult)
            {
               sessionNode.add(sqlPanelNode);
            }
         }

         if(sessionNodeHasResults)
         {
            rootNode.add(sessionNode);
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


      if(0 == rootNode.getChildCount())
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("GlobalSearchCtrl.no.results"));
      }
      else
      {
         _dlg.treeSearchResultNavi.setSelectionRow(0);
         GUIUtils.executeDelayed(() -> _dlg.treeSearchResultNavi.requestFocus(), 500);
      }
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

      _dlg.txtPreview.setCaretPosition(Math.min(executorResult.getCellTextTillFirstOccurrence().length() + executorResult.getFirstMatchingText().length(), _dlg.txtPreview.getText().length()));
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
