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
      for (String sql : sqls)
      {
         String tableSimpleName = SchemaUpdaterSqlAnalyzer.getTableSimpleName(sql);
         _session.getSchemaCacheValue().get().reloadMatchingTables(tableSimpleName);


         String procedureSimpleName = SchemaUpdaterSqlAnalyzer.getProcedureSimpleName(sql);
         _session.getSchemaCacheValue().get().reloadMatchingProcedures(procedureSimpleName);

         //_session.getSchemaCacheValue().get().reloadMatchingUDTs("receipts");
      }

      sqlTextAreaServices.updateHighlighting();

      _session.getDbConnectorResult().fireCacheUpdate();

   }
}
