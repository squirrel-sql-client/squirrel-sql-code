package net.sourceforge.squirrel_sql.client.session.schemainfo;

import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

public class SchemaInfoColumnCache implements Serializable
{
   private static final ILogger s_log = LoggerController.createLogger(SchemaInfoColumnCache.class);



   private Map<CaseInsensitiveString, List<ExtendedColumnInfo>> _extendedColumnInfosByTableName =
       Collections.synchronizedMap(new TreeMap<CaseInsensitiveString, List<ExtendedColumnInfo>>());

   private Map<CaseInsensitiveString, List<ExtendedColumnInfo>> _extColumnInfosByColumnName = 
           Collections.synchronizedMap(new TreeMap<CaseInsensitiveString, List<ExtendedColumnInfo>>());

   private Set<CaseInsensitiveString> _tablesWithInaccessibleColumns = Collections.synchronizedSet(new HashSet<CaseInsensitiveString>());

   void writeColumsToCache(TableColumnInfo[] infos, CaseInsensitiveString simpleTableName)
   {
      ArrayList<ExtendedColumnInfo> ecisInTable = new ArrayList<ExtendedColumnInfo>();
      for (int i = 0; i < infos.length; i++)
      {
         ExtendedColumnInfo eci = new ExtendedColumnInfo(infos[i], simpleTableName.toString());
         ecisInTable.add(eci);

         CaseInsensitiveString ciColName = new CaseInsensitiveString(eci.getColumnName());
         List<ExtendedColumnInfo> ecisInColName = _extColumnInfosByColumnName.get(ciColName);
         if(null == ecisInColName)
         {
            ecisInColName = new ArrayList<ExtendedColumnInfo>();
            _extColumnInfosByColumnName.put(ciColName, ecisInColName);
         }
         ecisInColName.add(eci);
      }

      // Note: A CaseInsensitiveString can be a mutable string.
      // In fact it is a mutable string here because this is usually called from
      // within Syntax coloring which uses a mutable string.
      CaseInsensitiveString imutableString = new CaseInsensitiveString(simpleTableName.toString());
      _extendedColumnInfosByTableName.put(imutableString, ecisInTable);
   }

   void clearColumns()
   {
      _tablesWithInaccessibleColumns.clear();
      _extColumnInfosByColumnName.clear();
      _extendedColumnInfosByTableName.clear();
   }

   public boolean didTryLoadingColumns(CaseInsensitiveString tableName)
   {
      return _extendedColumnInfosByTableName.containsKey(tableName) || _tablesWithInaccessibleColumns.contains(tableName);
   }


   Map<CaseInsensitiveString, List<ExtendedColumnInfo>> getExtColumnInfosByColumnNameForReadOnly()
   {
      return _extColumnInfosByColumnName;
   }

   void clearColumns(CaseInsensitiveString ciSimpleTableName)
   {
      _tablesWithInaccessibleColumns.remove(ciSimpleTableName);

      List<ExtendedColumnInfo> ecisInTable = _extendedColumnInfosByTableName.remove(ciSimpleTableName);

      if(null == ecisInTable)
      {
         // Columns have not yet been loaded
         return;
      }

      for(Iterator<ExtendedColumnInfo> j=ecisInTable.iterator();j.hasNext();)
      {
         ExtendedColumnInfo eci = j.next();
         CaseInsensitiveString ciColName = new CaseInsensitiveString(eci.getColumnName());
         _extColumnInfosByColumnName.remove(ciColName);
      }
   }

   public void writeColumsNotAccessible(Throwable th, CaseInsensitiveString simpleTableName)
   {
      // Note: A CaseInsensitiveString can be a mutable string.
      // In fact it is a mutable string here because this is usually called from
      // within Syntax coloring which uses a mutable string.
      CaseInsensitiveString imutableString = new CaseInsensitiveString(simpleTableName.toString());
      _tablesWithInaccessibleColumns.add(imutableString);
      s_log.error("Failed to load columns for table " + simpleTableName, th);
   }

   public List<ExtendedColumnInfo> getExtendedColumnInfosForReadOnly(CaseInsensitiveString cissTableName)
   {
      return _extendedColumnInfosByTableName.get(cissTableName);
   }
}
