package org.squirrelsql.session.completion.joingenerator;

import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.completion.CaretVicinity;
import org.squirrelsql.table.TableLoader;
import org.squirrelsql.table.TableLoaderFactory;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class SmartJoinGenerator extends JoinGeneratorBase
{
   public SmartJoinGenerator(CaretVicinity caretVicinity, Session session)
   {
      super(caretVicinity, session);
   }

   protected String createJoinClause(TableInfo table, String fkColumnName)
   {
      try
      {
         DatabaseMetaData metaData = getSession().getDbConnectorResult().getSQLConnection().getConnection().getMetaData();

         TableLoader column = TableLoaderFactory.loadDataFromResultSet(metaData.getColumns(table.getCatalog(), table.getSchema(), table.getName(), fkColumnName));

         if("YES".equalsIgnoreCase(column.getCellAsString("IS_NULLABLE", 0)))
         {
            return "LEFT JOIN";
         }
         else
         {
            return "INNER JOIN";
         }

      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   protected String getGeneratorName()
   {
      return JoinGeneratorProvider.GENERATOR_START + "j";
   }

   @Override
   protected JoinCompletionCandidateBase createCompletionCandidate(String replacement)
   {
      return new SmartJoinCompletionCandidate(replacement);
   }
}
