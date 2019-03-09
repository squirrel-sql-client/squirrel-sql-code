package net.sourceforge.squirrel_sql.plugins.hibernate.util;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class HibernateSQLUtil
{
   private static String format(String sqls, ISession session)
   {
      CodeReformator cr = new CodeReformator(CodeReformatorConfigFactory.createConfig(session));

      sqls = cr.reformat(sqls) + "\n";
      return sqls;
   }

   public static void copySqlToClipboard(HibernateConnection con, String hqlQuery, ISession session)
   {
      String sql = con.generateSQL(hqlQuery);

      String allSqlsString = format(sql, session).trim();

      StringSelection ss = new StringSelection(allSqlsString);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
   }
}
