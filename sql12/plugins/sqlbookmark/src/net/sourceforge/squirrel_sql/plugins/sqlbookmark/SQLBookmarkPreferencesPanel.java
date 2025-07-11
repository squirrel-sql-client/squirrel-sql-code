package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class SQLBookmarkPreferencesPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLBookmarkPreferencesPanel.class);

   JTree treBookmarks;

   JButton btnUp;
   JButton btnDown;
   JButton btnAdd;
   JButton btnAddFolder;
   JButton btnEdit;
   JButton btnDel;
   JButton btnRun;

   JButton btnExport;
   JButton btnImport;

   JCheckBox chkSquirrelMarksInPopup;
   JCheckBox chkUseContainsToFilterBookmarks;


   public SQLBookmarkPreferencesPanel(SQLBookmarkPlugin plugin)
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      treBookmarks = new JTree();
      gbc = new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      add(new JScrollPane(treBookmarks), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,5,5), 0,0);
      add(createButtonPane(), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      add(createSouthPane(plugin), gbc);
   }

   private JPanel createSouthPane(SQLBookmarkPlugin plugin)
   {
      JPanel pnlSouth = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      // i18n[sqlbookmark.squirrelMarksInPopup=Show SQuirreL bookmarks in ctrl+j popup]
      chkSquirrelMarksInPopup = new JCheckBox(s_stringMgr.getString("sqlbookmark.squirrelMarksInPopup"));
      pnlSouth.add(chkSquirrelMarksInPopup, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      // i18n[sqlbookmark.squirrelMarksInPopup=Show SQuirreL bookmarks in ctrl+j popup]
      chkUseContainsToFilterBookmarks = new JCheckBox(s_stringMgr.getString("sqlbookmark.useContainsToFilterBookmarks"));
      pnlSouth.add(chkUseContainsToFilterBookmarks, gbc);

      JLabel lblAccesshint = new JLabel(plugin.getResourceString(AddBookmarkDialog.BM_ACCESS_HINT));
      lblAccesshint.setForeground(Color.red);
      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      pnlSouth.add(lblAccesshint, gbc);

      gbc = new GridBagConstraints(1,0,1,3,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      pnlSouth.add(new JPanel(), gbc);


      return pnlSouth;
   }

   private JPanel createButtonPane()
   {
      JPanel buttonPane = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      btnRun = new JButton(s_stringMgr.getString("SQLBookmarkPreferencesPanel.button.run.title"));
      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnRun, gbc);


      btnUp = new JButton(s_stringMgr.getString("SQLBookmarkPreferencesPanel.button.up.title"));
      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnUp, gbc);

      btnDown = new JButton(s_stringMgr.getString("SQLBookmarkPreferencesPanel.button.down.title"));
      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnDown, gbc);

      btnAdd = new JButton(s_stringMgr.getString("SQLBookmarkPreferencesPanel.button.add.title"));
      gbc = new GridBagConstraints(0,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnAdd, gbc);

      btnAddFolder = new JButton(s_stringMgr.getString("SQLBookmarkPreferencesPanel.button.add.folder.title"));
      gbc = new GridBagConstraints(0,4,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnAddFolder, gbc);

      btnEdit = new JButton();
      gbc = new GridBagConstraints(0,5,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnEdit, gbc);

      btnDel = new JButton(s_stringMgr.getString("SQLBookmarkPreferencesPanel.button.del.title"));
      gbc = new GridBagConstraints(0,6,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,10,1), 0, 0);
      buttonPane.add(btnDel, gbc);


      btnExport = new JButton(s_stringMgr.getString("SQLBookmarkPreferencesPanel.button.export.title"));
      gbc = new GridBagConstraints(0,7,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      btnExport.setToolTipText(s_stringMgr.getString("SQLBookmarkPreferencesPanel.button.export.tooltip"));
      buttonPane.add(btnExport, gbc);

      btnImport = new JButton(s_stringMgr.getString("SQLBookmarkPreferencesPanel.button.import.title"));
      gbc = new GridBagConstraints(0,8,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      btnImport.setToolTipText(s_stringMgr.getString("SQLBookmarkPreferencesPanel.button.import.tooltip"));
      buttonPane.add(btnImport, gbc);

      gbc = new GridBagConstraints(0,9,1,1,0,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
      buttonPane.add(new JPanel(), gbc);
      return buttonPane;
   }
}
