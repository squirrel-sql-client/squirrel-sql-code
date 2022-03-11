package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JOptionPane;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SavedSessionMoreCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionMoreCtrl.class);

   private final SavedSessionMoreDlg _dlg;
   private ISession _session;
   private SavedSessionJsonBean _selectedSavedSession;

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

      _dlg.lstSavedSessions.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e)
      {
         onListClicked(e);
      }
   });

      _dlg.chkShowDefaultAliasMsg.setSelected(savedSessionsManager.isShowAliasChangeMsg());
      _dlg.chkShowDefaultAliasMsg.addActionListener(e -> savedSessionsManager.setShowAliasChangeMsg(_dlg.chkShowDefaultAliasMsg.isSelected()));

      _dlg.btnOpenSelected.addActionListener(e -> onOpenSelected());
      _dlg.btnClose.addActionListener(e -> close());

      _dlg.btnDeleteSelected.addActionListener(e -> onDeleteSelected());

      GUIUtils.enableCloseByEscape(_dlg);

      GUIUtils.initLocation(_dlg, 650, 580);

      GUIUtils.forceFocus(_dlg.lstSavedSessions);

      _dlg.setVisible(true);
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
      _selectedSavedSession = _dlg.lstSavedSessions.getSelectedValue();

      if(null == _selectedSavedSession)
      {
         JOptionPane.showConfirmDialog(_dlg, s_stringMgr.getString("SavedSessionMoreCtrl.no.saved.session.selected.to.open"));
         return;
      }
      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   public SavedSessionJsonBean getSelectedSavedSession()
   {
      return _selectedSavedSession;
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
