package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.awt.Component;
import java.awt.Font;
import java.util.HashSet;
import java.util.Set;

public class FontService
{
   public static final Font MONO_SPACED_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

   private final DataSetViewerTable _dataSetViewerTable;
   private Font _originalCellFont = null;
   private Set<Integer> _monospacedColumns = new HashSet<>();

   public FontService(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;
      _dataSetViewerTable.getButtonTableHeader().addColumnDragListener(() -> onColumnDragged());
   }

   private void onColumnDragged()
   {
      _monospacedColumns.clear();
      _dataSetViewerTable.repaint();
   }

   public void initCellFont(Component tableCellRendererComponent, int columnIx)
   {
      if (RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX ==  _dataSetViewerTable.getColumnModel().getColumn(columnIx).getModelIndex())
      {
         return;
      }

      if(null == _originalCellFont)
      {
         _originalCellFont = tableCellRendererComponent.getFont();
      }

      if(_monospacedColumns.contains(columnIx))
      {
         tableCellRendererComponent.setFont(MONO_SPACED_FONT);
      }
      else
      {
         tableCellRendererComponent.setFont(_originalCellFont);
      }
   }

   public void toggleMonoSpaced(int[] selectedColumnIndexes)
   {
      //List<Integer> buf = List.of(Arrays.stream(selectedColumnIndexes).boxed().toArray(Integer[]::new));
      //_monospacedColumns.removeIf(ix -> false == buf.contains(ix));

      for(int selectedColumn : selectedColumnIndexes)
      {
         if(_monospacedColumns.contains(selectedColumn))
         {
            _monospacedColumns.remove(selectedColumn);
         }
         else
         {
            _monospacedColumns.add(selectedColumn);
         }
      }

      _dataSetViewerTable.repaint();
   }
}
