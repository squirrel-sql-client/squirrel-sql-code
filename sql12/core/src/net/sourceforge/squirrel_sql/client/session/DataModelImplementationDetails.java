package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.sql.tablenamefind.TableNameFindService;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class DataModelImplementationDetails
{
   private ISession _session;
   private SQLExecutionInfo _exInfo;

   public DataModelImplementationDetails()
   {
   }

   public DataModelImplementationDetails(ISession session)
   {
      _session = session;
   }

   public DataModelImplementationDetails(ISession session, SQLExecutionInfo exInfo)
   {
      _session = session;
      _exInfo = exInfo;
   }

   public String getTableName(ColumnDisplayDefinition colDef)
   {
      if (null != _exInfo)
      {
         String tableNameFromSQL = new EditableSqlCheck(_exInfo, _session).getTableNameFromSQL();

         if(false == StringUtilities.isEmpty(tableNameFromSQL, true))
         {
            return tableNameFromSQL;
         }
      }

      return TableNameFindService.findTableNameInColumnDisplayDefinition(colDef);
   }


   public String getStatementSeparator()
   {
      if (null == _session)
      {
         return ";";
      }
      else
      {
         return _session.getProperties().getSQLStatementSeparator();
      }
   }

   public SQLExecutionInfo getSQLExecutionInfo()
   {
      return _exInfo;
   }

   public ISession getSession()
   {
      return _session;
   }
}
