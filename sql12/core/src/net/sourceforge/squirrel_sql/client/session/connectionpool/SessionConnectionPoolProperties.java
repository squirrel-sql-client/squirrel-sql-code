package net.sourceforge.squirrel_sql.client.session.connectionpool;

public class SessionConnectionPoolProperties
{
   private boolean _useQuerySqlConnections = false;
   private int _maxQuerySqlConnectionsCount = 3;

   public boolean isUseQuerySqlConnections()
   {
      return _useQuerySqlConnections;
   }

   public void setUseQuerySqlConnections(boolean useQuerySqlConnections)
   {
      _useQuerySqlConnections = useQuerySqlConnections;
   }

   public int getMaxQuerySqlConnectionsCount()
   {
      return _maxQuerySqlConnectionsCount;
   }

   public void setMaxQuerySqlConnectionsCount(int maxQuerySqlConnectionsCount)
   {
      _maxQuerySqlConnectionsCount = maxQuerySqlConnectionsCount;
   }
}
