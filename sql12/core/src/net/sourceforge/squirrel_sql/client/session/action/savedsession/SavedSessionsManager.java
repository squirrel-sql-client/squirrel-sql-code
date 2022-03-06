package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class SavedSessionsManager
{
   private final static ILogger s_log = LoggerController.createLogger(SavedSessionsManager.class);

   private SavedSessionsJsonBean _savedSessionsJsonBean = new SavedSessionsJsonBean();

   public boolean doesNameExist(String newSessionName)
   {
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
      return _savedSessionsJsonBean.isShowAliasChangeMsg();
   }

   public void setShowAliasChangeMsg(boolean b)
   {
      _savedSessionsJsonBean.setShowAliasChangeMsg(b);
   }

   public void beginStore(SavedSessionJsonBean savedSessionJsonBean)
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

      savedSessionJsonBean.getSessionSQLs().clear();
   }

   public void storeFile(SavedSessionJsonBean savedSessionJsonBean, SQLPanel sqlPanel, SqlPanelType sqlPanelType)
   {
      final SessionSqlJsonBean sqlJsonBean = new SessionSqlJsonBean();
      sqlJsonBean.setPanelType(sqlPanelType);

      if(null != sqlPanel.getSQLPanelAPI().getFileHandler().getFile())
      {
         sqlPanel.getSQLPanelAPI().getFileHandler().fileSave();
         sqlJsonBean.setExternalFilePath(sqlPanel.getSQLPanelAPI().getFileHandler().getFile().getAbsolutePath());
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
            Files.write(path, sqlPanel.getSQLPanelAPI().getBytesForSave());
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
      JsonMarshalUtil.writeObjectToFile(new ApplicationFiles().getSavedSessionsJsonFile(), _savedSessionsJsonBean);
   }
}
