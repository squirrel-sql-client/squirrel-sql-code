package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class GroupDlgSessionWrapper
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GroupDlgSessionWrapper.class);

   private final ISession _session;
   private boolean _groupMember;
   private SessionListCellPanel _sessionListCellPanel = new SessionListCellPanel();

   public GroupDlgSessionWrapper(ISession session, boolean groupMember)
   {
      _session = session;
      _groupMember = groupMember;
   }

   public ISession getSession()
   {
      return _session;
   }

   @Override
   public String toString()
   {
      return _session.toString();
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

   public boolean isInButtonFunctions(int xInSessionListCellPanel, int yInSessionListCellPanel)
   {
      return _sessionListCellPanel.btnMoveToNewSavedSession.getBounds().contains(xInSessionListCellPanel, yInSessionListCellPanel);
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
      if (null != _session.getSavedSession() && false == StringUtilities.isEmpty(_session.getSavedSession().getGroupId(), true))
      {
         SavedSessionsGroupJsonBean group = Main.getApplication().getSavedSessionsManager().getGroup(_session.getSavedSession().getGroupId());
         return s_stringMgr.getString("GroupDlgSessionWrapper.group.tooltip", group.getGroupName());
      }
      else if (null != _session.getSavedSession() && StringUtilities.isEmpty(_session.getSavedSession().getGroupId(), true))
      {
         return s_stringMgr.getString("GroupDlgSessionWrapper.saved.session.tooltip", _session.getSavedSession().getName());
      }
      return null;
   }
}
