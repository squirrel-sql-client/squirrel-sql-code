package net.sourceforge.squirrel_sql.client.session.connectionpool;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JComponent;

public class SessionConnectionPoolStatusBarCtrl extends JComponent
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionConnectionPoolStatusBarCtrl.class);

   private final SessionConnectionPoolStatusBarPanel _statusBarPanel;
   private SessionAdapter _sessionReconnectListener;
   private ISession _session;


   public SessionConnectionPoolStatusBarCtrl(ISession session, JComponent parent)
   {
      _session = session;
      _statusBarPanel = new SessionConnectionPoolStatusBarPanel(parent);

      initPoolListening();

      onPoolChanged();
   }

   private void initPoolListening()
   {
      _session.getConnectionPool().setPoolChangeListener(() -> onPoolChanged());

      _sessionReconnectListener = new SessionAdapter()
      {
         @Override
         public void reconnected(SessionEvent evt)
         {
            evt.getSession().getConnectionPool().setPoolChangeListener(() -> onPoolChanged());
         }
      };

      Main.getApplication().getSessionManager().addSessionListener(_sessionReconnectListener);

      _session.addSimpleSessionListener(() -> Main.getApplication().getSessionManager().removeSessionListener(_sessionReconnectListener));
   }

   private void onPoolChanged()
   {
      final int maxQuerySqlConnectionsCount = _session.getConnectionPool().getConnectionPoolProperties().getMaxQuerySqlConnectionsCount();
      final int inUseQuerySqlConnectionsCount = _session.getConnectionPool().getInUseQuerySqlConnectionsCount();
      final boolean autoCommit = _session.getConnectionPool().isAutoCommit();

      if (0 == maxQuerySqlConnectionsCount)
      {
         _statusBarPanel.btnState.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.WHITE_GEM));
         _statusBarPanel.textLbl.setText(s_stringMgr.getString("SessionConnectionPool.state.message.inactive", maxQuerySqlConnectionsCount, inUseQuerySqlConnectionsCount));
      }
      else if (false == autoCommit)
      {
         _statusBarPanel.btnState.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.WHITE_GEM));
         _statusBarPanel.textLbl.setText(s_stringMgr.getString("SessionConnectionPool.state.message.autocommit.false", maxQuerySqlConnectionsCount, inUseQuerySqlConnectionsCount));
      }
      else
      {
         if (inUseQuerySqlConnectionsCount == maxQuerySqlConnectionsCount)
         {
            if (1 < _session.getConnectionPool().getMaxCheckoutCount())
            {
               _statusBarPanel.btnState.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.RED_GEM));
               _statusBarPanel.textLbl.setText(s_stringMgr.getString("SessionConnectionPool.state.message.over.used", maxQuerySqlConnectionsCount, inUseQuerySqlConnectionsCount));
            }
            else
            {
               _statusBarPanel.btnState.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.YELLOW_GEM));
               _statusBarPanel.textLbl.setText(s_stringMgr.getString("SessionConnectionPool.state.message.all.used", maxQuerySqlConnectionsCount, inUseQuerySqlConnectionsCount));
            }
         }
         else
         {
            _statusBarPanel.btnState.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.GREEN_GEM));
            _statusBarPanel.textLbl.setText(s_stringMgr.getString("SessionConnectionPool.state.message.active", maxQuerySqlConnectionsCount, inUseQuerySqlConnectionsCount));
         }
      }
   }

   public SessionConnectionPoolStatusBarPanel getStatusBarPanel()
   {
      return _statusBarPanel;
   }
}
