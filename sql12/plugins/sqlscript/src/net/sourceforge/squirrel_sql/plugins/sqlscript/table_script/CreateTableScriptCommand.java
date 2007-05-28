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
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferencesManager;

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

    private static SQLScriptPreferenceBean prefs = 
            SQLScriptPreferencesManager.getPreferences();
    
    
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


   public void scriptTablesToSQLEntryArea(final IDatabaseObjectInfo[] dbObjs)
   {
       _session.getApplication().getThreadPool().addTask(new Runnable() {
           public void run() {
               final String script = createTableScriptString(dbObjs);
                if(null != script)
                {
                    GUIUtils.processOnSwingEventThread(new Runnable() {
                        public void run() {
                            ISQLPanelAPI api = 
                                FrameWorkAcessor.getSQLPanelAPI(_session, _plugin);
                            api.appendSQLScript(script, true);
                            _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);                            
                        }
                    });
                }               
           }
       });
	}

   public String createTableScriptString(IDatabaseObjectInfo dbObj) {
       return createTableScriptString(new IDatabaseObjectInfo[] { dbObj });
   }
   
   public String createTableScriptString(IDatabaseObjectInfo[] dbObjs)
   {
      StringBuffer sbScript = new StringBuffer(1000);
      StringBuffer sbConstraints = new StringBuffer(1000);
      ISQLDatabaseMetaData md = _session.getMetaData();
      try
      {
        boolean isJdbcOdbc = md.getURL().startsWith("jdbc:odbc:");
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

           String sTable = ScriptUtil.getTableName(ti);
           sbScript.append("CREATE TABLE ");
           sbScript.append(sTable);
           sbScript.append("\n(");

           Vector pks = new Vector();
           if (false == isJdbcOdbc)
           {
              try {
                  PrimaryKeyInfo[] infos = md.getPrimaryKey(ti);
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
           TableColumnInfo[] infos = md.getColumnInfo(ti);
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

      Vector<IndexColInfo> pkCols = new Vector<IndexColInfo>();
      while(primaryKeys.next())
      {
         String column = primaryKeys.getString(4); // COLUMN_NAME
         pkCols.add(new IndexColInfo(column));
      }
      primaryKeys.close();

      Collections.sort(pkCols, IndexColInfo.NAME_COMPARATOR);

      Hashtable<String, IndexInfo> buf = new Hashtable<String, IndexInfo>();

      ResultSet indexInfo = metaData.getIndexInfo(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName(), false, false);
      
      boolean unique = false;
      while(indexInfo.next())
      {
         String table = indexInfo.getString(3); // TABLE_NAME
         unique = !indexInfo.getBoolean(4); // NON_UNIQUE
         String ixName = indexInfo.getString(6); // INDEX_NAME
         String column = indexInfo.getString(9); // COLUMN_NAME 
         int ordinalPosition = indexInfo.getInt(8); // ORDINAL_POSITION
         
         if(null == ixName)
         {
            continue;
         }
         IndexInfo ixi = buf.get(ixName);

         if(null == ixi)
         {
            Vector<IndexColInfo> ixCols = new Vector<IndexColInfo>();
            
            ixCols.add(new IndexColInfo(column, ordinalPosition));
            buf.put(ixName, new IndexInfo(table, ixName, ixCols));
         }
         else
         {
            ixi.cols.add(new IndexColInfo(column, ordinalPosition));
         }
      }
      indexInfo.close();
      IndexInfo[] ixs = buf.values().toArray(new IndexInfo[buf.size()]);
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

      Hashtable<String, ConstraintInfo> buf = 
          new Hashtable<String, ConstraintInfo>();
      while(importedKeys.next())
      {
         String pkTable = importedKeys.getString(3);   // PKTABLE_NAME
         String pkColName = importedKeys.getString(4); // PKCOLUMN_NAME
         String fkTable = importedKeys.getString(7);   // FKTABLE_NAME         
         String fkColName = importedKeys.getString(8); // FKCOLUMN_NAME
         short updateRule = importedKeys.getShort(10); // UPDATE_RULE         
         short deleteRule = importedKeys.getShort(11); // DELETE_RULE
         String fkName = importedKeys.getString(12);   // FK_NAME
         
         ConstraintInfo ci = buf.get(fkName);

         if(null == ci)
         {
            Vector<String> fkCols = new Vector<String>();
            Vector<String> pkCols = new Vector<String>();
            fkCols.add(fkColName);
            pkCols.add(pkColName);
            ci = new ConstraintInfo(fkTable, 
                                    pkTable, 
                                    fkName, 
                                    fkCols, 
                                    pkCols,
                                    deleteRule,
                                    updateRule);
            buf.put(fkName, ci);
         }
         else
         {
            ci.fkCols.add(fkColName);
            ci.pkCols.add(pkColName);

         }
      }
      importedKeys.close();
      ConstraintInfo[] cis = buf.values().toArray(new ConstraintInfo[buf.size()]);



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
        
         sbToAppend.append(")");
         
         if (prefs.isDeleteRefAction()) {
             sbToAppend.append(" ON DELETE ");
             sbToAppend.append(prefs.getRefActionByType(prefs.getDeleteAction()));
         } else {
             switch (cis[i].deleteRule) {
                 case DatabaseMetaData.importedKeyCascade:
                     sbToAppend.append(" ON DELETE CASCADE");
                     break;
                 case DatabaseMetaData.importedKeySetNull:
                     sbToAppend.append(" ON DELETE SET NULL");
                     break;
                 case DatabaseMetaData.importedKeySetDefault:
                     sbToAppend.append(" ON DELETE SET DEFAULT");
                     break;
                 case DatabaseMetaData.importedKeyRestrict:
                 case DatabaseMetaData.importedKeyNoAction:
                 default:
                     sbToAppend.append(" ON DELETE NO ACTION");
             }
         }
         if (prefs.isUpdateRefAction()) {
             sbToAppend.append(" ON UPDATE ");
             sbToAppend.append(prefs.getRefActionByType(prefs.getUpdateAction()));             
         } else {
             switch (cis[i].updateRule) {
                 case DatabaseMetaData.importedKeyCascade:
                     sbToAppend.append(" ON UPDATE CASCADE");
                     break;
                 case DatabaseMetaData.importedKeySetNull:
                     sbToAppend.append(" ON UPDATE SET NULL");
                     break;
                 case DatabaseMetaData.importedKeySetDefault:
                     sbToAppend.append(" ON UPDATE SET DEFAULT");
                     break;
                 case DatabaseMetaData.importedKeyRestrict:
                 case DatabaseMetaData.importedKeyNoAction:
                 default:
                     sbToAppend.append(" ON UPDATE NO ACTION");
             }             
         }
         sbToAppend.append(ScriptUtil.getStatementSeparator(_session)).append("\n");
      }

      return sbToAppend.toString();
   }
}