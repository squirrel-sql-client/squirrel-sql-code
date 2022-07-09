package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackUtil;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.GitHandler;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SavedSessionsManager
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionsManager.class);

   private final static ILogger s_log = LoggerController.createLogger(SavedSessionsManager.class);

   private SavedSessionsJsonBean _savedSessionsJsonBean = null;
   private ExecutorService _singleThreadJsonWriteExecutorService;

   public boolean doesNameExist(String newSessionName)
   {
      initSavedSessions();

      for (SavedSessionJsonBean savedSessionJsonBean : _savedSessionsJsonBean.getSavedSessionJsonBeans())
      {
         if( StringUtilities.equalsRespectNullModuloEmptyAndWhiteSpace(savedSessionJsonBean.getName(), newSessionName, true) )
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

   public SessionSaveProcessHandle beginStore(SavedSessionJsonBean savedSessionJsonBean)
   {
      initSavedSessions();

      SessionSaveProcessHandle ret = new SessionSaveProcessHandle(savedSessionJsonBean);

      savedSessionJsonBean.getSessionSQLs().clear();

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


   public void endStore(SavedSessionJsonBean savedSessionJsonBean, SessionSaveProcessHandle sessionSaveProcessHandle)
   {
      sessionSaveProcessHandle.getToDelete(savedSessionJsonBean).forEach(b -> deleteInternallyStoredFile(b));

      _savedSessionsJsonBean.getSavedSessionJsonBeans().remove(savedSessionJsonBean);
      _savedSessionsJsonBean.getSavedSessionJsonBeans().add(0, savedSessionJsonBean);

      saveJsonBean();
   }

   private void saveJsonBean()
   {
      _singleThreadJsonWriteExecutorService.submit(() -> JsonMarshalUtil.writeObjectToFile(new ApplicationFiles().getSavedSessionsJsonFile(), _savedSessionsJsonBean));
   }

   public List<SavedSessionJsonBean> getSavedSessions()
   {
      initSavedSessions();
      return _savedSessionsJsonBean.getSavedSessionJsonBeans();
   }

   private void initSavedSessions()
   {
      if(null == _savedSessionsJsonBean)
      {
         final File jsonFile = new ApplicationFiles().getSavedSessionsJsonFile();
         if(jsonFile.exists())
         {
            _savedSessionsJsonBean = JsonMarshalUtil.readObjectFromFile(jsonFile, SavedSessionsJsonBean.class);
         }
         else
         {
            _savedSessionsJsonBean = new SavedSessionsJsonBean();
         }

         _singleThreadJsonWriteExecutorService = Executors.newSingleThreadExecutor();

         checkMaxSavedSessionLimit();

      }
   }

   private void checkMaxSavedSessionLimit()
   {
      if(   0 == _savedSessionsJsonBean.getMaxNumberSavedSessions()
         || _savedSessionsJsonBean.getSavedSessionJsonBeans().size() <= _savedSessionsJsonBean.getMaxNumberSavedSessions())
      {
         return;
      }

      final List<SavedSessionJsonBean> toDel =
            _savedSessionsJsonBean.getSavedSessionJsonBeans().subList(_savedSessionsJsonBean.getMaxNumberSavedSessions(), _savedSessionsJsonBean.getSavedSessionJsonBeans().size());

      final String msg = s_stringMgr.getString("SavedSessionsManager.maximum.number.saved.sessions.exceeded", _savedSessionsJsonBean.getMaxNumberSavedSessions(), toDel.size());
      Main.getApplication().getMessageHandler().showMessage(msg);
      s_log.info(msg);

      delete(toDel);
   }

   public void moveToTop(SavedSessionJsonBean savedSession)
   {
      initSavedSessions();

      _savedSessionsJsonBean.getSavedSessionJsonBeans().remove(savedSession);
      _savedSessionsJsonBean.getSavedSessionJsonBeans().add(0, savedSession);

      saveJsonBean();
   }

   public List<ISession> getOpenSessionsOfList(List<SavedSessionJsonBean> savedSessions)
   {
      initSavedSessions();

      return Main.getApplication().getSessionManager().getOpenSessions().stream().filter(s -> savedSessions.contains(s.getSavedSession())).collect(Collectors.toList());
   }

   public void delete(List<SavedSessionJsonBean> toDel)
   {
      initSavedSessions();

      for (ISession session : Main.getApplication().getSessionManager().getOpenSessions())
      {
         if(toDel.contains(session.getSavedSession()))
         {
            SavedSessionUtil.initSessionWithSavedSession(null, session);
         }
      }

      for (SavedSessionJsonBean savedSessionJsonBean : toDel)
      {
         deleteAllInternallyStoredFiles(savedSessionJsonBean);
      }
      _savedSessionsJsonBean.getSavedSessionJsonBeans().removeAll(toDel);

      saveJsonBean();
   }

   public int getMaxNumberSavedSessions()
   {
      return _savedSessionsJsonBean.getMaxNumberSavedSessions();
   }

   public void setMaxNumberSavedSessions(int maxNumberSavedSessions)
   {
      _savedSessionsJsonBean.setMaxNumberSavedSessions(maxNumberSavedSessions);
      saveJsonBean();
   }
}
