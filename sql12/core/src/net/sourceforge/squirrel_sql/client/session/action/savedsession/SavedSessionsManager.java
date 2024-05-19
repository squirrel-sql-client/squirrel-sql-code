package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SavedSessionGrouped;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SavedSessionGroupsJsonBean;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SavedSessionsGroupJsonBean;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackUtil;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.GitHandler;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SavedSessionsManager
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionsManager.class);

   private final static ILogger s_log = LoggerController.createLogger(SavedSessionsManager.class);

   private SavedSessionsJsonBean _savedSessionsJsonBean = null;
   private SavedSessionGroupsJsonBean _savedSessionGroupsJsonBean = null;
   private ExecutorService _singleThreadJsonWriteExecutorService;

   public boolean doesNameExist(String newSessionName, SavedSessionJsonBean toExcludeFromSameNameCheck)
   {
      initSavedSessions();

      for (SavedSessionJsonBean savedSessionJsonBean : _savedSessionsJsonBean.getSavedSessionJsonBeans())
      {
         if(   savedSessionJsonBean != toExcludeFromSameNameCheck
            && StringUtilities.equalsRespectNullModuloEmptyAndWhiteSpace(savedSessionJsonBean.getName(), newSessionName, true))
         {
            return true;
         }
      }
      return false;
   }

   public boolean isShowAliasChangeMsg()
   {
      initSavedSessions();
      return _savedSessionsJsonBean.isShowAliasChangeMsg();
   }

   public void setShowAliasChangeMsg(boolean b)
   {
      initSavedSessions();
      _savedSessionsJsonBean.setShowAliasChangeMsg(b);
   }

   public SessionSaveProcessHandle beginStore(SavedSessionJsonBean savedSessionJsonBean, boolean clearSessionSqls)
   {
      initSavedSessions();

      SessionSaveProcessHandle ret = new SessionSaveProcessHandle(savedSessionJsonBean);

      if(clearSessionSqls)
      {
         savedSessionJsonBean.getSessionSQLs().clear();
      }

      return ret;
   }

   private void deleteAllInternallyStoredFiles(SavedSessionJsonBean savedSessionJsonBean)
   {
      for (SessionSqlJsonBean sessionSQL : savedSessionJsonBean.getSessionSQLs())
      {
         deleteInternallyStoredFile(sessionSQL);
      }
   }

   private void deleteInternallyStoredFile(SessionSqlJsonBean sessionSQL)
   {
      if(false == StringUtilities.isEmpty(sessionSQL.getInternalFileName()))
      {
         final File toDelete = new File(SavedSessionUtil.getSavedSessionsDir(), sessionSQL.getInternalFileName());
         try
         {
            Files.deleteIfExists(Path.of(toDelete.toURI()));
            GitHandler.commitDelete(toDelete);
         }
         catch (Exception e)
         {
            s_log.error("Failed to delete internal saved session file: " + toDelete.getAbsolutePath(), e);
         }
      }
   }

   public SessionSqlJsonBean storeFile(SavedSessionJsonBean savedSessionJsonBean, SQLPanelSaveInfo sqlPanelSaveInfo, boolean gitCommit)
   {
      final SessionSqlJsonBean sqlJsonBean = new SessionSqlJsonBean();
      sqlJsonBean.setPanelType(sqlPanelSaveInfo.getSqlPanelType());
      sqlJsonBean.setSqlTabTitleWithoutFile(sqlPanelSaveInfo.getSqlTabTitleWithoutFile());
      sqlJsonBean.setActiveSqlPanel(sqlPanelSaveInfo.isActiveSqlPanel());
      sqlJsonBean.setCaretPosition(sqlPanelSaveInfo.getCaretPosition());

      if(null != sqlPanelSaveInfo.getSqlPanel().getSQLPanelAPI().getFileHandler().getFile())
      {
         if(gitCommit)
         {
            ChangeTrackUtil.gitCommitSqlPanel(sqlPanelSaveInfo.getSqlPanel(), sqlPanelSaveInfo.isActiveSqlPanel());
         }
         else
         {
            sqlPanelSaveInfo.getSqlPanel().getSQLPanelAPI().getFileHandler().fileSave();
         }

         if(SavedSessionUtil.isInSavedSessionsDir(sqlPanelSaveInfo.getSqlPanel().getSQLPanelAPI().getFileHandler().getFile()))
         {
            sqlJsonBean.setInternalFileName(sqlPanelSaveInfo.getSqlPanel().getSQLPanelAPI().getFileHandler().getFile().getName());
         }
         else
         {
            sqlJsonBean.setExternalFilePath(sqlPanelSaveInfo.getSqlPanel().getSQLPanelAPI().getFileHandler().getFile().getAbsolutePath());
         }
      }
      else
      {
         // Editor contents were not yet saved. So save it as a new internal file.

         String internalFileNameOrig =
               StringUtilities.javaNormalize(savedSessionJsonBean.getName(), false)
               + "_" + savedSessionJsonBean.getSessionSQLs().size();

         int emergencyPosFix = 0;
         String internalFileName = internalFileNameOrig + ".sql";
         while (Files.exists(Path.of(new File(SavedSessionUtil.getSavedSessionsDir(), internalFileName).toURI())))
         {
            ++emergencyPosFix;
            internalFileName = internalFileNameOrig + "__" + emergencyPosFix + ".sql";
         }

         sqlJsonBean.setInternalFileName(internalFileName);

         final Path path = Path.of(SavedSessionUtil.getSavedSessionsDir().getAbsolutePath(), internalFileName);
         try
         {
            sqlPanelSaveInfo.getSqlPanel().getSQLPanelAPI().getFileHandler().fileSaveInitiallyTo(path.toFile());
            if(gitCommit)
            {
               ChangeTrackUtil.gitCommitSqlPanel(sqlPanelSaveInfo.getSqlPanel(), sqlPanelSaveInfo.isActiveSqlPanel());
            }
         }
         catch (Exception e)
         {
            s_log.error("Error while saving Session: Failed to internally save SQL editor contents to internal file : " + path.toFile().getAbsolutePath(), e);
         }
      }

      savedSessionJsonBean.getSessionSQLs().add(sqlJsonBean);
      return sqlJsonBean;
   }


   public void endStore(SavedSessionJsonBean savedSessionJsonBean, SavedSessionsGroupJsonBean group, SessionSaveProcessHandle sessionSaveProcessHandle)
   {
      if(    null != group
         && false == _savedSessionGroupsJsonBean.getGroups().stream().anyMatch(g -> StringUtils.equals(g.getGroupId(), group.getGroupId())))
      {
         _savedSessionGroupsJsonBean.getGroups().add(group);
      }

      sessionSaveProcessHandle.getToDelete(savedSessionJsonBean).forEach(b -> deleteInternallyStoredFile(b));

      _savedSessionsJsonBean.getSavedSessionJsonBeans().remove(savedSessionJsonBean);
      _savedSessionsJsonBean.getSavedSessionJsonBeans().add(0, savedSessionJsonBean);

      saveJsonBeans();
   }

   private void saveJsonBeans()
   {
      consolidateGroups();
      _singleThreadJsonWriteExecutorService.submit(() -> saveFiles());
   }

   private void consolidateGroups()
   {
      initSavedSessions();
      Set<String> groupIdsInSavedSessions = _savedSessionsJsonBean.getSavedSessionJsonBeans()
                                                                  .stream().filter(s -> false == StringUtilities.isEmpty(s.getGroupId(), true))
                                                                  .map(s -> s.getGroupId()).collect(Collectors.toSet());

      Set<String> uniqueGroupIds = new HashSet<>();
      ArrayList<SavedSessionsGroupJsonBean> groupsToRemove = new ArrayList<>();
      for(SavedSessionsGroupJsonBean group : _savedSessionGroupsJsonBean.getGroups())
      {
         if(false == groupIdsInSavedSessions.contains(group.getGroupId()))
         {
            groupsToRemove.add(group);
            continue;
         }

         if(uniqueGroupIds.contains(group.getGroupId())) // Not supposed to happen
         {
            groupsToRemove.add(group);
         }
         uniqueGroupIds.add(group.getGroupId());
      }

      _savedSessionGroupsJsonBean.getGroups().removeAll(groupsToRemove);
   }

   private void saveFiles()
   {
      JsonMarshalUtil.writeObjectToFile(new ApplicationFiles().getSavedSessionsJsonFile(), _savedSessionsJsonBean);
      JsonMarshalUtil.writeObjectToFile(new ApplicationFiles().getSavedSessionGroupsJsonFile(), _savedSessionGroupsJsonBean);
   }

   public List<SavedSessionGrouped> getSavedSessionsGrouped()
   {
      initSavedSessions();

      List<SavedSessionGrouped> ret = new ArrayList<>();

      for (SavedSessionJsonBean savedSessionJsonBean : _savedSessionsJsonBean.getSavedSessionJsonBeans())
      {
         if(StringUtilities.isEmpty(savedSessionJsonBean.getGroupId(), true))
         {
            ret.add(new SavedSessionGrouped(savedSessionJsonBean));
         }
         else
         {
            SavedSessionsGroupJsonBean group = getGroup(savedSessionJsonBean.getGroupId());
            SavedSessionGrouped savedSessionGrouped = ret.stream().filter(ssg -> matchGroup(ssg, group)).findFirst().orElse(null);

            if (null != savedSessionGrouped)
            {
               savedSessionGrouped.addSavedSession(savedSessionJsonBean);
            }
            else if(null != group)
            {
               ret.add(new SavedSessionGrouped(savedSessionJsonBean, group));
            }
         }
      }

      return ret;
   }

   public SavedSessionGrouped getSavedSessionGrouped(String groupId)
   {
      return getSavedSessionsGrouped().stream()
                                      .filter(g -> null != g.getGroup() && StringUtils.equals(g.getGroup().getGroupId(), groupId))
                                      .findFirst().orElseThrow(() -> new IllegalArgumentException("Failed to find group by groupId=" + groupId));
   }

   public SavedSessionGrouped getSavedSessionGrouped(SavedSessionJsonBean savedSession)
   {
      if (StringUtilities.isEmpty(savedSession.getGroupId(), true))
      {
         return SavedSessionGrouped.of(savedSession);
      }
      else
      {
         return getSavedSessionGrouped(savedSession.getGroupId());
      }
   }



   private static boolean matchGroup(SavedSessionGrouped savedSessionGrouped, SavedSessionsGroupJsonBean savedSessionsGroupJsonBean)
   {
      if(null == savedSessionGrouped || false == savedSessionGrouped.isGroup() || null == savedSessionsGroupJsonBean)
      {
         return false;
      }

      return StringUtils.equals(savedSessionGrouped.getGroup().getGroupId(), savedSessionsGroupJsonBean.getGroupId());
   }

   private void initSavedSessions()
   {
      if(null == _savedSessionsJsonBean)
      {
         final File savedSessionsJsonFile = new ApplicationFiles().getSavedSessionsJsonFile();
         if(savedSessionsJsonFile.exists())
         {
            _savedSessionsJsonBean = JsonMarshalUtil.readObjectFromFileSave(savedSessionsJsonFile, SavedSessionsJsonBean.class, new SavedSessionsJsonBean());
         }
         else
         {
            _savedSessionsJsonBean = new SavedSessionsJsonBean();
         }

         final File jsonFile = new ApplicationFiles().getSavedSessionGroupsJsonFile();
         if(jsonFile.exists())
         {
            _savedSessionGroupsJsonBean = JsonMarshalUtil.readObjectFromFileSave(jsonFile, SavedSessionGroupsJsonBean.class, new SavedSessionGroupsJsonBean());
         }
         else
         {
            _savedSessionGroupsJsonBean = new SavedSessionGroupsJsonBean();
         }

         _singleThreadJsonWriteExecutorService = Executors.newSingleThreadExecutor();

         checkMaxSavedSessionLimit();

      }
   }

   private void checkMaxSavedSessionLimit()
   {
      List<SavedSessionGrouped> allSavedSessionsGrouped = getSavedSessionsGrouped();

      if(   0 == _savedSessionsJsonBean.getMaxNumberSavedSessions()
         || allSavedSessionsGrouped.size() <= _savedSessionsJsonBean.getMaxNumberSavedSessions())
      {
         return;
      }

      final List<SavedSessionGrouped> toDel = allSavedSessionsGrouped.subList(_savedSessionsJsonBean.getMaxNumberSavedSessions(), allSavedSessionsGrouped.size());

      final String msg = s_stringMgr.getString("SavedSessionsManager.maximum.number.saved.sessions.exceeded", _savedSessionsJsonBean.getMaxNumberSavedSessions(), toDel.size());
      Main.getApplication().getMessageHandler().showMessage(msg);
      s_log.info(msg);

      delete(toDel);
   }

   public void moveToTop(SavedSessionGrouped savedSessionGrouped)
   {
      initSavedSessions();

      for (SavedSessionJsonBean savedSession : savedSessionGrouped.getSavedSessions())
      {
         _savedSessionsJsonBean.getSavedSessionJsonBeans().remove(savedSession);
         _savedSessionsJsonBean.getSavedSessionJsonBeans().add(0, savedSession);
      }
      saveJsonBeans();
   }

   public List<ISession> getOpenSessionsForSavedSessionsGrouped(List<SavedSessionGrouped> savedSessionsGrouped)
   {
      initSavedSessions();

      List<ISession> ret = new ArrayList<>();

      for (SavedSessionGrouped savedSessionGrouped : savedSessionsGrouped)
      {
         ret.addAll(Main.getApplication().getSessionManager().getOpenSessions().stream().filter(s -> savedSessionGrouped.getSavedSessions().contains(s.getSavedSession())).collect(Collectors.toList()));
      }

      return ret;
   }

   public void delete(SavedSessionGrouped toDel)
   {
      delete(List.of(toDel));
   }

   public void delete(List<SavedSessionGrouped> toDelList)
   {
      initSavedSessions();

      for (ISession session : Main.getApplication().getSessionManager().getOpenSessions())
      {
         for (SavedSessionGrouped toDel : toDelList)
         {
            if(toDel.containsSavedSession(session.getSavedSession()))
            {
               SavedSessionUtil.initSessionWithSavedSession(null, session);
            }
         }
      }

      for (SavedSessionGrouped toDel : toDelList)
      {
         toDel.getSavedSessions().forEach(savedSess -> deleteAllInternallyStoredFiles(savedSess));
      }

      for (SavedSessionGrouped toDel : toDelList)
      {
         _savedSessionsJsonBean.getSavedSessionJsonBeans().removeAll(toDel.getSavedSessions());
      }

      for(SavedSessionGrouped toDel : toDelList)
      {
         if(toDel.isGroup())
         {
            _savedSessionGroupsJsonBean.getGroups().removeIf(g -> StringUtils.equals(g.getGroupId(), toDel.getGroup().getGroupId()));
         }
      }

      saveJsonBeans();
   }

   public int getMaxNumberSavedSessions()
   {
      return _savedSessionsJsonBean.getMaxNumberSavedSessions();
   }

   public void setMaxNumberSavedSessions(int maxNumberSavedSessions)
   {
      _savedSessionsJsonBean.setMaxNumberSavedSessions(maxNumberSavedSessions);
      saveJsonBeans();
   }

   public SavedSessionsGroupJsonBean getGroup(String groupId)
   {
      initSavedSessions();
      return _savedSessionGroupsJsonBean.getGroups().stream().filter(g -> StringUtils.equals(g.getGroupId(), groupId)).findFirst().orElse(null);
   }

   public void removeGroupIfEmpty(String groupId)
   {
      initSavedSessions();
      boolean save = false;
      if(false == _savedSessionsJsonBean.getSavedSessionJsonBeans().stream().anyMatch(s -> StringUtils.equals(s.getGroupId(),groupId)))
      {
         if(_savedSessionGroupsJsonBean.getGroups().removeIf(g -> StringUtils.equals(g.getGroupId(), groupId)))
         {
            save = true;
         }
      }

      if(save)
      {
         saveJsonBeans();
      }
   }
}
