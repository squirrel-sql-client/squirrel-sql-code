package net.sourceforge.squirrel_sql.fw.sql.querytokenizer;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class QueryHolder
{
   /**
    * Comments are stripped.
    */
   private final String _query;

   /**
    * Comments are contained.
    */
   private final String _originalQuery;

   public QueryHolder(String query)
   {
      this(query, query);
   }

   public QueryHolder(String query, String originalQuery)
   {
      _query = query;
      _originalQuery = originalQuery;
   }

   /**
    * Comments are stripped.
    */
   public String getQuery()
   {
      return _query;
   }

   /**
    * Comments are contained.
    */
   public String getOriginalQuery()
   {
      return _originalQuery;
   }

   /**
    *
    * @return Comments stripped. Trimmed and all sequences of white space are replaced by a single space.
    */
   public String getCleanQuery()
   {
      return StringUtilities.cleanString(_query);
   }
}
