/*
 * Copyright (C) 2004 Gerd Wagner
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.plugins.codecompletion;


import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.CodeCompletionPreferences;

public class CodeCompletionCatalogInfo extends CodeCompletionInfo
{
   private String _catalog;
   private CodeCompletionPreferences _prefs;

   public CodeCompletionCatalogInfo(String catalog, CodeCompletionPreferences prefs)
   {
      _catalog = catalog;
      _prefs = prefs;
   }

   public String getCompareString()
   {
      return _catalog;
   }

   @Override
   public String getCompletionString()
   {
      return CompletionCaseSpelling.valueOf(_prefs.getCatalogCaseSpelling()).adjustCaseSpelling(super.getCompletionString());
   }

   public String toString()
   {
      return _catalog  + " (CATALOG)";
   }
}