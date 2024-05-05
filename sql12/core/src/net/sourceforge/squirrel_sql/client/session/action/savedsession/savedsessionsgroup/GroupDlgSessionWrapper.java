package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.util.Objects;

public class GroupDlgSessionWrapper
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GroupDlgSessionWrapper.class);

   private final SavedSessionsGroupJsonBean _groupBeingEdited;
   private final ISession _session;
   private boolean _groupMember;
   private SessionListCellPanel _sessionListCellPanel = new SessionListCellPanel();

   public GroupDlgSessionWrapper(SavedSessionsGroupJsonBean groupBeingEdited, ISession session, boolean groupMember)
   {
      _groupBeingEdited = groupBeingEdited;
      _session = session;
      _groupMember = groupMember;
   }

   public ISession getSession()
   {
      return _session;
   }

   public SessionListCellPanel initAndGetListCellPanelForRendering(boolean isSelected, boolean cellHasFocus)
   {
      _sessionListCellPanel.initForRendering(this, isSelected, cellHasFocus);
      return _sessionListCellPanel;
   }

   public boolean isGroupMember()
   {
      return _groupMember;
   }

   public void invertGroupMemberFlag()
   {
      _groupMember = !_groupMember;
   }

   public void setGroupMemberFlag(boolean b)
   {
      _groupMember = b;
   }

   public SavedSessionsGroupJsonBean getGroupBeingEdited()
   {
      return _groupBeingEdited;
   }

   public boolean isInBtnSavedSessionOrGroupMemberInfo(int xInSessionListCellPanel, int yInSessionListCellPanel)
   {
      return _sessionListCellPanel.btnSavedSessionOrGroupMemberInfo.getBounds().contains(xInSessionListCellPanel, yInSessionListCellPanel);
   }

   public boolean isInChkSelected(int xInSessionListCellPanel, int yInSessionListCellPanel)
   {
      return _sessionListCellPanel.chkSelected.getBounds().contains(xInSessionListCellPanel, yInSessionListCellPanel)
            || _sessionListCellPanel.txtSessName.getBounds().contains(xInSessionListCellPanel, yInSessionListCellPanel);
   }

   public String getSavedSessionOrGroupMemberInfoToolTip()
   {
      if (null == _session.getSavedSession())
      {
         return null;
      }

      if (StringUtilities.isEmpty(_session.getSavedSession().getGroupId(), true))
      {
         return s_stringMgr.getString("GroupDlgSessionWrapper.saved.session.tooltip", _session.getSavedSession().getName());
      }
      else if (null == _groupBeingEdited || false == Objects.equals(_groupBeingEdited.getGroupId(), _session.getSavedSession().getGroupId()))
      {
         SavedSessionsGroupJsonBean group = Main.getApplication().getSavedSessionsManager().getGroup(_session.getSavedSession().getGroupId());
         return s_stringMgr.getString("GroupDlgSessionWrapper.member.of.other.group.tooltip", group.getGroupName());
      }

      return null;
   }

   @Override
   public String toString()
   {
      return _session.toString();
   }
}
