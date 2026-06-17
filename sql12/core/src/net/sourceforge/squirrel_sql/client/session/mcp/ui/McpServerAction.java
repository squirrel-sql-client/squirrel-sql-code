package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.awt.event.ActionEvent;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;

public class McpServerAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;

   @Override
   public void actionPerformed(ActionEvent e)
   {
      AdditionalSQLTab sqlTab = SessionUtils.createMcpTab(_session, new McpUiHandle(23367, "TODO Connect key TODO"));
   }

   @Override
   public void setSession(ISession session)
   {
      _session = session;
      setEnabled(null != _session);
   }
}
