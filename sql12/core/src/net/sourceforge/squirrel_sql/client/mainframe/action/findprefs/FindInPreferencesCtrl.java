package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesSheet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

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
import java.util.TreeMap;

public class FindInPreferencesCtrl
{
   private StringManager s_stringMgr = StringManagerFactory.getStringManager(FindInPreferencesCtrl.class);

   private final FindInPreferencesDlg _dlg;
   private FindInPreferencesModel _model;

   public FindInPreferencesCtrl(TreeMap<List<String>, List<PrefComponentInfo>> componentInfoByPath)
   {
      _model = new FindInPreferencesModel(componentInfoByPath);
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
   }

   private void onGoTo()
   {
      if(null == _dlg.tree.getSelectionPath())
      {
         s_stringMgr.getString("FindInPreferencesCtrl.no.tree.node.selected");
         return;
      }

      List<String> path = _model.treeNodeToComponentPath((DefaultMutableTreeNode)_dlg.tree.getSelectionPath().getLastPathComponent());

      final GlobalPreferencesDialogFindInfo openDialogsFindInfo = GlobalPreferencesSheet.showSheetAndGetPreferencesFinderInfo();

      new GotoHandler(openDialogsFindInfo).gotoPath(path);
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

      if(_model.detailsTextNeedsLineWrap(selectedNode))
      {
         _dlg.txtDetails.setLineWrap(true);
         _dlg.txtDetails.setWrapStyleWord(true);
      }
      else
      {
         _dlg.txtDetails.setLineWrap(false);
         _dlg.txtDetails.setWrapStyleWord(false);
      }

      _dlg.txtDetails.setText(_model.getDetailsText(selectedNode));

      SwingUtilities.invokeLater(() -> _dlg.txtDetails.scrollRectToVisible(new Rectangle(0,0)));
   }

}
