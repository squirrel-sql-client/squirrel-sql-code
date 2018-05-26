package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import java.awt.Color;

public interface FindServiceCallBack
{
   Color getBackgroundColor(int viewRow, int viewColumn);

   void tableCellStructureChanged();
}
