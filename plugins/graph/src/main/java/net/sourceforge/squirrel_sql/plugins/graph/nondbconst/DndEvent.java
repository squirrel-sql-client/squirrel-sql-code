package net.sourceforge.squirrel_sql.plugins.graph.nondbconst;

import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;
import net.sourceforge.squirrel_sql.plugins.graph.TableFrameController;

public class DndEvent
{
   private TableFrameController _tableFrameController;
   private ColumnInfo _columnInfo;

   public DndEvent(TableFrameController tableFrameController, ColumnInfo columnInfo)
   {
      _tableFrameController = tableFrameController;
      _columnInfo = columnInfo;
   }

   public TableFrameController getTableFrameController()
   {
      return _tableFrameController;
   }

   public ColumnInfo getColumnInfo()
   {
      return _columnInfo;
   }
}
