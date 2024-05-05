package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.ToolTipDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SessionsListCtrl
{
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

   private static List<GroupDlgSessionWrapper> createSessionWrapperList(SavedSessionsGroupJsonBean groupBeingEdited)
   {
      List<GroupDlgSessionWrapper> ret = new ArrayList<>();
      for (ISession session : Main.getApplication().getSessionManager().getOpenSessions())
      {
         GroupDlgSessionWrapper sessWrp;

         if ( null == groupBeingEdited )
         {
            sessWrp = new GroupDlgSessionWrapper(session, true);
         }
         else
         {
            if(      null != session.getSavedSession()
                  && Objects.equals(groupBeingEdited.getGroupId(), session.getSavedSession().getGroupId()))
            {
               sessWrp = new GroupDlgSessionWrapper(session, true);
            }
            else
            {
               sessWrp = new GroupDlgSessionWrapper(session, false);
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
      else if(wrapper.isInBtnSavedSessionOrGroupMemberInfo(xInSessionListCellPanel, yInSessionListCellPanel))
      {
         _toolTipDisplay.displayToolTip(e.getX(), e.getY(), wrapper.getSavedSessionOrGroupMemberInfoToolTip());
      }
      else if(wrapper.isInButtonFunctions(xInSessionListCellPanel, yInSessionListCellPanel))
      {
         System.out.println("SessionsListCtrl.onMouseButton --> Hit btnMoveToNewSavedSession");
      }
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
}
