package net.sourceforge.squirrel_sql.client.cli.clitest;

import net.sourceforge.squirrel_sql.client.cli.SquirrelBatch;
import org.apache.commons.cli.ParseException;

public class BatchTest
{
   public static void main(String[] args) throws ParseException
   {
      SquirrelBatch.main(new String[]{"/home/gerd/work/java/squirrel/squirrel-sql-git/sql12/output/dist/", "/home/gerd/work/java/squirrel/userdir", "-alias", "PostgreSQL p_c", "-sql", "select * from articles"});
   }
}
