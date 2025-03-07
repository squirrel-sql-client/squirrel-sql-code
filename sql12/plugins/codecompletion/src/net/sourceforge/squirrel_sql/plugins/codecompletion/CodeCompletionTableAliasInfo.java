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

import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAliasParseInfo;
import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.CodeCompletionPreferences;

public class CodeCompletionTableAliasInfo extends CodeCompletionTableInfo
{
   private TableAliasParseInfo _aliasInfo;

   private String _toString;

   public CodeCompletionTableAliasInfo(TableAliasParseInfo aliasInfo, boolean useCompletionPrefs, CodeCompletionPreferences prefs)
   {
      //SH add last parameter ISession. Used in the super class
      super(aliasInfo.getTableQualifier().getTableName(), "TABLE", aliasInfo.getTableQualifier().getCatalog(), aliasInfo.getTableQualifier().getSchema(), useCompletionPrefs, prefs, null);
      _aliasInfo = aliasInfo;
      _toString = _aliasInfo.getAliasName() + " (Alias for " + _aliasInfo.getTableName() + ")";
	}

	public String getCompareString()
	{
		return _aliasInfo.getAliasName();
	}

	public String toString()
	{
      return _toString;
	}

    public int getStatBegin()
    {
        return _aliasInfo.getStatBegin();
    }

   public boolean isInStatementOfAlias(int colPos)
   {
      return _aliasInfo.isInStatementOfAlias(colPos);
   }
}
