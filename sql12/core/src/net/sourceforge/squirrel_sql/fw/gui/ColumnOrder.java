package net.sourceforge.squirrel_sql.fw.gui;

public enum ColumnOrder
{
   NATURAL, ASC, DESC;

   public ColumnOrder next()
   {
      switch(this)
      {
         case NATURAL: return ASC;
         case ASC: return DESC;
         case DESC: return NATURAL;
         default: throw new IllegalStateException("Unknown ColumnOrder " + this);
      }

   }
}
