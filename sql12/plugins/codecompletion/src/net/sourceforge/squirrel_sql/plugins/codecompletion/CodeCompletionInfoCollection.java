/*
 * Copyright (C) 2003 Gerd Wagner
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

import net.sourceforge.squirrel_sql.client.session.ExtendedTableInfo;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionFunctionInfo;
import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;

import javax.swing.*;
import java.util.Collections;
import java.util.Vector;

public class CodeCompletionInfoCollection
{
	private boolean _loaded = false;
   private Vector _completionInfos = new Vector();
	private ISession _session;

	public CodeCompletionInfoCollection(ISession session)
	{
		_session = session;
	}

	private void load()
	{
		if(!_loaded)
		{
			if(!_session.getSchemaInfo().isLoaded())
			{
				String msg = "Code competion infomation is still being loaded.\nTry again later.";
				JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
				return;
			}

			try
			{
				ExtendedTableInfo[] tables = _session.getSchemaInfo().getExtendedTableInfos();
				for (int i = 0; i < tables.length; i++)
				{
					_completionInfos.add(new CodeCompletionTableInfo(tables[i].getTableName(), tables[i].getTableType()));
				}

				String[] keywords = _session.getSchemaInfo().getKeywords();
				for (int i = 0; i < keywords.length; i++)
				{
					_completionInfos.add(new CodeCompletionKeywordInfo(keywords[i]));
				}

				String[] dataTypes = _session.getSchemaInfo().getDataTypes();
				for (int i = 0; i < dataTypes.length; i++)
				{
					_completionInfos.add(new CodeCompletionTypeInfo(dataTypes[i]));
				}

				String[] functions = _session.getSchemaInfo().getFunctions();
				for (int i = 0; i < functions.length; i++)
				{
					_completionInfos.add(new CodeCompletionFunctionInfo(functions[i]));
				}
				Collections.sort(_completionInfos);
			}
			finally
			{
				_loaded = true;
			}
		}
	}


	public CodeCompletionInfo[] getInfosStartingWith(String prefix)
   {
		load();

      String upperCasePrefix = prefix.trim().toUpperCase();

      if("".equals(upperCasePrefix))
      {
         return (CodeCompletionInfo[])_completionInfos.toArray(new CodeCompletionInfo[0]);
      }

      Vector ret = new Vector();
      for(int i=0; i < _completionInfos.size(); ++i)
      {
         CodeCompletionInfo buf = (CodeCompletionInfo)_completionInfos.get(i);
         if(buf.upperCaseCompletionStringStartsWith(upperCasePrefix))
         {
            ret.add(buf);
         }
      }

      return (CodeCompletionInfo[])ret.toArray(new CodeCompletionInfo[0]);
   }

}
