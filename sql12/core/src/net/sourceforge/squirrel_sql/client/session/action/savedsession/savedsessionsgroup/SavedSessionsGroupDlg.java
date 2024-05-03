package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class SavedSessionsGroupDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionsGroupDlg.class);
   JTextField txtGroupName;
   JList<GroupDlgSessionWrapper> lstSessions;
   JComboBox<SavedSessionsGroupDlgDefaultButton> cboDefaultButton;
   JButton btnSaveGroup;
   JButton btnGitCommitGroup;
   JButton btnCancel;


   public SavedSessionsGroupDlg()
   {
      super(Main.getApplication().getMainFrame(), s_stringMgr.getString("SavedSessionsGroupDlg.title"));
      setModal(true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("SavedSessionsGroupDlg.name.of.saved.sessions.group")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      txtGroupName = new JTextField();
      getContentPane().add(txtGroupName, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,0,0), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("SavedSessionsGroupDlg.select.session.to.be.saved.in.group")), gbc);

      gbc = new GridBagConstraints(0,3,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,0,5), 0,0);
      lstSessions = new JList<>();
      getContentPane().add(new JScrollPane(lstSessions), gbc);

      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createDefaultButtonPanel(), gbc);

      gbc = new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,0,5,5), 0,0);
      getContentPane().add(createButtonsPanel(), gbc);
   }

   private JPanel createDefaultButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("SavedSessionsGroupDlg.select.default.button")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,3,0,0), 0,0);
      cboDefaultButton = new JComboBox<>(SavedSessionsGroupDlgDefaultButton.values());
      cboDefaultButton.setSelectedIndex(0);
      ret.add(cboDefaultButton, gbc);

      return ret;
   }

   private JPanel createButtonsPanel()
   {
      JPanel ret = new JPanel(new GridLayout(1,3,5,0));

      btnSaveGroup = new JButton(SavedSessionsGroupDlgDefaultButton.SAVE.toString(), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SESSION_SAVE));
      ret.add(btnSaveGroup);

      btnGitCommitGroup = new JButton(SavedSessionsGroupDlgDefaultButton.GIT_COMMIT.toString(), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SESSION_GIT_COMMIT));
      ret.add(btnGitCommitGroup);

      btnCancel = new JButton(s_stringMgr.getString("SavedSessionsGroupDlg.cancel"));
      ret.add(btnCancel);

      return ret;

   }
}
