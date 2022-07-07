package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.DontShowAgainDialog;
import net.sourceforge.squirrel_sql.fw.gui.DontShowAgainResult;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.text.SimpleDateFormat;
import java.util.Date;
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
      SavedSessionJsonBean savedSessionJsonBean = session.getSavedSession();

      final ISQLAliasExt alias = session.getAlias();
      final SavedSessionsManager savedSessionsManager = Main.getApplication().getSavedSessionsManager();
      String savedSessionNameTemplate = null;
      if(null == savedSessionJsonBean)
      {
         SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         savedSessionNameTemplate = alias.getName() + " | " + df.format(new Date());

         final SessionSaveDlg sessionSaveDlg = new SessionSaveDlg(GUIUtils.getOwningFrame(session.getSessionPanel()), savedSessionNameTemplate);

         if(false == sessionSaveDlg.isOk())
         {
            return false;
         }

         savedSessionJsonBean = new SavedSessionJsonBean();
         savedSessionJsonBean.setName(sessionSaveDlg.getSavedSessionName());
         savedSessionJsonBean.setDefaultAliasIdString(alias.getIdentifier().toString());
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
            return false;
         }

         if(res.isYes())
         {
            savedSessionJsonBean.setDefaultAliasIdString(alias.getIdentifier().toString());
         }
      }

      List<SQLPanelSaveInfo> sqlPanelSaveInfoList = SavedSessionUtil.getAllSQLPanelsOrderedAndTyped(session);

      SessionSaveProcessHandle sessionSaveProcessHandle = savedSessionsManager.beginStore(savedSessionJsonBean);
      SQLEditorActivator sqlEditorActivator = new SQLEditorActivator();
      for (SQLPanelSaveInfo panel : sqlPanelSaveInfoList)
      {
         SessionSqlJsonBean sessionSqlJsonBean = savedSessionsManager.storeFile(savedSessionJsonBean, panel, gitCommit);

         // The SQLEditorActivator is needed because externally saving a file selects the according SQL Tab.
         // But we want the active SQL-Editor to remain the same after saving the Session.
         sqlEditorActivator.prepareToActivateSQLPanelSaveInfo(panel, sessionSqlJsonBean);
      }
      savedSessionsManager.endStore(savedSessionJsonBean, sessionSaveProcessHandle);

      Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SessionPersister.saved.session.msg", savedSessionJsonBean.getName()));

      SavedSessionUtil.initSessionWithSavedSession(savedSessionJsonBean, session);

      if( savedSessionJsonBean.getSessionSQLs().stream().anyMatch(s -> StringUtilities.isEmpty(s.getExternalFilePath(), true)) )
      {
         // Activate needs to be called only when an external file was saved, see comment above.
         // On Windows this call results in a short flicker that's why call it when it's necessary only.
         sqlEditorActivator.activate();
      }

      return true;
   }
}
