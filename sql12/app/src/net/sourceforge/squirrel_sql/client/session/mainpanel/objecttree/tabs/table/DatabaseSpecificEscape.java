package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

/*
 * Copyright (C) 2005 Gerd Wagner, Adin Aronson
 * gerdwagner@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

public class DatabaseSpecificEscape
{
   public static final IEscape[] _escapes = 
      new IEscape[]
      {
         new PostgreSQLEscape(),
         new MckoiSQLEscape()
      };

   private static interface IEscape
   {
      public boolean productMatches(String databaseProductName);
      public String escapeSQL(String sql);
   }


   public static String escapeSQL(String sql, String databaseProductName)
   {
      for (int i = 0; i < _escapes.length; i++)
      {
         if(_escapes[i].productMatches(databaseProductName))
         {
            return _escapes[i].escapeSQL(sql);
         }
      }

      return sql;
   }

   public static class PostgreSQLEscape implements IEscape
   {
      public boolean productMatches(String databaseProductName)
      {
         return "PostgreSQL".equals(databaseProductName);
      }

      public String escapeSQL(String sql)
      {
         return sql.replaceAll("\\\\","\\\\\\\\");
      }
   }

   public static class MckoiSQLEscape implements IEscape
   {
      public boolean productMatches(String databaseProductName)
      {
         return databaseProductName.startsWith("Mckoi SQL Database");
      }

      public String escapeSQL(String sql)
      {
         return sql.replaceAll("\\\\","\\\\\\\\");
      }
   }



}
