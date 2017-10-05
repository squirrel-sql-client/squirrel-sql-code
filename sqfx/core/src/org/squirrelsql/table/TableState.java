package org.squirrelsql.table;

import javafx.scene.control.TableColumn;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;

import java.util.ArrayList;

public class TableState
{
   private ArrayList<TableStateColInfo> _tableStateColInfos = new ArrayList<>();
   private ArrayList<TableStateSortInfo> _tableStateSortInfos = new ArrayList<>();

   private ArrayList<Integer> _selectedRowIndices = new ArrayList<>();

   private final int _firstVisibleRow;


   public TableState(TableLoader sourceTableLoader)
   {

      for (int i = 0; i < sourceTableLoader.getColumnHandles().size(); i++)
      {
         ColumnHandle ch = sourceTableLoader.getColumnHandles().get(i);

         int viewIndex = sourceTableLoader.getColumnViewIndex(i);

         _tableStateColInfos.add(new TableStateColInfo(ch.getHeader(), ch.getTableColumn().getWidth(), viewIndex, i));
      }

      for (Object tableColumn : sourceTableLoader.getTableView().getSortOrder())
      {
         TableColumn col = (TableColumn) tableColumn;

         int modelIndex = sourceTableLoader.getColumnModelIndex(col);
         int viewIndex = sourceTableLoader.getColumnViewIndex(col);
         TableColumn.SortType sortType = col.getSortType();
         String header = col.getText();

         _tableStateSortInfos.add(new TableStateSortInfo(header, sortType, viewIndex, modelIndex));
      }

      _selectedRowIndices.addAll(sourceTableLoader.getTableView().getSelectionModel().getSelectedIndices());

      _firstVisibleRow = TableUtil.getFirstVisibleRow(sourceTableLoader.getTableView());
   }

   public void apply(TableLoader targetTableLoader)
   {
      try
      {
         if(null == targetTableLoader.getTableView())
         {
            throw new IllegalArgumentException("The TableLoader must have loaded a table before this method can be called.");
         }

         for (int i = 0; i < _tableStateColInfos.size(); i++)
         {
            TableStateColInfo tableStateColInfo = _tableStateColInfos.get(i);

            TableColumn matchCol = findMatchInTarget(tableStateColInfo.getModelIndex(), tableStateColInfo.getHeader(), targetTableLoader);
            if(null != matchCol)
            {
               matchCol.setPrefWidth(tableStateColInfo.getWidth());

               targetTableLoader.getTableView().getColumns().remove(matchCol);
               targetTableLoader.getTableView().getColumns().add(tableStateColInfo.getViewIndex(), matchCol);
            }
         }

         targetTableLoader.getTableView().getSortOrder().clear();

         for (TableStateSortInfo tableStateSortInfo : _tableStateSortInfos)
         {
            TableColumn matchCol = findMatchInTarget(tableStateSortInfo.getModelIndex(), tableStateSortInfo.getHeader(), targetTableLoader);
            if(null != matchCol)
            {
               matchCol.setSortType(tableStateSortInfo.getSortType());
               targetTableLoader.getTableView().getSortOrder().add(matchCol);
            }
         }

         targetTableLoader.getTableView().getSelectionModel().clearSelection();

         for (Integer selectedRowIndex : _selectedRowIndices)
         {
            targetTableLoader.getTableView().getSelectionModel().select((int)selectedRowIndex);
         }

         TableUtil.scrollTo(targetTableLoader.getTableView(), _firstVisibleRow);
      }
      catch (Exception e)
      {
         new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_LOG).error("Failed to apply table state", e);
      }
   }

   private TableColumn findMatchInTarget(int modelIndex, String header, TableLoader targetTableLoader)
   {
      if(modelIndex < targetTableLoader.getColumnCount()
            && targetTableLoader.getColumnHandles().get(modelIndex).getHeader().equals(header))
      {
         return targetTableLoader.getColumnHandles().get(modelIndex).getTableColumn();
      }

      return null;

   }

   private static class TableStateColInfo
   {
      private final String _header;
      private final double _width;
      private final int _viewIndex;
      private final int _modelIndex;

      public TableStateColInfo(String header, double width, int viewIndex, int modelIndex)
      {

         _header = header;
         _width = width;
         _viewIndex = viewIndex;
         _modelIndex = modelIndex;
      }

      public String getHeader()
      {
         return _header;
      }

      public double getWidth()
      {
         return _width;
      }

      public int getViewIndex()
      {
         return _viewIndex;
      }

      public int getModelIndex()
      {
         return _modelIndex;
      }
   }

   private static class TableStateSortInfo
   {
      private final String _header;
      private final TableColumn.SortType _sortType;
      private final int _viewIndex;
      private final int _modelIndex;

      public TableStateSortInfo(String header, TableColumn.SortType sortType, int viewIndex, int modelIndex)
      {
         _header = header;
         _sortType = sortType;
         _viewIndex = viewIndex;
         _modelIndex = modelIndex;
      }

      public String getHeader()
      {
         return _header;
      }

      public TableColumn.SortType getSortType()
      {
         return _sortType;
      }

      public int getViewIndex()
      {
         return _viewIndex;
      }

      public int getModelIndex()
      {
         return _modelIndex;
      }
   }
}
