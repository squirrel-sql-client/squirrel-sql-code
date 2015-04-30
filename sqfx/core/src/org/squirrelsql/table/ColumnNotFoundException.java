package org.squirrelsql.table;


public class ColumnNotFoundException extends IllegalArgumentException
{
   public ColumnNotFoundException(String msg)
   {
      super(msg);
   }
}
