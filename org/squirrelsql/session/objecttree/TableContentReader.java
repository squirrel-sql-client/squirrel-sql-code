package org.squirrelsql.session.objecttree;

import org.squirrelsql.session.Session;
import org.squirrelsql.table.TableLoader;
import org.squirrelsql.table.TableLoaderFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TableContentReader
{

   public static TableLoader readContent(Session session, ObjectTreeNode objectTreeNode)
   {
      try
      {
         String sql = "SELECT * FROM " + objectTreeNode.getTableInfo().getQualifiedName();

         Statement stat = session.getDbConnectorResult().getSQLConnection().getConnection().createStatement();

         stat.setMaxRows(session.getSessionProperties().getRowsLimit());
         ResultSet res = stat.executeQuery(sql);

         TableLoader tableLoader = TableLoaderFactory.loadDataFromResultSet(res);

         res.close();
         stat.close();

         return tableLoader;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }
}
