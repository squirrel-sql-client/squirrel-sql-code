package org.squirrelsql.session.completion.joingenerator;

import org.squirrelsql.session.TableInfo;

import java.util.List;

public class TableFromStringResult
{
   private List<TableInfo> _tables;

   public TableFromStringResult(List<TableInfo> tables)
   {
      _tables = tables;
   }

   public TableInfo getTableInfo()
   {
      if (0 == _tables.size())
      {
         return null;
      }
      else
      {
         return _tables.get(0);
      }
   }

   public boolean isUseQualified()
   {
      return _tables.size() > 1;
   }

   public String getCatalogSchema()
   {
      return _tables.get(0).getStructItemSchema().getQualifiedName();
   }
}
