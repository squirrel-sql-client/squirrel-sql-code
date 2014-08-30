package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

public class InStatColumnInfo
{
   private ColumnDisplayDefinition _colDef;
   private StringBuffer _instat;

   public void setColDef(ColumnDisplayDefinition colDef)
   {
      _colDef = colDef;
   }

   public ColumnDisplayDefinition getColDef()
   {
      return _colDef;
   }

   public void setInstat(StringBuffer instat)
   {
      _instat = instat;
   }

   public StringBuffer getInstat()
   {
      return _instat;
   }

   public String getDescription()
   {
      return _colDef.getColumnName() + " IN " + _instat;
   }
}
