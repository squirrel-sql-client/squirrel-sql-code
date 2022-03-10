package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import java.awt.Frame;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

public class SavedSessionMoreCtrl
{
   private final SavedSessionMoreDlg _dlg;
   private ISession _session;

   public SavedSessionMoreCtrl(ISession session)
   {
      _session = session;

      Frame owningFrame;
      boolean showWillDiscardExistingSqlPanelsWarning;
      if(null != _session)
      {
         owningFrame = GUIUtils.getOwningFrame(_session.getSessionPanel());
         showWillDiscardExistingSqlPanelsWarning = SavedSessionUtil.isSQLVirgin(_session);
      }
      else
      {
         owningFrame = Main.getApplication().getMainFrame();
         showWillDiscardExistingSqlPanelsWarning = false;
      }

      _dlg = new SavedSessionMoreDlg(owningFrame);

      GUIUtils.enableCloseByEscape(_dlg);

      GUIUtils.initLocation(_dlg, 400, 600);

      _dlg.setVisible(true);
   }

   public SavedSessionJsonBean getSelectedSavedSession()
   {
      return null;
   }
}
