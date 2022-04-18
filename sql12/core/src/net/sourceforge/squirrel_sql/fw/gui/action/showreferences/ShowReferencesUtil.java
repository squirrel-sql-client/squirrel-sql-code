package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;
import net.sourceforge.squirrel_sql.fw.gui.action.InStatColumnInfo;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowReferencesUtil
{
   public static References getReferences(ResultMetaDataTable globalDbTable, ISession session)
   {
      return new References(getReferenceKeys(globalDbTable, session, ReferenceType.EXPORTED_KEY), getReferenceKeys(globalDbTable, session, ReferenceType.IMPORTED_KEY));
   }


   public static HashMap<String, ReferenceKey> getReferenceKeys(ResultMetaDataTable globalDbTable, ISession session, ReferenceType referenceType)
   {
      try
      {
         DatabaseMetaData jdbcMetaData = session.getSQLConnection().getSQLMetaData().getJDBCMetaData();
         ResultSet refrenceKeys;

         if (referenceType == ReferenceType.IMPORTED_KEY)
         {
            refrenceKeys = jdbcMetaData.getImportedKeys(globalDbTable.getCatalogName(), globalDbTable.getSchemaName(), globalDbTable.getTableName());
         }
         else
         {
            refrenceKeys = jdbcMetaData.getExportedKeys(globalDbTable.getCatalogName(), globalDbTable.getSchemaName(), globalDbTable.getTableName());
         }

         HashMap<String, ReferenceKey> fkName_refrenceKey = new HashMap<String, ReferenceKey>();
         while(refrenceKeys.next())
         {
            String fkName = refrenceKeys.getString("FK_NAME");
            String fktable_cat = refrenceKeys.getString("FKTABLE_CAT");
            String fktable_schem = refrenceKeys.getString("FKTABLE_SCHEM");
            String fktable_name = refrenceKeys.getString("FKTABLE_NAME");

            String pktable_cat = refrenceKeys.getString("PKTABLE_CAT");
            String pktable_schem = refrenceKeys.getString("PKTABLE_SCHEM");
            String pktable_name = refrenceKeys.getString("PKTABLE_NAME");

            String fkcolumn_name = refrenceKeys.getString("FKCOLUMN_NAME");
            String pkcolumn_name = refrenceKeys.getString("PKCOLUMN_NAME");

            ReferenceKey referenceKey = fkName_refrenceKey.get(fkName);

            if(null == referenceKey)
            {
               referenceKey = new ReferenceKey(fkName, fktable_cat, fktable_schem, fktable_name, pktable_cat, pktable_schem, pktable_name, referenceType);
               fkName_refrenceKey.put(fkName, referenceKey);
            }
            referenceKey.addColumn(fkcolumn_name, pkcolumn_name);
         }
         refrenceKeys.close();

         return fkName_refrenceKey;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static JoinSQLInfo generateJoinSQLInfo(Object[] path)
   {
      path = removeReferenceTypeNodes(path);

      ArrayUtils.reverse(path);

      String sql =  "";

      String tableToBeEdited = null;

      TableEpressionBuilder teb = new TableEpressionBuilder();

      if (1 == path.length)
      {
         RootTable rootTable = (RootTable) ((DefaultMutableTreeNode) path[0]).getUserObject();

         sql = "SELECT * FROM "  + teb.getTableExpr(rootTable.getGlobalDbTable().getQualifiedName());

      }
      else
      {
         for (int i = 0; i < path.length - 1; i++) // path.length - 1 because we exclude root
         {
            ReferenceKey referenceKey = (ReferenceKey) ((DefaultMutableTreeNode)path[i]).getUserObject();

            if(i == 0)
            {
               String distinct = "";

               if (ReferenceType.EXPORTED_KEY == referenceKey.getReferenceType())
               {
                  tableToBeEdited = referenceKey.getFkResultMetaDataTable().getQualifiedName();
               }
               else
               {
                  distinct = " DISTINCT ";
                  tableToBeEdited = referenceKey.getPkResultMetaDataTable().getQualifiedName();
               }
               sql += "SELECT " + distinct + tableToBeEdited + ".* FROM " + teb.getTableExpr(tableToBeEdited);
            }

            if (ReferenceType.EXPORTED_KEY == referenceKey.getReferenceType())
            {
               sql += " INNER JOIN " + teb.getTableExpr(referenceKey.getPkResultMetaDataTable().getQualifiedName());
            }
            else
            {
               sql += " INNER JOIN " + teb.getTableExpr(referenceKey.getFkResultMetaDataTable().getQualifiedName());
            }

            String joinFields = null;
            for (Map.Entry<String, String> fk_pk : referenceKey.getFkColumn_pkcolumnMap().entrySet())
            {
               if (null == joinFields)
               {
                  joinFields = " ON " + teb.getLastTableOrAlias(referenceKey.getPkResultMetaDataTable().getQualifiedName()) + "." + fk_pk.getValue() +
                        " = " + teb.getLastTableOrAlias(referenceKey.getFkResultMetaDataTable().getQualifiedName()) + "." + fk_pk.getKey();
               }
               else
               {
                  joinFields += " AND " + teb.getLastTableOrAlias(referenceKey.getPkResultMetaDataTable().getQualifiedName()) + "." + fk_pk.getValue() +
                        " = " + teb.getLastTableOrAlias(referenceKey.getFkResultMetaDataTable().getQualifiedName()) + "." + fk_pk.getKey();
               }

            }

            sql += joinFields;

         }
      }

      RootTable rootTable = (RootTable) ((DefaultMutableTreeNode)path[path.length - 1]).getUserObject();

      for (int j = 0; j < rootTable.getInStatColumnInfos().size(); j++)
      {
         InStatColumnInfo inStatColumnInfo = rootTable.getInStatColumnInfos().get(j);

         if (0 == j)
         {
            sql += " WHERE " + teb.getLastTableOrAlias(rootTable.getGlobalDbTable().getQualifiedName()) + "." + inStatColumnInfo.getColDef().getColumnName() + " IN " + inStatColumnInfo.getInstat();
         }
         else
         {
            sql += " AND " + teb.getLastTableOrAlias(rootTable.getGlobalDbTable().getQualifiedName()) + "." + inStatColumnInfo.getColDef().getColumnName() + " IN " + inStatColumnInfo.getInstat();
         }
      }


      return new JoinSQLInfo(sql, tableToBeEdited);
   }

   private static Object[] removeReferenceTypeNodes(Object[] path)
   {
      ArrayList ret = new ArrayList();

      for (Object obj : path)
      {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;

         if(false == node.getUserObject() instanceof ReferenceType)
         {
            ret.add(node);
         }
      }

      return ret.toArray(new Object[ret.size()]);

   }

}
