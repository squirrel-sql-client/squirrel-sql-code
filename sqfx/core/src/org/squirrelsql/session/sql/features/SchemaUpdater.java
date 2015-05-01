package org.squirrelsql.session.sql.features;

import org.squirrelsql.session.Session;
import org.squirrelsql.session.sql.SQLTextAreaServices;

import java.util.ArrayList;

public class SchemaUpdater
{
   ArrayList<String> sqls = new ArrayList<>();
   private Session _session;

   public SchemaUpdater(Session session)
   {
      _session = session;
   }

   public void addSql(String currentSql)
   {
      sqls.add(currentSql);
   }

   public void doUpdates(SQLTextAreaServices sqlTextAreaServices)
   {
      boolean schemaUpdated = false;


      for (String sql : sqls)
      {
         String tableSimpleName = SchemaUpdaterSqlAnalyzer.getTableSimpleName(sql);

         if (null != tableSimpleName )
         {
            schemaUpdated = true;
            _session.getSchemaCacheValue().get().reloadMatchingTables(tableSimpleName);
         }


         String procedureSimpleName = SchemaUpdaterSqlAnalyzer.getProcedureSimpleName(sql);
         if (null != procedureSimpleName)
         {
            schemaUpdated = true;
            _session.getSchemaCacheValue().get().reloadMatchingProcedures(procedureSimpleName);
         }

         //_session.getSchemaCacheValue().get().reloadMatchingUDTs("receipts");
      }

      if (schemaUpdated)
      {
         sqlTextAreaServices.updateHighlighting();
         _session.getDbConnectorResult().fireCacheUpdate();
      }

   }
}
