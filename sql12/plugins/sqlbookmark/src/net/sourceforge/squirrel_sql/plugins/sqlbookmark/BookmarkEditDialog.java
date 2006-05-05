package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class BookmarkEditDialog extends JDialog
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(BookmarkEditDialog.class);

   JTextField txtName;
   JTextField txtDescription;
   JTextArea txtSql;
   JButton btnOk;
   JButton btnCancel;
   boolean cancelled = false;

   /**
    * Create the entry dialog
    *
    * @param owner The frame the dialog will be centered in
    */
   public BookmarkEditDialog(Frame owner)
   {

      // i18n[sqlbookmark.editBookmark=Edit bookmark]
      super(owner, s_stringMgr.getString("sqlbookmark.editBookmark"), true);

      Container contentPane = getContentPane();
      contentPane.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
      // i18n[sqlbookmark.prefName=Name:]
      contentPane.add(new JLabel(s_stringMgr.getString("sqlbookmark.prefName")), gbc);

      txtName = new JTextField(30);
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0);
      contentPane.add(txtName, gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0);
      // i18n[sqlbookmark.desc=Description:]
      contentPane.add(new JLabel(s_stringMgr.getString("sqlbookmark.desc")), gbc);

      txtDescription = new JTextField();
      gbc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0);
      contentPane.add(txtDescription, gbc);

      gbc = new GridBagConstraints(0, 2, 2, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0);
      // i18n[sqlbookmark.script=Script:]
      contentPane.add(new JLabel(s_stringMgr.getString("sqlbookmark.script")), gbc);

      txtSql = new JTextArea(5, 30);
      gbc = new GridBagConstraints(0, 3, 2, 1, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0);
      contentPane.add(new JScrollPane(txtSql), gbc);

      // i18n[sqlbookmark.prefOk=OK]
      btnOk = new JButton(s_stringMgr.getString("sqlbookmark.prefOk"));
      gbc = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0);
      contentPane.add(btnOk, gbc);

      // i18n[sqlbookmark.prefClose=Close]
      btnCancel = new JButton(s_stringMgr.getString("sqlbookmark.prefClose"));
      gbc = new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0);
      contentPane.add(btnCancel, gbc);

      getRootPane().setDefaultButton(btnOk);

      pack();
   }
}
