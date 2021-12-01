package net.sourceforge.squirrel_sql.plugins.dataimport.importer;

import java.util.Calendar;
import java.util.Date;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class FailedToInterpretHandler
{
   private static final StringManager stringMgr = StringManagerFactory.getStringManager(FailedToInterpretHandler.class);

   private final static ILogger s_log = LoggerController.createLogger(FailedToInterpretHandler.class);


   private int numberOfLogs = 0;
   public Double failedToInterpretNumeric(int column, String toInterpret, Exception e)
   {

      if(10 == numberOfLogs)
      {
         String msg = stringMgr.getString("ExcelFileImporter.failed.to.interpret.numeric.no.more.messages", numberOfLogs);
         Main.getApplication().getMessageHandler().showErrorMessage(msg);
         s_log.error(msg, e);

         ++numberOfLogs;

         return getNumericDummy();
      }
      else if( 10 < numberOfLogs)
      {
         return getNumericDummy();
      }

      String msg = stringMgr.getString("ExcelFileImporter.failed.to.interpret.numeric", toInterpret, column + 1, getNumericDummy());
      Main.getApplication().getMessageHandler().showErrorMessage(msg);
      s_log.error(msg);

      ++numberOfLogs;

      return getNumericDummy();
   }

   public Double getNumericDummy()
   {
      return -42d;
   }


   public Date failedToInterpretDate(int column, String toInterpret, Exception e)
   {
      if(10 == numberOfLogs)
      {
         String msg = stringMgr.getString("ExcelFileImporter.failed.to.interpret.date.no.more.messages", numberOfLogs);
         Main.getApplication().getMessageHandler().showErrorMessage(msg);
         s_log.error(msg, e);

         ++numberOfLogs;
      }
      else if( 10 < numberOfLogs)
      {
         return getDummyDate();
      }

      String msg = stringMgr.getString("ExcelFileImporter.failed.to.interpret.date", toInterpret, column + 1, getDummyDate());
      Main.getApplication().getMessageHandler().showErrorMessage(msg);
      s_log.error(msg);

      ++numberOfLogs;

      return getDummyDate();
   }

   private Date getDummyDate()
   {
      Calendar cal = Calendar.getInstance();
      cal.set(1977, Calendar.JULY, 7);
      return cal.getTime();
   }
}
