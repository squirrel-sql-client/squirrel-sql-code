package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class GroupOfSavedSessionsDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GroupOfSavedSessionsDlg.class);
   JTextField txtGroupName;
   JList<ISession> lstSessions;
   JButton btnSaveGroup;
   JButton btnGitCommitGroup;
   JButton btnCancel;


   public GroupOfSavedSessionsDlg()
   {
      super(Main.getApplication().getMainFrame(), s_stringMgr.getString("GroupOfSavedSessionsDlg.title"));
      setModal(true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("GroupOfSavedSessionsDlg.name.of.saved.sessions.group")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      txtGroupName = new JTextField();
      getContentPane().add(txtGroupName, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,0,0), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("GroupOfSavedSessionsDlg.select.session.to.be.saved.in.group")), gbc);

      gbc = new GridBagConstraints(0,3,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,0,5), 0,0);
      lstSessions = new JList<>();
      getContentPane().add(new JScrollPane(lstSessions), gbc);

      gbc = new GridBagConstraints(0,4,1,0,0,0,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,0,5,5), 0,0);
      getContentPane().add(createButtonsPanel(), gbc);
   }

   private JPanel createButtonsPanel()
   {
      JPanel ret = new JPanel(new GridLayout(1,2,5,5));

      btnSaveGroup = new JButton(s_stringMgr.getString("GroupOfSavedSessionsDlg.save.group"), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SESSION_SAVE));
      ret.add(btnSaveGroup);

      btnGitCommitGroup = new JButton(s_stringMgr.getString("GroupOfSavedSessionsDlg.git.commit.group"), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SESSION_GIT_COMMIT));
      ret.add(btnGitCommitGroup);

      btnCancel = new JButton(s_stringMgr.getString("GroupOfSavedSessionsDlg.cancel"));
      ret.add(btnCancel);

      return ret;

   }
}
