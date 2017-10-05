package org.squirrelsql.dialects;

import java.sql.Connection;
import java.sql.SQLException;

public class DialectFactory
{
   public static boolean isSyBase(Connection con)
   {
      try
      {
         String databaseProductName = con.getMetaData().getDatabaseProductName();
         String databaseProductVersion = con.getMetaData().getDatabaseProductVersion();

         if (databaseProductName == null)
         {
            return false;
         }
         String lname = databaseProductName.trim().toLowerCase();
         if (lname.startsWith("sybase") || lname.startsWith("adaptive") || lname.startsWith("sql server"))
         {
            // We don't yet have the need to discriminate by version.
            return true;
         }
         return false;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static boolean isMSSQLServer(Connection con)
   {
      try
      {
         String databaseProductName = con.getMetaData().getDatabaseProductName();
         String databaseProductVersion = con.getMetaData().getDatabaseProductVersion();

         if (databaseProductName == null)
         {
            return false;
         }
         if (databaseProductName.trim().toLowerCase().startsWith("microsoft"))
         {
            // We don't yet have the need to discriminate by version.
            return true;
         }
         return false;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }


   public static boolean isDB2(Connection con)
   {
      try
      {
         String databaseProductName = con.getMetaData().getDatabaseProductName();
         String databaseProductVersion = con.getMetaData().getDatabaseProductVersion();

         if (databaseProductName == null)
         {
            return false;
         }

         if ("DB2".startsWith(databaseProductName.trim()))
         {
            // We don't yet have the need to discriminate by version.
            return true;
         }
         return false;


      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static boolean isInformix(Connection con)
   {
      try
      {
         String databaseProductName = con.getMetaData().getDatabaseProductName();
         String databaseProductVersion = con.getMetaData().getDatabaseProductVersion();

         if (databaseProductName == null)
         {
            return false;
         }
         if (databaseProductName.toLowerCase().contains("informix"))
         {
            // We don't yet have the need to discriminate by version.
            return true;
         }
         return false;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static boolean isProgress(Connection con)
   {
      try
      {
         String databaseProductName = con.getMetaData().getDatabaseProductName();
         String databaseProductVersion = con.getMetaData().getDatabaseProductVersion();
         if (databaseProductName == null)
         {
            return false;
         }

         if (databaseProductName.trim().toLowerCase().startsWith("progress")
               || databaseProductName.trim().toLowerCase().startsWith("openedge"))
         {
            // We don't yet have the need to discriminate by version.
            return true;
         }
         return false;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }

   }

   public static boolean isHSQL(Connection con)
   {
      try
      {
         String databaseProductName = con.getMetaData().getDatabaseProductName();
         String databaseProductVersion = con.getMetaData().getDatabaseProductVersion();

         if (databaseProductName == null)
         {
            return false;
         }
         if (databaseProductName.trim().startsWith("HSQL"))
         {
            // We don't yet have the need to discriminate by version.
            return true;
         }
         return false;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }
}
