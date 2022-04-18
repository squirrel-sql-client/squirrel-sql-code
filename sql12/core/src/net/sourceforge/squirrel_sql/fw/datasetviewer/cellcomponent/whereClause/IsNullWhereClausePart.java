package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.dialects.DialectUtils2;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.sql.PreparedStatement;

/**
 * The part of an where-clause, which presents an <code>column is null</code>
 * statement. This implemementation doesn't use parameters, so it is not allowed
 * to call {@link #setParameter(PreparedStatement, int)}
 *
 * @author Stefan Willinger
 */
public class IsNullWhereClausePart extends NoParameterWhereClausePart
{

   /**
    * Constructs an part of an where-clause in the style of <code>column is null</code>
    *
    * @param columnDef         The column which is null
    * @param dataTypeComponent
    */
   public IsNullWhereClausePart(ColumnDisplayDefinition columnDef)
   {
      super(columnDef, valueIsNull(columnDef));
   }


   /**
    * Creates an <code>column is null</code> where clause.
    *
    * @param columnDef
    */
   private static String valueIsNull(ColumnDisplayDefinition columnDef)
   {
      if (StringUtilities.isEmpty(columnDef.getColumnName(), true))
      {
         throw new IllegalArgumentException("columnDef contains not an usable columnName");
      }
      return DialectUtils2.checkColumnDoubleQuotes(columnDef.getDialectType(), columnDef.getColumnName()) + " is null";
   }

   @Override
   public Object getParamValue()
   {
      return "IS NULL";
   }


}