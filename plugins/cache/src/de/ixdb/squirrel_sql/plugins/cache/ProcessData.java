package de.ixdb.squirrel_sql.plugins.cache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;


public class ProcessData
{
   public int pid;
   public Integer blockedByPid;
   public String deadLockChain;
   public int lockCount;
   public String locks;
   public String nspace;
   public String routine;
   public long lines;
   public long globals;
   public String state;
   public String userName;
   public String clientName;
   public String exeName;
   public String ipAdress;
   public String device;
   public int job;


   public static String[] getColumns()
   {
      return new String[]
      {
         "Pid",
         "Blocked by Pid",
         "Deadlock chain (Pids)",
         "Lock count",
         "Locks",
         "Nspace",
         "Routine",
         "Lines",
         "Globals",
         "State",
         "Username",
         "Client Name",
         "EXE Name",
         "IP Address",
         "Device",
         "Job#"
      };
   }



   private ProcessData blockedBy;

   public ProcessData(ResultSet procList)
   {
      try
      {
         job = procList.getInt("Job#");
         pid = procList.getInt("Pid");
         device = procList.getString("Device");
         nspace = procList.getString("Nspace");
         routine = procList.getString("Routine");
         lines = procList.getLong("Lines");
         globals =procList.getLong("Globals");
         state = procList.getString("State");
         userName = procList.getString("Username");
         clientName = procList.getString("Client Name");
         exeName = procList.getString("EXE Name");
         ipAdress = procList.getString("IP Address");
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }


   }


   public void fillBlocks(ProcessData[] allProcData)
   {
      int[] blockedPids = parseBlockedPidsFromLocks();

      for (int i = 0; i < allProcData.length; i++)
      {
         for (int j = 0; j < blockedPids.length; j++)
         {
            if(blockedPids[j] == allProcData[i].pid)
            {
               allProcData[i].blockedBy = this;
               allProcData[i].blockedByPid = new Integer(this.pid);
            }
         }
      }
   }

   private int[] parseBlockedPidsFromLocks()
   {
      // pid 25493, lock examples:
      // 5  ["  /db1/shd_data_4_3/"]User.ygwaBvbPosKorrD(1)  25493  1D,  25499 X  E
      // 5  ["  /db1/shd_data_4_3/"]User.ygwaBvbPosKorrD(1)  25493  1D,  25499,25506  X,X  E,E
      // 5  ["  /db1/shd_data_4_3/"]User.ygwaBvbPosKorrD(1)  25493  1D,  25499,25506,25508  X,X,X  E,E,E
      //
      // oder
      //
      //6  ["  d:\db\shd_data_lov\"]User.WLagerartikelD(76)  5544  1D,
      //7  ["  d:\db\shd_data_lov\"]User.WLagerartikelD(77)  5544  1D,  3804  S  E
      //8  ["  d:\db\shd_data_lov\"]User.WVorgangD(24)  5544  1D,
      //
      // oder fuer kein Lock:
      //
      //3  ["  f:\db\shd_data\"]User.ygwaTestD(989)  3792  1D,
      //4  ["  f:\db\shd_data\"]User.ygwaTestD(990)  3792  1D,
      //5  ["  f:\db\shd_data\"]User.ygwaTestD(991)  3792  1D,
      //6  ["  f:\db\shd_data\"]User.ygwaTestD(992)  3792  1D,
      //7  ["  f:\db\shd_data\"]User.ygwaTestD(993)  3792  1D,
      //8  ["  f:\db\shd_data\"]User.ygwaTestD(994)  3792  1D,
      //9  ["  f:\db\shd_data\"]User.ygwaTestD(995)  3792  1D,
      //
      //


      if(null == locks)
      {
         return new int[0];
      }

      HashMap blockedPids = new HashMap();
      String[] splits = locks.split("\n");




      for (int i = 0; i < splits.length; i++)
      {
         String[] splits2 = splits[i].split(",");

         if(2 > splits2.length)
         {
            continue;
         }

         for (int j = 1; j < splits2.length; j++)
         {
            String[] splits3 = splits2[j].replaceAll("\t", " ").split(" ");

            for (int k = 0; k < splits3.length; k++)
            {
               String[] splits4 = splits3[k].split(",");

               for (int l = 0; l < splits4.length; l++)
               {
                  try
                  {
                     Integer pidBuf = new Integer(splits4[l].trim());

                     // Es mach nichts, wenn es die PID nicht g�be.
                     // Sie wird dann einfach oben nicht zugeordnet.
                     if(pidBuf.intValue() != pid)
                     {
                        blockedPids.put(pidBuf, pidBuf);
                     }
                  }
                  catch (NumberFormatException e)
                  {
                  }
               }

            }
         }


      }

      Integer[] blockedPidsBuf = (Integer[]) blockedPids.keySet().toArray(new Integer[0]);

      int[] ret = new int[blockedPidsBuf.length];

      for (int i = 0; i < ret.length; i++)
      {
         ret[i] = blockedPidsBuf[i].intValue();
      }

      return ret;
   }

   public void fillDeadLockChain()
   {

      ProcessData blocker = blockedBy;


      ArrayList chain = new ArrayList();

      HashMap blockers = new HashMap();

      while(null != blocker)
      {
         chain.add(new Integer(blocker.pid));

         if(blocker.pid == pid)
         {
            deadLockChain = getChainAsSting(chain);
            break;
         }

         if(null != blockers.get(blocker))
         {
            // If we are blocked by a member of a deadlock chain
            // but do not belong to the deadlock chain ourselves
            // this prevents a forever loop.
            break;
         }

         blockers.put(blocker, blocker);
         blocker = blocker.blockedBy;
      }
   }

   private String getChainAsSting(ArrayList chain)
   {
      ///////////////////////////////////////////////////////////////////////////////
      // A deadlock chain is circular and does not have a defined beginning Pid.
      // In order to make the string representation of a deadlock chain unique
      // we put the smallest Pid at the beginning of the chain.
      Integer minPid = (Integer)chain.get(0);
      int ixOfMinPid = 0;

      for (int i = 0; i < chain.size(); i++)
      {
         Integer buf = ((Integer) chain.get(i));
         if(minPid.intValue() > buf.intValue())
         {
            minPid = buf;
            ixOfMinPid = i;
         }
      }

      for(int i =0; i < ixOfMinPid; ++i)
      {
         chain.add(chain.remove(0));
      }
      //
      /////////////////////////////////////////////////////////////////////////////

      String ret = "" + chain.get(0);
      for (int i = 1; i < chain.size(); i++)
      {
         ret += "-->" + chain.get(i);
      }
      ret += "-->" + chain.get(0);

      return ret;
   }
}
