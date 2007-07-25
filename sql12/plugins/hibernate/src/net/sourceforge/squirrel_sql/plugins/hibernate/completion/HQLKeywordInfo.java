package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import java.util.ArrayList;

public class HQLKeywordInfo extends SimpleHQLCompletionInfo
{
   private static final String[] hqlKeywords = {"between", "class", "delete",
      "desc", "distinct", "elements", "escape", "exists", "false",
      "fetch", "from", "full", "group", "having", "in", "indices",
      "inner", "insert", "into", "is", "join", "left", "like", "new",
      "not", "null", "or", "order", "outer", "properties", "right",
      "select", "set", "some", "true", "union", "update", "versioned",
      "where", "and", "or", "as", "on", "with",

      // -- EJBQL tokens --
      "both", "empty", "leading", "member", "object", "of", "trailing",
   };
   private String _toString;

   public static ArrayList<HQLKeywordInfo> createInfos()
   {
      ArrayList<HQLKeywordInfo> ret = new ArrayList<HQLKeywordInfo>(hqlKeywords.length);

      for (String hqlKeyword : hqlKeywords)
      {
         ret.add(new HQLKeywordInfo(hqlKeyword));
      }

      return ret;
   }


   public HQLKeywordInfo(String infoString)
   {
      super(infoString);
      _toString = super.toString() + " (keyword)";
   }


   public String toString()
   {
      return _toString;
   }
}
