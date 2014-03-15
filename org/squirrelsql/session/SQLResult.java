package org.squirrelsql.session;

import org.squirrelsql.table.TableLoader;

import java.sql.SQLException;

public class SQLResult
{
   private SQLException _sqlExc;
   private TableLoader _tableLoader;
   private long _executionTime;
   private long _buildingOutputTime;

   public SQLResult(SQLException sqlExc)
   {
      _sqlExc = sqlExc;
   }

   public SQLResult(TableLoader tableLoader, long executionTime, long buildingOutputTime)
   {
      _tableLoader = tableLoader;
      _executionTime = executionTime;
      _buildingOutputTime = buildingOutputTime;
   }

   public SQLException getSqlException()
   {
      return _sqlExc;
   }

   public TableLoader getTableLoader()
   {
      return _tableLoader;
   }

   public long getExecutionTime()
   {
      return _executionTime;
   }

   public long getBuildingOutputTime()
   {
      return _buildingOutputTime;
   }

   public long getCompleteTime()
   {
      return _executionTime + _buildingOutputTime;
   }
}
