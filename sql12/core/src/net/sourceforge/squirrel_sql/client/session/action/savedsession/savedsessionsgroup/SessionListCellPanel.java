package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.*;

public class SessionListCellPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionListCellRenderer.class);

   final JCheckBox chkSelected;
   final JTextField txtSessName;
   final JButton btnSavedSessionOrGroupMemberInfo;
   final JButton btnMoveToNewSavedSession;

   private GroupDlgSessionWrapper _value;


   public SessionListCellPanel()
   {
      super(new GridBagLayout());
      JTextField txtVanilla = new JTextField();

      setBackground(txtVanilla.getBackground());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      chkSelected = new JCheckBox();
      chkSelected.setBackground(txtVanilla.getBackground());
      add(chkSelected, gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0);
      txtSessName = new JTextField();
      txtSessName.setEditable(false);
      txtSessName.setBackground(txtVanilla.getBackground());
      txtSessName.setBorder(BorderFactory.createEmptyBorder());
      GUIUtils.setMinimumWidth(txtSessName, 0);
      GUIUtils.setPreferredWidth(txtSessName, 0);
      add(txtSessName, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2,3,2,0), 0,0);
      btnMoveToNewSavedSession = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.THREE_DOTS));
      add(GUIUtils.styleAsToolbarButton(btnMoveToNewSavedSession), gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2,3,2,3), 0,0);
      btnSavedSessionOrGroupMemberInfo = new JButton();
      add(GUIUtils.styleAsToolbarButton(btnSavedSessionOrGroupMemberInfo), gbc);

   }

   public void init(GroupDlgSessionWrapper value, boolean isSelected, boolean cellHasFocus)
   {
      _value = value;
      JTextField txtVanilla = new JTextField();

      txtSessName.setText(_value.toString());

      btnSavedSessionOrGroupMemberInfo.setIcon(null);
      btnSavedSessionOrGroupMemberInfo.setToolTipText(null);
      GUIUtils.styleAsToolbarButton(btnSavedSessionOrGroupMemberInfo, false, false);

      btnMoveToNewSavedSession.setIcon(null);
      btnMoveToNewSavedSession.setToolTipText(null);
      GUIUtils.styleAsToolbarButton(btnMoveToNewSavedSession, false, false);

      if (null != _value.getSession().getSavedSession() && false == StringUtilities.isEmpty(_value.getSession().getSavedSession().getGroupId(), true))
      {
         btnSavedSessionOrGroupMemberInfo.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SESSION_GROUP_SAVE));
         GUIUtils.styleAsToolbarButton(btnSavedSessionOrGroupMemberInfo, false, true);

         btnMoveToNewSavedSession.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.TO_SAVED_SESSION));
         GUIUtils.styleAsToolbarButton(btnMoveToNewSavedSession, false, true);
      }
      else if (null != _value.getSession().getSavedSession() && StringUtilities.isEmpty(_value.getSession().getSavedSession().getGroupId(), true))
      {
         btnSavedSessionOrGroupMemberInfo.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SESSION));
         GUIUtils.styleAsToolbarButton(btnSavedSessionOrGroupMemberInfo, false, true);
      }

      chkSelected.setSelected(_value.isInCurrentSession());


      setBackground(txtVanilla.getBackground());
      chkSelected.setBackground(txtVanilla.getBackground());
      txtSessName.setBackground(txtVanilla.getBackground());
      if(isSelected)
      {
         setBackground(txtVanilla.getSelectionColor());
         chkSelected.setBackground(txtVanilla.getSelectionColor());
         txtSessName.setBackground(txtVanilla.getSelectionColor());
         //lblImage.setBackground(new JTextField().getSelectionColor());
      }

      setBorder(BorderFactory.createEmptyBorder());
      if(cellHasFocus)
      {
         setBorder(BorderFactory.createLineBorder(Color.GRAY));
      }
   }
}
