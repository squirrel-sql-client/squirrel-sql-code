package org.squirrelsql.session.schemainfo;

import org.squirrelsql.services.CaseInsensitiveString;
import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.TableInfo;

import java.io.Serializable;
import java.util.*;

public class CaseInsensitiveCache implements Serializable
{
   private HashMap<CaseInsensitiveString, List<TableInfo>> _ciTableNames = new HashMap<>();
   private HashMap<CaseInsensitiveString, String> _ciProcedureName_csProcedureName = new HashMap<>();
   private HashSet<CaseInsensitiveString> _ciKeywords = new HashSet<>();
   private HashSet<CaseInsensitiveString> _ciColumns = new HashSet<>();

   private CaseInsensitiveString _buf = new CaseInsensitiveString();

   public void addProc(String s)
   {
      _ciProcedureName_csProcedureName.put(new CaseInsensitiveString(s), s);
   }

   public void addKeyword(String s)
   {
      _ciKeywords.add(new CaseInsensitiveString(s));
   }

   public void addTable(TableInfo ti)
   {
      CaseInsensitiveString tableName = new CaseInsensitiveString(ti.getName());
      List<TableInfo> tableInfos = _ciTableNames.get(tableName);

      if(null == tableInfos)
      {
         tableInfos = new ArrayList<>();
         _ciTableNames.put(tableName, tableInfos);
      }
      tableInfos.add(ti);
   }

   public void addColumn(String s)
   {
      _ciColumns.add(new CaseInsensitiveString(s));
   }


   public List<TableInfo> getTables(String tableName)
   {
      return getTables(tableName.toCharArray(), 0, tableName.length());
   }


   public List<TableInfo> getTables(char[] buffer, int offset, int len)
   {
      return _ciTableNames.get(_buf.setCharBuffer(buffer, offset, len));
   }

   public boolean isProcedure(char[] buffer, int offset, int len)
   {
      return _ciProcedureName_csProcedureName.containsKey(_buf.setCharBuffer(buffer, offset, len));
   }

   public boolean isKeyword(char[] buffer, int offset, int len)
   {
      return _ciKeywords.contains(_buf.setCharBuffer(buffer, offset, len));
   }

   public boolean isColumn(char[] buffer, int offset, int len)
   {
      return _ciColumns.contains(_buf.setCharBuffer(buffer, offset, len));
   }

   public void removeTable(TableInfo tableInfo)
   {
      CaseInsensitiveString ciTableName = new CaseInsensitiveString(tableInfo.getName());

      List<TableInfo> tableInfos = _ciTableNames.get(ciTableName);

      if(null == tableInfos)
      {
         return;
      }

      for (TableInfo info : tableInfos)
      {
         for (ColumnInfo columnInfo : info.getColumnsIfLoaded())
         {
            _ciColumns.remove(new CaseInsensitiveString(columnInfo.getColName()));
         }
      }

      _ciTableNames.remove(ciTableName);
   }

   public void removeProc(String procedureName)
   {
      _ciProcedureName_csProcedureName.remove(new CaseInsensitiveString(procedureName));
   }

   public List<String> getMatchingCaseSensitiveTableNames(String tableName)
   {
      List<TableInfo> tableInfos = _ciTableNames.get(new CaseInsensitiveString(tableName));


      if(null == tableInfos)
      {
         return Arrays.asList(tableName);
      }


      ArrayList<String> ret = new ArrayList<>();
      for (TableInfo tableInfo : tableInfos)
      {
         ret.add(tableInfo.getName());
      }

      return ret;

   }

   public String getCaseSensitiveProcedureName(String procedureName)
   {
      String ret = _ciProcedureName_csProcedureName.get(new CaseInsensitiveString(procedureName));

      if(null == ret)
      {
         ret = procedureName;
      }

      return ret;
   }
}
