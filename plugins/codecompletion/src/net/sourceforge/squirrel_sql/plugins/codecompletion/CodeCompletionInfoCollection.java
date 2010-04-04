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
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoUpdateListener;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAliasInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.CodeCompletionPreferences;

import javax.swing.*;
import java.util.*;

public class CodeCompletionInfoCollection
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CodeCompletionInfoCollection.class);


   private Hashtable<String, Vector<CodeCompletionInfo>> _completionInfosByCataLogAndSchema =
	    new Hashtable<String, Vector<CodeCompletionInfo>>();
   private Vector<CodeCompletionInfo> _aliasCompletionInfos =
       new Vector<CodeCompletionInfo>();

   private Vector<CodeCompletionSchemaInfo> _schemas =
       new Vector<CodeCompletionSchemaInfo>();
   private Vector<CodeCompletionCatalogInfo> _catalogs =
       new Vector<CodeCompletionCatalogInfo>();

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

      _session.getSchemaInfo().addSchemaInfoUpdateListener(new SchemaInfoUpdateListener()
      {
         public void schemaInfoUpdated()
         {
            _completionInfosByCataLogAndSchema = 
                new Hashtable<String, Vector<CodeCompletionInfo>>();
         }
      });
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

            CodeCompletionTableInfo tableInfo = new CodeCompletionTableInfo(tables[i].getSimpleName(),
                                                                            tables[i].getType(),
                                                                            tables[i].getCatalogName(),
                                                                            tables[i].getSchemaName(),
                                                                            _useCompletionPrefs,
                                                                            _prefs.isShowRemarksInColumnCompletion());

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
               completionInfos.add(new CodeCompletionCatalogInfo(catalogs[i]));
               _catalogs.add(new CodeCompletionCatalogInfo(catalogs[i]));
            }

            String[] schemas = _session.getSchemaInfo().getSchemas();
            for (int i = 0; i < schemas.length; i++)
            {
               completionInfos.add(new CodeCompletionSchemaInfo(schemas[i]));
               _schemas.add(new CodeCompletionSchemaInfo(schemas[i]));
            }

            AutoCorrectProvider autoCorrectProvider =
               (AutoCorrectProvider) _session.getApplication().getPluginManager().bindExternalPluginService("syntax", AutoCorrectProvider.class);

            if(null == autoCorrectProvider)
            {
					// i18n[codecompletion.useSyntaxPlugin=Code completion will work better if you use the Syntax plugin. Get it from squirrelsql.org, it's free!]
					_session.showMessage(s_stringMgr.getString("codecompletion.useSyntaxPlugin"));
            }
            else
            {
               Hashtable<String, String> autoCorrections = autoCorrectProvider.getAutoCorrects();

               for(Enumeration<String> e=autoCorrections.keys(); e.hasMoreElements();)
               {
                  String toCorrect = e.nextElement();
                  String correction = autoCorrections.get(toCorrect);

                  completionInfos.add(new CodeCompletionAutoCorrectInfo(toCorrect, correction));
               }
            }

         }



         Collections.sort(completionInfos);

         _completionInfosByCataLogAndSchema.put(key, completionInfos);
		}
	}


	public CodeCompletionInfo[] getInfosStartingWith(String catalog, String schema, String prefix)
   {
		load(catalog, schema, true);

      Vector<CodeCompletionInfo> completionInfos = getCompletionInfos(catalog, schema);

      if(null == completionInfos)
      {
         // CompletionInfos are still loading
         return new CodeCompletionInfo[0];
      }

      String upperCasePrefix = prefix.trim().toUpperCase();

      if("".equals(upperCasePrefix))
      {
			Vector<CodeCompletionInfo> buf = new Vector<CodeCompletionInfo>();
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

      Vector<CodeCompletionInfo> ret = new Vector<CodeCompletionInfo>();

		for(int i=0; i < _aliasCompletionInfos.size(); ++i)
		{
			CodeCompletionInfo buf = _aliasCompletionInfos.get(i);
			if(buf.upperCaseCompletionStringStartsWith(upperCasePrefix))
			{
				ret.add(buf);
			}
		}


      for(int i=0; i < completionInfos.size(); ++i)
      {
         CodeCompletionInfo buf = completionInfos.get(i);
         if(buf.upperCaseCompletionStringStartsWith(upperCasePrefix))
         {
            ret.add(buf);

            if(MAX_COMPLETION_INFOS < ret.size())
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

   public void replaceLastAliasInfos(TableAliasInfo[] aliasInfos)
	{
		_aliasCompletionInfos = new Vector<CodeCompletionInfo>(aliasInfos.length);

		for (int i = 0; i < aliasInfos.length; i++)
		{
         if(false == aliasInfos[i].aliasName.startsWith("#"))
         {
			   _aliasCompletionInfos.add(new CodeCompletionTableAliasInfo(aliasInfos[i], _useCompletionPrefs, _prefs.isShowRemarksInColumnCompletion()));
         }
		}
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
