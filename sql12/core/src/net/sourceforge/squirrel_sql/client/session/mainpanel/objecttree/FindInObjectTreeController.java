package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.objecttreesearch.ObjectTreeSearch;
import net.sourceforge.squirrel_sql.fw.props.Props;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class FindInObjectTreeController
{
   private static final String PREF_KEY_OBJECT_TREE_SEARCH_FILTER = "Squirrel.objTreeSearchFilter";


   private FindInObjectTreePanel _findInObjectTreePanel;
   private DefaultSQLEntryPanel _filterEditSQLEntryPanel;
   private IObjectTreeAPI _objectTreeAPI;

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


      _findInObjectTreePanel._btnFind.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onFind(false);
         }
      });

      _findInObjectTreePanel._btnApplyAsFilter.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onFind(false == _findInObjectTreePanel._btnApplyAsFilter.isSelected());
         }
      });
   }

   private void onFind(boolean unfilterTreeFirst)
   {
      if(unfilterTreeFirst)
      {
         _objectTreeAPI.getSession().getProperties().setObjectFilterInclude(null);
         _objectTreeAPI.refreshSelectedNodes();
      }

      if(_findInObjectTreePanel._btnApplyAsFilter.isSelected())
      {
         _objectTreeAPI.getSession().getProperties().setObjectFilterInclude(_filterEditSQLEntryPanel.getText());
         _objectTreeAPI.refreshSelectedNodes();
          new ObjectTreeSearch().viewObjectInObjectTree(_objectTreeAPI.getSession().getProperties().getObjectFilterInclude(), _objectTreeAPI);
      }
      else
      {
         new ObjectTreeSearch().viewObjectInObjectTree(_filterEditSQLEntryPanel.getText(), _objectTreeAPI);
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
