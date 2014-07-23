package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import java.util.HashMap;

public class TableEpressionBuilder
{

   private HashMap<String, Integer> uctableName_usageCount = new HashMap<String, Integer>();

   public String getLastTableOrAlias(String tableName)
   {
      Integer count = uctableName_usageCount.get(tableName.toUpperCase());

      if(null == count || 0 == count)
      {
         return tableName;
      }

      return getAliasName(tableName, count);
   }

   public String getTableExpr(String tableName)
   {
      Integer count = uctableName_usageCount.get(tableName.toUpperCase());
      if(null == count)
      {
         uctableName_usageCount.put(tableName.toUpperCase(), 0);
         return tableName;
      }

      ++count;
      String ret = tableName + " AS " + getAliasName(tableName, count);

      uctableName_usageCount.put(tableName.toUpperCase(), count);

      return ret;
   }

   private String getAliasName(String tableName, Integer count)
   {
      String buf = tableName;
      if(buf.lastIndexOf('.') > 0)
      {
         buf = buf.substring(buf.lastIndexOf('.') + 1, buf.length());
      }


      return buf + "_" + count;
   }

}
