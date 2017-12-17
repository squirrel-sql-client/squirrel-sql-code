package net.sourceforge.squirrel_sql.client.cli.clitest;



import net.sourceforge.squirrel_sql.client.cli.CliInitializer;
import net.sourceforge.squirrel_sql.client.cli.ShellMode;

import static net.sourceforge.squirrel_sql.client.cli.SquirrelCli.*;

public class CliTest
{
   public static void main(String[] args)
   {
      testSetRowlimit();
      //testNewAlias();
      //testAliasAutoLogon();
      //testAliasPassword();

   }

   private static void testSetRowlimit()
   {
      System.setProperty("squirrel.home", "/home/gerd/work/java/squirrel/squirrel-sql-git/sql12/output/dist/");
      System.setProperty("squirrel.userdir", "/home/gerd/work/java/squirrel/userdir/");


      CliInitializer.initializeSquirrelInCliMode(ShellMode.CLI);

      connect(
            "jdbc:postgresql://localhost/pos_central",
            "gerd",
            null,
            "org.postgresql.Driver",
            "/home/gerd/programme/datennbanken/postgresql-9.1.1/jdbc/postgresql-42.1.4.jar");


      setMaxRows(0);

      exec("SELECT * FROM articles");
      //exec("select * from receipts");
   }

   private static void testNewAlias()
   {
      System.setProperty("squirrel.home", "/home/gerd/work/java/squirrel/squirrel-sql-git/sql12/output/dist/");
      System.setProperty("squirrel.userdir", "/home/gerd/work/java/squirrel/userdir/");


      CliInitializer.initializeSquirrelInCliMode(ShellMode.CLI);

      connect(
            "jdbc:postgresql://localhost/pos_central",
            "gerd",
            null,
            "org.postgresql.Driver",
            "/home/gerd/programme/datennbanken/postgresql-9.1.1/jdbc/postgresql-42.1.4.jar");

      // exec("SELECT * FROM articles");

      exec("select * from receipts");
   }

   private static void testAliasPassword()
   {
      System.setProperty("squirrel.home", "/home/gerd/work/java/squirrel/squirrel-sql-git/sql12/output/dist/");
      System.setProperty("squirrel.userdir", "/home/gerd/work/java/squirrel/userdir/");


      CliInitializer.initializeSquirrelInCliMode(ShellMode.CLI);

      connect("Derby internal-db nopwd", "derbypass");

      // exec("SELECT * FROM articles");

      exec("select * from receipts");
   }


   private static void testAliasAutoLogon()
   {
      System.setProperty("squirrel.home", "/home/gerd/work/java/squirrel/squirrel-sql-git/sql12/output/dist/");
      System.setProperty("squirrel.userdir", "/home/gerd/work/java/squirrel/userdir/");


      CliInitializer.initializeSquirrelInCliMode(ShellMode.CLI);

      connect("PostgreSQL p_c");

      // exec("SELECT * FROM articles");

      exec("update barcodes set barcode = 'SaraTest' where id = 1423971");
   }
}
