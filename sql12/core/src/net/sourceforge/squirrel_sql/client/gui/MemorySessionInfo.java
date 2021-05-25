package net.sourceforge.squirrel_sql.client.gui;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.text.DateFormat;
import java.util.Date;

class MemorySessionInfo implements Comparable<MemorySessionInfo>
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MemorySessionInfo.class);

   MemorySessionInfo(IIdentifier sessionId, String aliasName)
   {
      this.sessionId = sessionId;
      this.aliasName = aliasName;
   }

   IIdentifier sessionId;
   String aliasName;
   java.util.Date created = new Date();
   Date closed;
   Date finalized;

   public String toString()
   {
      DateFormat df = DateFormat.getInstance();

      Object[] params = new Object[]
            {
                  sessionId,
                  aliasName,
                  df.format(created),
                  null == closed ? "" : df.format(closed),
                  null == finalized ? "" : df.format(finalized)
            };

      if (null != closed && null == finalized)
      {
         // i18n[MemoryPanel.sessionInfo.toString1=Session: ID={0}, Alias={1}: created at {2}, closed at {3}]
         return s_stringMgr.getString("MemoryPanel.sessionInfo.toString1", params);
      }
      else if (null == closed)
      {
         // i18n[MemoryPanel.sessionInfo.toString2=Session: ID={0}, Alias={1}: created at {2}]
         return s_stringMgr.getString("MemoryPanel.sessionInfo.toString2", params);
      }
      else if (null != finalized)
      {
         // i18n[MemoryPanel.sessionInfo.toString3=Session: ID={0}, Alias={1}: created at {2}, closed at {3}, finalized at {4}]
         return s_stringMgr.getString("MemoryPanel.sessionInfo.toString3", params);
      }
      else
      {
         throw new IllegalStateException("Unknown Session state");
      }
   }

   public int compareTo(MemorySessionInfo other)
   {
      return Integer.valueOf(sessionId.toString()).compareTo(Integer.valueOf(other.sessionId.toString()));
   }
}
