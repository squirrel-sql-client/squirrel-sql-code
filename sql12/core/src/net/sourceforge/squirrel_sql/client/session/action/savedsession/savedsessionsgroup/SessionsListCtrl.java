package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionJsonBean;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionUtil;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionPersister;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.ToolTipDisplay;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SessionsListCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionsListCtrl.class);

   private final JList<GroupDlgSessionWrapper> _lstSessions;
   private final GroupMembersListener _groupMembersListener;
   private final ToolTipDisplay _toolTipDisplay;

   public SessionsListCtrl(JList<GroupDlgSessionWrapper> lstSessions,
                           SavedSessionsGroupJsonBean groupBeingEdited,
                           GroupMembersListener groupMembersListener)
   {
      _groupMembersListener = groupMembersListener;
      _lstSessions = lstSessions;
      DefaultListModel<GroupDlgSessionWrapper> sessionListModel = new DefaultListModel<>();
      sessionListModel.addAll(createSessionWrapperList(groupBeingEdited));

      _lstSessions.setCellRenderer(new SessionListCellRenderer());
      _lstSessions.setModel(sessionListModel);
      _lstSessions.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

      _toolTipDisplay = GUIUtils.createToolTipDisplay(_lstSessions);

      // http://blog.mynotiz.de/programmieren/java-checkbox-in-jlist-1061/
      _lstSessions.addMouseListener(new MouseAdapter()
      {

         @Override
         public void mousePressed(MouseEvent e)
         {
            onMousePressed(e);
         }
      });

   }

   private List<GroupDlgSessionWrapper> createSessionWrapperList(SavedSessionsGroupJsonBean groupBeingEdited)
   {
      List<GroupDlgSessionWrapper> ret = new ArrayList<>();
      for (ISession session : Main.getApplication().getSessionManager().getOpenSessions())
      {
         GroupDlgSessionWrapper sessWrp;

         if (null == groupBeingEdited)
         {
            sessWrp = new GroupDlgSessionWrapper(groupBeingEdited, session, null == session.getSavedSession());
         }
         else
         {
            if(   null != session.getSavedSession()
               && Objects.equals(groupBeingEdited.getGroupId(), session.getSavedSession().getGroupId()))
            {
               sessWrp = new GroupDlgSessionWrapper(groupBeingEdited, session, true);
            }
            else
            {
               sessWrp = new GroupDlgSessionWrapper(groupBeingEdited, session, false);
            }
         }

         ret.add(sessWrp);
      }

      return ret;
   }

   private void onMousePressed(MouseEvent e)
   {
      _toolTipDisplay.closeToolTip();

      int index = _lstSessions.locationToIndex(e.getPoint());
      if(-1 == index)
      {
         return;
      }

      GroupDlgSessionWrapper wrapper = _lstSessions.getModel().getElementAt(index);
      Rectangle listCellBounds = _lstSessions.getCellBounds(index, index);

      if(false == listCellBounds.contains(e.getX(), e.getY()))
      {
         return;
      }

      int xInSessionListCellPanel = e.getX() - listCellBounds.x;
      int yInSessionListCellPanel = e.getY() - listCellBounds.y;

      if(wrapper.isInChkSelected(xInSessionListCellPanel, yInSessionListCellPanel))
      {
         if (   0 == (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK)
             && 0 == (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK))
         {
            wrapper.invertGroupMemberFlag();
            _lstSessions.repaint();
            _groupMembersListener.groupMembersChanged();
         }
      }
      else if(wrapper.isShowSavedSessionOrGroupMemberInfoToolTip(xInSessionListCellPanel, yInSessionListCellPanel))
      {
         _toolTipDisplay.displayToolTip(e.getX(), e.getY(), wrapper.getSavedSessionOrGroupMemberInfoToolTip());
      }
      else if(wrapper.isAllowToMoveGroupMemberToSavedSession(xInSessionListCellPanel, yInSessionListCellPanel))
      {
         onMoveGroupMemberToSavedSession(wrapper);
      }
   }

   private void onMoveGroupMemberToSavedSession(GroupDlgSessionWrapper wrapperToMove)
   {
      SavedSessionJsonBean savedSessionToMove = wrapperToMove.getSession().getSavedSession();
      String savedSessionNameTemplate = savedSessionToMove.getName();

      if(StringUtils.startsWith(savedSessionNameTemplate, SessionPersister.GROUP_SAVED_SESSION_NAME_DUMMY))
      {
         savedSessionNameTemplate = SavedSessionUtil.createSavedSessionNameTemplate(wrapperToMove.getSession());
      }

      String newName =
            SavedSessionUtil.showEditSavedSessionNameDialog(GUIUtils.getOwningWindow(_lstSessions),
                                                            savedSessionNameTemplate,
                                                            savedSessionToMove,
                                                            s_stringMgr.getString("SessionsListCtrl.move.group.member.title"));

      if(null == newName)
      {
         return;
      }

      SessionPersister.moveSavedSessionFromGroupToStandalone(savedSessionToMove, newName);

      wrapperToMove.setGroupMemberFlag(false);

      _lstSessions.repaint();

      Main.getApplication().getMainFrame().getMainFrameTitleHandler().updateMainFrameTitle();
   }

   public List<GroupDlgSessionWrapper> getInCurrentGroupList()
   {
      List<GroupDlgSessionWrapper> ret = new ArrayList<>();
      for (int i = 0; i < _lstSessions.getModel().getSize(); i++)
      {
         GroupDlgSessionWrapper sessWrp = _lstSessions.getModel().getElementAt(i);
         if(sessWrp.isGroupMember())
         {
            ret.add(sessWrp);
         }
      }

      return ret;
   }

   public void selectAll()
   {
      for(int i = 0; i < _lstSessions.getModel().getSize(); i++)
      {
         _lstSessions.getModel().getElementAt(i).setGroupMemberFlag(true);
      }
      _lstSessions.repaint();
      _groupMembersListener.groupMembersChanged();

   }
}
