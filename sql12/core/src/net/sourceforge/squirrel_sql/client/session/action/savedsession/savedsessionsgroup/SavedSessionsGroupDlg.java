package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallToolTipInfoButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class SavedSessionsGroupDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionsGroupDlg.class);
   JLabel lblOfTxtGroupName;
   JTextField txtGroupName;
   JList<GroupDlgSessionWrapper> lstSessions;
   JCheckBox chkOptimizeStoringOpenSessions;

   JComboBox<SavedSessionsGroupDlgDefaultButton> cboDefaultButton;
   JButton btnSaveGroup;
   JButton btnGitCommitGroup;
   JButton btnDelete;
   JButton btnCancel;


   public SavedSessionsGroupDlg()
   {
      super(Main.getApplication().getMainFrame(), s_stringMgr.getString("SavedSessionsGroupDlg.title"));
      setModal(true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      lblOfTxtGroupName = new JLabel();
      getContentPane().add(lblOfTxtGroupName, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      txtGroupName = new JTextField();
      getContentPane().add(txtGroupName, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,0,0), 0,0);
      getContentPane().add(createGroupListTitle(), gbc);

      gbc = new GridBagConstraints(0,3,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,0,5), 0,0);
      lstSessions = new JList<>();
      getContentPane().add(new JScrollPane(lstSessions), gbc);

      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      getContentPane().add(createOptimizeStoringOpenSessionsPanel(), gbc);

      gbc = new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(15,5,5,5), 0,0);
      getContentPane().add(createDefaultButtonPanel(), gbc);

      gbc = new GridBagConstraints(0,6,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createButtonsPanel(), gbc);
   }

   private JPanel createOptimizeStoringOpenSessionsPanel()
   {
      JPanel ret = new JPanel(new BorderLayout());

      chkOptimizeStoringOpenSessions = new JCheckBox(s_stringMgr.getString("SavedSessionsGroupDlg.optimize.storing.all.open.session"));
      ret.add(chkOptimizeStoringOpenSessions, BorderLayout.CENTER);

      ret.add(new SmallToolTipInfoButton(s_stringMgr.getString("SavedSessionsGroupDlg.optimize.storing.all.open.session.info")).getButton(), BorderLayout.EAST);

      return ret;
   }

   private static JPanel createGroupListTitle()
   {
      JPanel ret = new JPanel(new BorderLayout());
      ret.add(new JLabel(s_stringMgr.getString("SavedSessionsGroupDlg.select.session.to.be.saved.in.group")), BorderLayout.CENTER);

      String toolTipHtml =
            s_stringMgr.getString("SavedSessionsGroupDlg.select.session.list.info.button",
                                  Main.getApplication().getResources().getIconUrl(SquirrelResources.IImageNames.TO_SAVED_SESSION),
                                  Main.getApplication().getResources().getIconUrl(SquirrelResources.IImageNames.SESSION),
                                  Main.getApplication().getResources().getIconUrl(SquirrelResources.IImageNames.SESSION_GROUP_SAVE));

      ret.add(new SmallToolTipInfoButton(toolTipHtml).getButton(), BorderLayout.EAST);
      return ret;
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
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnSaveGroup = new JButton(SavedSessionsGroupDlgDefaultButton.SAVE.toString(), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SESSION_SAVE));
      ret.add(btnSaveGroup, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnGitCommitGroup = new JButton(SavedSessionsGroupDlgDefaultButton.GIT_COMMIT.toString(), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SESSION_GIT_COMMIT));
      ret.add(btnGitCommitGroup,gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,15,0,0), 0,0);
      btnDelete = new JButton(s_stringMgr.getString("SavedSessionsGroupDlg.delete"));
      ret.add(btnDelete, gbc);

      gbc = new GridBagConstraints(3,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);

      gbc = new GridBagConstraints(4,0,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnCancel = new JButton(s_stringMgr.getString("SavedSessionsGroupDlg.cancel"));
      ret.add(btnCancel, gbc);

      return ret;

   }
}
