package net.sourceforge.squirrel_sql.fw.datasetviewer;

public interface RowSelectionListener
{
   void selectionChanged(int[] nowSelectedIx, int[] formerSelectedIx);
}
