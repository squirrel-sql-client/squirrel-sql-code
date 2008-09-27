package net.sourceforge.squirrel_sql.client.session.schemainfo;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class FilterMatcher
{
   private String _sqlOrMetaDataMatchString;
   private ArrayList<Pattern> _includeFilterPatterns = new ArrayList<Pattern>();
   private ArrayList<Pattern> _excludeFilterPatterns = new ArrayList<Pattern>();

   public FilterMatcher(String filterInclude, String filterExclude)
   {
      if(null != filterInclude)
      {
         String[] splits = filterInclude.split(",");

         for (int i = 0; i < splits.length; i++)
         {


            if(0 < splits[i].trim().length())
            {
               String regExStr = splits[i].replaceAll("\\.", "\\\\.")
                                          .replaceAll("%", ".*")
                                          .replaceAll("_",".{1}");

               _includeFilterPatterns.add(Pattern.compile(regExStr, Pattern.CASE_INSENSITIVE));

               if(1 == _includeFilterPatterns.size())
               {
                  _sqlOrMetaDataMatchString = splits[i];
               }


            }
         }
      }

      if(null != filterExclude)
      {
         String[] splits = filterExclude.split(",");

         for (int i = 0; i < splits.length; i++)
         {
            if(0 < splits[i].trim().length())
            {
               String regExStr = splits[i].replaceAll("\\.", "\\\\.")
                                          .replaceAll("%", ".*")
                                          .replaceAll("_",".{1}");

               _excludeFilterPatterns.add(Pattern.compile(regExStr, Pattern.CASE_INSENSITIVE));
            }
         }
      }
   }


   public boolean matches(String simpleObjectName)
   {
      return matchesPatterns(simpleObjectName, _includeFilterPatterns, false) && !matchesPatterns(simpleObjectName, _excludeFilterPatterns, true);
   }

   private boolean matchesPatterns(String simpleObjectName, ArrayList<Pattern> patterns, boolean exclude)
   {
      boolean matchesPatterns;

      if(exclude)
      {
         matchesPatterns = false;
      }
      else
      {
         matchesPatterns = (0 == patterns.size());
      }

      for (Pattern includeFilterPattern : patterns)
      {
         if(includeFilterPattern.matcher(simpleObjectName).matches())
         {
            matchesPatterns = true;
            break;
         }
      }
      return matchesPatterns;
   }

   public String getMetaDataMatchString()
   {
      return _sqlOrMetaDataMatchString;
   }

   public String getSqlLikeMatchString()
   {
      if(null != _sqlOrMetaDataMatchString)
      {
         return _sqlOrMetaDataMatchString;
      }
      else
      {
         return "%";
      }
   }

   public static void main(String[] args)
   {
      boolean b = new FilterMatcher("Geer%", null).matches("Gerhard");

      System.out.println(b);

   }
}
