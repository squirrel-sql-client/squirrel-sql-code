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
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAliasInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;

import javax.swing.*;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

public class CodeCompletionInfoCollection
{
   private Hashtable _completionInfosByCataLogAndSchema = new Hashtable();
   private Vector _aliasCompletionInfos = new Vector();

   private Vector _schemas = new Vector();
   private Vector _catalogs = new Vector();

	private ISession _session;

   public CodeCompletionInfoCollection(ISession session)
	{
		_session = session;
	}

	private void load(String catalog, String schema)
	{
      String key = catalog + "," + schema;

		if(null == _completionInfosByCataLogAndSchema.get(key))
		{
			if(!_session.getSchemaInfo().isLoaded())
			{
				String msg = "Code competion infomation is still being loaded.\nTry again later.";
				JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
				return;
			}

         Vector completionInfos = new Vector();

         ExtendedTableInfo[] tables = _session.getSchemaInfo(catalog, schema).getExtendedTableInfos();
         for (int i = 0; i < tables.length; i++)
         {
            completionInfos.add(new CodeCompletionTableInfo(tables[i].getTableName(), tables[i].getTableType()));
         }

         IProcedureInfo[] storedProceduresInfos = _session.getSchemaInfo(catalog, schema).getStoredProceduresInfos();
         for (int i = 0; i < storedProceduresInfos.length; i++)
         {
            CodeCompletionStoredProcedureInfo buf =
               new CodeCompletionStoredProcedureInfo(storedProceduresInfos[i].getSimpleName(), 
                                                     storedProceduresInfos[i].getType(),
                                                     _session,
                                                     catalog,
                                                     schema);
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
               completionInfos.add(new CodeCompletionTypeInfo(dataTypes[i]));
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
         }



         Collections.sort(completionInfos);

         _completionInfosByCataLogAndSchema.put(key, completionInfos);
		}
	}


	public CodeCompletionInfo[] getInfosStartingWith(String catalog, String schema, String prefix)
   {
		load(catalog, schema);

      Vector completionInfos = getCompletionInfos(catalog, schema);

      String upperCasePrefix = prefix.trim().toUpperCase();

      if("".equals(upperCasePrefix))
      {
			Vector buf = new Vector();
			buf.addAll(_aliasCompletionInfos);
			buf.addAll(completionInfos);
         return (CodeCompletionInfo[])buf.toArray(new CodeCompletionInfo[0]);
      }

      Vector ret = new Vector();

		for(int i=0; i < _aliasCompletionInfos.size(); ++i)
		{
			CodeCompletionInfo buf = (CodeCompletionInfo)_aliasCompletionInfos.get(i);
			if(buf.upperCaseCompletionStringStartsWith(upperCasePrefix))
			{
				ret.add(buf);
			}
		}


      for(int i=0; i < completionInfos.size(); ++i)
      {
         CodeCompletionInfo buf = (CodeCompletionInfo)completionInfos.get(i);
         if(buf.upperCaseCompletionStringStartsWith(upperCasePrefix))
         {
            ret.add(buf);
         }
      }

      return (CodeCompletionInfo[])ret.toArray(new CodeCompletionInfo[0]);
   }

   private Vector getCompletionInfos(String catalog, String schema)
   {
      String key = catalog + "," + schema;
      Vector ret = (Vector) _completionInfosByCataLogAndSchema.get(key);

      if(null == ret)
      {
         load(catalog, schema);
      }
      ret = (Vector) _completionInfosByCataLogAndSchema.get(key);

      return ret;
   }

   public void replaceLastAliasInfos(TableAliasInfo[] aliasInfos)
	{
		_aliasCompletionInfos = new Vector(aliasInfos.length);

		for (int i = 0; i < aliasInfos.length; i++)
		{
			_aliasCompletionInfos.add(new CodeCompletionTableAliasInfo(aliasInfos[i]));
		}
	}

   public boolean isCatalog(String name)
   {
      for (int i = 0; i < _catalogs.size(); i++)
      {
         CodeCompletionCatalogInfo info = (CodeCompletionCatalogInfo) _catalogs.get(i);
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
         CodeCompletionSchemaInfo info = (CodeCompletionSchemaInfo) _schemas.get(i);
         if(info.getCompareString().equalsIgnoreCase(name))
         {
            return true;
         }
      }
      return false;
   }

}
