package org.squirrelsql.session.sql.copysqlpart;

import org.squirrelsql.session.ColumnInfo;

public class InStatColumnInfo
{
   private ColumnInfo _columnInfo;

   private StringBuffer _instat;

   public void setColumnInfo(ColumnInfo columnInfo)
   {
      _columnInfo = columnInfo;
   }

   public ColumnInfo getColumnInfo()
   {
      return _columnInfo;
   }

   public void setInstat(StringBuffer instat)
   {
      _instat = instat;
   }

   public StringBuffer getInstat()
   {
      return _instat;
   }


}
