package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesSheet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.lang3.StringUtils;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FindInPreferencesCtrl
{
   private StringManager s_stringMgr = StringManagerFactory.getStringManager(FindInPreferencesCtrl.class);

   private final FindInPreferencesDlg _dlg;
   private TreeMap<List<String>, List<PrefComponentInfo>> _componentInfoByPath;

   public FindInPreferencesCtrl(TreeMap<List<String>, List<PrefComponentInfo>> componentInfoByPath)
   {
      _componentInfoByPath = componentInfoByPath;
      _dlg = new FindInPreferencesDlg();

      _dlg.tree.addTreeSelectionListener(e -> onTreeSelectionChanged(e));

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

      GUIUtils.initLocation(_dlg, 600, 600);
      GUIUtils.enableCloseByEscape(_dlg);

      _dlg.setVisible(true);
   }

   private void onGoTo()
   {
      if(null == _dlg.tree.getSelectionPath())
      {
         s_stringMgr.getString("FindInPreferencesCtrl.no.tree.node.selected");
         return;
      }

      final Object[] pathIncludingRoot = ((DefaultMutableTreeNode) _dlg.tree.getSelectionPath().getLastPathComponent()).getUserObjectPath();
      List<String> path = Stream.of(pathIncludingRoot).map(o -> (String)o).collect(Collectors.toList()).subList(1, pathIncludingRoot.length);

      final GlobalPreferencesDialogFindInfo openDialogsFindInfo = GlobalPreferencesSheet.showSheetAndGetPreferencesFinderInfo();

      final TreeMap<List<String>, List<PrefComponentInfo>> infoForOpenDialog = ComponentInfoByPathCreator.create(openDialogsFindInfo);

      final List<PrefComponentInfo> prefComponentInfoList = infoForOpenDialog.get(path);

      openDialogsFindInfo.selectTabOfPathComponent(prefComponentInfoList.get(0).getComponent());
   }

   private void updateTree()
   {
      String filterText = _dlg.txtFind.getText();

      DefaultMutableTreeNode root = new DefaultMutableTreeNode("");

      for (Map.Entry<List<String>, List<PrefComponentInfo>> entry : _componentInfoByPath.entrySet())
      {
         if(false == matches(entry.getKey(), filterText))
         {
            continue;
         }

         DefaultMutableTreeNode parent = root;
         for (String nodeName : entry.getKey())
         {
            boolean found = false;
            for (int i = 0; i < parent.getChildCount(); i++)
            {
               DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
               if(nodeName.equals(child.getUserObject()))
               {
                  parent = child;
                  found = true;
                  break;
               }
            }
            if(false == found)
            {
               DefaultMutableTreeNode child = new DefaultMutableTreeNode(nodeName);
               parent.add(child);
               parent = child;
            }
         }
      }

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

   private boolean matches(List<String> path, String filterText)
   {
      if(StringUtilities.isEmpty(filterText, true))
      {
         return true;
      }

      filterText = filterText.trim();
      for (String pathEntry : path)
      {
         if(StringUtils.containsIgnoreCase(pathEntry, filterText))
         {
            return true;
         }
      }
      return false;
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

      DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();

      _dlg.txtDetails.setText("" + lastPathComponent.getUserObject());

      SwingUtilities.invokeLater(() -> _dlg.txtDetails.scrollRectToVisible(new Rectangle(0,0)));
   }

}
