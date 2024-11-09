package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabheader;

public class NormalizedSqlCompareCacheEntry
{
   private final String _normalizedSql;
   private final int _countNo;

   public NormalizedSqlCompareCacheEntry(String normalizedSql, int countNo)
   {
      _normalizedSql = normalizedSql;
      _countNo = countNo;
   }

   public String getNormalizedSql()
   {
      return _normalizedSql;
   }

   public int getCountNo()
   {
      return _countNo;
   }
}
