package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.Frame;

public class SavedSessionOpenCtrl
{
   private final SavedSessionOpenDlg _dlg;
   private ISession _session;


   public SavedSessionOpenCtrl(ISession session)
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

      _dlg = new SavedSessionOpenDlg(owningFrame, showWillDiscardExistingSqlPanelsWarning);

   }

   public SavedSessionJsonBean getSelectedSavedSession()
   {
      return null;
   }
}
