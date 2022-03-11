package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.Frame;

public class SavedSessionMoreCtrl
{
   private final SavedSessionMoreDlg _dlg;
   private ISession _session;

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

      _dlg.lstSavedSessions.setListData(Main.getApplication().getSavedSessionsManager().getSavedSessions().toArray(new SavedSessionJsonBean[0]));

      GUIUtils.enableCloseByEscape(_dlg);

      GUIUtils.initLocation(_dlg, 650, 580);

      _dlg.setVisible(true);
   }

   public SavedSessionJsonBean getSelectedSavedSession()
   {
      return null;
   }
}
