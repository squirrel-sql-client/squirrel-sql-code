package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SavedSessionsManager
{
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

   public void beginStore(SavedSessionJsonBean savedSessionJsonBean)
   {
      initSavedSessions();
      deleteInternallyStoredFiles(savedSessionJsonBean);

      savedSessionJsonBean.getSessionSQLs().clear();
   }

   private void deleteInternallyStoredFiles(SavedSessionJsonBean savedSessionJsonBean)
   {
      for (SessionSqlJsonBean sessionSQL : savedSessionJsonBean.getSessionSQLs())
      {
         if(false == StringUtilities.isEmpty(sessionSQL.getInternalFileName()))
         {
            final File toDelete = new File(new ApplicationFiles().getSavedSessionsDir(), sessionSQL.getInternalFileName());
            try
            {
               Files.deleteIfExists(Path.of(toDelete.toURI()));
            }
            catch (Exception e)
            {
               s_log.error("Failed to delete internal saved session file: " + toDelete.getAbsolutePath(), e);
            }
         }
      }
   }

   public void storeFile(SavedSessionJsonBean savedSessionJsonBean, SQLPanelSaveInfo sqlPanelSaveInfo)
   {
      final SessionSqlJsonBean sqlJsonBean = new SessionSqlJsonBean();
      sqlJsonBean.setPanelType(sqlPanelSaveInfo.getSqlPanelType());
      sqlJsonBean.setActiveSqlPanel(sqlPanelSaveInfo.isActiveSqlPanel());
      sqlJsonBean.setCaretPosition(sqlPanelSaveInfo.getCaretPosition());

      if(null != sqlPanelSaveInfo.getSqlPanel().getSQLPanelAPI().getFileHandler().getFile())
      {
         sqlPanelSaveInfo.getSqlPanel().getSQLPanelAPI().getFileHandler().fileSave();
         sqlJsonBean.setExternalFilePath(sqlPanelSaveInfo.getSqlPanel().getSQLPanelAPI().getFileHandler().getFile().getAbsolutePath());
      }
      else
      {
         String internalFileNameOrig =
               StringUtilities.javaNormalize(savedSessionJsonBean.getName(), false)
               + "_" + savedSessionJsonBean.getSessionSQLs().size();

         int emergencyPosFix = 0;
         String internalFileName = internalFileNameOrig + ".sql";
         while (Files.exists(Path.of(new File(new ApplicationFiles().getSavedSessionsDir(), internalFileName).toURI())))
         {
            ++emergencyPosFix;
            internalFileName = internalFileNameOrig + "__" + emergencyPosFix + ".sql";
         }

         sqlJsonBean.setInternalFileName(internalFileName);

         final Path path = Path.of(new ApplicationFiles().getSavedSessionsDir().getAbsolutePath(), internalFileName);
         try
         {
            Files.write(path, sqlPanelSaveInfo.getSqlPanel().getSQLPanelAPI().getBytesForSave());
         }
         catch (Exception e)
         {
            s_log.error("Error while saving Session: Failed to internally save SQL editor contents to internal file : " + path.toFile().getAbsolutePath(), e);
         }

      }

      savedSessionJsonBean.getSessionSQLs().add(sqlJsonBean);
   }

   public void endStore(SavedSessionJsonBean savedSessionJsonBean)
   {
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

      }
   }

   public void moveToTop(SavedSessionJsonBean savedSession)
   {
      initSavedSessions();

      _savedSessionsJsonBean.getSavedSessionJsonBeans().remove(savedSession);
      _savedSessionsJsonBean.getSavedSessionJsonBeans().add(0, savedSession);

      saveJsonBean();
   }

   public boolean areUsedInOpenSessions(List<SavedSessionJsonBean> savedSessions)
   {
      initSavedSessions();

      return Main.getApplication().getSessionManager().getOpenSessions().stream().anyMatch(s -> savedSessions.contains(s.getSavedSession()));
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
         deleteInternallyStoredFiles(savedSessionJsonBean);
      }
      _savedSessionsJsonBean.getSavedSessionJsonBeans().removeAll(toDel);

      saveJsonBean();
   }
}
