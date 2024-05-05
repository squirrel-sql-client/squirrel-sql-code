package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class SessionListCellPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionListCellRenderer.class);

   final JCheckBox chkSelected;
   final JTextField txtSessName;
   final JButton btnSavedSessionOrGroupMemberInfo;


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

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2,3,2,3), 0,0);
      btnSavedSessionOrGroupMemberInfo = new JButton();
      add(GUIUtils.styleAsToolbarButton(btnSavedSessionOrGroupMemberInfo), gbc);

   }

   public void initForRendering(GroupDlgSessionWrapper wrapper, boolean isSelected, boolean cellHasFocus)
   {
      JTextField txtVanilla = new JTextField();

      txtSessName.setText(wrapper.toString());

      btnSavedSessionOrGroupMemberInfo.setIcon(null);
      btnSavedSessionOrGroupMemberInfo.setToolTipText(null);
      GUIUtils.styleAsToolbarButton(btnSavedSessionOrGroupMemberInfo, false, false);

      if (null != wrapper.getSession().getSavedSession())
      {
         // Does the wrapped Session belong to a group?
         if (false == StringUtilities.isEmpty(wrapper.getSession().getSavedSession().getGroupId(), true))
         {
            if (null == wrapper.getGroupBeingEdited())
            {
               btnSavedSessionOrGroupMemberInfo.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SESSION_GROUP_SAVE));
               GUIUtils.styleAsToolbarButton(btnSavedSessionOrGroupMemberInfo, false, true);
            }
            // Does the wrapped Session belong another group than to the one being edited?
            else if(Objects.equals(wrapper.getGroupBeingEdited().getGroupId(), wrapper.getSession().getSavedSession().getGroupId()))
            {
               // This offers to move the group member to a standalone Saved Session outside the group
               // After this function is executed wrapper._groupMember must be automatically set to false.
               btnSavedSessionOrGroupMemberInfo.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.TO_SAVED_SESSION));
               GUIUtils.styleAsToolbarButton(btnSavedSessionOrGroupMemberInfo, false, true);
            }
            else if(false == Objects.equals(wrapper.getGroupBeingEdited().getGroupId(), wrapper.getSession().getSavedSession().getGroupId()))
            {
               btnSavedSessionOrGroupMemberInfo.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SESSION_GROUP_SAVE));
               GUIUtils.styleAsToolbarButton(btnSavedSessionOrGroupMemberInfo, false, true);
            }
         }
         else if (StringUtilities.isEmpty(wrapper.getSession().getSavedSession().getGroupId(), true))
         {
            btnSavedSessionOrGroupMemberInfo.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SESSION));
            GUIUtils.styleAsToolbarButton(btnSavedSessionOrGroupMemberInfo, false, true);
         }
      }

      chkSelected.setSelected(wrapper.isGroupMember());


      setBackground(txtVanilla.getBackground());
      chkSelected.setBackground(txtVanilla.getBackground());
      txtSessName.setBackground(txtVanilla.getBackground());
      if(isSelected)
      {
         setBackground(txtVanilla.getSelectionColor());
         chkSelected.setBackground(txtVanilla.getSelectionColor());
         txtSessName.setBackground(txtVanilla.getSelectionColor());
      }

      setBorder(BorderFactory.createEmptyBorder());
      if(cellHasFocus)
      {
         setBorder(BorderFactory.createLineBorder(Color.GRAY));
      }
   }

}
