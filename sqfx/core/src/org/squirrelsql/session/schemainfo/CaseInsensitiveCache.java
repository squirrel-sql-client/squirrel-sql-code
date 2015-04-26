package org.squirrelsql.session.schemainfo;

import org.squirrelsql.services.CaseInsensitiveString;
import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.TableInfo;

import java.io.Serializable;
import java.util.*;

public class CaseInsensitiveCache implements Serializable
{
   public static final int DUMMY_PROCEDURE_TYPE = -23366;

   private HashMap<CaseInsensitiveString, List<TableInfo>> _ciTableNames = new HashMap<>();
   private HashMap<CaseInsensitiveString, List<ProcedureInfo>> _ciProcedureNames = new HashMap<>();
   private HashSet<CaseInsensitiveString> _ciKeywords = new HashSet<>();
   private HashSet<CaseInsensitiveString> _ciColumns = new HashSet<>();

   private CaseInsensitiveString _buf = new CaseInsensitiveString();

   public void addProc(String procName)
   {
      addProc(new ProcedureInfo(null, null, procName, DUMMY_PROCEDURE_TYPE));
   }

   public void addProc(ProcedureInfo pi)
   {
      CaseInsensitiveString procedureName = new CaseInsensitiveString(pi.getName());
      List<ProcedureInfo> procedureInfos = _ciProcedureNames.get(procedureName);

      if(null == procedureInfos)
      {
         procedureInfos = new ArrayList<>();
         _ciProcedureNames.put(procedureName, procedureInfos);
      }
      procedureInfos.add(pi);
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
      return _ciProcedureNames.containsKey(_buf.setCharBuffer(buffer, offset, len));
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
      _ciProcedureNames.remove(new CaseInsensitiveString(procedureName));
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

   public List<String> getMatchingCaseSensitiveProcedureNames(String procedureName)
   {
      List<ProcedureInfo> procedureInfos = _ciProcedureNames.get(new CaseInsensitiveString(procedureName));

      if(null == procedureInfos)
      {
         return Arrays.asList(procedureName);
      }

      ArrayList<String> ret = new ArrayList<>();
      for (ProcedureInfo procedureInfo : procedureInfos)
      {
         ret.add(procedureInfo.getName());
      }

      return ret;
   }

   public List<ProcedureInfo> getProcedures(String procedureName)
   {
      return _ciProcedureNames.get(new CaseInsensitiveString(procedureName));
   }

}
