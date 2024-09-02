package net.sourceforge.squirrel_sql.plugins.hibernate.server;

/**
 * Copied from StringUtilities because this package must not use non JDK classes
 */
public class HibernateServerStringUtils
{
   public static String escapeJsonChars(String str)
   {
      if(null == str)
      {
         return null;
      }

      String buf = str.replace("\\", "\\\\"); // Must be first
      buf = buf.replace("\"", "\\\"");
      buf = buf.replace("\n", "\\n");
      buf = buf.replace("\t", "\\t");
      return buf;
   }

   public static String escapeXmlChars(String str)
   {
      if(null == str)
      {
         return null;
      }

      String buf = str.replace("'", "&apos;");
      buf = buf.replace("&", "&amp;");
      buf = buf.replace("<", "&lt;");
      buf = buf.replace(">", "&gt;");
      buf = buf.replace("\"", "&quot;");
      return buf;
   }
}
