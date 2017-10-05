package org.squirrelsql.session.graph;

import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.graph.graphdesktop.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TableWindowCtrl
{
   private Session _session;
   private final Window _window;
   private ColumnListCtrl _columnListCtrl;
   private GraphChannel _graphChannel;
   private TableInfo _tableInfo;
   private HashMap<String, FkProps> _fkPropsByFkName = new HashMap<>();
   private TableWindowCloseListener _tableWindowCloseListener;

   public TableWindowCtrl(Session session, GraphChannel graphChannel, TableInfo tableInfo, double x, double y, double width, double height, HashMap<String, FkProps> fkPropsByFkName, List<ColumnPersistence> columnPersistences, DrawLinesListener drawLinesListener, TableWindowCloseListener tableWindowCloseListener)
   {
      _session = session;
      _graphChannel = graphChannel;
      _tableInfo = tableInfo;
      _fkPropsByFkName = fkPropsByFkName;
      _tableWindowCloseListener = tableWindowCloseListener;

      _window = new Window(_tableInfo.getName());

      _columnListCtrl = new ColumnListCtrl(_session, _graphChannel, _tableInfo, _window, columnPersistences, () -> drawLinesListener.drawLines(TableWindowCtrl.this));


      ListView<GraphColumn> columnListView = _columnListCtrl.getColumnListView();

      BorderPane contentPane = new BorderPane();
      BorderPane.setMargin(columnListView, new Insets(0,5,5,5));
      contentPane.setCenter(columnListView);

      _window.setContentPane(contentPane);

      _window.setLayoutX(x);
      _window.setLayoutY(y);

      _window.setPrefSize(width, height);

      _window.setCtrl(this);

      _window.boundsInParentProperty().addListener((observable, oldValue, newValue) -> drawLinesListener.drawLines(TableWindowCtrl.this));

      _window.setOnClosedAction(e -> onRemovedFromGraph(drawLinesListener));
   }

   private void onRemovedFromGraph(DrawLinesListener drawLinesListener)
   {
      drawLinesListener.drawLines(TableWindowCtrl.this);

      for (TableWindowCtrl tableWindowCtrl : _graphChannel.getGraphFinder().getAllTableCtrls())
      {
         if(tableWindowCtrl == this)
         {
            return;
         }

         tableWindowCtrl.removeNonDBConstraintDataTo(this);
      }
      _tableWindowCloseListener.closed(this);

   }

   private void removeNonDBConstraintDataTo(TableWindowCtrl other)
   {
      for (GraphColumn graphColumn : _columnListCtrl.getGraphColumns())
      {
         for (GraphColumn otherCol : other._columnListCtrl.getGraphColumns())
         {
            List<String> keysToRemove = graphColumn.removeNonDBConstraintDataTo(otherCol);

            for (String keyToRemove : keysToRemove)
            {
               removeNonDBFkId(keyToRemove);
            }
         }
      }
   }

   public void removeNonDBFkId(String constraintsNonDbFkId)
   {
      _fkPropsByFkName.remove(constraintsNonDbFkId);
   }



   public Window getWindow()
   {
      return _window;
   }

   public List<LineSpec> getLineSpecs(TableWindowCtrl pkCtrl)
   {
      if(pkCtrl == this)
      {
         return new ArrayList<>();
      }

      ArrayList<LineSpec> ret = new ArrayList<>();

      TableWindowSide windowSide;

      if(pkCtrl.getMidX() < getMidX())
      {
         windowSide = TableWindowSide.RIGHT;
      }
      else
      {
         windowSide = TableWindowSide.LEFT;
      }

      List<FkSpec> fkSpecs = _columnListCtrl.getFkSpecsTo(pkCtrl._tableInfo, windowSide);

      for (FkSpec fkSpec : fkSpecs)
      {
         fkSpec.setFkProps(getPersistenFkProps(fkSpec.getFkNameOrId()));
      }

      if(0 < fkSpecs.size())
      {

         ///////////////////////////////////////////////////////////
         // A table has either zero or one primary key.
         // That's why we consider only a single PkSpec.
         PkSpec pkSpec = pkCtrl._columnListCtrl.getPkSpec(windowSide);

         if (null != pkSpec)
         {
            for (FkSpec fkSpec : fkSpecs)
            {
               if (false == fkSpec.isNonDB())
               {
                  ret.add(new LineSpec(pkSpec, fkSpec));
               }
            }
         }
         //
         ////////////////////////////////////////////////////////

         //////////////////////////////////////////////////////////
         // A table may have several non DB constraints pointing to
         // several combinations of columns.
         // That's why we consider several non DB PkSpecs.
         for (FkSpec fkSpec : fkSpecs)
         {
            if (fkSpec.isNonDB())
            {
               PkSpec nonDbPkSpec = pkCtrl._columnListCtrl.getNonDbPkSpec(windowSide, fkSpec.getFkNameOrId());

               ret.add(new LineSpec(nonDbPkSpec, fkSpec));
            }
         }
         //
         ////////////////////////////////////////////////////////////
      }

      return ret;
   }

   private FkProps getPersistenFkProps(String fkName)
   {
      FkProps ret = _fkPropsByFkName.get(fkName);
      if(null == ret)
      {
         ret = new FkProps(fkName);
         _fkPropsByFkName.put(fkName, ret);
      }

      return ret;
   }

   private double getMidX()
   {
      return _window.getBoundsInParent().getMinX() + (_window.getBoundsInParent().getMaxX() - _window.getBoundsInParent().getMinX()) / 2.0;
   }

   public TableInfo getTableInfo()
   {
      return _tableInfo;
   }

   public HashMap<String, FkProps> getFkPropsByFkName()
   {
      return _fkPropsByFkName;
   }

   public List<GraphColumn> getGraphColumns()
   {
      return _columnListCtrl.getGraphColumns();
   }

   public List<ColumnPersistence> getNonDbColumnImportPersistences()
   {
      return _columnListCtrl.getNonDbColumnImportPersistences();
   }

}
