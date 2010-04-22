package de.ixdb.squirrel_sql.plugins.cache;

import com.intersys.cache.CacheObject;
import com.intersys.cache.Dataholder;
import com.intersys.cache.jbind.JBindDatabase;
import com.intersys.classes.CharacterStream;
import com.intersys.objects.*;
import net.sourceforge.squirrel_sql.client.session.ISession;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ShowProcessesCommand
{
   private ISession _session;
   private ProcessListTab _processListTab;

   public ShowProcessesCommand(ISession session)
	{
      _session = session;
   }


   public void execute()
   {
      try
      {
         ProcessData[] procData = getProcessesFromCache(_session);

         ProcessListTabListener ptl = new ProcessListTabListener()
         {
            public ProcessData[] refreshRequested(ISession session)
            {
               return onRefreshRequested(session);
            }

            public int terminateRequested(ISession session, ProcessData processData)
            {
               return onTerminateRequested(session, processData);
            }

            public void closeRequested(ISession session)
            {
               _session.getSessionSheet().removeMainTab(_processListTab);
            }
         };


         _processListTab = new ProcessListTab(procData, ptl);
         int index = _session.getSessionSheet().addMainTab(_processListTab);
         _session.getSessionSheet().selectMainTab(index);


      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   private int onTerminateRequested(ISession session, ProcessData processData)
   {
      try
      {

         if(false == VersionInfo.is5(_session))
         {
            VersionInfo.showNotSupported(_session);
            return 1;
         }
         
         Database conn =  (JBindDatabase) CacheDatabase.getDatabase(session.getSQLConnection().getConnection());

         Id id = new Id(processData.pid);
         com.intersys.classes.SYSTEM.Process proc = (com.intersys.classes.SYSTEM.Process) com.intersys.classes.SYSTEM.Process._open(conn, id);
         Dataholder dataholder = proc.terminate();
         return dataholder.getIntValue();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private ProcessData[] onRefreshRequested(ISession session)
   {
      try
      {
         return getProcessesFromCache(session);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private ProcessData[] getProcessesFromCache(ISession session)
      throws CacheException, SQLException
   {
      Database conn =  (JBindDatabase) CacheDatabase.getDatabase(session.getSQLConnection().getConnection());
      CacheQuery qryNamespaces;

      if (VersionInfo.is5(_session))
      {
         qryNamespaces = new CacheQuery(conn, "%SYSTEM.Process", "CONTROLPANEL");
      }
      else
      {
         qryNamespaces = new CacheQuery(conn, "%SYS.ProcessQuery", "CONTROLPANEL");
      }

      ResultSet procList = qryNamespaces.execute(new Object[]{"*"});

      Vector procData = new Vector();
      while(procList.next())
      {
         procData.add(new ProcessData(procList));
      }

      ProcessData[] ret = (ProcessData[]) procData.toArray(new ProcessData[procData.size()]);

      if (VersionInfo.is5(_session))
      {
         fillLocks(ret);

         fillBlocksAndDeadLocks(ret);
      }


      return ret;

   }

   private void fillBlocksAndDeadLocks(ProcessData[] procData)
   {

      for (int i = 0; i < procData.length; i++)
      {
         procData[i].fillBlocks(procData);
      }

      for (int i = 0; i < procData.length; i++)
      {
         procData[i].fillDeadLockChain();
      }

   }

   private void fillLocks(ProcessData[] procData)
   {
      try
      {
         Dataholder[] argv = new Dataholder[0];

         Connection con = _session.getSQLConnection().getConnection();
         Database database =  (JBindDatabase) CacheDatabase.getDatabase(con);

         Dataholder res = null;
         try
         {
            res = database.runClassMethod("CM.methM1", "M1", argv, Database.RET_OBJECT);
         }
         catch (CacheException e)
         {
            Statement stat = con.createStatement();
            stat.executeUpdate
            (

               "CREATE METHOD CM.M1()" +
               "  RETURNS %GlobalCharacterStream" +
               "  LANGUAGE COS" +
               "  {" +
               "     SET outStream = ##class(%GlobalCharacterStream).%New()" +
               "     SET c=\"\"" +
               "     DO list^%Wslocks(,.d,.c)" +
               "     SET lockCount=$Length(d,$$del1^%Wprim)" +
               "     FOR index=1:1:lockCount-1 {" +
               "         DO outStream.WriteLine($PIECE(d,$$del1^%Wprim,index))" +
               "     }" +
               "     quit outStream" +
               "  }"
            );
            stat.close();

            res = database.runClassMethod("CM.methM1", "M1", argv, Database.RET_OBJECT);
         }

         CacheObject cobj = res.getCacheObject();
         if (cobj == null)
         {
            System.out.println("null");
         }
         CharacterStream cs = (CharacterStream)(cobj.newJavaInstance());
         BufferedReader br = new BufferedReader(cs.getReader());
         String line = br.readLine();


         Pattern patJobNr = Pattern.compile("([\\x01\\x13\\^]+)(\\d+)([\\x01\\x13\\^]+)");
         Pattern pat = Pattern.compile("[\\x01\\x13\\^]+");

         Hashtable locksByProcID = new Hashtable();

         int len = 0;

         while (null != line)
         {
            len += line.length();

            Matcher matJobNr = patJobNr.matcher(line);
            Matcher mat = pat.matcher(line);

            if(matJobNr.find())
            {
               Integer pid = new Integer(matJobNr.group(matJobNr.groupCount()-1));
               LockData lockData = (LockData) locksByProcID.get(pid);
               if(null == lockData)
               {
                  lockData = new LockData();
                  locksByProcID.put(pid, lockData);
                  lockData.locks.append(mat.replaceAll("  "));
                  ++lockData.lockCount;
               }
               else
               {
                  lockData.locks.append('\n').append(mat.replaceAll("  "));
                  ++lockData.lockCount;
               }
            }

            line = br.readLine();
         }

         if(27500 < len)
         {
            String msg =
               "Too many locks! " +
               "The process list does probably not show all existing locks. " +
               "Deadlock and blocking information might be incomplete too.";
            _session.showErrorMessage(msg);
         }

         System.out.println("len = " + len);

         for (int i = 0; i < procData.length; i++)
         {
            LockData lockData = (LockData) locksByProcID.get(new Integer(procData[i].pid));

            if(null != lockData)
            {
               procData[i].locks = lockData.locks.toString();
               procData[i].lockCount = lockData.lockCount;
            }

         }

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void setSession(ISession session)
   {
      _session = session;
   }

   private static class LockData
   {
      StringBuffer locks = new StringBuffer();
      int lockCount = 0;
   }

}
