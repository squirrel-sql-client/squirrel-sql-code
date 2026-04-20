package net.sourceforge.squirrel_sql.client.session.editexternal;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ProcessBuilderCommandLineUtil
{
   private static ILogger s_log = LoggerController.createLogger(ProcessBuilderCommandLineUtil.class);


   static String[] splitCommandLine(String cmd)
   {
      //return new String[]{"/usr/bin/emacs", "+7:35", "/home/gerd/work/java/squirrel/testsqls/fucksql.sql"};

      List<String> parts = new ArrayList<>();
      StringBuilder cur = new StringBuilder();
      boolean inDouble = false, inSingle = false;
      for(int i = 0; i < cmd.length(); i++)
      {
         char c = cmd.charAt(i);
         if(c == '\\')
         {
            if(i + 1 < cmd.length())
            {
               cur.append(cmd.charAt(++i));
            }
         }
         else if(c == '"' && !inSingle)
         {
            inDouble = !inDouble;
         }
         else if(c == '\'' && !inDouble)
         {
            inSingle = !inSingle;
         }
         else if(Character.isWhitespace(c) && !inDouble && !inSingle)
         {
            if(cur.length() > 0)
            {
               parts.add(cur.toString());
               cur.setLength(0);
            }
         }
         else
         {
            cur.append(c);
         }
      }
      if(cur.length() > 0)
      {
         parts.add(cur.toString());
      }
      String[] ret = parts.toArray(new String[0]);

      s_log.info("Command line \"%s\" was passed to java.lang.ProcessBuilder(String...) split into: %s".formatted(cmd, parts));
      return ret;
   }
}
