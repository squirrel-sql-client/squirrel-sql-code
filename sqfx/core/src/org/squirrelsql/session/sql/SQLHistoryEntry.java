package org.squirrelsql.session.sql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.squirrelsql.services.Utils;
import org.squirrelsql.table.RowObjectTableLoaderColumn;

import java.util.Date;

public class SQLHistoryEntry
{
   private String _normalizedSql;
   private String _sql;
   private boolean _new = true;
   private Date _stamp = new Date();
   private String _displaySql;
   private String _aliasName;

   public SQLHistoryEntry()
   {
      // For deserialization
   }

   public SQLHistoryEntry(String sql, String aliasName)
   {
      setSql(sql);
      _aliasName = aliasName;
   }

   @Override
   public String toString()
   {
      return _displaySql;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SQLHistoryEntry that = (SQLHistoryEntry) o;

      if (_normalizedSql != null ? !_normalizedSql.equals(that._normalizedSql) : that._normalizedSql != null) return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      return _normalizedSql != null ? _normalizedSql.hashCode() : 0;
   }


   @JsonIgnore
   @RowObjectTableLoaderColumn(columnIndex = 2, columnHeaderI18nKey = "SQLHistoryEntry.col.header.sql")
   public String getNormalizedSql()
   {
      return _normalizedSql;
   }

   public String getSql()
   {
      return _sql;
   }

   public void setSql(String sql)
   {
      _sql = sql.trim();

      String buf = Utils.removeNewLines(_sql);

      int bufLen = buf.length();
      buf = buf.replaceAll("  ", " ");

      while (buf.length() < bufLen)
      {
         bufLen = buf.length();
         buf = buf.replaceAll("  ", " ");
      }

      _normalizedSql = buf;

      _displaySql = Utils.createSqlShortText(_normalizedSql, 300);
   }

   public boolean isNew()
   {
      return _new;
   }

   public void setNew(boolean aNew)
   {
      _new = aNew;
   }

   @RowObjectTableLoaderColumn(columnIndex = 0, columnHeaderI18nKey = "SQLHistoryEntry.col.header.last.used")
   public Date getStamp()
   {
      return _stamp;
   }

   public void setStamp(Date stamp)
   {
      _stamp = stamp;
   }

   @RowObjectTableLoaderColumn(columnIndex = 1, columnHeaderI18nKey = "SQLHistoryEntry.col.header.alias.name")
   public String getAliasName()
   {
      return _aliasName;
   }

   public void setAliasName(String aliasName)
   {
      _aliasName = aliasName;
   }
}
