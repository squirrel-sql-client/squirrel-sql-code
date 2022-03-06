package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.awt.Frame;
import java.util.List;

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
         showWillDiscardExistingSqlPanelsWarning = isSQLVirgin(_session);
      }
      else
      {
         owningFrame = Main.getApplication().getMainFrame();
         showWillDiscardExistingSqlPanelsWarning = false;
      }

      _dlg = new SavedSessionOpenDlg(owningFrame, showWillDiscardExistingSqlPanelsWarning);

   }

   private boolean isSQLVirgin(ISession session)
   {
      List<SQLPanelTyped> sqlPanelTypedList =  SavedSessionUtil.getAllSQLPanelsOrderedAndTyped(session);

      if(1 < sqlPanelTypedList.size())
      {
         return true;
      }

      final SQLPanel mainSQLPanel = sqlPanelTypedList.get(0).getSqlPanel();
      return false == StringUtilities.isEmpty(mainSQLPanel.getSQLPanelAPI().getEntireSQLScript(), true);
   }

   public SavedSessionJsonBean getSelectedSavedSession()
   {
      return null;
   }
}
