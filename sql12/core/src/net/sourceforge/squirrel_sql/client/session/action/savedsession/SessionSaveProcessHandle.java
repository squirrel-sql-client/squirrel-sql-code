package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SessionSaveProcessHandle
{
   private final List<SessionSqlJsonBean> _internalFileBeansBeforeStore = new ArrayList<>();

   public SessionSaveProcessHandle(SavedSessionJsonBean savedSessionJsonBean)
   {
      for (SessionSqlJsonBean sessionSQL : savedSessionJsonBean.getSessionSQLs())
      {
         if(false == StringUtilities.isEmpty(sessionSQL.getInternalFileName(), true))
         {
            _internalFileBeansBeforeStore.add(Utilities.cloneObject(sessionSQL, SessionSaveProcessHandle.class.getClassLoader()));
         }
      }
   }

   public List<SessionSqlJsonBean> getToDelete(SavedSessionJsonBean updatedSavedSessionJsonBean)
   {
      List<SessionSqlJsonBean> ret = new ArrayList<>();

      for (SessionSqlJsonBean oldSessionSQL : _internalFileBeansBeforeStore)
      {
         boolean found = false;
         for (SessionSqlJsonBean newSessionSQL : updatedSavedSessionJsonBean.getSessionSQLs())
         {
            if(StringUtils.equalsIgnoreCase(newSessionSQL.getInternalFileName(), oldSessionSQL.getInternalFileName()))
            {
               found = true;
               break;
            }
         }

         if(false == found)
         {
            ret.add(oldSessionSQL);
         }
      }

      return ret;
   }
}
