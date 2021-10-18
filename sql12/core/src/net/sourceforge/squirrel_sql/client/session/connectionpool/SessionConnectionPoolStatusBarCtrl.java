package net.sourceforge.squirrel_sql.client.session.connectionpool;

import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.JComponent;

public class SessionConnectionPoolStatusBarCtrl extends JComponent
{
   private final SessionConnectionPoolStatusBarPanel _statusBarPanel;
   private ISession _session;

   public SessionConnectionPoolStatusBarCtrl(ISession session)
   {
      _session = session;
      _statusBarPanel = new SessionConnectionPoolStatusBarPanel();
   }

   public SessionConnectionPoolStatusBarPanel getStatusBarPanel()
   {
      return _statusBarPanel;
   }
}
