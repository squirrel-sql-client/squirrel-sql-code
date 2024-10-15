package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.util.List;

public class ColsToSearchHolder
{
   public static final ColsToSearchHolder UNFILTERED = new ColsToSearchHolder();

   private List<CheckColumnWrapper> _checkColumnWrappers;
   private FindService _findService;

   private ColsToSearchHolder()
   {
   }

   ColsToSearchHolder(List<CheckColumnWrapper> checkColumnWrappers, FindService findService)
   {
      _checkColumnWrappers = checkColumnWrappers;
      _findService = findService;
   }

   public List<CheckColumnWrapper> getCheckColumnWrappers()
   {
      return _checkColumnWrappers;
   }

   public boolean isToSearch(int columnViewIx)
   {
      if(this == UNFILTERED)
      {
         return true;
      }

      ColumnDisplayDefinition cddByViewIx = _findService.getColumnDisplayDefinitionByViewIndex(columnViewIx);

      if(null == cddByViewIx)
      {
         return false;
      }

      boolean ret = _checkColumnWrappers.stream()
                                        .filter(cw -> cw.isToSearch())
                                        .anyMatch(cw -> cddByViewIx.matchesByQualifiedName(cw.getColumnDisplayDefinition()));

      return ret;
   }

   public boolean isNarrowed()
   {
      return this != UNFILTERED && _checkColumnWrappers.stream().anyMatch(c -> false == c.isToSearch());
   }
}
