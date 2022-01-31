package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder.ObjectTreeFinderGoToNextResultHandle;
import net.sourceforge.squirrel_sql.client.session.objecttreesearch.ObjectTreeSearch;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class FindInObjectTreeController
{
   private static final String PREF_KEY_OBJECT_TREE_SEARCH_FILTER = "Squirrel.objTreeSearchFilter";


   private FindInObjectTreePanel _findInObjectTreePanel;
   private DefaultSQLEntryPanel _filterEditSQLEntryPanel;
   private IObjectTreeAPI _objectTreeAPI;
   private ObjectTreeFinderGoToNextResultHandle _goToNextResultHandle = new ObjectTreeFinderGoToNextResultHandle();

   public FindInObjectTreeController(IObjectTreeAPI objectTreeAPI)
   {
      _objectTreeAPI = objectTreeAPI;
      _filterEditSQLEntryPanel = new DefaultSQLEntryPanel(objectTreeAPI.getSession());
      _filterEditSQLEntryPanel.setMarkCurrentSQLActive(false);
      _findInObjectTreePanel = new FindInObjectTreePanel(_filterEditSQLEntryPanel.getTextComponent(), Main.getApplication().getResources());


      Action findAction = new AbstractAction("ObjectTree.Find")
      {
          public void actionPerformed(ActionEvent e)
          {
             onEnter();
          }
      };

      Action transferFocusAction = new AbstractAction("ObjectTree.TransferFocus")
      {
          public void actionPerformed(ActionEvent e)
          {
             _filterEditSQLEntryPanel.getTextComponent().transferFocus();
          }
      };

      _filterEditSQLEntryPanel.getTextComponent().registerKeyboardAction(findAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);

      _filterEditSQLEntryPanel.getTextComponent().registerKeyboardAction(transferFocusAction, KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK, false), JComponent.WHEN_FOCUSED);


      boolean filter = Props.getBoolean(PREF_KEY_OBJECT_TREE_SEARCH_FILTER, false);
      _findInObjectTreePanel._btnApplyAsFilter.setSelected(filter);


      _findInObjectTreePanel._btnFind.addActionListener(e -> onFind(false));

      _findInObjectTreePanel._btnApplyAsFilter.addActionListener(e -> onFind(false == _findInObjectTreePanel._btnApplyAsFilter.isSelected()));
   }

   private void onFind(boolean unfilterTreeFirst)
   {
      if(unfilterTreeFirst)
      {
         _objectTreeAPI.getSession().getProperties().setObjectFilterInclude(null);
         _objectTreeAPI.refreshSelectedNodes();
      }

      final String searchString = _filterEditSQLEntryPanel.getText();
      if(StringUtilities.isEmpty(searchString, true))
      {
         return;
      }

      if(_findInObjectTreePanel._btnApplyAsFilter.isSelected())
      {
         if(_goToNextResultHandle.setCurrentSearchState(searchString, true))
         {
            _objectTreeAPI.getSession().getProperties().setObjectFilterInclude(searchString);
            _objectTreeAPI.refreshSelectedNodes();
         }
         SwingUtilities.invokeLater(() -> new ObjectTreeSearch().viewObjectInObjectTree(searchString, _objectTreeAPI, _goToNextResultHandle));
      }
      else
      {
         _goToNextResultHandle.setCurrentSearchState(searchString, false);
         new ObjectTreeSearch().viewObjectInObjectTree(searchString, _objectTreeAPI, _goToNextResultHandle);
      }
   }

   private void onEnter()
   {
      _findInObjectTreePanel._btnFind.doClick();
   }



   public JPanel getFindInObjectTreePanel()
   {
      return _findInObjectTreePanel;
   }

   public ISQLEntryPanel getFindEntryPanel()
   {
      return _filterEditSQLEntryPanel;
   }

   public void dispose()
   {
      Props.putBoolean(PREF_KEY_OBJECT_TREE_SEARCH_FILTER, _findInObjectTreePanel._btnApplyAsFilter.isSelected());
   }
}
