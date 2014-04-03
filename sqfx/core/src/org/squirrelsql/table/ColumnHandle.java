package org.squirrelsql.table;

import org.squirrelsql.aliases.SchemaLoadOptions;

public class ColumnHandle
{
   private String _header;
   private SchemaLoadOptions[] _selectableValues = new SchemaLoadOptions[0];

   public ColumnHandle(String header)
   {
      _header = header;
   }

   public String getHeader()
   {
      return _header;
   }

   public void setSelectableValues(SchemaLoadOptions... selectableValues)
   {
     _selectableValues = selectableValues;
   }

   public SchemaLoadOptions[] getSelectableValues()
   {
      return _selectableValues;
   }
}
