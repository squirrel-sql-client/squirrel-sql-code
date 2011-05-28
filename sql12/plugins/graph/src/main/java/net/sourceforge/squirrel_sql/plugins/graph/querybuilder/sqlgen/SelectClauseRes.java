package net.sourceforge.squirrel_sql.plugins.graph.querybuilder.sqlgen;

import java.util.ArrayList;

public class SelectClauseRes
{
   private String _select;
   private String _groupBy;
   private ArrayList<String> _qualifiedColsOrderedAsTheyAppearInSelect;

   public SelectClauseRes(StringBuffer select, ArrayList<String> qualifiedColsOrderedAsTheyAppearInSelect)
   {
      this(select, new StringBuffer(""), qualifiedColsOrderedAsTheyAppearInSelect);
   }

   public SelectClauseRes(StringBuffer select, StringBuffer groupBy, ArrayList<String> qualifiedColsOrderedAsTheyAppearInSelect)
   {
      _select = select.toString();
      _groupBy = groupBy.toString();
      _qualifiedColsOrderedAsTheyAppearInSelect = qualifiedColsOrderedAsTheyAppearInSelect;
   }


   public String getSelectClause()
   {
      return _select;
   }

   public String getGroupByClause()
   {
      return _groupBy;
   }

   public int getSQLSelectPositionForCol(String qualifiedCol)
   {
      for (int i = 0; i < _qualifiedColsOrderedAsTheyAppearInSelect.size(); i++)
      {
         if(_qualifiedColsOrderedAsTheyAppearInSelect.get(i).equalsIgnoreCase(qualifiedCol))
         {
            return i + 1;
         }
      }

      throw new IllegalArgumentException("Unknown column " + qualifiedCol);
   }
}
