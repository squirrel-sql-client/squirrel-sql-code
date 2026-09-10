package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import javax.swing.Timer;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.globalsearch.GlobalSearchType;
import net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup.CellDataWindow;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultDataSetAndCellDetailDisplayHandler;

public class DetailsTextFindExecutor
{
   private Timer _timer;

   public DetailsTextFindExecutor()
   {
   }

   public void findDelayedInRelatedDetailTextDisplays(String currentSearchString, ResultDataSetAndCellDetailDisplayHandler resultDisplayHandler, GlobalSearchType globalSearchType)
   {
      if(null == _timer)
      {
         _timer = new Timer(250, e -> doFind(currentSearchString, resultDisplayHandler, globalSearchType));
      }

      _timer.restart();
   }

   private void doFind(String currentSearchString, ResultDataSetAndCellDetailDisplayHandler resultDisplayHandler, GlobalSearchType globalSearchType)
   {
      _timer.stop();
      _timer = null;

      GlobalFindRemoteControl cellDetalRemoteControl = resultDisplayHandler.getCellDetailFindRemoteControlOrNull();
      if(null != cellDetalRemoteControl)
      {
         cellDetalRemoteControl.executeFindTillFirstResult(currentSearchString, globalSearchType, true);
      }

      CellDataWindow pinnedCellDataWindow = Main.getApplication().getGlobalCellDataDialogManager().getPinnedCellDataDialog();

      if(null == pinnedCellDataWindow)
      {
         return;
      }

      GlobalFindRemoteControl pinnedCellRemoteControl = pinnedCellDataWindow.getCellDetailFindRemoteControlOrNull();

      if(null != pinnedCellRemoteControl)
      {
         pinnedCellRemoteControl.executeFindTillFirstResult(currentSearchString, globalSearchType, true);
      }
   }
}
