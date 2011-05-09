package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum WhereClauseOperator
{
   WHERE("WHERE (" + StringManagerProvider.s_stringMgr.getString("graph.GraphQueryWherePanelCtrl.like") + " AND)", "WHERE", 0),
   AND("AND", "AND", 1),
   OR("OR", "OR", 2), ;

   private String _title;

   private String _opSQL;
   private int _idx;

   WhereClauseOperator(String title, String opSQL, int idx)
   {
      _title = title;
      _opSQL = opSQL;
      _idx = idx;
   }


   @Override
   public String toString()
   {
      return _title;
   }

   public int getIdx()
   {
      return _idx;
   }

   public String getOpSQL()
   {
      return _opSQL;
   }

   public static WhereClauseOperator getByIx(int ix)
   {
      for (WhereClauseOperator op : WhereClauseOperator.values())
      {
         if(op.getIdx() == ix)
         {
            return op;
         }
      }

      throw new IllegalArgumentException("Unknown Index " + ix);
   }



   private static class StringManagerProvider
   {
      private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GraphQueryWherePanel.class);
   }
}
