package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class SQLBookmarkPreferencesPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SQLBookmarkPreferencesPanel.class);



   private interface IPrefKeys
   {
      String BM_UP = "button.up.title";
      String BM_DOWN = "button.down.title";
      String BM_ADD = "button.add.title";
      String BM_DEL = "button.del.title";
      String BM_RUN = "button.run.title";
   }


   JTree treBookmarks;

   JButton btnUp;
   JButton btnDown;
   JButton btnAdd;
   JButton btnEdit;
   JButton btnDel;
   JButton btnRun;

   JCheckBox chkSquirrelMarksInPopup;


   public SQLBookmarkPreferencesPanel(SQLBookmarkPlugin plugin)
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      treBookmarks = new JTree();
      gbc = new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      add(new JScrollPane(treBookmarks), gbc);

      JPanel buttonPane = createButtonPane(plugin);
      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,5,5), 0,0);
      add(buttonPane, gbc);

      JPanel southPane = createSouthPane(plugin);
      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      add(southPane, gbc);
   }

   private JPanel createSouthPane(SQLBookmarkPlugin plugin)
   {
      JPanel pnlSouth = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      // i18n[sqlbookmark.squirrelMarksInPopup=Show SQuirreL bookmarks in ctrl+j popup]
      chkSquirrelMarksInPopup = new JCheckBox(s_stringMgr.getString("sqlbookmark.squirrelMarksInPopup"));
      pnlSouth.add(chkSquirrelMarksInPopup, gbc);

      JLabel lblAccesshint = new JLabel(plugin.getResourceString(AddBookmarkDialog.BM_ACCESS_HINT));
      lblAccesshint.setForeground(Color.red);
      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      pnlSouth.add(lblAccesshint, gbc);

      gbc = new GridBagConstraints(1,0,1,2,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      pnlSouth.add(new JPanel(), gbc);


      return pnlSouth;
   }

   private JPanel createButtonPane(SQLBookmarkPlugin plugin)
   {
      JPanel buttonPane = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      btnRun = new JButton(plugin.getResourceString(IPrefKeys.BM_RUN));
      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnRun, gbc);


      btnUp = new JButton(plugin.getResourceString(IPrefKeys.BM_UP));
      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnUp, gbc);

      btnDown = new JButton(plugin.getResourceString(IPrefKeys.BM_DOWN));
      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnDown, gbc);

      btnAdd = new JButton(plugin.getResourceString(IPrefKeys.BM_ADD));
      gbc = new GridBagConstraints(0,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnAdd, gbc);

      btnEdit = new JButton();
      gbc = new GridBagConstraints(0,4,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnEdit, gbc);

      btnDel = new JButton(plugin.getResourceString(IPrefKeys.BM_DEL));
      gbc = new GridBagConstraints(0,5,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnDel, gbc);

      gbc = new GridBagConstraints(0,6,1,1,0,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
      buttonPane.add(new JPanel(), gbc);
      return buttonPane;
   }
}
