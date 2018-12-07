package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring;

import java.awt.Color;
import java.util.HashMap;

public class RowColorHandlerState
{
   private HashMap<Integer, Color> _colorByRow;

   RowColorHandlerState(HashMap<Integer, Color> _colorByRow)
   {
      this._colorByRow = _colorByRow;
   }

   HashMap<Integer, Color> getColorByRow()
   {
      return _colorByRow;
   }
}
