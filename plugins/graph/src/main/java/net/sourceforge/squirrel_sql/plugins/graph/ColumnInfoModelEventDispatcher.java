package net.sourceforge.squirrel_sql.plugins.graph;

public interface ColumnInfoModelEventDispatcher
{
   void fireChanged(TableFramesModelChangeType changeType);
}
