package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.lang3.StringUtils;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FindInPreferencesCtrl
{
   private StringManager s_stringMgr = StringManagerFactory.getStringManager(FindInPreferencesCtrl.class);

   private final FindInPreferencesDlg _dlg;
   private FindInPreferencesModel _model;

   public FindInPreferencesCtrl(PrefsFindInfo prefsFindInfo)
   {
      _model = new FindInPreferencesModel(prefsFindInfo);
      final MainFrame parent = Main.getApplication().getMainFrame();
      _dlg = new FindInPreferencesDlg(parent);

      _dlg.tree.addTreeSelectionListener(e -> onTreeSelectionChanged(e));

      _dlg.tree.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            onTreeClicked(e);
         }
         @Override
         public void mouseReleased(MouseEvent e)
         {
            onTreeClicked(e);
         }
      });

      _dlg.tree.setCellRenderer(new FindPrefsTreeCellRenderer());

      _dlg.txtFind.getDocument().addDocumentListener(new DocumentListener()
      {
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            updateTree();
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            updateTree();
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            updateTree();
         }
      });

      _dlg.txtFind.addKeyListener(new KeyAdapter()
      {
         @Override
         public void keyPressed(KeyEvent e)
         {
            onKeyPressed(e);
         }
      });

      _dlg.btnGoTo.addActionListener(e -> onGoTo());


      updateTree();

      SwingUtilities.invokeLater(() -> _dlg.txtFind.requestFocus());

      GUIUtils.initLocation(_dlg, 750, 600);
      _dlg.setLocation(parent.getX() + 20, _dlg.getLocation().y);

      GUIUtils.enableCloseByEscape(_dlg);

      _dlg.setVisible(true);
   }

   private void onTreeClicked(MouseEvent me)
   {
      if(me.isPopupTrigger())
      {
         JPopupMenu popup = new JPopupMenu();

         final JMenuItem mnuItem = new JMenuItem(s_stringMgr.getString("FindInPreferencesCtrl.tree.popup.menu"));
         popup.add(mnuItem);
         mnuItem.addActionListener(e -> onGoTo());

         final TreePath pathForLocation = _dlg.tree.getPathForLocation(me.getX(), me.getY());
         if(null != pathForLocation)
         {
            _dlg.tree.setSelectionPath(pathForLocation);
         }
         popup.show(_dlg.tree, me.getX(), me.getY());
      }
      else if (2 == me.getClickCount())
      {
         if(null == _dlg.tree.getSelectionPath())
         {
            return;
         }

         List<String> componentPath = _model.treeNodeToComponentPath((DefaultMutableTreeNode)_dlg.tree.getSelectionPath().getLastPathComponent());

         final GotoHandler gotoHandler = new GotoHandler();

         gotoHandler.gotoPath(componentPath, true);

      }
   }

   private void onGoTo()
   {
      if(null == _dlg.tree.getSelectionPath())
      {
         s_stringMgr.getString("FindInPreferencesCtrl.no.tree.node.selected");
         return;
      }

      List<String> componentPath = _model.treeNodeToComponentPath((DefaultMutableTreeNode)_dlg.tree.getSelectionPath().getLastPathComponent());

      final GotoHandler gotoHandler = new GotoHandler();

      if(GotoPathResult.NO_ACTION_BECAUSE_COMPONENT_NOT_FOUND == gotoHandler.gotoPath(componentPath, false))
      {
         ///////////////////////////////////////////////////////////////////////
         // E.g the shortcut table changes when the preference window is opened.
         // That is why we refresh the tree here.
         // This block is of minor interest and may be removed if it causes trouble.
         _model = new FindInPreferencesModel(gotoHandler.getPrefsFindInfoUpdate());
         updateTree();
         tryRestorePreviousSelection(componentPath);
         //
         ////////////////////////////////////////////////////////////////////////
      }
   }

   private void tryRestorePreviousSelection(List<String> componentPath)
   {
      // Restore previous selection
      final DefaultMutableTreeNode root = (DefaultMutableTreeNode) _dlg.tree.getModel().getRoot();
      for (int i = componentPath.size(); i > 0; i--)
      {
         List<String> path = componentPath.subList(0, i);

         final DefaultMutableTreeNode toSelect = findTreePathByComponentPath(path, root);
         if(null != toSelect)
         {
            _dlg.tree.setSelectionPath(new TreePath(toSelect.getPath()));
            break;
         }
      }
   }

   private DefaultMutableTreeNode findTreePathByComponentPath(List<String> componentPath, DefaultMutableTreeNode node)
   {
      for (int i = 0; i < node.getChildCount(); i++)
      {
         final DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
         PathEntry pe = (PathEntry) child.getUserObject();
         if(pe.getComponentInfoList().get(0).getPath().equals(componentPath))
         {
            return child;
         }

         final DefaultMutableTreeNode ret = findTreePathByComponentPath(componentPath, child);
         if(null != ret)
         {
            return ret;
         }
      }

      return null;
   }

   private void updateTree()
   {
      String filterText = _dlg.txtFind.getText();

      DefaultMutableTreeNode root = _model.createFilteredTreeNodes(filterText);

      DefaultTreeModel dtm = new DefaultTreeModel(root);
      _dlg.tree.setModel(dtm);
      _dlg.tree.setRootVisible(false);

      _dlg.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

      if(false == StringUtilities.isEmpty(filterText, true))
      {
         for (int i = 0; i < _dlg.tree.getRowCount(); i++)
         {
            _dlg.tree.expandRow(i);
         }
      }

      _dlg.tree.setSelectionRow(0);

   }

   private void onKeyPressed(KeyEvent e)
   {
      if(e.getKeyCode() == KeyEvent.VK_UP)
      {
         int selIx;
         if(0 == _dlg.tree.getSelectionRows().length)
         {
            selIx = 0;
         }
         else
         {
            selIx = _dlg.tree.getSelectionRows()[0];
         }

         if(0 < selIx)
         {
            _dlg.tree.setSelectionRow(selIx - 1);
            _dlg.tree.scrollRowToVisible(selIx - 1);
         }
      }
      else if(e.getKeyCode() == KeyEvent.VK_DOWN)
      {
         int selIx;
         if(0 == _dlg.tree.getSelectionRows().length)
         {
            selIx = 0;
         }
         else
         {
            selIx = _dlg.tree.getSelectionRows()[0];
         }

         if(_dlg.tree.getRowCount() - 1 > selIx)
         {
            _dlg.tree.setSelectionRow(selIx + 1);
            _dlg.tree.scrollRowToVisible(selIx + 1);
         }
      }
   }


   private void onTreeSelectionChanged(TreeSelectionEvent e)
   {
      if(null == e.getNewLeadSelectionPath())
      {
         return;
      }

      DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();

      _dlg.txtDetails.setLineWrap(_model.detailsTextNeedsLineWrap(selectedNode));


      final String detailsText = _model.getDetailsText(selectedNode);

      _dlg.txtDetails.setText("");
      if(null == getFilterText())
      {
         _dlg.txtDetails.setText(detailsText);
      }
      else
      {
         final String filterText = getFilterText();
         int startIx = 0;

         for(;;)
         {
            int matchIx = StringUtils.indexOfIgnoreCase(detailsText, filterText, startIx);

            if(-1 == matchIx)
            {
               _dlg.txtDetails.appendToPane(detailsText.substring(startIx), false);
               break;
            }
            else
            {
               _dlg.txtDetails.appendToPane(detailsText.substring(startIx, matchIx), false);
               _dlg.txtDetails.appendToPane(detailsText.substring(matchIx,  matchIx + filterText.length() ), true);
               startIx = matchIx + filterText.length();
            }
         }
      }

      SwingUtilities.invokeLater(() -> _dlg.txtDetails.scrollRectToVisible(new Rectangle(0,0)));
   }

   private String getFilterText()
   {
      final String text = _dlg.txtFind.getText();

      if(StringUtilities.isEmpty(text, true))
      {
         return null;
      }

      return text.trim();

   }

}
