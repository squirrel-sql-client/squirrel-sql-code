package net.sourceforge.squirrel_sql.plugins.graph;

public enum QueryJoinType
{
   OUTER(0), NONE(1), INNER(2);
   private int _ix;

   QueryJoinType(int ix)
   {
      _ix = ix;
   }

   public int getIndex()
   {
      return _ix;
   }

   public static QueryJoinType getByIndex(int ix)
   {
      for (QueryJoinType queryJoinType : values())
      {
         if(queryJoinType._ix == ix)
         {
            return queryJoinType;
         }
      }

      throw new IllegalArgumentException("Unkown Join Type index " + ix);
   }
}
