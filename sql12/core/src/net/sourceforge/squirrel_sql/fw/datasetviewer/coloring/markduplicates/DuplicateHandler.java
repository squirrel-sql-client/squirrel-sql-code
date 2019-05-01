package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import java.awt.Color;

public interface DuplicateHandler
{
   void markDuplicates(boolean selected);

   MarkDuplicatesMode getMode();

   Color getBackgroundForCell(int row, int column, Object value);
}
