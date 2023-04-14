package net.sourceforge.squirrel_sql.fw.gui.table;

import net.sourceforge.squirrel_sql.fw.gui.ColumnOrder;

public interface SortingListener
{
   void sortingDone(int modelColumnIx, ColumnOrder columnOrder);
}
