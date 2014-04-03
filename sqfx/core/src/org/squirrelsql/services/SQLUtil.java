package org.squirrelsql.services;

import org.squirrelsql.dialects.DialectFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUtil
{
   public static void close(Connection con)
   {
      if (null == con)
      {
         return;
      }

      try
      {
         con.close();
      }
      catch (Throwable e)
      {
         new MessageHandler(SQLUtil.class, MessageHandlerDestination.MESSAGE_LOG).warning("Error closing connection", e);
      }
   }

   public static void close(ResultSet res)
   {
      if (null == res)
      {
         return;
      }

      try
      {
         res.close();
      }
      catch (Throwable e)
      {
         new MessageHandler(SQLUtil.class, MessageHandlerDestination.MESSAGE_LOG).warning("Error closing ResultSet", e);
      }
   }

   public static String quoteIdentifier(String s)
   {
      if (s == null)
      {
         return null;
      }
      StringBuilder buff = null;
      buff = new StringBuilder();
      for (int i = 0; i < s.length(); i++)
      {
         char c = s.charAt(i);
         if (c == '"' && i != 0 && i != s.length() - 1)
         {
            buff.append(c);
         }
         buff.append(c);
      }
      String result = buff.toString();
      return result;
   }

   public static String generateQualifiedName(Connection con, String catalog, String schema, String simpleName, DatabaseObjectType databaseObjectType)
   {
      try
      {
         DatabaseMetaData md = con.getMetaData();

         String catSep = null;
         String identifierQuoteString = null;
         boolean supportsSchemasInDataManipulation = false;
         boolean supportsCatalogsInDataManipulation = false;
         boolean supportsSchemasInTableDefinitions = false;

         // check for Informix - it has very "special" qualified names
         if (DialectFactory.isInformix(con))
         {
            return getInformixQualifiedName(catalog, schema, simpleName);
         }
         // Progress claims to support catalogs in data manip - but it actually doesn't honor that claim.
         if (DialectFactory.isProgress(con))
         {
            return getProgressQualifiedName(catalog, schema, simpleName);
         }

         try
         {
            supportsSchemasInDataManipulation = md.supportsSchemasInDataManipulation();
         }
         catch (SQLException ignore)
         {
            // Ignore.
         }
         try
         {
            supportsCatalogsInDataManipulation = md.supportsCatalogsInDataManipulation();
         }
         catch (SQLException ignore)
         {
            // Ignore.
         }

         try
         {
            supportsSchemasInTableDefinitions = md.supportsSchemasInTableDefinitions();
         }
         catch (SQLException ignore)
         {
            // Ignore.
         }

         try
         {
            catSep = md.getCatalogSeparator();
         }
         catch (SQLException ignore)
         {
            // Ignore.
         }


         if (Utils.isEmptyString(catSep))
         {
            catSep = ".";
         }

         try
         {
            identifierQuoteString = md.getIdentifierQuoteString();
            if (identifierQuoteString != null
                  && identifierQuoteString.equals(" "))
            {
               identifierQuoteString = null;
            }
         }
         catch (SQLException ignore)
         {
            // Ignore.
         }

         if (DialectFactory.isSyBase(con))
         {
            identifierQuoteString =
                  checkSybaseIdentifierQuoteString(md, identifierQuoteString);
         }

         StringBuilder buf = new StringBuilder();
         if (supportsCatalogsInDataManipulation
               && !Utils.isEmptyString(catalog))
         {
            if (identifierQuoteString != null)
            {
               buf.append(identifierQuoteString);
            }
            buf.append(catalog);
            if (identifierQuoteString != null)
            {
               buf.append(identifierQuoteString);
            }
            buf.append(catSep);
         }

         if (shouldQualifyWithSchema(supportsSchemasInDataManipulation, supportsSchemasInTableDefinitions, con, schema, databaseObjectType))
         {
            if (identifierQuoteString != null)
            {
               buf.append(identifierQuoteString);
            }
            buf.append(schema);
            if (identifierQuoteString != null)
            {
               buf.append(identifierQuoteString);
            }

            buf.append(catSep);
         }

         if (identifierQuoteString != null)
         {
            buf.append(identifierQuoteString);
         }
         String quoteExpandedName = SQLUtil.quoteIdentifier(simpleName);
         buf.append(quoteExpandedName);
         if (identifierQuoteString != null)
         {
            buf.append(identifierQuoteString);
         }
         return buf.toString();
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private static String getInformixQualifiedName(String catalog, String schema, String simpleName)
   {
      StringBuilder result = new StringBuilder();
      if (catalog != null && schema != null)
      {
         result.append(catalog);
         result.append(":");
         result.append("\"");
         result.append(schema);
         result.append("\"");
         result.append(".");
      }
      result.append(simpleName);
      return result.toString();
   }

   private static String getProgressQualifiedName(String catalog, String schema, String simpleName)
   {
      StringBuilder result = new StringBuilder();
      if (schema != null)
      {
         result.append(schema);
         result.append(".");
      }
      result.append(simpleName);
      return result.toString();
   }

   private static String checkSybaseIdentifierQuoteString(DatabaseMetaData md, final String quoteString)
   {
      String result = quoteString;
      String productName = null;
      CharSequence sybaseTwelveVersionId = "12.";
      try {
         productName = md.getDatabaseProductVersion();
      } catch (SQLException e) {
         // ignore
      }
      if (productName != null) {
         if (productName.contains(sybaseTwelveVersionId)) {
            result = "";
         }
      }
      return result;
   }

   private static boolean shouldQualifyWithSchema(boolean supportsSchemasInDataManipulation, boolean supportsSchemasInTableDefinitions, Connection con, String schema, DatabaseObjectType databaseObjectType)
   {

      if (schema == null || schema.length() == 0)
      {
         return false;
      }
      if (supportsSchemasInDataManipulation)
      {
         return true;
      }
      if (databaseObjectType == DatabaseObjectType.TABLE)
      {
         if (supportsSchemasInTableDefinitions)
         {
            return true;
         }
      }
      // Always qualify table name for HSQL - Bug 1596240 HSQLDB->Objects->Content.  Cannot detect if table
      // like above, since objects in HSQLDB's INFORMATION_SCHEMA under "SYSTEM TABLE" node have a database
      // object type of "OTHER", instead of "TABLE".
      //
      if (DialectFactory.isHSQL(con))
      {
         return true;
      }
      return false;
   }


   public static String getQualifiedName(String catalogName, String schemaName)
   {
      if(null == catalogName && null == schemaName)
      {
         return null;
      }

      if(null != catalogName && null == schemaName)
      {
         return catalogName;
      }

      if(null == catalogName && null != schemaName)
      {
         return schemaName;
      }

      return catalogName + "." + schemaName;
   }
}
