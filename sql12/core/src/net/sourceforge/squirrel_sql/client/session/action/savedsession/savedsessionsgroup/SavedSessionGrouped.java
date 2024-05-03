package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionJsonBean;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionSqlJsonBean;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is to help make UI handle Saved Sessions and Saved Session Groups correctly.
 */
public class SavedSessionGrouped
{
   private final SavedSessionsGroupJsonBean _group;

   /**
    * If the {@link #_group} is null then this list has exactly one element.
    */
   private final List<SavedSessionJsonBean> _savedSessionJsonBeans = new ArrayList<>();

   public SavedSessionGrouped(SavedSessionJsonBean savedSessionJsonBean)
   {
      this(savedSessionJsonBean, null);
   }

   public SavedSessionGrouped(SavedSessionJsonBean savedSessionJsonBean, SavedSessionsGroupJsonBean group)
   {
      if(false == StringUtilities.isEmpty(savedSessionJsonBean.getGroupId(), true) && null == group)
      {
         throw new IllegalArgumentException("If savedSessionJsonBean has a groupId the group parameter must not be null.");
      }

      _savedSessionJsonBeans.add(savedSessionJsonBean);
      _group = group;
   }

   public static SavedSessionGrouped of(SavedSessionJsonBean savedSession)
   {
      return new SavedSessionGrouped(savedSession);
   }

   public SavedSessionsGroupJsonBean getGroup()
   {
      return _group;
   }

   public void addSavedSession(SavedSessionJsonBean savedSessionJsonBean)
   {
      _savedSessionJsonBeans.add(savedSessionJsonBean);
   }

   public String getName()
   {
      if (null != _group)
      {
         return _group.getGroupName();
      }
      else
      {
         return _savedSessionJsonBeans.get(0).getName();
      }
   }

   public void setName(String name)
   {
      if (null != _group)
      {
         _group.setGroupName(name);
      }
      else
      {
         _savedSessionJsonBeans.get(0).setName(name);
      }
   }


   public boolean isGroup()
   {
      return null != _group;
   }

   public SavedSessionJsonBean getNoGroupedSavedSession()
   {
      if (false == isGroup())
      {
         return _savedSessionJsonBeans.get(0);
      }

      throw new IllegalStateException("This is a Saved Session Group");
   }

   public List<SavedSessionJsonBean> getSavedSessions()
   {
      return _savedSessionJsonBeans;
   }

   public boolean matchesFilterText(String filterText)
   {
      if (null != _group)
      {
         if(StringUtils.containsIgnoreCase(_group.getGroupName(), filterText))
         {
            return true;
         }
      }

      for (SavedSessionJsonBean savedSessionJsonBean : _savedSessionJsonBeans)
      {
         // Check Saved Session name when its no group only.
         if(null == _group && StringUtils.containsIgnoreCase(savedSessionJsonBean.getName(), filterText))
         {
            return true;
         }

         if(matchesSavedSessionContents(savedSessionJsonBean , filterText))
         {
            return true;
         }
      }

      return false;
   }

   private boolean matchesSavedSessionContents(SavedSessionJsonBean savedSession, String filterText)
   {

      if(false == StringUtilities.isEmpty(savedSession.getDefaultAliasIdString(), true))
      {
         final SQLAlias alias = Main.getApplication().getAliasesAndDriversManager().getAlias(new UidIdentifier(savedSession.getDefaultAliasIdString()));

         if(null != alias )
         {
            if((null != alias.getName() && StringUtils.containsIgnoreCase(alias.getName(), filterText))
                  || (null != alias.getUrl() && StringUtils.containsIgnoreCase(alias.getUrl(), filterText))
                  || (null != alias.getUserName() && StringUtils.containsIgnoreCase(alias.getUserName(), filterText))
            )
            {
               return true;
            }
         }
      }

      for (SessionSqlJsonBean sessionSQL : savedSession.getSessionSQLs())
      {
         //if(false == StringUtilities.isEmpty(sessionSQL.getInternalFileName(), true))
         //{
         //   if(StringUtils.containsIgnoreCase(sessionSQL.getInternalFileName(), filterText))
         //   {
         //      return true;
         //   }
         //}

         if(false == StringUtilities.isEmpty(sessionSQL.getExternalFilePath(), true))
         {
            if(StringUtils.containsIgnoreCase(sessionSQL.getExternalFilePath(), filterText))
            {
               return true;
            }
         }
      }

      return false;
   }

   public boolean contains(SavedSessionJsonBean savedSession)
   {
      return _savedSessionJsonBeans.contains(savedSession);
   }

   @Override
   public String toString()
   {
      return getName();
   }
}
