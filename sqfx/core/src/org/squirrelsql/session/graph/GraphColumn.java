package org.squirrelsql.session.graph;

import org.squirrelsql.session.ColumnInfo;

public class GraphColumn
{
   private ColumnInfo _columnInfo;
   private String _postFix;

   public GraphColumn(ColumnInfo columnInfo, PrimaryKeyInfo pk, ImportedKeysInfo impKeysInfo)
   {
      _columnInfo = columnInfo;

      _postFix = " ";

      if(pk.isPk(_columnInfo))
      {
         _postFix += "(PK)";
      }

      if(impKeysInfo.isFk(_columnInfo))
      {
         _postFix += "(FK)";
      }

   }

   public String getDescription()
   {
      return _columnInfo.getDescription() + _postFix;
   }
}
