package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CellDetailDisplayModeManager
{
   private List<Pair<ColumnDisplayDefinition, DisplayMode>> _columnsToNonDefaultDisplayMode = new ArrayList<>();

   public void putDisplayMode(ColumnDisplayDefinition currentColumnDisplayDefinition, DisplayMode displayMode)
   {
      _columnsToNonDefaultDisplayMode.removeIf(p -> p.getLeft().matchesByQualifiedName(currentColumnDisplayDefinition));
      if(displayMode != DisplayMode.DEFAULT)
      {
         _columnsToNonDefaultDisplayMode.add(Pair.of(currentColumnDisplayDefinition, displayMode));
      }
   }

   public DisplayMode getNonDefaultDisplayMode(ColumnDisplayDefinition cdd)
   {
      Optional<Pair<ColumnDisplayDefinition, DisplayMode>> nonDefaultDisplay =
            _columnsToNonDefaultDisplayMode.stream().filter(p -> p.getLeft().matchesByQualifiedName(cdd)).findFirst();

      if(nonDefaultDisplay.isPresent())
      {
         return nonDefaultDisplay.get().getRight();
      }

      return null;
   }
}
