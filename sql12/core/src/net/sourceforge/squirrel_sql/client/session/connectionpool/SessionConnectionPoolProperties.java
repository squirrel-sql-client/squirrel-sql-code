package net.sourceforge.squirrel_sql.client.session.connectionpool;

public class SessionConnectionPoolProperties
{
   private int _maxQuerySqlConnectionsCount = 2;

   public int getMaxQuerySqlConnectionsCount()
   {
      return _maxQuerySqlConnectionsCount;
   }

   public void setMaxQuerySqlConnectionsCount(int maxQuerySqlConnectionsCount)
   {
      _maxQuerySqlConnectionsCount = maxQuerySqlConnectionsCount;
   }
}
