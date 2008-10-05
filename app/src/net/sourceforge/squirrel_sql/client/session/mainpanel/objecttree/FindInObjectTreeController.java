package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.client.session.DefaultSQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ObjectTreeSearch;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.*;
import javax.swing.text.Keymap;
import java.awt.event.*;

public class FindInObjectTreeController
{
   private FindInObjectTreePanel _findInObjectTreePanel;
   private DefaultSQLEntryPanel _defaultSQLEntryPanel;
   private ISession _session;

   public FindInObjectTreeController(ISession session)
   {
      _session = session;
      _defaultSQLEntryPanel = new DefaultSQLEntryPanel(session);
      _findInObjectTreePanel = new FindInObjectTreePanel(_defaultSQLEntryPanel.getTextComponent(), session.getApplication().getResources());

      Keymap km = _defaultSQLEntryPanel.getTextComponent().getKeymap();

      Action FindAction = new AbstractAction("ObjectTree.Find")
      {
          private static final long serialVersionUID = 1L;

          public void actionPerformed(ActionEvent e)
          {
             onEnter();
          }
      };

      km.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), FindAction);


      _findInObjectTreePanel._btnFind.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onFind();
         }
      });

   }

   private void onFind()
   {
      new ObjectTreeSearch().viewObjectInObjectTree(_defaultSQLEntryPanel.getText(), _session);
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
      return _defaultSQLEntryPanel;
   }
}
