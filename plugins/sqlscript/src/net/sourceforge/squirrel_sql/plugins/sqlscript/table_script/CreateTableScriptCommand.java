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

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Collections;

import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

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
      FrameWorkAcessor.getSQLPanelAPI(_session, _plugin).appendSQLScript(script, true);
      _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
   }


   public String createTableScriptString(IDatabaseObjectInfo[] dbObjs)
   {
      StringBuffer sbScript = new StringBuffer(1000);
      StringBuffer sbConstraints = new StringBuffer(1000);
      try
      {
         SQLConnection conn = _session.getSQLConnection();
         final Statement stmt = conn.createStatement();
         try
         {
            boolean isJdbcOdbc = conn.getSQLMetaData().getJDBCMetaData().getURL().startsWith("jdbc:odbc:");
            if (isJdbcOdbc)
            {
               _session.getMessageHandler().showErrorMessage("JDBC-ODBC Bridge doesn't provide necessary meta data. Script will be incomplete");
            }


            TableScriptConfigCtrl tscc = new TableScriptConfigCtrl(_session.getApplication().getMainFrame());
            if (1 < dbObjs.length)
            {
               tscc.doModal();
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
                  ResultSet rsPks = null;
                  rsPks = conn.getSQLMetaData().getPrimaryKeys(ti);
                  //					String sPkName = "";
                  while (rsPks.next())
                  {
                     //						sPkName = rsPks.getString(6);
                     int iKeySeq = rsPks.getInt(5) - 1;
                     if (pks.size() <= iKeySeq)
                        pks.setSize(iKeySeq + 1);
                     pks.set(iKeySeq, rsPks.getString(4));
                  }
                  rsPks.close();
               }
               ResultSet rsColumns = conn.getSQLMetaData().getColumns(ti);
               while (rsColumns.next())
               {

                  String decimalDigitsString = "";
                  if (false == isJdbcOdbc)
                  {
                     int decimalDigits = rsColumns.getInt(9);
                     decimalDigitsString = 0 == decimalDigits ? "" : "," + decimalDigits;
                  }

                  String sColumnName = rsColumns.getString(4);
                  sbScript.append("\n   ");
                  sbScript.append(sColumnName);
                  sbScript.append(" ");
                  //						int iType = rsColumns.getInt()
                  String sType = rsColumns.getString(6);
                  sbScript.append(sType);
                  String sLower = sType.toLowerCase();
                  if (sLower.indexOf("char") != -1)
                  {
                     sbScript.append("(");
                     sbScript.append(rsColumns.getString(7)).append(decimalDigitsString);
                     sbScript.append(")");
                  }
                  else if (sLower.equals("numeric"))
                  {
                     sbScript.append("(");
                     sbScript.append(rsColumns.getString(7)).append(decimalDigitsString);
                     sbScript.append(")");
                  }
                  else if (sLower.equals("number"))
                  {
                     sbScript.append("(");
                     sbScript.append(rsColumns.getString(7)).append(decimalDigitsString);
                     ;
                     sbScript.append(")");
                  }
                  else if (sLower.equals("decimal"))
                  {
                     sbScript.append("(");
                     sbScript.append(rsColumns.getString(7)).append(decimalDigitsString);
                     sbScript.append(")");
                  }
                  if (pks.size() == 1 && pks.get(0).equals(sColumnName))
                  {
                     sbScript.append(" PRIMARY KEY");
                  }
                  if ("NO".equalsIgnoreCase(rsColumns.getString(18)))
                  {
                     sbScript.append(" not null");
                  }
                  sbScript.append(",");

               }
               rsColumns.close();

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

               sbScript.append("\n)").append(getStatementSeparator()).append("\n");


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
         finally
         {
            try
            {
               stmt.close();
            }
            catch (Exception ignore)
            {
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
      StringBuffer sbToAppend = new StringBuffer();
      DatabaseMetaData metaData = _session.getSQLConnection().getConnection().getMetaData();
      ResultSet indexInfo = metaData.getIndexInfo(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName(), false, false);
      ResultSet primaryKeys = metaData.getPrimaryKeys(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName());

      Vector pkCols = new Vector();
      while(primaryKeys.next())
      {
         pkCols.add(new IndexColInfo(primaryKeys.getString("COLUMN_NAME")));
      }
      primaryKeys.close();

      Collections.sort(pkCols, IndexColInfo.NAME_COMPARATOR);

      Hashtable buf = new Hashtable();

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
         sbToAppend.append(")").append(getStatementSeparator()).append("\n");
      }

      return sbToAppend.toString();
   }

   private String createConstraints(ITableInfo ti, IDatabaseObjectInfo[] selection, boolean includeConstToTablesNotInScript)
      throws SQLException
   {

      StringBuffer sbToAppend = new StringBuffer();
      DatabaseMetaData metaData = _session.getSQLConnection().getConnection().getMetaData();
      ResultSet importedKeys = metaData.getImportedKeys(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName());

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

         sbToAppend.append(")").append(getStatementSeparator()).append("\n");
      }

      return sbToAppend.toString();
   }

   private String getStatementSeparator()
   {
      String statementSeparator = _session.getProperties().getSQLStatementSeparator();

      if (1 < statementSeparator.length())
      {
         statementSeparator = "\n" + statementSeparator + "\n";
      }

      return statementSeparator;
   }
}