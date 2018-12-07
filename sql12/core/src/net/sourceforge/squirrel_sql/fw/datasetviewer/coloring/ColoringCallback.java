package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring;

import java.awt.Color;

public interface ColoringCallback
{
   Color getCellColor(int row, int column, boolean isSelected);
}
