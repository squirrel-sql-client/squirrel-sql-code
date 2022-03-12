package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SavedSessionMoreCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionMoreCtrl.class);

   private final SavedSessionMoreDlg _dlg;
   private ISession _session;
   private SavedSessionJsonBean _savedSessionToOpen;

   public SavedSessionMoreCtrl(ISession session)
   {
      _session = session;

      Frame owningFrame;
      SavedSessionMoreDlgState state;
      if(null != _session)
      {
         owningFrame = GUIUtils.getOwningFrame(_session.getSessionPanel());
         state = SavedSessionMoreDlgState.CURRENT_SESSION_WARN_DISCARD_SQL_EDITORS;
         if(SavedSessionUtil.isSQLVirgin(_session))
         {
            state = SavedSessionMoreDlgState.CURRENT_SESSION;
         }
      }
      else
      {
         owningFrame = Main.getApplication().getMainFrame();
         state = SavedSessionMoreDlgState.CURRENT_SESSION_NONE;
      }

      _dlg = new SavedSessionMoreDlg(owningFrame, state);

      final SavedSessionsManager savedSessionsManager = Main.getApplication().getSavedSessionsManager();

      _dlg.lstSavedSessions.setListData(savedSessionsManager.getSavedSessions().toArray(new SavedSessionJsonBean[0]));
      if(0 < _dlg.lstSavedSessions.getModel().getSize())
      {
         _dlg.lstSavedSessions.setSelectedIndex(0);
      }

      _dlg.lstSavedSessions.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            onListClicked(e);
         }

         @Override
         public void mousePressed(MouseEvent e)
         {
            maybeTriggerPopup(e);
         }

         @Override
         public void mouseReleased(MouseEvent e)
         {
            maybeTriggerPopup(e);
         }
      });

      _dlg.chkShowDefaultAliasMsg.setSelected(savedSessionsManager.isShowAliasChangeMsg());
      _dlg.chkShowDefaultAliasMsg.addActionListener(e -> savedSessionsManager.setShowAliasChangeMsg(_dlg.chkShowDefaultAliasMsg.isSelected()));

      _dlg.txtMaxNumberSavedSessions.setInt(savedSessionsManager.getMaxNumberSavedSessions());


      _dlg.btnOpenSelected.addActionListener(e -> onOpenSelected());
      _dlg.btnClose.addActionListener(e -> close());

      _dlg.btnDeleteSelected.addActionListener(e -> onDeleteSelected());

      GUIUtils.enableCloseByEscape(_dlg, dialog -> savedSessionsManager.setMaxNumberSavedSessions(_dlg.txtMaxNumberSavedSessions.getInt()));

      GUIUtils.initLocation(_dlg, 650, 580);

      GUIUtils.forceFocus(_dlg.lstSavedSessions);

      _dlg.setVisible(true);
   }

   private void maybeTriggerPopup(MouseEvent me)
   {
      if(false == me.isPopupTrigger())
      {
         return;
      }

      JPopupMenu popupMenu = new JPopupMenu();
      JMenuItem menuItem;

      menuItem = new JMenuItem(s_stringMgr.getString("SavedSessionMoreCtrl.rename"));
      menuItem.addActionListener(e -> onRename());
      popupMenu.add(menuItem);
      popupMenu.show(_dlg.lstSavedSessions, me.getX(), me.getY());
   }

   private void onRename()
   {
      final SavedSessionJsonBean selectedSavedSession = _dlg.lstSavedSessions.getSelectedValue();
      if(null == selectedSavedSession)
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("SavedSessionMoreCtrl.no.section.to.rename"));
      }

      final SessionSaveDlg sessionSaveDlg = new SessionSaveDlg(_dlg, selectedSavedSession.getName());

      if(false == sessionSaveDlg.isOk())
      {
         return;
      }

      selectedSavedSession.setName(sessionSaveDlg.getSavedSessionName());
      Main.getApplication().getSavedSessionsManager().moveToTop(selectedSavedSession);
   }

   private void onDeleteSelected()
   {
      final List<SavedSessionJsonBean> selectedValuesList = _dlg.lstSavedSessions.getSelectedValuesList();

      if(0 == selectedValuesList.size())
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("SavedSessionMoreCtrl.no.session.selected.to.delete"));
         return;
      }

      if(Main.getApplication().getSavedSessionsManager().areUsedInOpenSessions(selectedValuesList))
      {
         if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_dlg, s_stringMgr.getString("SavedSessionMoreCtrl.confirm.delete.including.used.in.open")))
         {
            return;
         }
      }
      else
      {
         if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_dlg, s_stringMgr.getString("SavedSessionMoreCtrl.confirm.delete")))
         {
            return;
         }
      }

      Main.getApplication().getSavedSessionsManager().delete(selectedValuesList);

      _dlg.lstSavedSessions.setListData(Main.getApplication().getSavedSessionsManager().getSavedSessions().toArray(new SavedSessionJsonBean[0]));

      if(0 < _dlg.lstSavedSessions.getModel().getSize())
      {
         _dlg.lstSavedSessions.setSelectedIndex(1);
      }
   }

   private void onListClicked(MouseEvent e)
   {
      if(2 == e.getClickCount())
      {
         onOpenSelected();
      }
   }

   private void onOpenSelected()
   {
      _savedSessionToOpen = _dlg.lstSavedSessions.getSelectedValue();

      if(null == _savedSessionToOpen)
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("SavedSessionMoreCtrl.no.saved.session.selected.to.open"));
         return;
      }
      close();
   }

   private void close()
   {
      Main.getApplication().getSavedSessionsManager().setMaxNumberSavedSessions(_dlg.txtMaxNumberSavedSessions.getInt());
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   public SavedSessionJsonBean getSavedSessionToOpen()
   {
      return _savedSessionToOpen;
   }

   public boolean isOpenInNewSession()
   {
      if(null == _dlg.openInSessionPanel)
      {
         return true;
      }

      return _dlg.openInSessionPanel.isOpenInNewSession();
   }
}
