package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.client.session.DefaultSQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ObjectTreeSearch;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.*;
import javax.swing.text.Keymap;
import java.awt.event.*;
import java.util.prefs.Preferences;

public class FindInObjectTreeController
{
   private static final String PREF_KEY_OBJECT_TREE_SEARCH_FILTER = "Squirrel.objTreeSearchFilter";


   private FindInObjectTreePanel _findInObjectTreePanel;
   private DefaultSQLEntryPanel _filterEditSQLEntryPanel;
   private ISession _session;

   public FindInObjectTreeController(ISession session)
   {
      _session = session;
      _filterEditSQLEntryPanel = new DefaultSQLEntryPanel(session);
      _findInObjectTreePanel = new FindInObjectTreePanel(_filterEditSQLEntryPanel.getTextComponent(), session.getApplication().getResources());


      Action findAction = new AbstractAction("ObjectTree.Find")
      {
          public void actionPerformed(ActionEvent e)
          {
             onEnter();
          }
      };
      JComponent comp = _filterEditSQLEntryPanel.getTextComponent();
      comp.registerKeyboardAction(findAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);


      boolean filter = Preferences.userRoot().getBoolean(PREF_KEY_OBJECT_TREE_SEARCH_FILTER, false);
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
         _session.getProperties().setObjectFilterInclude(null);
         _session.getObjectTreeAPIOfActiveSessionWindow().refreshSelectedNodes();
      }

      if(_findInObjectTreePanel._btnApplyAsFilter.isSelected())
      {
         _session.getProperties().setObjectFilterInclude(_filterEditSQLEntryPanel.getText());
         _session.getObjectTreeAPIOfActiveSessionWindow().refreshSelectedNodes();
          new ObjectTreeSearch().viewObjectInObjectTree(_session.getProperties().getObjectFilterInclude(), _session);
      }
      else
      {
         new ObjectTreeSearch().viewObjectInObjectTree(_filterEditSQLEntryPanel.getText(), _session);
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
      Preferences.userRoot().putBoolean(PREF_KEY_OBJECT_TREE_SEARCH_FILTER, _findInObjectTreePanel._btnApplyAsFilter.isSelected());
   }
}
