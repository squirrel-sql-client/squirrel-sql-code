package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.gui.DontShowAgainDialog;
import net.sourceforge.squirrel_sql.fw.gui.DontShowAgainResult;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class SessionSaveAction extends SquirrelAction implements ISessionAction
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionSaveAction.class);

	private ISession _session;

	public SessionSaveAction(IApplication app)
	{
		super(app);
	}

	public void actionPerformed(ActionEvent e)
	{
		SavedSessionJsonBean savedSessionJsonBean = _session.getSavedSession();

		final ISQLAliasExt alias = _session.getAlias();
		final SavedSessionsManager savedSessionsManager = getApplication().getSavedSessionsManager();
		if(null == savedSessionJsonBean)
		{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String savedSessionNameTemplate = alias.getName() + " | " + df.format(new Date());

			final SessionSaveDlg sessionSaveDlg = new SessionSaveDlg(GUIUtils.getOwningFrame(_session.getSessionPanel()), savedSessionNameTemplate, savedSessionsManager);

			if(false == sessionSaveDlg.isOk())
			{
				return;
			}

			savedSessionJsonBean = new SavedSessionJsonBean();
			savedSessionJsonBean.setName(sessionSaveDlg.getSavedSessionName());
			savedSessionJsonBean.setDefaultAliasId(alias.getIdentifier());
		}
		else if(false == alias.getIdentifier().equals(savedSessionJsonBean.getDefaultAliasId()) && savedSessionsManager.isShowAliasChangeMsg())
		{
			final DontShowAgainDialog dlgMsg = new DontShowAgainDialog(GUIUtils.getOwningFrame(_session.getSessionPanel()),
																						  s_stringMgr.getString("SessionSaveAction.change.default.alias.to"),
																						  s_stringMgr.getString("SessionSaveAction.change.default.alias.how.to"));


			final DontShowAgainResult res = dlgMsg.showAndGetResult("SessionSaveAction.change.alias", 470, 200);
			savedSessionsManager.setShowAliasChangeMsg(res.isDontShowAgain());
			if(res.isYes())
			{
				savedSessionJsonBean.setDefaultAliasId(alias.getIdentifier());
			}
		}

		List<SQLPanelTyped> sqlPanelTypedList =  SavedSessionUtil.getAllSQLPanelsOrderedAndTyped(_session);

		SavedSessionJsonBean finalSavedSessionJsonBean = savedSessionJsonBean;
		savedSessionsManager.beginStore(savedSessionJsonBean);
		sqlPanelTypedList.forEach(p -> savedSessionsManager.storeFile(finalSavedSessionJsonBean, p.getSqlPanel(), p.getSqlPanelType()));
		savedSessionsManager.endStore(savedSessionJsonBean);

		_session.setSavedSession(savedSessionJsonBean);
	}

	@Override
	public void setSession(ISession session)
	{
		_session = session;
		setEnabled(null != _session);
	}

}
