package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

/*
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

public class CreateTableScriptCommand implements ICommand
{
   /**
    * Current session.
    */
   private ISession _session;

   /**
    * Current plugin.
    */
   private final SQLScriptPlugin _plugin;

    /** Logger for this class. */
    private static ILogger s_log = LoggerController.createLogger(CreateTableScriptCommand.class);
   
    /** i18n strings for this class */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(CreateTableScriptCommand.class);

    
    
   /**
    * Ctor specifying the current session.
    */
   public CreateTableScriptCommand(ISession session, SQLScriptPlugin plugin)
   {
      super();
      _session = session;
      _plugin = plugin;
   }

   /**
    * Execute this command. Use the database meta data to construct a Create Table
    * SQL script and place it in the SQL entry panel.
    */
   public void execute()
   {
      IObjectTreeAPI api = FrameWorkAcessor.getObjectTreeAPI(_session, _plugin);
      IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
      scriptTablesToSQLEntryArea(dbObjs);
   }


   public void scriptTablesToSQLEntryArea(IDatabaseObjectInfo[] dbObjs)
   {
      String script = createTableScriptString(dbObjs);
		if(null != script)
		{
			FrameWorkAcessor.getSQLPanelAPI(_session, _plugin).appendSQLScript(script, true);
			_session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
		}
	}


   public String createTableScriptString(IDatabaseObjectInfo[] dbObjs)
   {
      StringBuffer sbScript = new StringBuffer(1000);
      StringBuffer sbConstraints = new StringBuffer(1000);
      SQLConnection conn = _session.getSQLConnection();
      try
      {
        boolean isJdbcOdbc = conn.getSQLMetaData().getURL().startsWith("jdbc:odbc:");
        if (isJdbcOdbc)
        {
           _session.getMessageHandler().showErrorMessage("JDBC-ODBC Bridge doesn't provide necessary meta data. Script will be incomplete");
        }


        TableScriptConfigCtrl tscc = new TableScriptConfigCtrl(_session.getApplication().getMainFrame());
        if (1 < dbObjs.length)
        {
           tscc.doModal();
				if(false == tscc.isOk())
				{
					return null;
				}
			}

        for (int k = 0; k < dbObjs.length; k++)
        {
           if (false == dbObjs[k] instanceof ITableInfo)
           {
              continue;

           }
           ITableInfo ti = (ITableInfo) dbObjs[k];

           String sTable = ti.getSimpleName();
           sbScript.append("CREATE TABLE ");
           sbScript.append(sTable);
           sbScript.append("\n(");

           Vector pks = new Vector();
           if (false == isJdbcOdbc)
           {
              try {
                  PrimaryKeyInfo[] infos = 
                      conn.getSQLMetaData().getPrimaryKey(ti);
                  for (int i = 0; i < infos.length; i++) {
                      int iKeySeq = infos[i].getKeySequence() - 1;
                      if (pks.size() <= iKeySeq)
                         pks.setSize(iKeySeq + 1);
                      pks.set(iKeySeq, infos[i].getColumnName());                      
                  }
              } catch (SQLException e) {
                  //i18n[CreateTableScriptCommand.error.getprimarykey=Unable to get primary key info for table {0}]
                  String msg = 
                      s_stringMgr.getString(
                              "CreateTableScriptCommand.error.getprimarykey", 
                              sTable);
                  s_log.error(msg, e);
              }
           }

           ScriptUtil su = new ScriptUtil();
           TableColumnInfo[] infos = conn.getSQLMetaData().getColumnInfo(ti);
           for (int i = 0; i < infos.length; i++) {
               int decimalDigits = 0;
               if (false == isJdbcOdbc)
               {
                  decimalDigits = infos[i].getDecimalDigits();
               }
               String sColumnName = infos[i].getColumnName();
               String sType = infos[i].getTypeName();
               int colSize = infos[i].getColumnSize();
               String isNullable = infos[i].isNullable();
               
               sbScript.append("\n   ");
               sbScript.append(su.getColumnDef(sColumnName, sType, colSize, decimalDigits));
               
               if (pks.size() == 1 && pks.get(0).equals(sColumnName))
               {
                  sbScript.append(" PRIMARY KEY");
               }
               if ("NO".equalsIgnoreCase(isNullable))
               {
                  sbScript.append(" not null");
               }
               sbScript.append(",");                   
           }
           
           if (pks.size() > 1)
           {
              sbScript.append("\n   CONSTRAINT ");
              sbScript.append(sTable);
              sbScript.append("_PK PRIMARY KEY (");
              for (int i = 0; i < pks.size(); i++)
              {
                 sbScript.append(pks.get(i));
                 sbScript.append(",");
              }
              sbScript.setLength(sbScript.length() - 1);
              sbScript.append("),");
           }
           sbScript.setLength(sbScript.length() - 1);

           sbScript.append("\n)").append(ScriptUtil.getStatementSeparator(_session)).append("\n");


           if(isJdbcOdbc)
           {
              continue;
           }

           String constraints = createConstraints(ti, dbObjs, tscc.includeConstToTablesNotInScript());
           String indexes = createIndexes(ti);

           if(0 < constraints.length())
           {
              if(tscc.isConstAndIndAtEnd())
              {
                 sbConstraints.append(constraints);
              }
              else
              {
                 sbScript.append(constraints);
              }
           }

           if(0 < indexes.length())
           {
              if(tscc.isConstAndIndAtEnd())
              {
                 sbConstraints.append(indexes);
              }
              else
              {
                 sbScript.append(indexes);
              }
           }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         _session.getMessageHandler().showErrorMessage(e);
      }

      return sbScript.append("\n").append(sbConstraints).toString();
   }


   private String createIndexes(ITableInfo ti)
      throws SQLException
   {
      if (ti.getDatabaseObjectType() == DatabaseObjectType.VIEW) {
          return "";
      }
      StringBuffer sbToAppend = new StringBuffer();
      DatabaseMetaData metaData = _session.getSQLConnection().getConnection().getMetaData();
      
      ResultSet primaryKeys = null;
      try {
          primaryKeys = 
              metaData.getPrimaryKeys(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName());
      } catch (SQLException e) {
          //i18n[CreateTableScriptCommand.error.getprimarykey=Unable to get primary key info for table {0}]
          String msg = 
              s_stringMgr.getString(
                      "CreateTableScriptCommand.error.getprimarykey", 
                      ti.getSimpleName());
          s_log.error(msg, e);
          return "";
      }

      Vector pkCols = new Vector();
      while(primaryKeys.next())
      {
         pkCols.add(new IndexColInfo(primaryKeys.getString("COLUMN_NAME")));
      }
      primaryKeys.close();

      Collections.sort(pkCols, IndexColInfo.NAME_COMPARATOR);

      Hashtable buf = new Hashtable();

      ResultSet indexInfo = metaData.getIndexInfo(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName(), false, false);
      
      boolean unique = false;
      while(indexInfo.next())
      {
         String ixName = indexInfo.getString("INDEX_NAME");

         if(null == ixName)
         {
            continue;
         }

         unique = !indexInfo.getBoolean("NON_UNIQUE");

         IndexInfo ixi = (IndexInfo) buf.get(ixName);

         if(null == ixi)
         {
            Vector ixCols = new Vector();
            String table = indexInfo.getString("TABLE_NAME");
            ixCols.add(new IndexColInfo(indexInfo.getString("COLUMN_NAME"), indexInfo.getInt("ORDINAL_POSITION")));
            buf.put(ixName, new IndexInfo(table, ixName, ixCols));
         }
         else
         {
            ixi.cols.add(new IndexColInfo(indexInfo.getString("COLUMN_NAME"), indexInfo.getInt("ORDINAL_POSITION")));
         }
      }
      indexInfo.close();
      IndexInfo[] ixs = (IndexInfo[]) buf.values().toArray(new IndexInfo[buf.size()]);
      for (int i = 0; i < ixs.length; i++)
      {
         Collections.sort(ixs[i].cols, IndexColInfo.NAME_COMPARATOR);

         if(pkCols.equals(ixs[i].cols))
         {
            // Serveral DBs automatically create an index for primary key fields
            // and return this index in getIndexInfo(). We remove this index from the script
            // because it would break the script with an index already exists error.
            continue;
         }

         Collections.sort(ixs[i].cols, IndexColInfo.ORDINAL_POSITION_COMPARATOR);


         sbToAppend.append("CREATE" + (unique ? " UNIQUE ": " ") + "INDEX " + ixs[i].ixName + " ON " + ixs[i].table);

         if(ixs[i].cols.size() == 1)
         {
            sbToAppend.append("(").append(ixs[i].cols.get(0));

            for (int j = 1; j < ixs[i].cols.size(); j++)
            {
               sbToAppend.append(",").append(ixs[i].cols.get(j));
            }
         }
         else
         {
            sbToAppend.append("\n(\n");
            for (int j = 0; j < ixs[i].cols.size(); j++)
            {
               if(j < ixs[i].cols.size() -1)
               {
                  sbToAppend.append("  " + ixs[i].cols.get(j) + ",\n");
               }
               else
               {
                  sbToAppend.append("  " + ixs[i].cols.get(j) + "\n");
               }
            }
         }
         sbToAppend.append(")").append(ScriptUtil.getStatementSeparator(_session)).append("\n");
      }

      return sbToAppend.toString();
   }

   private String createConstraints(ITableInfo ti, IDatabaseObjectInfo[] selection, boolean includeConstToTablesNotInScript)
      throws SQLException
   {

      StringBuffer sbToAppend = new StringBuffer();
      DatabaseMetaData metaData = _session.getSQLConnection().getConnection().getMetaData();
      ResultSet importedKeys = null;
      try {
          importedKeys = 
              metaData.getImportedKeys(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName());
      } catch (SQLException e) {
          //i18n[CreateTableScriptCommand.error.getimportedkeys=Unable to get exported keys info for table {0}]
          String msg = 
              s_stringMgr.getString("CreateTableScriptCommand.error.getimportedkeys",
                                    ti.getSimpleName());
          s_log.error(msg, e);
          return "";
      }

      Hashtable buf = new Hashtable();
      while(importedKeys.next())
      {
         String fkName = importedKeys.getString("FK_NAME");

         ConstraintInfo ci = (ConstraintInfo) buf.get(fkName);

         if(null == ci)
         {
            Vector fkCols = new Vector();
            Vector pkCols = new Vector();
            String fkTable = importedKeys.getString("FKTABLE_NAME");
            String pkTable = importedKeys.getString("PKTABLE_NAME");
            fkCols.add(importedKeys.getString("FKCOLUMN_NAME"));
            pkCols.add(importedKeys.getString("PKCOLUMN_NAME"));
            buf.put(fkName, new ConstraintInfo(fkTable, pkTable, fkName, fkCols, pkCols));
         }
         else
         {
            ci.fkCols.add(importedKeys.getString("FKCOLUMN_NAME"));
            ci.pkCols.add(importedKeys.getString("PKCOLUMN_NAME"));

         }
      }
      importedKeys.close();
      ConstraintInfo[] cis = (ConstraintInfo[]) buf.values().toArray(new ConstraintInfo[buf.size()]);



      for (int i = 0; i < cis.length; i++)
      {


         if(false == includeConstToTablesNotInScript)
         {
            boolean found = false;
            for (int j = 0; j < selection.length; j++)
            {
               if(selection[j] instanceof ITableInfo)
               {
                  if(((ITableInfo)selection[j]).getSimpleName().equalsIgnoreCase(cis[i].pkTable))
                  {
                     found = true;
                     break;
                  }
               }
            }

            if(false == found)
            {
               continue;
            }
         }



         sbToAppend.append("ALTER TABLE " + cis[i].fkTable + "\n");
         sbToAppend.append("ADD CONSTRAINT " + cis[i].fkName + "\n");


         if(cis[i].fkCols.size() == 1)
         {
            sbToAppend.append("FOREIGN KEY (").append(cis[i].fkCols.get(0));

            for (int j = 1; j < cis[i].fkCols.size(); j++)
            {
               sbToAppend.append(",").append(cis[i].fkCols.get(j));
            }
            sbToAppend.append(")\n");

            sbToAppend.append("REFERENCES " + cis[i].pkTable + "(");
            sbToAppend.append(cis[i].pkCols.get(0));
            for (int j = 1; j < cis[i].pkCols.size(); j++)
            {
               sbToAppend.append(",").append(cis[i].pkCols.get(j));
            }
         }
         else
         {
            sbToAppend.append("FOREIGN KEY\n");
            sbToAppend.append("(\n");
            for (int j = 0; j < cis[i].fkCols.size(); j++)
            {
               if(j < cis[i].fkCols.size() -1)
               {
                  sbToAppend.append("  " + cis[i].fkCols.get(j) + ",\n");
               }
               else
               {
                  sbToAppend.append("  " + cis[i].fkCols.get(j) + "\n");
               }
            }
            sbToAppend.append(")\n");

            sbToAppend.append("REFERENCES " + cis[i].pkTable + "\n");
            sbToAppend.append("(\n");
            for (int j = 0; j < cis[i].pkCols.size(); j++)
            {
               if(j < cis[i].pkCols.size() -1)
               {
                  sbToAppend.append("  " + cis[i].pkCols.get(j) + ",\n");
               }
               else
               {
                  sbToAppend.append("  " + cis[i].pkCols.get(j) + "\n");
               }
            }
         }

         sbToAppend.append(")").append(ScriptUtil.getStatementSeparator(_session)).append("\n");
      }

      return sbToAppend.toString();
   }
}