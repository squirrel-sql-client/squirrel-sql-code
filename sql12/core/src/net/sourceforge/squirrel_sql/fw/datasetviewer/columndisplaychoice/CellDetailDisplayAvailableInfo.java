package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class CellDetailDisplayAvailableInfo
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellDetailDisplayAvailableInfo.class);
   private static ILogger s_log = LoggerController.createLogger(CellDetailDisplayAvailableInfo.class);

   static final CellDetailDisplayAvailableInfo INFO_DISPLAY_AVAILABLE = new CellDetailDisplayAvailableInfo();

   static final CellDetailDisplayAvailableInfo INFO_NO_DISPLAY_HANDLER = new CellDetailDisplayAvailableInfo();


   private final String _displayMessage;

   private CellDetailDisplayAvailableInfo()
   {
      this(null);
   }

   CellDetailDisplayAvailableInfo(String displayMessage)
   {
      _displayMessage = displayMessage;
   }

   public void displayNotAvailableMessage()
   {
      if(this == INFO_DISPLAY_AVAILABLE)
      {
         throw new IllegalStateException("Should be called only when this.isAvailable() == false");
      }

      if(this == INFO_NO_DISPLAY_HANDLER)
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("CellDetailDisplayAvailableInfo.cell.details.display.not.available"));
         s_log.warn(s_stringMgr.getString("CellDetailDisplayAvailableInfo.cell.details.display.not.available"));
      }

      Main.getApplication().getMessageHandler().showWarningMessage(_displayMessage);
      s_log.warn(_displayMessage);

   }

   public boolean isAvailable()
   {
      return this == INFO_DISPLAY_AVAILABLE;
   }
}
