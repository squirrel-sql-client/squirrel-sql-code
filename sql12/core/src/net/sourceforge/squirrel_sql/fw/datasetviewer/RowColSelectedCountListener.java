package net.sourceforge.squirrel_sql.fw.datasetviewer;

public interface RowColSelectedCountListener
{
   void rowColSelectedCountOrPosChanged(int selectedRowCount, int selectedColumnCount, int selectedRow, int selectedColumn);
}
