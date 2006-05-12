package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class BookmarEditController
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(BookmarEditController.class);


   private boolean _cancelled;
   private BookmarkEditDialog _dlg;
   private Bookmark _mark;


   public BookmarEditController(Frame owner, Bookmark mark, boolean editable)
   {
      _mark = mark;
      _dlg = new BookmarkEditDialog(owner);

      _dlg.btnOk.setEnabled(editable);

      if(null != _mark)
      {
         _dlg.txtName.setText(_mark.getName());
         _dlg.txtDescription.setText(_mark.getDescription());
         _dlg.txtSql.setText(_mark.getSql());
      }

      _dlg.btnOk.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onOK();
         }
      });
      _dlg.btnCancel.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onCancel();
         }
      });

      AbstractAction closeAction = new AbstractAction()
      {
         public void actionPerformed(ActionEvent actionEvent)
         {
            onCancel();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      _dlg.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getActionMap().put("CloseAction", closeAction);


      GUIUtils.centerWithinParent(_dlg);
      _dlg.setVisible(true);

   }

   private void onOK()
   {
      String name = _dlg.txtName.getText();
      if(null == name || 0 == name.trim().length() || containsWhiteSpaces(name))
      {
         // i18n[sqlbookmark.enterName=Please enter a bookmark name with no blanks]
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("sqlbookmark.enterName"));
         return;
      }

      String description = _dlg.txtDescription.getText();
      if(null == description || 0 == description.trim().length())
      {
         // i18n[sqlbookmark.enterDescription=Please enter a bookmark description]
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("sqlbookmark.enterDescription"));
         return;
      }

      String sql = _dlg.txtSql.getText();
      if(null == sql || 0 == sql.trim().length())
      {
         // i18n[sqlbookmark.enterSql=Please enter a bookmark sql]
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("sqlbookmark.enterSql"));
         return;
      }


      if(null == _mark)
      {
         _mark  = new Bookmark(name.trim(), description.trim(), sql.trim());
      }
      else
      {
         _mark.setName(name.trim());
         _mark.setDescription(description.trim());
         _mark.setSql(sql.trim());
      }

      _dlg.setVisible(false);
      _cancelled = false;
   }

   private boolean containsWhiteSpaces(String name)
   {
      for (int i = 0;  i < name.length(); ++i)
      {
         if(Character.isWhitespace(name.charAt(i)))
         {
            return true;
         }
      }
      return false;

   }

   private void onCancel()
   {
      _dlg.setVisible(false);
      _cancelled = true;
   }

   /**
    * Report whether user cancelled the operation or not.
    *
    * @return if true, user cancelled operation.
    */
   public boolean isCancelled()
   {
      return _cancelled;
   }

   /**
    * Set the cancelled status. Called by the button actions.
    *
    * @param status The cancelled status.
    */
   public void setCancelled(boolean status)
   {
      _cancelled = status;
   }

   public Bookmark getBookmark()
   {
      return _mark;
   }
}
