package org.squirrelsql.session.schemainfo;

import org.squirrelsql.services.CaseInsensitiveString;
import org.squirrelsql.session.TableInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CaseInsensitiveCache implements Serializable
{
   private HashMap<CaseInsensitiveString, List<TableInfo>> _ciTableNames = new HashMap<>();
   private HashSet<CaseInsensitiveString> _ciProcedureNames = new HashSet<>();
   private HashSet<CaseInsensitiveString> _ciKeywords = new HashSet<>();
   private HashSet<CaseInsensitiveString> _ciColumns = new HashSet<>();

   private CaseInsensitiveString _buf = new CaseInsensitiveString();

   public void addProc(String s)
   {
      _ciProcedureNames.add(new CaseInsensitiveString(s));
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


   public List<TableInfo> getTables(char[] buffer, int offset, int len)
   {
      return _ciTableNames.get(_buf.setCharBuffer(buffer, offset, len));
   }

   public boolean isProcedure(char[] buffer, int offset, int len)
   {
      return _ciProcedureNames.contains(_buf.setCharBuffer(buffer, offset, len));
   }

   public boolean isKeyword(char[] buffer, int offset, int len)
   {
      return _ciKeywords.contains(_buf.setCharBuffer(buffer, offset, len));
   }

   public boolean isColumn(char[] buffer, int offset, int len)
   {
      return _ciColumns.contains(_buf.setCharBuffer(buffer, offset, len));
   }
}
