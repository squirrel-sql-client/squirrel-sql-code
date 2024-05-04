package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.ToolTipDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SessionsListCtrl
{
   private final JList<GroupDlgSessionWrapper> _lstSessions;
   private final SessionsListSelectionListener _sessionsListSelectionListener;
   private final ToolTipDisplay _toolTipDisplay;

   public SessionsListCtrl(JList<GroupDlgSessionWrapper> lstSessions,
                           SavedSessionsGroupJsonBean activeSavedSessionsGroup,
                           SessionsListSelectionListener sessionsListSelectionListener)
   {
      _sessionsListSelectionListener = sessionsListSelectionListener;
      _lstSessions = lstSessions;
      DefaultListModel<GroupDlgSessionWrapper> sessionListModel = new DefaultListModel<>();
      sessionListModel.addAll(Main.getApplication().getSessionManager().getOpenSessions().stream().map(s -> new GroupDlgSessionWrapper(s)).collect(Collectors.toList()));

      _lstSessions.setCellRenderer(new SessionListCellRenderer());
      _lstSessions.setModel(sessionListModel);
      _lstSessions.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

      _toolTipDisplay = GUIUtils.createToolTipDisplay(_lstSessions);


      for (int i = 0; i < _lstSessions.getModel().getSize(); i++)
      {
         GroupDlgSessionWrapper sessWrp = _lstSessions.getModel().getElementAt(i);
         if ( null != activeSavedSessionsGroup )
         {
            if(null != sessWrp.getSession().getSavedSession() && Objects.equals(activeSavedSessionsGroup.getGroupId(), sessWrp.getSession().getSavedSession().getGroupId()))
            {
               sessWrp.setInCurrentSession(true);
            }
         }
         else
         {
            sessWrp.setInCurrentSession(true);
         }
      }


      // _dlg.lstSessions.addMouseListener(); ...
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
         wrapper.invertSelected();
         _lstSessions.repaint();
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

   public List<GroupDlgSessionWrapper> getSelectedValuesList()
   {
      List<GroupDlgSessionWrapper> ret = new ArrayList<>();
      for (int i = 0; i < _lstSessions.getModel().getSize(); i++)
      {
         GroupDlgSessionWrapper sessWrp = _lstSessions.getModel().getElementAt(i);
         if(sessWrp.isInCurrentSession())
         {
            ret.add(sessWrp);
         }
      }

      return ret;
   }
}
