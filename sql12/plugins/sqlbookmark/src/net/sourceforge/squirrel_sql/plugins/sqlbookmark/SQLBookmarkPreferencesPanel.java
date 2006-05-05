package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import javax.swing.*;
import java.awt.*;

public class SQLBookmarkPreferencesPanel extends JPanel
{
   JButton btnUp;
   JButton btnDown;
   JButton btnAdd;
   JButton btnEdit;
   JButton btnDel;
   JButton btnRun;


   private interface IPrefKeys
   {
      String BM_UP = "button.up.title";
      String BM_DOWN = "button.down.title";
      String BM_ADD = "button.add.title";
      String BM_EDIT = "button.edit.title";
      String BM_DEL = "button.del.title";
      String BM_RUN = "button.run.title";
   }


   JList lstBookmarks;

   public SQLBookmarkPreferencesPanel(SQLBookmarkPlugin plugin)
   {
      setLayout(new BorderLayout());

      lstBookmarks = new JList();
      add(new JScrollPane(lstBookmarks), BorderLayout.CENTER);

      JLabel lblAccesshint = new JLabel(plugin.getResourceString(AddBookmarkDialog.BM_ACCESS_HINT));
      lblAccesshint.setForeground(Color.red);
      add(lblAccesshint, BorderLayout.SOUTH);


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

      btnEdit = new JButton(plugin.getResourceString(IPrefKeys.BM_EDIT));
      gbc = new GridBagConstraints(0,4,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnEdit, gbc);

      btnDel = new JButton(plugin.getResourceString(IPrefKeys.BM_DEL));
      gbc = new GridBagConstraints(0,5,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnDel, gbc);

      gbc = new GridBagConstraints(0,6,1,1,0,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
      buttonPane.add(new JPanel(), gbc);
      
      add(buttonPane, BorderLayout.EAST);


   }
}
