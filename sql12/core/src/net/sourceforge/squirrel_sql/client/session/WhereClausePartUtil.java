package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Provides some utility methods for handling parts of an where-clause.
 *
 * @author Stefan Willinger
 */
public class WhereClausePartUtil implements IWhereClausePartUtil
{
   /**
    * @see IWhereClausePartUtil#createWhereClause(java.util.List)
    */
   @Override
   public String createWhereClause(List<IWhereClausePart> whereClauseParts)
   {
      StringBuilder sb = new StringBuilder("");
      for (IWhereClausePart whereClausePart : whereClauseParts)
      {
         if (whereClausePart.shouldBeUsed())
         {
            whereClausePart.appendToClause(sb);
         }
      }

      if (sb.length() > 0)
      {
         return sb.toString();
      }
      else
      {
         return null;
      }
   }

   /**
    * @see IWhereClausePartUtil#setParameters(java.sql.PreparedStatement, java.util.List
    */
   @Override
   public int setParameters(PreparedStatement pstmt, List<IWhereClausePart> whereClauseParts, int firstPosition, CountResult countResult)
         throws SQLException
   {

      int position = firstPosition;
      for (IWhereClausePart whereClausePart : whereClauseParts)
      {
         if (whereClausePart.shouldBeUsed())
         {
            if (whereClausePart.isParameterUsed())
            {
               if (null != countResult)
               {
                  countResult.addWhereClausePart(whereClausePart.getWhereClause(), whereClausePart.getParamValue());
               }

               whereClausePart.setParameter(pstmt, position);
               position++;
            }
         }

      }
      return position++;
   }

   /**
    * @see IWhereClausePartUtil#hasUsableWhereClause(java.util.List)
    */
   @Override
   public boolean hasUsableWhereClause(List<IWhereClausePart> whereClauseParts)
   {
      if (whereClauseParts == null || whereClauseParts.isEmpty())
      {
         return false;
      }
      for (IWhereClausePart whereClausePart : whereClauseParts)
      {
         if (whereClausePart.shouldBeUsed())
         {
            return true;
         }
      }
      return false;
   }
}
