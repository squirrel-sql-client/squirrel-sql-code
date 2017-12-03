package net.sourceforge.squirrel_sql.client.cli.clitest;



import net.sourceforge.squirrel_sql.client.cli.CliInitializer;

import static net.sourceforge.squirrel_sql.client.cli.SquirrelCli.*;

public class CliTest
{
   public static void main(String[] args)
   {
      System.setProperty("squirrel.home", "/home/gerd/work/java/squirrel/squirrel-sql-git/sql12/output/dist/");
      System.setProperty("squirrel.userdir", "/home/gerd/work/java/squirrel/userdir/");


      CliInitializer.initializeSquirrelInCliMode();

      connect("PostgreSQL p_c");

      exec("SELECT * FROM articles");

   }
}
