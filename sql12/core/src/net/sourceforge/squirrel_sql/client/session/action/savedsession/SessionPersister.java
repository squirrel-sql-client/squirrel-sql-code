package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SavedSessionsGroupJsonBean;
import net.sourceforge.squirrel_sql.fw.gui.DontShowAgainDialog;
import net.sourceforge.squirrel_sql.fw.gui.DontShowAgainResult;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.util.List;

public class SessionPersister
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionPersister.class);


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

   public static SaveSessionResult saveSessionGroup(ISession sess, SavedSessionsGroupJsonBean group, boolean gitCommit, boolean activeSessionInGroup)
   {
      return _saveSession(sess,
                          false, // As groups can only be opened on the same combination of Aliases
                          gitCommit,
                          group, activeSessionInGroup);
   }

   private static SaveSessionResult _saveSession(ISession session, boolean allowAliasChangeMsg, boolean gitCommit, SavedSessionsGroupJsonBean group, boolean activeSessionInGroup)
   {
      SavedSessionJsonBean savedSessionJsonBean = session.getSavedSession();

      final SQLAlias alias = session.getAlias();
      final SavedSessionsManager savedSessionsManager = Main.getApplication().getSavedSessionsManager();
      if(null == savedSessionJsonBean && null == group)
      {
         String  savedSessionNameTemplate = SavedSessionUtil.createSavedSessionNameTemplate(session);

         final SessionSaveDlg sessionSaveDlg = new SessionSaveDlg(GUIUtils.getOwningFrame(session.getSessionPanel()), savedSessionNameTemplate);

         if(false == sessionSaveDlg.isOk())
         {
            return SaveSessionResult.ofUserCanceledSavingSession();
         }

         savedSessionJsonBean = new SavedSessionJsonBean();
         savedSessionJsonBean.setName(sessionSaveDlg.getSavedSessionName());
         savedSessionJsonBean.setDefaultAliasIdString(alias.getIdentifier().toString());
         savedSessionJsonBean.setAliasNameForDebug(alias.getName());
      }
      else if(null == savedSessionJsonBean && null != group)
      {
         savedSessionJsonBean = new SavedSessionJsonBean();
         savedSessionJsonBean.setGroupId(group.getGroupId());
         savedSessionJsonBean.setName("<NameNotDisplayGroupSession_groupId=" + group.getGroupId() + ">");
         savedSessionJsonBean.setDefaultAliasIdString(alias.getIdentifier().toString());
         savedSessionJsonBean.setAliasNameForDebug(alias.getName());
      }
      else if(allowAliasChangeMsg
              && null == group // Cannot change Aliases of SavedSessions in a Session group
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

      List<SQLPanelSaveInfo> sqlPanelSaveInfoList = SavedSessionUtil.getAllSQLPanelsOrderedAndTyped(session);

      SessionSaveProcessHandle sessionSaveProcessHandle = savedSessionsManager.beginStore(savedSessionJsonBean);
      SQLEditorActivator sqlEditorActivator = new SQLEditorActivator();
      for (SQLPanelSaveInfo sqlPanelSaveInfo : sqlPanelSaveInfoList)
      {
         SessionSqlJsonBean sessionSqlJsonBean = savedSessionsManager.storeFile(savedSessionJsonBean, sqlPanelSaveInfo, gitCommit);

         // The SQLEditorActivator is needed because externally saving a file selects the according SQL Tab.
         // But we want the active SQL-Editor to remain the same after saving the Session.
         sqlEditorActivator.prepareToActivateSQLPanelSaveInfo(sqlPanelSaveInfo, sessionSqlJsonBean);
      }

      savedSessionJsonBean.setActiveSessionInGroup(activeSessionInGroup);

      savedSessionsManager.endStore(savedSessionJsonBean, group, sessionSaveProcessHandle);

      Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SessionPersister.saved.session.msg", savedSessionJsonBean.getName()));

      SavedSessionUtil.initSessionWithSavedSession(savedSessionJsonBean, session);

      sqlEditorActivator.activate();

      Main.getApplication().getMainFrame().getMainFrameTitleHandler().updateMainFrameTitle();

      return SaveSessionResult.ofSessionWasSaved(sqlEditorActivator);
   }
}
