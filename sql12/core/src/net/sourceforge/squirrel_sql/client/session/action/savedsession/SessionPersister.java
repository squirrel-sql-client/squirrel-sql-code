package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SavedSessionsGroupJsonBean;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SavedSessionsGroupUtil;
import net.sourceforge.squirrel_sql.fw.gui.DontShowAgainDialog;
import net.sourceforge.squirrel_sql.fw.gui.DontShowAgainResult;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.util.List;

public class SessionPersister
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionPersister.class);
   public static final String GROUP_SAVED_SESSION_NAME_DUMMY = "<<<SAVED_SESSION_IN_GROUP_NAME_DUMMY>>>";


   public static boolean saveSession(ISession session)
   {
      return saveSession(session, true);
   }

   public static boolean saveSession(ISession session, boolean allowAliasChangeMsg)
   {
      return saveSession(session, allowAliasChangeMsg, false);
   }

   public static boolean saveSession(ISession session, boolean allowAliasChangeMsg, boolean gitCommit)
   {
      return _saveSession(session, allowAliasChangeMsg, gitCommit, null, false).isSessionWasSaved();
   }

   public static SaveSessionResult saveSessionInGroup(ISession sess, SavedSessionsGroupJsonBean group, boolean gitCommit, boolean activeSessionInGroup)
   {
      return _saveSession(sess,
                          false, // As groups can only be opened on the same combination of Aliases
                          gitCommit,
                          group,
                          activeSessionInGroup);
   }

   private static SaveSessionResult _saveSession(ISession session, boolean allowAliasChangeMsg, boolean gitCommit, SavedSessionsGroupJsonBean group, boolean activeSessionInGroup)
   {
      SavedSessionJsonBean savedSessionJsonBean = session.getSavedSession();

      final SQLAlias alias = session.getAlias();
      final SavedSessionsManager savedSessionsManager = Main.getApplication().getSavedSessionsManager();

      if(null == group) // Standalone Saved Session
      {
         if(null == savedSessionJsonBean)
         {
            String  savedSessionNameTemplate = SavedSessionUtil.createSavedSessionNameTemplate(session);

            String newName =
                  SavedSessionUtil.showEditSavedSessionNameDialog(GUIUtils.getOwningFrame(session.getSessionPanel()), savedSessionNameTemplate);

            if(null == newName)
            {
               return SaveSessionResult.ofUserCanceledSavingSession();
            }

            savedSessionJsonBean = new SavedSessionJsonBean();
            savedSessionJsonBean.setName(newName);
            savedSessionJsonBean.setDefaultAliasIdString(alias.getIdentifier().toString());
            savedSessionJsonBean.setAliasNameForDebug(alias.getName());
         }
         else if(allowAliasChangeMsg
                 && false == alias.getIdentifier().toString().equals(savedSessionJsonBean.getDefaultAliasIdString())
                 && savedSessionsManager.isShowAliasChangeMsg())
         {
            final DontShowAgainDialog dlgMsg = new DontShowAgainDialog(GUIUtils.getOwningFrame(session.getSessionPanel()),
                                                                       s_stringMgr.getString("SessionPersister.change.default.alias.to", savedSessionJsonBean.getName(), alias.getName(), alias.getUrl()),
                                                                       s_stringMgr.getString("SessionPersister.change.default.alias.how.to"));

            dlgMsg.setTitle(s_stringMgr.getString("SessionPersister.change.default.alias.title"));

            final DontShowAgainResult res = dlgMsg.showAndGetResult("SessionPersister.change.alias", 600, 250);
            savedSessionsManager.setShowAliasChangeMsg(false == res.isDontShowAgain());

            if(res.isCancel())
            {
               return SaveSessionResult.ofUserCanceledSavingSession();
            }

            if(res.isYes())
            {
               savedSessionJsonBean.setDefaultAliasIdString(alias.getIdentifier().toString());
               savedSessionJsonBean.setAliasNameForDebug(alias.getName());
            }
         }
      }
      else // Saved Session in group
      {
         if(null == savedSessionJsonBean)
         {
            savedSessionJsonBean = new SavedSessionJsonBean();

            // For an existing Saved Session we keep the name as a template in case it's later moved out of the group again.
            savedSessionJsonBean.setName(GROUP_SAVED_SESSION_NAME_DUMMY + "_groupId_" + group.getGroupId());
         }
         savedSessionJsonBean.setGroupId(group.getGroupId());
         savedSessionJsonBean.setDefaultAliasIdString(alias.getIdentifier().toString());
         savedSessionJsonBean.setAliasNameForDebug(alias.getName());
      }

      List<SQLPanelSaveInfo> sqlPanelSaveInfoList = SavedSessionUtil.getAllSQLPanelsOrderedAndTyped(session);

      SessionSaveProcessHandle sessionSaveProcessHandle = savedSessionsManager.beginStore(savedSessionJsonBean, true);
      SQLEditorActivator sqlEditorActivator = new SQLEditorActivator();
      for (SQLPanelSaveInfo sqlPanelSaveInfo : sqlPanelSaveInfoList)
      {
         SessionSqlJsonBean sessionSqlJsonBean = savedSessionsManager.storeFile(savedSessionJsonBean, sqlPanelSaveInfo, gitCommit);

         // The SQLEditorActivator is needed because externally saving a file selects the according SQL Tab.
         // But we want the active SQL-Editor to remain the same after saving the Session.
         sqlEditorActivator.prepareToActivateSQLPanelSaveInfo(sqlPanelSaveInfo, sessionSqlJsonBean, SavedSessionsGroupUtil.shouldForceToFocusActiveSqlEditor(group, activeSessionInGroup));
      }

      savedSessionJsonBean.setActiveSessionInGroup(activeSessionInGroup);

      savedSessionsManager.endStore(savedSessionJsonBean, group, sessionSaveProcessHandle);

      Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SessionPersister.saved.session.msg", savedSessionJsonBean.getName()));

      SavedSessionUtil.initSessionWithSavedSession(savedSessionJsonBean, session);

      sqlEditorActivator.activate();

      Main.getApplication().getMainFrame().getMainFrameTitleHandler().updateMainFrameTitle();

      return SaveSessionResult.ofSessionWasSaved(sqlEditorActivator);
   }

   public static void moveSavedSessionFromGroupToStandalone(SavedSessionJsonBean savedSessionToMove, String newSavedSessionName)
   {
      String groupId = savedSessionToMove.getGroupId();
      savedSessionToMove.setName(newSavedSessionName);
      savedSessionToMove.setGroupId(null);
      SessionSaveProcessHandle sessionSaveProcessHandle = Main.getApplication().getSavedSessionsManager().beginStore(savedSessionToMove, false);
      Main.getApplication().getSavedSessionsManager().endStore(savedSessionToMove, null, sessionSaveProcessHandle);
      Main.getApplication().getSavedSessionsManager().removeGroupIfEmpty(groupId);
   }
}
