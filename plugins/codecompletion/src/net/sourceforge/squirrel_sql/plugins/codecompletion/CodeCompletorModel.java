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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsListener;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsAdapter;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAliasInfo;
import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;
import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfoCollection;

import java.util.*;
import java.sql.SQLException;

public class CodeCompletorModel
{
   private ISession _session;
   private ILogger _log = LoggerController.createLogger(CodeCompletorModel.class);
   private CodeCompletionInfoCollection _codeCompletionInfos;

	private String _lastSelectedCompletionName;

   CodeCompletorModel(ISession session, CodeCompletionInfoCollection codeCompletionInfos)
   {
      try
      {
         _session = session;
         _codeCompletionInfos = codeCompletionInfos;

			_session.getParserEventsProcessor().addParserEventsListener(new ParserEventsAdapter()
			{
				public void aliasesFound(TableAliasInfo[] aliasInfos)
				{
					onAliasesFound(aliasInfos);
				}
			});
      }
      catch(Exception e)
      {
         _log.error("Could not get DB-Meta data", e);
      }
   }

	private void onAliasesFound(TableAliasInfo[] aliasInfos)
	{
		_codeCompletionInfos.replaceLastAliasInfos(aliasInfos);
	}

	CodeCompletionInfo[] getCompletionInfos(String beginning)
   {
      StringTokenizer st = new StringTokenizer(beginning, ".");
      Vector buf = new Vector();
      while(st.hasMoreTokens())
      {
         buf.add(st.nextToken());
      }


      if(beginning.endsWith(".") || 0 == buf.size())
      {
         buf.add("");
      }

      if(1 == buf.size())
      {
         Vector ret = new Vector();

         ///////////////////////////////////////////////////////////////////////////////
         // The colums of the last completed table/view that match the tableNamePat
         // will be returned on top of the collection
         ret.addAll( Arrays.asList(getColumnsFromLastSelectionStartingWith((String)buf.get(0))) );
         //
         //////////////////////////////////////////////////////////////////////////////

         ret.addAll( Arrays.asList(_codeCompletionInfos.getInfosStartingWith(null, null, (String)buf.get(0))) );
         return (CodeCompletionInfo[])ret.toArray(new CodeCompletionInfo[0]);
      }
      else // 1 < buf.size()
      {
         String catalog = null;
         int catAndSchemCount = 0;
         if(_codeCompletionInfos.isCatalog((String)buf.get(0)))
         {
            catalog = (String)buf.get(0);
            catAndSchemCount = 1;
         }

         String schema = null;
         if(_codeCompletionInfos.isSchema((String)buf.get(0)))
         {
            schema = (String)buf.get(0);
            catAndSchemCount = 1;
         }
         else if(_codeCompletionInfos.isSchema((String)buf.get(1)))
         {
            schema = (String)buf.get(1);
            catAndSchemCount = 2;
         }

         // Might also be a catalog or a schema name
         String tableNamePat1 = (String)buf.get(buf.size() - 2);
         String colNamePat1 = (String)buf.get(buf.size() - 1);

         Vector ret = new Vector();

         if(0 < catAndSchemCount)
         {
            String tableNamePat2 = (String)buf.get(catAndSchemCount);

            if(buf.size() > catAndSchemCount + 1)
            {
               String colNamePat2 = (String)buf.get(catAndSchemCount+1);
               ret.addAll(Arrays.asList(getColumnsForName(catalog, schema, tableNamePat2, colNamePat2)));
            }
            else
            {
               ret.addAll(Arrays.asList(_codeCompletionInfos.getInfosStartingWith(catalog, schema, tableNamePat2)));
            }

         }
         ret.addAll(Arrays.asList(getColumnsForName(null, null, tableNamePat1, colNamePat1)));


         return (CodeCompletionInfo[]) ret.toArray(new CodeCompletionInfo[ret.size()]);
      }
   }

	private CodeCompletionInfo[] getColumnsForName(String catalog, String schema, String name, String colNamePat)
	{
		CodeCompletionInfo[] infos = _codeCompletionInfos.getInfosStartingWith(catalog, schema, name);
		String upperCaseTableNamePat = name.toUpperCase();
		for(int i=0; i < infos.length; ++i)
		{
			if( infos[i].upperCaseCompletionStringEquals(upperCaseTableNamePat))
			{
				try
				{
					return infos[i].getColumns(_session.getSQLConnection().getSQLMetaData().getJDBCMetaData(), catalog, schema, colNamePat);
				}
				catch(SQLException e)
				{
					_log.error("Error retrieving columns", e);
				}
			}
		}
		return new CodeCompletionInfo[0];
	}



   private CodeCompletionInfo[] getColumnsFromLastSelectionStartingWith(String colNamePat)
   {
      if(null != _lastSelectedCompletionName)
      {
         return getColumnsForName(null, null, _lastSelectedCompletionName, colNamePat);
      }

      return new CodeCompletionInfo[0];
   }


	public SQLTokenListener getSQLTokenListener()
	{
		return
			new SQLTokenListener()
			{
				public void tableOrViewFound(String name)
				{performTableOrViewFound(name);}
			};
	}

	private void performTableOrViewFound(String name)
	{
   	_lastSelectedCompletionName = name;
	}


////////////////////////////////////////////////////////////
// TEST ONLY
///////////////////////////////////////////////////////////


//   Vector _data = null;
//   String[] getCompletionStrings(String beginning)
//   {
//      if(null == _data)
//      {
//         _data = new Vector();
//         for(int i=0; i < 20; ++i)
//         {
//            _data.add("Birgit" + i);
//            _data.add("Sara" + i);
//            _data.add("Anna" + i);
//            _data.add("Gerd" + i);
//         }
//      }
//      return (String[])_data.toArray(new String[0]);
//   }
//
//   String getCompletionStringAt(int index)
//   {
//      return (String)_data.get(index);
//   }
}
