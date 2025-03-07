/*
 * Copyright (C) 2005 Gerd Wagner
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
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsAdapter;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAliasParseInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAndAliasParseResult;
import net.sourceforge.squirrel_sql.client.session.schemainfo.synonym.SynonymHandler;
import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.CodeCompletionPreferences;

import java.util.ArrayList;
import java.util.Arrays;


public class StandardCompletorModel
{

   private ISession _session;
   private ILogger _log = LoggerController.createLogger(CodeCompletorModel.class);
   private CodeCompletionInfoCollection _codeCompletionInfos;

	private ArrayList<String> _lastSelectedCompletionNames = new ArrayList<>();
   private CodeCompletionPlugin _plugin;

   private SynonymHandler _synonymHandler;

   StandardCompletorModel(ISession session, CodeCompletionPlugin plugin, CodeCompletionInfoCollection codeCompletionInfos, IIdentifier sqlEntryPanelIdentifier)
   {
      _plugin = plugin;
      try
      {
         _session = session;
         _codeCompletionInfos = codeCompletionInfos;

         _synonymHandler = new SynonymHandler(_session.getMetaData());

			_session.getParserEventsProcessor(sqlEntryPanelIdentifier).addParserEventsListener(new ParserEventsAdapter()
			{
            @Override
            public void tableAndAliasParseResultFound(TableAndAliasParseResult tableAndAliasParseResult)
            {
               onTableAndAliasParseResultFound(tableAndAliasParseResult);
            }
			});
      }
      catch(Exception e)
      {
         _log.error("Could not get DB-Meta data", e);
      }
   }

	private void onTableAndAliasParseResultFound(TableAndAliasParseResult tableAndAliasParseResult)
	{
		_codeCompletionInfos.replaceLastTableAndAliasParseResult(tableAndAliasParseResult);
	}


   CompletionCandidates getCompletionCandidates(String textTillCaret)
   {
      CompletionParser parser = new CompletionParser(textTillCaret);

      ArrayList<CodeCompletionInfo> ret = new ArrayList<>();

      if(false == parser.isQualified())
      {

         ///////////////////////////////////////////////////////////////////////////////
         // The colums of the last completed table/view that match the tableNamePat
         // will be returned on top of the collection
         ret.addAll( getColumnsFromLastSelectionStartingWith(parser.getStringToParse()) );
         //
         //////////////////////////////////////////////////////////////////////////////

         ret.addAll( Arrays.asList(_codeCompletionInfos.getInfosStartingWith(null, null, parser.getStringToParse(), parser.getStringToParsePosition())) );
      }
      else // 1 < buf.size()
      {
         String catalog = null;
         int catAndSchemCount = 0;
         if(_codeCompletionInfos.isCatalog(parser.getToken(0)))
         {
            catalog = parser.getToken(0);
            catAndSchemCount = 1;
         }

         String schema = null;
         if(_codeCompletionInfos.isSchema(parser.getToken(0)))
         {
            schema = parser.getToken(0);
            catAndSchemCount = 1;
         }
         else if(_codeCompletionInfos.isSchema(parser.getToken(1)))
         {
            schema = parser.getToken(1);
            catAndSchemCount = 2;
         }

         // Might also be a catalog or a schema name
         String tableNamePat1 = parser.getToken(parser.size() - 2);
         String colNamePat1 = parser.getToken(parser.size() - 1);

         if(0 < catAndSchemCount)
         {
            String tableNamePat2 = parser.getToken(catAndSchemCount);

            if(parser.size() > catAndSchemCount + 1)
            {
               String colNamePat2 = parser.getToken(catAndSchemCount+1);
               ret.addAll( getColumnsForName(catalog, schema, tableNamePat2, colNamePat2, parser.getStringToParsePosition()) );
            }
            else
            {
               ret.addAll(Arrays.asList(_codeCompletionInfos.getInfosStartingWith(catalog, schema, tableNamePat2, parser.getStringToParsePosition())));
            }

         }
         else
         {
            ret.addAll( getColumnsForName(null, null, tableNamePat1, colNamePat1, parser.getStringToParsePosition()) );
         }
      }

      CodeCompletionInfo[] ccis = ret.toArray(new CodeCompletionInfo[ret.size()]);

      return new CompletionCandidates(ccis, parser.getReplacementStart(), parser.getStringToReplace());
   }


   private ArrayList<? extends CodeCompletionInfo> getColumnsForName(String catalog, String schema, String name, String colNamePat, int colPos)
	{
		CodeCompletionInfo[] infos = _codeCompletionInfos.getInfosStartingWith(catalog, schema, name, colPos);
      CodeCompletionInfo toReturn = null;

      if (colPos != TableAliasParseInfo.POSITION_NON)
		{
			// First check aliases
			for (int j = 0; j < infos.length; j++)
			{
				CodeCompletionInfo info = infos[j];
				if (info instanceof CodeCompletionTableAliasInfo)
				{
					if (info.matchesCompletionString(name))
					{
						// See if this is the same statement
						CodeCompletionTableAliasInfo a = (CodeCompletionTableAliasInfo) info;
						if (a.isInStatementOfAlias(colPos)) // because is now already done in
						{
							toReturn = a;
						}
					}
				}
			}
		}

		if (toReturn == null)
		{
			for (int i = 0; i < infos.length; ++i)
			{
				if (infos[i].matchesCompletionString(name))
				{
               if (ignoreAsCodeCompletionTableInfo(infos[i]))
               {
                  continue;
               }
					toReturn = infos[i];
					break;
				}
			}
		}

		if (toReturn == null)
		{
         return new ArrayList<>();
      }


      return toReturn.getColumns(_session.getSchemaInfo(), colNamePat);
	}


   public boolean ignoreAsCodeCompletionTableInfo(CodeCompletionInfo info)
   {
      if (false == info instanceof CodeCompletionTableInfo)
      {
         return false;
      }

      CodeCompletionTableInfo codeCompletionTableInfo = (CodeCompletionTableInfo) info;
      return _synonymHandler.ignoreAsCodeCompletionTableInfo(codeCompletionTableInfo.getTableType());
   }


   private ArrayList<CodeCompletionInfo> getColumnsFromLastSelectionStartingWith(String colNamePat)
	{
      ArrayList<CodeCompletionInfo> ret = new ArrayList<>();

      for (String lastSelectedCompletionName : _lastSelectedCompletionNames)
      {
         ret.addAll(getColumnsForName(null, null, lastSelectedCompletionName, colNamePat, TableAliasParseInfo.POSITION_NON));
      }

		return ret;
	}


	public SQLTokenListener getSQLTokenListener()
	{
		return name -> performTableOrViewFound(name);
	}

	private void performTableOrViewFound(String name)
	{
      // Makes sure that the last name is always in top of the list.
      _lastSelectedCompletionNames.remove(name);

      _lastSelectedCompletionNames.add(0, name);


      CodeCompletionPreferences prefs = (CodeCompletionPreferences) _session.getPluginObject(_plugin, CodeCompletionPlugin.PLUGIN_OBJECT_PREFS_KEY);
      int maxLastSelectedCompletionNames = prefs.getMaxLastSelectedCompletionNames();
      
      if(maxLastSelectedCompletionNames < _lastSelectedCompletionNames.size())
      {
         _lastSelectedCompletionNames.remove(_lastSelectedCompletionNames.size()-1);
      }
   }

   public CodeCompletionInfoCollection getCodeCompletionInfoCollection()
   {
      return _codeCompletionInfos;
   }
}
