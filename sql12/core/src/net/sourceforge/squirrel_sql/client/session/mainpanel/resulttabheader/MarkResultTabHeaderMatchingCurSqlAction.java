package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabheader;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.IToolsPopupDescription;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.event.ActionEvent;

public class MarkResultTabHeaderMatchingCurSqlAction extends SquirrelAction implements ISQLPanelAction, IToolsPopupDescription
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MarkResultTabHeaderMatchingCurSqlAction.class);

	private static final ILogger s_log = LoggerController.createLogger(MarkResultTabHeaderMatchingCurSqlAction.class);
   private ISQLPanelAPI _sqlPanelAPI;

   public MarkResultTabHeaderMatchingCurSqlAction()
	{
		super(Main.getApplication());
	}

	public void setSQLPanel(ISQLPanelAPI sqlPanelAPI)
   {
      _sqlPanelAPI = sqlPanelAPI;

      setEnabled(null != _sqlPanelAPI);
   }

	@Override
	public String getToolsPopupDescription()
	{
		return s_stringMgr.getString("MarkResultTabHeaderMatchingCurSqlAction.tools.popup.descr");
	}

	public void actionPerformed(ActionEvent evt)
	{
		//System.out.println("MarkResultTabHeaderMatchingCurSqlAction.actionPerformed");
		_sqlPanelAPI.activateLastMarkedResultTabHeader();

	}
}
