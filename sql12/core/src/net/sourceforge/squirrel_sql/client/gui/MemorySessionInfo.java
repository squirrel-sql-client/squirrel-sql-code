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
   int countFinalizedResultTabsWhileSessionOpen;

   public String toString()
   {
      DateFormat df = DateFormat.getInstance();

      Object[] params = new Object[]
            {
                  sessionId,
                  aliasName,
                  df.format(created),
                  countFinalizedResultTabsWhileSessionOpen,
                  null == closed ? "" : df.format(closed),
                  null == finalized ? "" : df.format(finalized),
            };

      if (null != closed && null == finalized)
      {
         return s_stringMgr.getString("MemoryPanel.sessionInfo.toString1.incl.res.tab", params);
      }
      else if (null == closed)
      {
         return s_stringMgr.getString("MemoryPanel.sessionInfo.toString2.incl.res.tab", params);
      }
      else if (null != finalized)
      {
         return s_stringMgr.getString("MemoryPanel.sessionInfo.toString3.incl.res.tab", params);
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
