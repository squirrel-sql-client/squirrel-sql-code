package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class BookmarkEditController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(BookmarkEditController.class);


   private boolean _canceled;
   private BookmarkEditDialog _dlg;
   private final DefaultMutableTreeNode _userBookmarksNode;
   private Bookmark _mark;


   public BookmarkEditController(Frame owner, Bookmark mark, boolean editable, DefaultMutableTreeNode userBookmarksNode)
   {
      _mark = mark;
      _dlg = new BookmarkEditDialog(owner, null == _mark);
      _userBookmarksNode = userBookmarksNode;

      _dlg.btnOk.setEnabled(editable);

      if(null != _mark)
      {
         _dlg.txtName.setText(_mark.getName());
         _dlg.txtDescription.setText(_mark.getDescription());
         _dlg.txtSql.setText(_mark.getSql());
      }

      _dlg.btnOk.addActionListener(e -> onOK());
      _dlg.btnCancel.addActionListener(e -> onCancel());

      GUIUtils.enableCloseByEscape(_dlg, dialog -> _canceled = true);

      _dlg.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            _canceled = true;
         }
      });

      GUIUtils.initLocation(_dlg, 570, 330);

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
         for(DefaultMutableTreeNode leaf : BookmarkAsTreeUtil.getLeaves(_userBookmarksNode))
         {
            Bookmark bookmark = (Bookmark) leaf.getUserObject();

            if(StringUtilities.equalsRespectNullModuloEmptyAndWhiteSpace(bookmark.getName(), name))
            {
               JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("BookmarkEditController.duplicate.bookmark.name", name));
               return;
            }
         }
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
      _dlg.dispose();
      _canceled = false;
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
      _dlg.dispose();
      _canceled = true;
   }

   /**
    * Report whether user cancelled the operation or not.
    *
    * @return if true, user cancelled operation.
    */
   public boolean isCanceled()
   {
      return _canceled;
   }

   /**
    * Set the cancelled status. Called by the button actions.
    *
    * @param status The cancelled status.
    */
   public void setCanceled(boolean status)
   {
      _canceled = status;
   }

   public Bookmark getBookmark()
   {
      return _mark;
   }
}
