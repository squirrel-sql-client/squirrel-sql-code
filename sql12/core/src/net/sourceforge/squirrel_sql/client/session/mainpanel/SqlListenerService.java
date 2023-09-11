package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.event.SQLExecutionAdapter;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryHolder;

import java.util.ArrayList;
import java.util.List;

public class SqlListenerService
{
   private final ISession _session;
   private final SqlHistoryListener _sqlHistoryListener;

   private final List<ISQLExecutionListener> _sqlExecutionListeners = new ArrayList<>();
   public SqlListenerService(ISession session, SqlHistoryListener sqlHistoryListener)
   {
      _session = session;
      _sqlHistoryListener = sqlHistoryListener;

      _sqlExecutionListeners.add(new SQLExecutionAdapter()
      {
         @Override
         public void statementExecuted(QueryHolder queryHolder)
         {
            addSQLToHistory(queryHolder.getOriginalQuery());
         }
      });
      Main.getApplication().getSQLHistory().addSQLHistoryListener(_sqlHistoryListener);

      _sqlExecutionListeners.add(new SQLExecutionAdapter()
      {
         @Override
         public void statementExecuted(QueryHolder queryHolder)
         {
            _session.getConnectionPool().sqlStatementExecuted(queryHolder);
         }
      });
   }

   private void addSQLToHistory(String sql)
   {
      if (sql == null)
      {
         throw new IllegalArgumentException("sql == null");
      }

      final SQLHistoryItem shi = new SQLHistoryItem(sql, _session.getAlias().getName());
      if (_session.getProperties().getSQLShareHistory())
      {
         // Here the _sqlHistoryListener is by the application's SQLHistory.
         Main.getApplication().getSQLHistory().addSQLHistoryItem(shi);
         Main.getApplication().savePreferences(PreferenceType.SQLHISTORY);
      }
      else
      {
         // Here the application's SQLHistory is not active and we need to fire the listener ourselves.
         _sqlHistoryListener.newSqlHistoryItem(shi);
      }
   }

   public List<ISQLExecutionListener> getSqlExecutionListeners()
   {
      return _sqlExecutionListeners;
   }

   public void close()
   {
      Main.getApplication().getSQLHistory().removeSQLHistoryListener(_sqlHistoryListener);
   }
}
