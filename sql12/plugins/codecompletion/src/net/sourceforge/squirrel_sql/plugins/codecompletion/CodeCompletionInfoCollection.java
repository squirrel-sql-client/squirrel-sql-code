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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAliasParseInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IUDTInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.CodeCompletionPreferences;

import javax.swing.JOptionPane;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class CodeCompletionInfoCollection
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CodeCompletionInfoCollection.class);

   private Hashtable<String, Vector<CodeCompletionInfo>> _completionInfosByCataLogAndSchema = new Hashtable<>();
   private Vector<CodeCompletionTableAliasInfo> _aliasCompletionInfos = new Vector<>();

   private Vector<CodeCompletionSchemaInfo> _schemas = new Vector<>();
   private Vector<CodeCompletionCatalogInfo> _catalogs = new Vector<>();

	private ISession _session;
	private CodeCompletionPlugin _plugin;
   private boolean _useCompletionPrefs;

   private static final int MAX_COMPLETION_INFOS = 300;

	// i18n[codecompletion.listTruncated=Completion list truncated. Narrow by typing to get missing entries.]
	private static final String TOO_MANY_COMPLETION_INFOS = s_stringMgr.getString("codecompletion.listTruncated");
   private CodeCompletionPreferences _prefs;

   public CodeCompletionInfoCollection(ISession session, CodeCompletionPlugin plugin, boolean useCompletionPrefs)
	{
		_session = session;
		_plugin = plugin;
      _useCompletionPrefs = useCompletionPrefs;

      _prefs = (CodeCompletionPreferences) _session.getPluginObject(_plugin, CodeCompletionPlugin.PLUGIN_OBJECT_PREFS_KEY);

      _session.getSchemaInfo().addSchemaInfoUpdateListener(() -> _completionInfosByCataLogAndSchema = new Hashtable<>());
   }

	private void load(String catalog, String schema, boolean showLoadingMessage)
	{
      String key = (catalog + "," + schema).toUpperCase();

		if(null == _completionInfosByCataLogAndSchema.get(key))
		{
			if(!_session.getSchemaInfo().isLoaded())
			{
            if(showLoadingMessage)
            {
               // i18n[codecompletion.beingLoaded=Code competion infomation is still being loaded.\nTry again later.]
               String msg = s_stringMgr.getString("codecompletion.beingLoaded");
               JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
            }
            return;
			}

         Vector<CodeCompletionInfo> completionInfos = new Vector<CodeCompletionInfo>();

         ITableInfo[] tables = _session.getSchemaInfo().getITableInfos(catalog, schema);

         Hashtable<String, CodeCompletionInfo> completionInfoByUcTableName = new Hashtable<String, CodeCompletionInfo>();


         for (int i = 0; i < tables.length; i++)
         {
            String ucTableName = tables[i].getSimpleName().toUpperCase();

            CodeCompletionTableInfo dupl = (CodeCompletionTableInfo) completionInfoByUcTableName.get(ucTableName);

            //SH add the session object as last parameter
            CodeCompletionTableInfo tableInfo = new CodeCompletionTableInfo(tables[i].getSimpleName(),
                  tables[i].getType(),
                  tables[i].getCatalogName(),
                  tables[i].getSchemaName(),
                  _useCompletionPrefs,
                  _prefs,
                  _session);

            if(null != dupl)
            {
               tableInfo.setHasDuplicateNameInDfifferentSchemas();
               dupl.setHasDuplicateNameInDfifferentSchemas();
            }

            completionInfos.add(tableInfo);
            completionInfoByUcTableName.put(ucTableName, tableInfo);
         }

         IProcedureInfo[] storedProceduresInfos = _session.getSchemaInfo().getStoredProceduresInfos(catalog, schema);
         for (int i = 0; i < storedProceduresInfos.length; i++)
         {
            CodeCompletionStoredProcedureInfo buf =
               new CodeCompletionStoredProcedureInfo(storedProceduresInfos[i].getSimpleName(),
                  storedProceduresInfos[i].getProcedureType(),
                  _session,
                  catalog,
                  schema,
                  _useCompletionPrefs,
                  _prefs);
            completionInfos.add(buf);
         }

         if (_prefs.isIncludeUDTs())
         {
            IUDTInfo[] udtInfos = _session.getSchemaInfo().getUDTInfos(catalog, schema);
            for (int i = 0; i < udtInfos.length; i++)
            {
               CodeCompletionUDTInfo buf =
                     new CodeCompletionUDTInfo(udtInfos[i].getSimpleName(),
                           udtInfos[i].getDataType(),
                           catalog,
                           schema
                     );
               completionInfos.add(buf);
            }
         }


         if(null == catalog && null == schema)
         {
            // These objects do not depend on catalogs or schemas.
            // It is enough if we load them once.

            String[] keywords = _session.getSchemaInfo().getKeywords();
            for (int i = 0; i < keywords.length; i++)
            {
               completionInfos.add(new CodeCompletionKeywordInfo(keywords[i]));
            }

            String[] dataTypes = _session.getSchemaInfo().getDataTypes();
            for (int i = 0; i < dataTypes.length; i++)
            {

               if(false == _session.getSchemaInfo().isTable(dataTypes[i]))
               {
                  // For example Postgres returns table names as datatypes.
                  // In those cases this prevents to have the table names twice.
                  completionInfos.add(new CodeCompletionTypeInfo(dataTypes[i]));
               }
            }

            String[] functions = _session.getSchemaInfo().getFunctions();
            for (int i = 0; i < functions.length; i++)
            {
               completionInfos.add(new CodeCompletionFunctionInfo(functions[i]));
            }

            String[] catalogs = _session.getSchemaInfo().getCatalogs();
            for (int i = 0; i < catalogs.length; i++)
            {
               completionInfos.add(new CodeCompletionCatalogInfo(catalogs[i], _prefs));
               _catalogs.add(new CodeCompletionCatalogInfo(catalogs[i], _prefs));
            }

            String[] schemas = _session.getSchemaInfo().getSchemas();
            for (int i = 0; i < schemas.length; i++)
            {
               completionInfos.add(new CodeCompletionSchemaInfo(schemas[i], _prefs));
               _schemas.add(new CodeCompletionSchemaInfo(schemas[i], _prefs));
            }

            Hashtable<String, String> autoCorrections = Main.getApplication().getSyntaxManager().getAutoCorrectData().getAutoCorrectsHash();

            for(Enumeration<String> e=autoCorrections.keys(); e.hasMoreElements();)
            {
               String toCorrect = e.nextElement();
               String correction = autoCorrections.get(toCorrect);

               completionInfos.add(new CodeCompletionAutoCorrectInfo(toCorrect, correction));
            }

         }



         Collections.sort(completionInfos);

         _completionInfosByCataLogAndSchema.put(key, completionInfos);
		}
	}


	public CodeCompletionInfo[] getInfosStartingWith(String catalog, String schema, String prefix, int pos)
   {
		load(catalog, schema, true);

      Vector<CodeCompletionInfo> completionInfos = getCompletionInfos(catalog, schema);

      if(null == completionInfos)
      {
         // CompletionInfos are still loading
         return new CodeCompletionInfo[0];
      }

      String trimmedPrefix = prefix.trim();

      if("".equals(trimmedPrefix))
      {
			Vector<CodeCompletionInfo> buf = new Vector<>();
			buf.addAll(_aliasCompletionInfos);

         if(MAX_COMPLETION_INFOS < completionInfos.size())
         {
            buf.addAll(completionInfos.subList(0,MAX_COMPLETION_INFOS));
            _session.showMessage(TOO_MANY_COMPLETION_INFOS);
         }
         else
         {
            buf.addAll(completionInfos);
         }


         return buf.toArray(new CodeCompletionInfo[0]);
      }

      Vector<CodeCompletionInfo> ret = new Vector<>();

		for(int i=0; i < _aliasCompletionInfos.size(); ++i)
		{
         CodeCompletionTableAliasInfo buf = _aliasCompletionInfos.get(i);
         if (buf.isInStatementOfAlias(pos) && buf.matchesCompletionStringStart(trimmedPrefix, CompletionMatchTypeUtil.matchTypeOf(_useCompletionPrefs, _prefs)))
         {
            ret.add(buf);
         }
		}


      for(int i=0; i < completionInfos.size(); ++i)
      {
         CodeCompletionInfo buf = completionInfos.get(i);
         if (buf.matchesCompletionStringStart(trimmedPrefix, CompletionMatchTypeUtil.matchTypeOf(_useCompletionPrefs, _prefs)))
         {
            ret.add(buf);

            if (MAX_COMPLETION_INFOS < ret.size())
            {
               _session.showMessage(TOO_MANY_COMPLETION_INFOS);
               break;
            }
         }
      }

      return ret.toArray(new CodeCompletionInfo[0]);
   }

   private Vector<CodeCompletionInfo> getCompletionInfos(String catalog, String schema)
   {
      String key = (catalog + "," + schema).toUpperCase();
      Vector<CodeCompletionInfo> ret = _completionInfosByCataLogAndSchema.get(key);

      if(null == ret)
      {
         load(catalog, schema, false);
      }
      ret = _completionInfosByCataLogAndSchema.get(key);

      return ret;
   }

   public void replaceLastAliasInfos(List<TableAliasParseInfo> aliasInfos)
	{
      Vector<CodeCompletionTableAliasInfo> buf = new Vector<>(aliasInfos.size());
      for(TableAliasParseInfo aliasInfo : aliasInfos)
      {
		   buf.add(new CodeCompletionTableAliasInfo(aliasInfo, _useCompletionPrefs, _prefs));
		}
      _aliasCompletionInfos = buf;
	}

   public boolean isCatalog(String name)
   {
      for (int i = 0; i < _catalogs.size(); i++)
      {
         CodeCompletionCatalogInfo info = _catalogs.get(i);
         if(info.getCompareString().equalsIgnoreCase(name))
         {
            return true;
         }
      }
      return false;
   }

   public boolean isSchema(String name)
   {
      for (int i = 0; i < _schemas.size(); i++)
      {
         CodeCompletionSchemaInfo info = _schemas.get(i);
         if(info.getCompareString().equalsIgnoreCase(name))
         {
            return true;
         }
      }
      return false;
   }

   public boolean addCompletionsAtListBegin(String catalog, String schema, CodeCompletionInfo[] completions)
   {
      Vector<CodeCompletionInfo> completionInfos = getCompletionInfos(catalog, schema);

      if(null == completionInfos)
      {
         // CompletionInfos are still loading
         return false;
      }
      else
      {
         Arrays.sort(completions);
         completionInfos.addAll(0,Arrays.asList(completions));
         return true;
      }
   }
}
