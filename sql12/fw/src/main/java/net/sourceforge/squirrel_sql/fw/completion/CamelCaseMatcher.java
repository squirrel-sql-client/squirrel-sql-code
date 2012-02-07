package net.sourceforge.squirrel_sql.fw.completion;

public class CamelCaseMatcher
{
   public static boolean matchesCamelCase(String testString, String dbObjectName)
   {
      int[] bufTest = new int[]{0};
      int[] bufComp = new int[]{0};

      String nextCamelCaseSubstring = getNextCamelCaseSubstring(testString, bufTest);

      if(null == nextCamelCaseSubstring)
      {
         return false;
      }

      String dbObjCamelCaseSubstring = getNextCamelCaseSubstring(dbObjectName, bufComp);
      while(null != dbObjCamelCaseSubstring && dbObjCamelCaseSubstring.startsWith(nextCamelCaseSubstring))
      {
         nextCamelCaseSubstring = getNextCamelCaseSubstring(testString, bufTest);

         if(null == nextCamelCaseSubstring)
         {
            return true;
         }
         dbObjCamelCaseSubstring = getNextCamelCaseSubstring(dbObjectName, bufComp);

      }

      return false;
   }

   private static String getNextCamelCaseSubstring(String str, int[] nextBegPos)
   {
      if(0 == str.length())
      {
         return null;
      }

      int beg = nextBegPos[0];

      if(beg == str.length())
      {
         return null;
      }


      for(++nextBegPos[0]; nextBegPos[0] < str.length(); ++nextBegPos[0])
      {
         if(Character.isUpperCase(str.charAt(nextBegPos[0])))
         {
            return str.substring(beg, nextBegPos[0]);
         }
      }

      return str.substring(beg, nextBegPos[0]);
   }

   public static void main(String[] args)
   {
      System.out.println("1 true = " + CamelCaseMatcher.matchesCamelCase("WKP", "WKvPos"));
      System.out.println("2 true = " + CamelCaseMatcher.matchesCamelCase("WK", "WKvPos"));
      System.out.println("3 true = " + CamelCaseMatcher.matchesCamelCase("W", "WKvPos"));
      System.out.println("4 true = " + CamelCaseMatcher.matchesCamelCase("WuKv", "WunKvPos"));
      System.out.println("5 true = " + CamelCaseMatcher.matchesCamelCase("WuKP", "WunKvPos"));
      System.out.println("6 false = " + CamelCaseMatcher.matchesCamelCase("WuP", "WunKvPos"));
      System.out.println("7 true = " + CamelCaseMatcher.matchesCamelCase("WK", "WKvKvPos"));
      System.out.println("8 true = " + CamelCaseMatcher.matchesCamelCase("WKK", "WKvKvPos"));
      System.out.println("9 false = " + CamelCaseMatcher.matchesCamelCase("WKKK", "WKvKvPos"));
      System.out.println("10 false = " + CamelCaseMatcher.matchesCamelCase("", "WKvKvPos"));
      System.out.println("11 false = " + CamelCaseMatcher.matchesCamelCase(" ", "WKvKvPos"));
      System.out.println("12 false = " + CamelCaseMatcher.matchesCamelCase(" W", "WKvKvPos"));
      System.out.println("13 false = " + CamelCaseMatcher.matchesCamelCase("W ", "WKvKvPos"));
      System.out.println("14 false = " + CamelCaseMatcher.matchesCamelCase("K", "WKvKvPos"));
      System.out.println("15 false = " + CamelCaseMatcher.matchesCamelCase("K", "WKKP"));
      System.out.println("16 true = " + CamelCaseMatcher.matchesCamelCase("W", "WKKP"));
      System.out.println("17 true = " + CamelCaseMatcher.matchesCamelCase("WK", "WKKP"));
      System.out.println("18 true = " + CamelCaseMatcher.matchesCamelCase("WKK", "WKKP"));
      System.out.println("19 false = " + CamelCaseMatcher.matchesCamelCase("WKKK", "WKKP"));
      System.out.println("20 true = " + CamelCaseMatcher.matchesCamelCase("WKKP", "WKKP"));
      System.out.println("21 false = " + CamelCaseMatcher.matchesCamelCase("WKKPa", "WKKP"));
   }

}
