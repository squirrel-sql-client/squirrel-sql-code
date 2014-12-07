package org.squirrelsql.table;

public class CellProperties
{
   private final String _value;
   private final String _style;
   private boolean _multiLineCell;

   public CellProperties(String value, String style, boolean multiLineCell)
   {
      _value = value;
      _style = style;
      _multiLineCell = multiLineCell;
   }

   public String getValue()
   {
      return _value;
   }

   public String getStyle()
   {
      return _style;
   }

   public boolean isMultiLineCell()
   {
      return _multiLineCell;
   }
}
