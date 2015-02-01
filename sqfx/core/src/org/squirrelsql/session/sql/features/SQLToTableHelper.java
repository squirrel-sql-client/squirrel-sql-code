package org.squirrelsql.session.sql.features;

import org.squirrelsql.table.tableedit.SQLResultMetaDataFacade;

import java.util.ArrayList;
import java.util.Hashtable;

public class SQLToTableHelper
{
   private Hashtable<String, String> _uniqueColNames = new Hashtable<String, String>();

   private ArrayList<String> _columnDefs = new ArrayList<>();
   private ArrayList<String> _columnNames = new ArrayList<>();
   private final String _sql;


   public SQLToTableHelper(String tableName, SQLResultMetaDataFacade metaData)
   {
      StringBuffer sbCreate = new StringBuffer();
      sbCreate.append("CREATE TABLE ").append(tableName).append('\n');
      sbCreate.append("(\n");

      sbCreate.append("   ").append(createColumnDef(metaData, 0));

      for(int i=1; i < metaData.getColumnCount(); ++i)
      {
         sbCreate.append(",\n");
         sbCreate.append("   ").append(createColumnDef(metaData, i));
      }

      _sql = sbCreate.append("\n)").toString();


   }

   private String createColumnDef(SQLResultMetaDataFacade metaData, int colIx)
   {
      String colName = metaData.getColumnNameAt(colIx);
      String colType = metaData.getSqlTypeNameAt(colIx);
      int colSize = metaData.getColumnDisplaySizeAt(colIx);
      int decimalDigits =  metaData.getColumnScaleAt(colIx);

      colName = makeColumnNameUnique(colName);

      String ret = createColumnDefinitionString(colName, colType, colSize, decimalDigits);

      _columnDefs.add(ret);
      _columnNames.add(colName);

      return ret;
   }

   public String getCreateSql()
   {
      return _sql;
   }

   private String createColumnDefinitionString(String sColumnName, String sType, int columnSize, int decimalDigits)
   {
      String decimalDigitsString = 0 == decimalDigits ? "" : "," + decimalDigits;

      StringBuffer sbColDef = new StringBuffer();
      String sTypeLower = sType.toLowerCase();
      sbColDef.append(sColumnName).append(" ");
      sbColDef.append(sType);

      if (sTypeLower.indexOf("char") != -1)
      {
         sbColDef.append("(");
         //sbColDef.append(columnSize).append(decimalDigitsString);
         sbColDef.append(columnSize);
         sbColDef.append(")");
      }
      else if (sTypeLower.equals("numeric"))
      {
         sbColDef.append("(");
         sbColDef.append(columnSize).append(decimalDigitsString);
         sbColDef.append(")");
      }
      else if (sTypeLower.equals("number"))
      {
         sbColDef.append("(");
         sbColDef.append(columnSize).append(decimalDigitsString);
         sbColDef.append(")");
      }
      else if (sTypeLower.equals("decimal"))
      {
         sbColDef.append("(");
         sbColDef.append(columnSize).append(decimalDigitsString);
         sbColDef.append(")");
      }

      return sbColDef.toString();
   }


   private String makeColumnNameUnique(String sColumnName)
   {
      return makeColumnNameUniqueIntern(sColumnName, 0);
   }

   private String makeColumnNameUniqueIntern(String sColumnName, int postFixSeed)
   {
      String upperCaseColumnName = sColumnName.toUpperCase();
      String sRet = sColumnName;

      if(0 < postFixSeed)
      {
         sRet += "_" + postFixSeed;
         upperCaseColumnName += "_" + postFixSeed;
      }

      if(null == _uniqueColNames.get(upperCaseColumnName))
      {
         _uniqueColNames.put(upperCaseColumnName,upperCaseColumnName);
         return sRet;
      }
      else
      {
         return makeColumnNameUniqueIntern(sColumnName, ++postFixSeed);
      }
   }

   public boolean columnsMatch(SQLResultMetaDataFacade metaDataOfAlreadyExisitingTable)
   {
      SQLToTableHelper buf = new SQLToTableHelper("dum", metaDataOfAlreadyExisitingTable);

      if(buf._columnDefs.size() != _columnDefs.size())
      {
         return false;
      }

      for (int i = 0; i < _columnDefs.size(); i++)
      {
         if(false == buf._columnDefs.get(i).equalsIgnoreCase(_columnDefs.get(i)))
         {
            return false;
         }
      }

      return true;

   }

   public String getInsertSql(String tableName, String sql)
   {
      String ret = "INSERT INTO " + tableName + "(";

      for (int i = 0; i < _columnNames.size(); i++)
      {
         if (0 == i)
         {
            ret += _columnNames.get(i);
         }
         else
         {
            ret += "," + _columnNames.get(i);
         }
      }

      ret += ")\n" + sql.trim();


      return ret;
   }
}
