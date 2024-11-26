package net.sourceforge.squirrel_sql.fw.sql.commentandliteral;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class SQLCommentRemover
{
   public static String removeComments(String sql)
   {
      String lineCommentBegin = "--";

      if(StringUtilities.isNotEmpty(Main.getApplication().getSquirrelPreferences().getSessionProperties().getStartOfLineComment(), true))
      {
         lineCommentBegin = Main.getApplication().getSquirrelPreferences().getSessionProperties().getStartOfLineComment();
      }

      final SQLCommentAndLiteralHandler commentAndLiteralHandler = new SQLCommentAndLiteralHandler(sql, lineCommentBegin, true, true);

      StringBuilder ret = new StringBuilder();
      for (int i = 0; i < sql.length(); ++i)
      {
         final NextPositionAction nextPositionAction = commentAndLiteralHandler.nextPosition(i);

         if(NextPositionAction.APPEND == nextPositionAction)
         {
            ret.append(sql.charAt(i));
         }
      }

      return ret.toString();
   }


   public static void main(String[] args)
   {
      System.out.println(removeComments(sql));
   }


//   Bug 479 (former bug number was 1639662): /*/ must not be treated as comment begin and end
   private static final String sql =
         "/*PARAM1*/ thing /*C*/ = 'default value' /*/PARAM1*/";

//   private static String sql =
//            "-- sql \n INSERT INTO code (txt) VALUES -- haha\n 'for(int i = e-1; i >= 0; --i)') -- nice test";


//   private static String sql =
//         "-- Comment to remove\n" +
//               "SELECT * FROM articles   /*one\n" +
//               "more comment to remove*/\n" +
//               "-- Go away\n" +
//               "where descr = 'sssss --Should be left /*alone*/ hhhh'\n" +
//               "-- do away again";


//   SQL by Stefan Mueller, see change log.
//   private static String sql = "SELECT * FROM myTable where /*com1*/ id = :myId /*com2*/ ORDER BY id";

// Bug #1329
//   private static String sql = "select 1 from dual\n" +
//         "/*\n" +
//         "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aliquam ante. Maecenas fermentum, sem in pharetra pellentesque, velit turpis volutpat ante, in pharetra metus odio a lectus. Vivamus luctus egestas leo. Maecenas sollicitudin. Aliquam in lorem sit amet leo accumsan lacinia. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vivamus porttitor turpis ac leo. Aliquam erat volutpat. Aliquam in lorem sit amet leo accumsan lacinia. Aenean placerat. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos hymenaeos. Nulla non lectus sed nisl molestie malesuada. Vivamus ac leo pretium faucibus. Vestibulum fermentum tortor id mi. Maecenas ipsum velit, consectetuer eu lobortis ut, dictum at dui. \n" +
//         "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aliquam ante. Maecenas fermentum, sem in pharetra pellentesque, velit turpis volutpat ante, in pharetra metus odio a lectus. Vivamus luctus egestas leo. Maecenas sollicitudin. Aliquam in lorem sit amet leo accumsan lacinia. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vivamus porttitor turpis ac leo. Aliquam erat volutpat. Aliquam in lorem sit amet leo accumsan lacinia. Aenean placerat. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos hymenaeos. Nulla non lectus sed nisl molestie malesuada. Vivamus ac leo pretium faucibus. Vestibulum fermentum tortor id mi. Maecenas ipsum velit, consectetuer eu lobortis ut, dictum at dui. \n" +
//         "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aliquam ante. Maecenas fermentum, sem in pharetra pellentesque, velit turpis volutpat ante, in pharetra metus odio a lectus. Vivamus luctus egestas leo. Maecenas sollicitudin. Aliquam in lorem sit amet leo accumsan lacinia. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vivamus porttitor turpis ac leo. Aliquam erat volutpat. Aliquam in lorem sit amet leo accumsan lacinia. Aenean placerat. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos hymenaeos. Nulla non lectus sed nisl molestie malesuada. Vivamus ac leo pretium faucibus. Vestibulum fermentum tortor id mi. Maecenas ipsum velit, consectetuer eu lobortis ut, dictum at dui. \n" +
//         "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aliquam ante. Maecenas fermentum, sem in pharetra pellentesque, velit turpis volutpat ante, in pharetra metus odio a lectus. Vivamus luctus egestas leo. Maecenas sollicitudin. Aliquam in lorem sit amet leo accumsan lacinia. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. \n" +
//         "Vivamus porttitor turpis ac leo. Aliquam erat volutpat. Aliquam in lorem sit amet leo accumsan lacinia. abcdefghijklmnopqrstuvwxz12*/";


}


