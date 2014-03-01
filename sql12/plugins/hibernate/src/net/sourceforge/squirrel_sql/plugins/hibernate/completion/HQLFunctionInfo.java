package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import java.util.ArrayList;

public class HQLFunctionInfo extends SimpleHQLCompletionInfo
{
   private static final String[] _builtInFunctions = {
      // standard sql92 functions
      "substring", "locate", "trim", "length", "bit_length", "coalesce",
      "nullif", "abs", "mod", "sqrt",
      "upper",
      "lower",
      "cast",
      "extract",

      // time functions mapped to ansi extract
      "second", "minute", "hour", "day",
      "month",
      "year",

      "str",

      // misc functions - based on oracle dialect
      "sign", "acos", "asin", "atan", "cos", "cosh", "exp", "ln", "sin",
      "sinh", "stddev", "sqrt", "tan", "tanh", "variance",

      "round", "trunc", "ceil", "floor",

      "chr", "initcap", "lower", "ltrim", "rtrim", "soundex", "upper",
      "ascii", "length", "to_char", "to_date",

      "current_date", "current_time", "current_timestamp", "lastday",
      "sysday", "systimestamp", "uid", "user",

      "rowid", "rownum",

      "concat", "instr", "instrb", "lpad", "replace", "rpad", "substr",
      "substrb", "translate",

      "substring", "locate", "bit_length", "coalesce",

      "atan2", "log", "mod", "nvl", "nvl2", "power",

      "add_months", "months_between", "next_day",

      "max", "min",};

   private String _toString;


   public static ArrayList<HQLFunctionInfo> createInfos()
   {
      ArrayList<HQLFunctionInfo> ret = new ArrayList<HQLFunctionInfo>(_builtInFunctions.length);

      for (String builtInFunction : _builtInFunctions)
      {
         ret.add(new HQLFunctionInfo(builtInFunction));
      }

      return ret;
   }

   public HQLFunctionInfo(String infoString)
   {
      super(infoString);
      _toString = super.toString() + " (function)";
   }


   public String toString()
   {
      return _toString;
   }
}
