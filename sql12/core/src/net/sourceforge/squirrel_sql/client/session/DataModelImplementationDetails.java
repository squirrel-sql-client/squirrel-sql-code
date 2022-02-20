package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataModelImplementationDetails
{
   private static final Pattern FILL_COLUMN_NAME_PATTERN = Pattern.compile(".+:([^:]+):[^:]+$");
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
         String tableNameFromSQL = new EditableSqlCheck(_exInfo).getTableNameFromSQL();

         if(false == StringUtilities.isEmpty(tableNameFromSQL, true))
         {
            return tableNameFromSQL;
         }
      }

      if(false == StringUtilities.isEmpty(colDef.getTableName(), true))
      {
         return colDef.getTableName();
      }

      if (    null != colDef.getResultMetaDataTable()
           && false == StringUtilities.isEmpty(colDef.getResultMetaDataTable().getTableName(), true))
      {
         return colDef.getResultMetaDataTable().getTableName();
      }

      if (false == StringUtilities.isEmpty(colDef.getFullTableColumnName(), true))
      {
         Matcher matcher = FILL_COLUMN_NAME_PATTERN.matcher(colDef.getFullTableColumnName());

         if (matcher.matches())
         {
            return matcher.group(1);
         }
      }

      return null;
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
