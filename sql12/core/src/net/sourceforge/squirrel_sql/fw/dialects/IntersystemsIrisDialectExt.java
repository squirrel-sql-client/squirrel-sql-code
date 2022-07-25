/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.dialects;

/**
 * Dummy Extension for the Intersystems Cache DB
 */
public class IntersystemsIrisDialectExt extends IntersystemsDialectExt
{
   /**
    * The string which identifies this dialect in the dialect chooser.
    *
    * @return a descriptive name that tells the user what database this dialect is design to work with.
    */
   public String getDisplayName()
   {
      return "InterSystems IRIS";
   }

   /**
    * Returns boolean value indicating whether or not this dialect supports the specified database
    * product/version.
    *
    * @param databaseProductName
    *           the name of the database as reported by DatabaseMetaData.getDatabaseProductName()
    * @param databaseProductVersion
    *           the version of the database as reported by DatabaseMetaData.getDatabaseProductVersion()
    * @return true if this dialect can be used for the specified product name and version; false otherwise.
    */
   public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
   {
      if (databaseProductName == null)
      {
         return false;
      }
      if (databaseProductName.trim().startsWith("InterSystems IRIS"))
      {
         // We don't yet have the need to discriminate by version.
         return true;
      }
      return false;
   }

   @Override
   public DialectType getDialectType()
   {
      return DialectType.INTERSYSTEMS_IRIS;
   }
}