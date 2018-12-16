package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

public class RecentFilesDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RecentFilesDialog.class);

   public static final String PREF_KEY_RECENT_FILES_DIALOG_WIDTH = "Squirrel.RecentFilesDialogWidth";
   public static final String PREF_KEY_RECENT_FILES_DIALOG_HEIGHT = "Squirrel.RecentFilesDialogHeight";


   JTree treFiles;
   IntegerField txtNumberRecentFiles;
   JButton btnFavourites;
   JButton btnAliasFavourites;
   JCheckBox chkAppend;
   JButton btnOpenFile;
   JButton btnClose;
   JButton btnRemoveSeleted;


   public RecentFilesDialog(Frame parent, boolean isCalledFromAliasView, ISQLAlias alias)
   {
      super(parent, true);

      if (isCalledFromAliasView)
      {
         setTitle(s_stringMgr.getString("recentfiles.RecentFilesDialog.title.connect", alias.getName()));
      }
      else
      {
         setTitle(s_stringMgr.getString("recentfiles.RecentFilesDialog.title"));
      }

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10,5,5,5), 0,0);
      treFiles = new JTree();
      getContentPane().add(new JScrollPane(treFiles), gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10,5,5,5), 0,0);
      getContentPane().add(createConfigPanel(), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,0,5), 0,0);
      getContentPane().add(createButtonsPanel(isCalledFromAliasView, alias), gbc);

      setSize(getDim());

      GUIUtils.centerWithinParent(this);
      GUIUtils.enableCloseByEscape(this);

      addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onWindowClosing();
         }

         @Override
         public void windowClosed(WindowEvent e)
         {
            onWindowClosing();
         }
      });
   }

   private void onWindowClosing()
   {
      Preferences.userRoot().putInt(PREF_KEY_RECENT_FILES_DIALOG_WIDTH, getSize().width);
      Preferences.userRoot().putInt(PREF_KEY_RECENT_FILES_DIALOG_HEIGHT, getSize().height);
   }


   private Dimension getDim()
   {
      return new Dimension(
            Preferences.userRoot().getInt(PREF_KEY_RECENT_FILES_DIALOG_WIDTH, 500),
            Preferences.userRoot().getInt(PREF_KEY_RECENT_FILES_DIALOG_HEIGHT, 500)
      );
   }

   private JPanel createButtonsPanel(boolean isCalledFromAliasView, ISQLAlias alias)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      int gridy = 0;

      if (false == isCalledFromAliasView)
      {
         gbc = new GridBagConstraints(0, gridy,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
         chkAppend = new JCheckBox(s_stringMgr.getString("recentfiles.RecentFilesDialog.append"));
         ret.add(chkAppend, gbc);
      }

      ++gridy;
      gbc = new GridBagConstraints(0,gridy,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      if (isCalledFromAliasView)
      {
         btnOpenFile = new JButton(s_stringMgr.getString("recentfiles.RecentFilesDialog.openFileInNewSession.for.alias", alias.getName()));
      }
      else
      {
         btnOpenFile = new JButton(s_stringMgr.getString("recentfiles.RecentFilesDialog.openFile"));
      }
      ret.add(btnOpenFile, gbc);




      gbc = new GridBagConstraints(1,gridy,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnClose = new JButton(s_stringMgr.getString("recentfiles.RecentFilesDialog.close"));
      ret.add(btnClose, gbc);


      gbc = new GridBagConstraints(0,++gridy,2,1,1,1, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);

      return ret;

   }

   private JPanel createConfigPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("recentfiles.RecentFilesDialog.configurations")));


      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(createMaximumNumberLinePanel(), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(createAddFilesToFolderLine(), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnRemoveSeleted = new JButton(s_stringMgr.getString("recentfiles.RecentFilesDialog.removeSelectedFiles"));
      ret.add(btnRemoveSeleted, gbc);

      gbc = new GridBagConstraints(0,3,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);


      return ret;

   }

   private JPanel createAddFilesToFolderLine()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("recentfiles.RecentFilesDialog.addFilesTo")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnFavourites = new JButton(s_stringMgr.getString("recentfiles.RecentFilesDialog.favourites"));
      ret.add(btnFavourites, gbc);

      gbc = new GridBagConstraints(2,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnAliasFavourites = new JButton(s_stringMgr.getString("recentfiles.RecentFilesDialog.aliasFavourites"));
      ret.add(btnAliasFavourites, gbc);

      return ret;
   }

   private JPanel createMaximumNumberLinePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("recentfiles.RecentFilesDialog.maxNumber")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      txtNumberRecentFiles = new IntegerField(4);
      ret.add(txtNumberRecentFiles, gbc);

      return ret;
   }
}
