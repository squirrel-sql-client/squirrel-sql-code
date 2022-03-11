package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.DontShowAgainDialog;
import net.sourceforge.squirrel_sql.fw.gui.DontShowAgainResult;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SessionPersister
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionSaveAction.class);


   public static boolean saveSession(ISession session)
   {
      return saveSession(session, true);
   }

   public static boolean saveSession(ISession session, boolean allowAliasChangeMsg)
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

      List<SQLPanelTyped> sqlPanelTypedList = SavedSessionUtil.getAllSQLPanelsOrderedAndTyped(session);

      SavedSessionJsonBean finalSavedSessionJsonBean = savedSessionJsonBean;
      savedSessionsManager.beginStore(savedSessionJsonBean);
      sqlPanelTypedList.forEach(p -> savedSessionsManager.storeFile(finalSavedSessionJsonBean, p.getSqlPanel(), p.getSqlPanelType()));
      savedSessionsManager.endStore(savedSessionJsonBean);

      Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SessionPersister.saved.session.msg", savedSessionJsonBean.getName()));

      SavedSessionUtil.initSessionWithSavedSession(savedSessionJsonBean, session);

      return true;
   }
}
