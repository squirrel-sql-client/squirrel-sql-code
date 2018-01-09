package net.sourceforge.squirrel_sql.client.cli.clitest;

import net.sourceforge.squirrel_sql.client.cli.SquirrelBatch;
import org.apache.commons.cli.ParseException;

public class BatchTest
{
   public static void main(String[] args) throws ParseException
   {
      //testExistingAlias();
      testNewAlias();
   }

   private static void testNewAlias() throws ParseException
   {
      SquirrelBatch.main(new String[]{
            "/home/gerd/work/java/squirrel/squirrel-sql-git/sql12/output/dist/",
            "/home/gerd/work/java/squirrel/userdir",
            "-url", "jdbc:postgresql://localhost/pos_central",
            "-user", "gerd",
            "-password", "blabber",
            "-driver", "org.postgresql.Driver",
            "-drivercp", "/home/gerd/programme/datennbanken/postgresql-9.1.1/jdbc/postgresql-42.1.4.jar",
            "-sql", "select * from articles"});
   }

   private static void testExistingAlias() throws ParseException
   {
      SquirrelBatch.main(new String[]{
            "/home/gerd/work/java/squirrel/squirrel-sql-git/sql12/output/dist/",
            "/home/gerd/work/java/squirrel/userdir",
            "-alias", "PostgreSQL p_c",
            "-sql", "select * from articles"});
   }
}
