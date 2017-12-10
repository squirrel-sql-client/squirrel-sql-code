package net.sourceforge.squirrel_sql.client.cli.clitest;



import net.sourceforge.squirrel_sql.client.cli.CliInitializer;
import net.sourceforge.squirrel_sql.client.cli.ShellMode;

import static net.sourceforge.squirrel_sql.client.cli.SquirrelCli.*;

public class CliTest
{
   public static void main(String[] args)
   {
      System.setProperty("squirrel.home", "/home/gerd/work/java/squirrel/squirrel-sql-git/sql12/output/dist/");
      System.setProperty("squirrel.userdir", "/home/gerd/work/java/squirrel/userdir/");


      CliInitializer.initializeSquirrelInCliMode(ShellMode.CLI);

      connect("PostgreSQL p_c");

      // exec("SELECT * FROM articles");

      exec("update barcodes set barcode = 'SaraTest' where id = 1423971");

   }
}
